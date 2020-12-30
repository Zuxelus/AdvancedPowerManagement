package com.zuxelus.apm.tileentities;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.utils.MovingAverage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityEnergySource;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBatteryStation extends TileEntityEnergySource implements ISlotItemFilter, ITilePacketHandler, IWrenchable, IBlockHorizontal {
	public static final int[] TIER = {1, 2, 3, 4};
	public static final int[] OUTPUT = {32, 128, 512, 2048};

	public static final int SLOT_IN = 0;
	public static final int SLOT_OUT = 1;
	public static final int SLOT_DISCHARGE = 2; // x12

	private int meta;
	public boolean doingWork;
	public boolean powered;
	private double energyTotal;
	private boolean opMode;
	public MovingAverage outputTracker = new MovingAverage(12);

	public TileEntityBatteryStation(int meta) {
		super("tile." + APM.PREFIX[meta] + "_battery_station", TIER[meta], OUTPUT[meta], OUTPUT[meta]);
		this.meta = meta;
		doingWork = false;
		energyTotal = 0.0D;
		opMode = true;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (worldObj.isRemote)
			powered = isPowered;
	}

	public double getEnergyTotal() {
		return energyTotal;
	}

	public boolean getOpMode() {
		return opMode;
	}

	public void setOpMode(boolean value) {
		if (!worldObj.isRemote && opMode != value)
			notifyBlockUpdate();
		opMode = value;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setOpMode(tag.getInteger("value") == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("average"))
				outputTracker.setAverage(tag.getDouble("average"));
			if (tag.hasKey("energyTotal"))
				energyTotal = tag.getDouble("energyTotal");
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		tag.setBoolean("powered", powered);
		tag.setBoolean("doingWork", doingWork);
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
		if (tag.hasKey("powered"))
			updatePowered(tag.getBoolean("powered"));
		boolean oldDoingWork = doingWork;
		if (tag.hasKey("doingWork"))
			doingWork = tag.getBoolean("doingWork");
		boolean oldOpMode = opMode;
		if (tag.hasKey("opMode"))
			opMode = tag.getBoolean("opMode");
		if (worldObj != null && worldObj.isRemote && (doingWork != oldDoingWork || opMode != oldOpMode))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setBoolean("opMode", opMode);
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

		onLoad();

		energyTotal = 0.0D;
		boolean oldDoingWork = doingWork;
		doingWork = false;
		handleDischarger();
		moveOutputItems();
		acceptInputItems();
		updateState(oldDoingWork);
	}

	private void handleDischarger() {
		double old = energy;
		for (int slot = SLOT_DISCHARGE; slot < SLOT_DISCHARGE + 12; slot++) {
			ItemStack stack = getStackInSlot(slot);
			if (stack != null && stack.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) stack.getItem();
				if (item.canProvideEnergy(stack)) {
					if (capacity > energy)
						energy += ElectricItem.manager.discharge(stack, capacity - energy, tier, false, false, false);
					energyTotal += ElectricItem.manager.discharge(stack, Double.POSITIVE_INFINITY, tier, true, false, true);
				}
			}
		}
		outputTracker.tick(energy - old);
		doingWork = energy != old;
	}
	
	private void moveOutputItems() {
		ItemStack output = getStackInSlot(SLOT_OUT);
		if (output != null && (!output.isStackable() || output.stackSize >= output.getMaxStackSize()))
			return;

		for (int slot = SLOT_DISCHARGE; slot < SLOT_DISCHARGE + 12; slot++) {
			ItemStack stack = getStackInSlot(slot);
			if (stack != null && stack.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) stack.getItem();
				if (ElectricItem.manager.discharge(stack, 1, tier, true, false, true) == 0) {
					if (output == null)
						setInventorySlotContents(SLOT_OUT, stack);
					else {
						if (!stack.isItemEqual(output) || output.stackSize >= output.getMaxStackSize())
							continue;
						output.stackSize += 1;
						setInventorySlotContents(SLOT_OUT, output);
					}
					setInventorySlotContents(slot, null);
				}
			}
		}
	}

	private void acceptInputItems() {
		if (opMode && energy >= capacity)
			return;
		ItemStack stack = getStackInSlot(SLOT_IN);
		if (stack == null || !(stack.getItem() instanceof IElectricItem))
			return;
		IElectricItem item = (IElectricItem) stack.getItem();
		if (item.getTier(stack) > tier)
			return;
		for (int slot = SLOT_DISCHARGE; slot < SLOT_DISCHARGE + 12; slot++)
			if (getStackInSlot(slot) == null) {
				ItemStack input = stack.copy();
				input.stackSize = 1;
				stack.stackSize -= 1;
				if (stack.stackSize <= 0)
					setInventorySlotContents(SLOT_IN, null);
				setInventorySlotContents(slot, input);
				if (opMode)
					return;
				break;
			}
	}

	public void updateState(boolean oldDoingWork) {
		if (!worldObj.isRemote && doingWork != oldDoingWork)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	private void notifyBlockUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// IEnergySource
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection dir) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		return powered ? 0.0D : energy >= output ? output : 0.0D; 
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 14;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		if (!(stack.getItem() instanceof IElectricItem))
			return false;
		IElectricItem item = (IElectricItem) stack.getItem();
		return item.getTier(stack) <= tier;
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
		return new ItemStack(ModItems.blockBatteryStation, 1, meta);
	}
}
