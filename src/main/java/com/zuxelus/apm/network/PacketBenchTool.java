package com.zuxelus.apm.network;

import com.zuxelus.apm.items.ItemBenchTool;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class PacketBenchTool implements IMessage, IMessageHandler<PacketBenchTool, IMessage> {
	private int x;
	private int y;
	private int z;
	private int meta;

	public PacketBenchTool() { }

	public PacketBenchTool(int x, int y, int z, int meta) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.meta = meta;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		meta = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(meta);
	}


	@Override
	public IMessage onMessage(PacketBenchTool message, MessageContext ctx) {
		TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		if (te instanceof TileEntityChargingBench && message.meta != ((TileEntityChargingBench) te).getBaseTier())
			ItemBenchTool.swapBenchComponents((TileEntityChargingBench) te, FMLClientHandler.instance().getClient().theWorld, message.x, message.y, message.z, message.meta);
		return null;
	}
}
