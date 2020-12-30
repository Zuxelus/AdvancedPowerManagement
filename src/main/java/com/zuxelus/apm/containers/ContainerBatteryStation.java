package com.zuxelus.apm.containers;

import com.zuxelus.apm.containers.slots.SlotInput;
import com.zuxelus.apm.containers.slots.SlotOutput;
import com.zuxelus.apm.tileentities.TileEntityBatteryStation;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerBatteryStation extends ContainerBase<TileEntityBatteryStation> {
	private double lastAverage = -1.0D;
	private double lastEnergyTotal = -1.0D;

	public ContainerBatteryStation(EntityPlayer player, TileEntityBatteryStation te) {
		super(te);

		addSlotToContainer(new SlotInput(te, TileEntityBatteryStation.SLOT_IN, 17, 24));
		addSlotToContainer(new SlotOutput(te, TileEntityBatteryStation.SLOT_OUT, 143, 78));

		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 3; col++)
				addSlotToContainer(new SlotDischargeable(te, TileEntityBatteryStation.SLOT_DISCHARGE + col + row * 3, 62 + col * 18, 24 + row * 18, te.getSourceTier()));

		// inventory
		addPlayerInventorySlots(player, 182);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double average = te.outputTracker.getAverage();
		double energyTotal = te.getEnergyTotal();
		for (int i = 0; i < crafters.size(); i++)
			if (lastAverage != average || lastEnergyTotal != energyTotal) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("average", average);
				tag.setDouble("energyTotal", energyTotal);
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, tag);
			}
		lastAverage = average;
		lastEnergyTotal = energyTotal;
	}
}