package com.zuxelus.apm.network;

import com.zuxelus.apm.APM;
import com.zuxelus.zlib.network.PacketTileEntity;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ChannelHandler {
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(APM.MODID);

	public static void init() {
		network.registerMessage(PacketTileEntity.class, PacketTileEntity.class, 1, Side.CLIENT);
		network.registerMessage(PacketTileEntity.class, PacketTileEntity.class, 2, Side.SERVER);
		network.registerMessage(PacketBenchTool.class, PacketBenchTool.class, 3, Side.CLIENT);
	}
}