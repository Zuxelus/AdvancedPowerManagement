package com.zuxelus.apm.tileentities;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.utils.MovingAverage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityEnergyStorage;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
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

public class TileEntityChargingBench extends TileEntityEnergyStorage implements ISlotItemFilter, ITilePacketHandler, IWrenchable, IBlockHorizontal {
	public static final int[] TIER = {1, 2, 3, 4};
	public static final int[] CAPACITY = {40000, 300000, 4000000, 40000000};

	public static final int SLOT_IN = 0;
	public static final int SLOT_DISCHARGER = 1;
	public static final int SLOT_OUT = 2;
	public static final int SLOT_CHARGE = 3; // x12
	public static final int SLOT_UPGRADE = 15; // x4

	private int meta;
	private int chargeLevel;
	public boolean doingWork;
	public boolean powered;
	private double drainFactor;
	private double chargeFactor;
	private int ticksRequired;
	private double energyRequired;
	public MovingAverage inputTracker = new MovingAverage(12);

	public TileEntityChargingBench(int meta) {
		super("tile." + APM.PREFIX[meta] + "_charging_bench", TIER[meta], 0, CAPACITY[meta]);
		this.meta = meta;
		chargeLevel = 0;
		doingWork = false;
		drainFactor = 1.0D;
		chargeFactor = 1.0D;
		ticksRequired = 0;
		energyRequired = 0.0D;
		energyReceived = 0.0D;
	}

	public int getChargeLevel() {
		return chargeLevel;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (worldObj.isRemote)
			powered = isPowered;
	}

	public int getTicksRequired() {
		return ticksRequired;
	}

	public double getEnergyRequired() {
		return energyRequired;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) { }

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("energy"))
				energy = tag.getDouble("energy");
			if (tag.hasKey("average"))
				inputTracker.setAverage(tag.getDouble("average"));
			if (tag.hasKey("ticksRequired"))
				ticksRequired = tag.getInteger("ticksRequired");
			if (tag.hasKey("energyRequired"))
				energyRequired = tag.getDouble("energyRequired");
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		tag.setBoolean("powered", powered);
		tag.setInteger("chargeLevel", chargeLevel);
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
		int oldChargeLevel = chargeLevel;
		if (tag.hasKey("chargeLevel"))
			chargeLevel = tag.getInteger("chargeLevel");
		boolean oldDoingWork = doingWork;
		if (tag.hasKey("doingWork"))
			doingWork = tag.getBoolean("doingWork");
		if (worldObj != null && worldObj.isRemote && (chargeLevel != oldChargeLevel || doingWork != oldDoingWork))
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
//		tag.setByte("redstoneMode", redstoneMode);
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

		inputTracker.tick(energyReceived);
		ticksRequired = 0;
		energyRequired = 0.0D;
		energyReceived = 0.0D;
		boolean oldDoingWork = doingWork;
		doingWork = false;
		handleDischarger(SLOT_DISCHARGER);
		for (int slot = SLOT_CHARGE; slot < SLOT_CHARGE + 12; slot++)
			handleOveclockedCharger(slot);
		moveOutputItems();
		acceptInputItems();

		// Determine if and how completion time will be affected by lack of energy and input rate
		if (energyRequired > energy) {
			double avg = inputTracker.getAverage();
			if (avg >= 1.0F) {
				int time = (int) Math.ceil((energyRequired - energy) / avg);
				if (time > ticksRequired)
					ticksRequired = time;
			} else
				ticksRequired = -1;
		}
		updateState(oldDoingWork);
	}

	private void handleOveclockedCharger(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null && energy > 0 && stack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) stack.getItem();
			double limit = item.getTransferLimit(stack) * chargeFactor;
			if (limit == 0)
				return;
			double amount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, true, true);
			int eta = (int) Math.ceil(amount / limit);
			if (ticksRequired < eta)
				ticksRequired = eta;
			energyRequired += drainFactor / chargeFactor * amount;
			amount = Math.min(Math.min(amount, limit), energy);
			if (amount > 0) {
				energy -= ElectricItem.manager.charge(stack, amount, tier, false, false) * drainFactor / chargeFactor;
				doingWork = true;
			}
		}
	}

	private void moveOutputItems() {
		ItemStack stack = getStackInSlot(SLOT_OUT);
		if (stack != null)
			return;

		for (int slot = SLOT_CHARGE; slot < SLOT_CHARGE + 12; slot++) {
			stack = getStackInSlot(slot);
			if (stack != null && stack.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) stack.getItem();
				if (ElectricItem.manager.charge(stack.copy(), 1, item.getTier(stack), false, true) == 0) {
					setInventorySlotContents(SLOT_OUT, stack);
					setInventorySlotContents(slot, null);
					break;
				}
			}
		}
	}

	private void acceptInputItems() {
		ItemStack stack = getStackInSlot(SLOT_IN);
		if (stack == null || !(stack.getItem() instanceof IElectricItem))
			return;
		IElectricItem item = (IElectricItem) stack.getItem();
		if (item.getTier(stack) > tier)
			return;
		for (int slot = SLOT_CHARGE; slot < SLOT_CHARGE + 12; slot++)
			if (getStackInSlot(slot) == null) {
				ItemStack input = stack.copy();
				input.stackSize = 1;
				stack.stackSize -= 1;
				if (stack.stackSize <= 0)
					setInventorySlotContents(SLOT_IN, null);
				setInventorySlotContents(slot, input);
				break;
			}
	}

	public void updateState(boolean oldDoingWork) {
		int old = chargeLevel;
		chargeLevel = calcChargeLevel();
		if (!worldObj.isRemote && (chargeLevel != old || doingWork != oldDoingWork))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private int calcChargeLevel() {
		return (int) (12 * energy / capacity);
	}

	@Override
	public void markDirty() {
		doUpgradeEffects();
		super.markDirty();
	}

	public void doUpgradeEffects() {
		int ocCount = 0;
		int tfCount = 0;
		int esCount = 0;
		for (int slot = SLOT_UPGRADE; slot < SLOT_UPGRADE + 4; slot++) {
			ItemStack stack = getStackInSlot(slot);
			if (stack != null) {
				if (stack.isItemEqual(IC2Items.getItem("overclockerUpgrade")))
					ocCount += stack.stackSize;
				else if (stack.isItemEqual(IC2Items.getItem("energyStorageUpgrade")))
					esCount += stack.stackSize;
				else if (stack.isItemEqual(IC2Items.getItem("transformerUpgrade")))
					tfCount += stack.stackSize;
			}
		}

		if (ocCount > 20)
			ocCount = 20;
		if (esCount > 64)
			esCount = 64;
		if (tfCount > 3)
			tfCount = 3;

		// Overclockers:
		chargeFactor = (float) Math.pow(1.3D, ocCount); // 30% more power transferred to an item per overclocker, exponential.
		drainFactor = (float) Math.pow(1.5D, ocCount); // 50% more power drained per overclocker, exponential. Yes, you waste power, that's how OCs work.

		// Transformers:
		tier = baseTier + tfCount; // Allows better energy storage items to be plugged into the battery slot of lower tier benches.
		if (tier > 4)
			tier = 4;
		output = (int) Math.pow(2, 2 * tier + 3);

		// Energy Storage:
		switch (baseTier) {
		case 1:
			capacity = baseCapacity + esCount * 10000; // LV: 25% additional storage per upgrade (10,000).
			break;
		case 2:
			capacity = baseCapacity + esCount * 60000; // MV: 10% additional storage per upgrade (60,000).
			break;
		case 3:
			capacity = baseCapacity + esCount * 500000; // HV: 5% additional storage per upgrade (500,000).
			break;
		default:
			capacity = baseCapacity; // This shouldn't ever happen, but just in case, it shouldn't crash it - storage upgrades just won't work.
		}
		if (energy > capacity)
			energy = capacity; // If storage has decreased, lose any excess energy.
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	private void notifyBlockUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection dir) {
		return true;
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 20;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		if (slot >= SLOT_UPGRADE) {
			return stack.isItemEqual(IC2Items.getItem("overclockerUpgrade"))
					|| stack.isItemEqual(IC2Items.getItem("transformerUpgrade"))
					|| stack.isItemEqual(IC2Items.getItem("energyStorageUpgrade"));
		}
		if (!(stack.getItem() instanceof IElectricItem))
			return false;
		IElectricItem item = (IElectricItem) stack.getItem();
		return item.getTier(stack) <= tier;
	}

	@Override
	public double getDemandedEnergy() {
		if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			return 0;
		return super.getDemandedEnergy();
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
		return new ItemStack(ModItems.blockChargingBench, 1, meta);
	}
}
