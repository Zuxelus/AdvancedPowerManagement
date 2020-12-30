package com.zuxelus.apm.containers;

import com.zuxelus.apm.containers.slots.*;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotArmor;
import com.zuxelus.zlib.containers.slots.SlotChargeable;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerChargingBench extends ContainerBase<TileEntityChargingBench> {
	private double lastEnergy = -1.0D;
	private double lastAverage = -1.0D;
	private int lastTicksRequired = -1;
	private double lastEnergyRequired = -1.0D;
	
	public ContainerChargingBench(EntityPlayer player, TileEntityChargingBench te) {
		super(te);

		addSlotToContainer(new SlotInput(te, TileEntityChargingBench.SLOT_IN, 130, 68));
		addSlotToContainer(new SlotDischargeable(te, TileEntityChargingBench.SLOT_DISCHARGER, 130, 95, te.getSinkTier()));
		addSlotToContainer(new SlotOutput(te, TileEntityChargingBench.SLOT_OUT, 130, 122));

		for (int row = 0; row < 4; row++)
			addSlotToContainer((Slot) new SlotArmor(player.inventory, row, 8, 68 + row * 18));
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 3; col++)
				addSlotToContainer(new SlotChargeable(te, TileEntityChargingBench.SLOT_CHARGE + col + row * 3, 52 + col * 18, 68 + row * 18));
		for (int row = 0; row < 4; row++)
			addSlotToContainer(new SlotUpgrade(te, TileEntityChargingBench.SLOT_UPGRADE + row, 152, 68 + row * 18));

		// inventory
		addPlayerInventorySlots(player, 226);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		double average = te.inputTracker.getAverage();
		int ticksRequired = te.getTicksRequired();
		double energyRequired = te.getEnergyRequired();
		for (int i = 0; i < crafters.size(); i++)
			if (lastEnergy != energy || lastAverage != average || lastTicksRequired != ticksRequired || lastEnergyRequired != energyRequired) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("energy", energy);
				tag.setDouble("average", average);
				tag.setInteger("ticksRequired", ticksRequired);
				tag.setDouble("energyRequired", energyRequired);
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, tag);
			}
		lastEnergy = energy;
		lastAverage = average;
		lastTicksRequired = ticksRequired;
		lastEnergyRequired = energyRequired;
	}
}