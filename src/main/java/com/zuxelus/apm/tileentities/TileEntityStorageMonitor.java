package com.zuxelus.apm.tileentities;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.items.ItemLinkCard;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStorageMonitor extends TileEntityInventory implements ISlotItemFilter, ITilePacketHandler, IWrenchable, IBlockHorizontal {
	public static final int SLOT_CARD = 0;
	private static final int TICK_DELAY = 5;
	private int tickTime;
	private int chargeLevel;
	private int lowerBoundary;
	private int upperBoundary;
	private int energy;
	private int capacity;
	private boolean poweredBlock;
	public boolean blockState;

	public TileEntityStorageMonitor() {
		super("tile.storage.monitor");
		tickTime = 0;
		lowerBoundary = 60;
		upperBoundary = 90;
		energy = 0;
		capacity = 0;
		poweredBlock = false;
		blockState = false;
	}

	public int getChargeLevel() {
		return chargeLevel;
	}

	public int getLowerBoundary() {
		return lowerBoundary;
	}

	public void setLowerBoundary(int value) {
		lowerBoundary = value;
		if (lowerBoundary > 100)
			lowerBoundary = 100;
		if (lowerBoundary < 1)
			lowerBoundary = 1;
		if (lowerBoundary > upperBoundary)
			upperBoundary = lowerBoundary;
	}

	public int getUpperBoundary() {
		return upperBoundary;
	}

	public void setUpperBoundary(int value) {
		upperBoundary = value;
		if (upperBoundary > 100)
			upperBoundary = 100;
		if (upperBoundary < 1)
			upperBoundary = 1;
		if (upperBoundary < lowerBoundary)
			lowerBoundary = upperBoundary;
	}

	public int getEnergy() {
		return energy;
	}

	public int getCapacity() {
		return capacity;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	public boolean getBlockState() {
		return blockState;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setUpperBoundary(tag.getInteger("value"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setLowerBoundary(tag.getInteger("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("chargeLevel") && tag.hasKey("energy") && tag.hasKey("capacity")) {
				chargeLevel = tag.getInteger("chargeLevel");
				energy = tag.getInteger("energy");
				capacity = tag.getInteger("capacity");
			}
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setInteger("chargeLevel", chargeLevel);
		tag.setBoolean("poweredBlock", poweredBlock);
		tag.setBoolean("blockState", blockState);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if (!worldObj.isRemote)
			return;
		readProperties(pkt.func_148857_g());
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		upperBoundary = tag.getInteger("upperBoundary");
		lowerBoundary = tag.getInteger("lowerBoundary");

		int oldChargeLevel = chargeLevel;
		if (tag.hasKey("chargeLevel"))
			chargeLevel = tag.getInteger("chargeLevel");
		boolean oldPoweredBlock = poweredBlock;
		if (tag.hasKey("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
		boolean oldBlockState = blockState;
		if (tag.hasKey("blockState"))
			blockState = tag.getBoolean("blockState");
		if (worldObj != null && worldObj.isRemote && (chargeLevel != oldChargeLevel || poweredBlock != oldPoweredBlock || blockState != oldBlockState))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // change icons
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("upperBoundary", upperBoundary);
		tag.setInteger("lowerBoundary", lowerBoundary);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;

		if (tickTime-- > 0)
			return;
		tickTime = TICK_DELAY;

		int[] coords = ItemLinkCard.getCoordinates(getStackInSlot(SLOT_CARD));
		energy = 0;
		capacity = 0;
		int oldChargeLevel = chargeLevel;
		boolean oldPoweredBlock = poweredBlock;
		boolean oldBlockState = blockState;
		if (coords != null) {
			TileEntity te = worldObj.getTileEntity(coords[0], coords[1], coords[2]);
			if (te instanceof IEnergyStorage) {
				energy = ((IEnergyStorage) te).getStored();
				capacity = ((IEnergyStorage) te).getCapacity();
			}
		}

		blockState = capacity > 0;
		if (blockState) {
			double chargePercent = energy * 100.0D / capacity;
			if ((!poweredBlock && chargePercent < lowerBoundary) || (poweredBlock && chargePercent >= upperBoundary))
				poweredBlock = !poweredBlock;
			chargeLevel = (int) (12 * energy / capacity);
		} else {
			poweredBlock = false;
			chargeLevel = 0;
		}

		if (poweredBlock != oldPoweredBlock)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		if (chargeLevel != oldChargeLevel || blockState != oldBlockState || poweredBlock != oldPoweredBlock)
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		return stack.isItemEqual(new ItemStack(ModItems.itemLinkCard, 1, ItemLinkCard.DAMAGE_CARD));
	}

	// IWrenchable
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public short getFacing() {
		return (short) facing.ordinal();
	}

	@Override
	public void setFacing(short facing) {
		setFacing((int) facing);
	}

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
		return new ItemStack(ModItems.blockStorageMonitor);
	}
}
