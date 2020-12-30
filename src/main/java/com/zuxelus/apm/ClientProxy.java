package com.zuxelus.apm;

import com.zuxelus.apm.containers.*;
import com.zuxelus.apm.gui.*;
import com.zuxelus.apm.tileentities.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClientProxy extends ServerProxy {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
		case 1:
			if (te instanceof TileEntityChargingBench)
				return new GuiChargingBench(new ContainerChargingBench(player, (TileEntityChargingBench) te));
			break;
		case 2:
			if (te instanceof TileEntityBatteryStation)
				return new GuiBatteryStation(new ContainerBatteryStation(player, (TileEntityBatteryStation) te));
			break;
		case 3:
			if (te instanceof TileEntityStorageMonitor)
				return new GuiStorageMonitor(new ContainerStorageMonitor(player, (TileEntityStorageMonitor) te));
			break;
		case 4:
			if (te instanceof TileEntityAdvEmitter)
				return new GuiAdvEmitter((TileEntityAdvEmitter) te);
			break;
		case 5:
			if (te instanceof TileEntityAdjTransformer)
				return new GuiAdjTransformer(new ContainerAdjTransformer((TileEntityAdjTransformer) te));
			break;
		}
		return null;
	}
}
