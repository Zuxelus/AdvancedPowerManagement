package com.zuxelus.apm.containers;

import com.zuxelus.apm.tileentities.TileEntityAdjTransformer;
import com.zuxelus.apm.tileentities.TileEntityBatteryStation;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerAdjTransformer extends Container {
	public TileEntityAdjTransformer te;
	private double lastAverageIn = -1.0D;
	private double lastAverageOut = -1.0D;

	public ContainerAdjTransformer(TileEntityAdjTransformer te) {
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord) != te ? false : player.getDistanceSq((double)te.xCoord + 0.5D, (double)te.yCoord + 0.5D, (double)te.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double averageIn = te.inputTracker.getAverage();
		double averageOut = te.outputTracker.getAverage();
		for (int i = 0; i < crafters.size(); i++)
			if (lastAverageIn != averageIn || lastAverageOut != averageOut) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("averageIn", averageIn);
				tag.setDouble("averageOut", averageOut);
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, tag);
			}
		lastAverageIn = averageIn;
		lastAverageOut = averageOut;
	}
}
