package com.zuxelus.apm.containers;

import com.zuxelus.apm.containers.slots.SlotInput;
import com.zuxelus.apm.containers.slots.SlotOutput;
import com.zuxelus.apm.containers.slots.SlotUpgrade;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;
import com.zuxelus.apm.tileentities.TileEntityStorageMonitor;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotArmor;
import com.zuxelus.zlib.containers.slots.SlotCard;
import com.zuxelus.zlib.containers.slots.SlotChargeable;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerStorageMonitor extends ContainerBase<TileEntityStorageMonitor> {
	private int lastChargeLevel = -1;
	private double lastEnergy = -1.0D;
	private double lastCapacity = -1.0D;

	public ContainerStorageMonitor(EntityPlayer player, TileEntityStorageMonitor te) {
		super(te);

		addSlotToContainer(new SlotCard(te, TileEntityStorageMonitor.SLOT_CARD, 8, 9));
		// inventory
		addPlayerInventorySlots(player, 190);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int chargeLevel = te.getChargeLevel();
		int energy = te.getEnergy();
		int capacity = te.getCapacity();
		for (int i = 0; i < crafters.size(); i++)
			if (lastChargeLevel != chargeLevel || lastEnergy != energy || lastCapacity != capacity) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setInteger("chargeLevel", chargeLevel);
				tag.setInteger("energy", energy);
				tag.setInteger("capacity", capacity);
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, tag);
			}
		lastChargeLevel = chargeLevel;
		lastEnergy = energy;
		lastCapacity = capacity;
	}
}
