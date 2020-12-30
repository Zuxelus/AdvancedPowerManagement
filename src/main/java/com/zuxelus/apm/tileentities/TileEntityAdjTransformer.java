package com.zuxelus.apm.tileentities;

import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.utils.MovingAverage;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.Info;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAdjTransformer extends TileEntity implements IEnergySource, IEnergySink, ITilePacketHandler, IWrenchable {
	public final static int MIN_PACKET = 1;
	public final static int MAX_PACKET = 2048;
	public static final int MIN_OUTPUT = 1;
	public static final int MAX_OUTPUT = 8192;
	public static final int PACKETS_TICK = 64;

	public MovingAverage outputTracker = new MovingAverage(12);
	public MovingAverage inputTracker = new MovingAverage(12);

	private boolean addedToEnet;
	protected int maxInput = 8192;
	public double energy = 0;

	public int outputRate = 32;
	public int packetSize = 32;
	public int capacity = 32; 

	public byte[] sideSettings = {0, 0, 0, 0, 0, 0}; // DOWN, UP, NORTH, SOUTH, WEST, EAST

	private boolean isPowered() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	public double getEnergy() {
		return energy;
	}

	public int getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(int value) {
		int old = packetSize;
		packetSize = value;
		if (packetSize > MAX_PACKET)
			packetSize = MAX_PACKET;
		if (packetSize < MIN_PACKET)
			packetSize = MIN_PACKET;
		if (outputRate > packetSize * PACKETS_TICK)
			outputRate = packetSize * PACKETS_TICK;
		if (packetSize != old && worldObj != null && worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // change icons
	}

	public int getOutputRate() {
		return outputRate;
	}

	public void setOutputRate(int value) {
		outputRate = value;
		if (outputRate > packetSize * PACKETS_TICK)
			outputRate = packetSize * PACKETS_TICK;
		if (outputRate > MAX_OUTPUT)
			outputRate = MAX_OUTPUT;
		if (outputRate < MIN_OUTPUT)
			outputRate = MIN_OUTPUT;
		setPacketSize(outputRate);
	}

	public byte[] getSideSettings() {
		return sideSettings;
	}

	public void setSideSettings(int id, byte value) {
		if (worldObj != null && !worldObj.isRemote) {
			//TODO How can we make IC2 check the new emit/accept values without doing a reload?
			onChunkUnload();
			sideSettings[id] = value;
			onLoad();
		}
		sideSettings[id] = value;
		if (worldObj != null && worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // change icons
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setPacketSize(tag.getInteger("value"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setOutputRate(tag.getInteger("value"));
			break;
		case 3:
			if (tag.hasKey("id") && tag.hasKey("value"))
				setSideSettings(tag.getInteger("id"), tag.getByte("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("averageIn"))
				inputTracker.setAverage(tag.getDouble("averageIn"));
			if (tag.hasKey("averageOut"))
				outputTracker.setAverage(tag.getDouble("averageOut"));
			break;
		}
	}

	protected void readProperties(NBTTagCompound tag) {
		setPacketSize(tag.getInteger("packetSize"));
		setOutputRate(tag.getInteger("outputRate"));
		energy = tag.getDouble("energy");

		if (energy > packetSize * PACKETS_TICK)
			energy = packetSize * PACKETS_TICK;
		capacity = Math.max(packetSize, outputRate);

		NBTTagList list = tag.getTagList("sideSettings", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound entry = (NBTTagCompound) list.getCompoundTagAt(i);
			if (i >= 0 && i < sideSettings.length)
				sideSettings[i] = (byte) (entry.getByte("Flags") & 255);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag.setInteger("packetSize", packetSize);
		tag.setInteger("outputRate", outputRate);
		tag.setDouble("energy", energy);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < sideSettings.length; ++i) {
			NBTTagCompound entry = new NBTTagCompound();
			entry.setByte("Flags", sideSettings[i]);
			list.appendTag(entry);
		}
		tag.setTag("sideSettings", list);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	public void onLoad() {
		if (!addedToEnet && worldObj != null && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
		}
	}

	@Override
	public void invalidate() {
		onChunkUnload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		if (addedToEnet && worldObj != null && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;

		onLoad();
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return (sideSettings[direction.ordinal()] & 1) == 0;
	}

	@Override
	public double getDemandedEnergy() {
		return isPowered() ? 0 : Math.max(capacity - energy, 0);
	}

	@Override
	public int getSinkTier() {
		return 5;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if (energy >= capacity)
			return amount;
		energy += amount;
		inputTracker.tick(amount);
		return 0;
	}

	// IEnergySource
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return (sideSettings[direction.ordinal()] & 1) == 1;
	}

	@Override
	public double getOfferedEnergy() {
		return isPowered() ? 0 : Math.min(energy, outputRate);
	}

	@Override
	public void drawEnergy(double amount) {
		energy -= amount;
		outputTracker.tick((int)amount);
	}

	@Override
	public int getSourceTier() {
		for (int i = 0; i < 5; i++)
		if (packetSize <= TileEntityBatteryStation.OUTPUT[i])
			return i + 1;
		return 5;
	}

	// IWrenchable
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public short getFacing() {
		return 0;
	}

	@Override
	public void setFacing(short facing) { }

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(ModItems.blockAdjTransformer);
	}
}
