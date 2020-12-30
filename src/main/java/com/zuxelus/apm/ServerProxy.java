package com.zuxelus.apm;

import com.zuxelus.apm.containers.*;
import com.zuxelus.apm.tileentities.*;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ServerProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
		case 1:
			if (te instanceof TileEntityChargingBench)
				return new ContainerChargingBench(player, (TileEntityChargingBench) te);
			break;
		case 2:
			if (te instanceof TileEntityBatteryStation)
				return new ContainerBatteryStation(player, (TileEntityBatteryStation) te);
			break;
		case 3:
			if (te instanceof TileEntityStorageMonitor)
				return new ContainerStorageMonitor(player, (TileEntityStorageMonitor) te);
			break;
		/*case 4:
			if (te instanceof TileEntityAdjEmitter)
				return new ContainerAdjEmitter((TileEntityAdjEmitter) te);
			break;*/
		case 5:
			if (te instanceof TileEntityAdjTransformer)
				return new ContainerAdjTransformer((TileEntityAdjTransformer) te);
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
