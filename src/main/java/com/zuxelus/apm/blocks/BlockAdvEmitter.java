package com.zuxelus.apm.blocks;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityAdvEmitter;
import com.zuxelus.apm.tileentities.TileEntityBatteryStation;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAdvEmitter extends BlockContainer {
	private IIcon[] icon = new IIcon[5];

	public BlockAdvEmitter() {
		super(Material.wood);
		setHardness(0.75F);
		setResistance(5F);
		setStepSound(soundTypeWood);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAdvEmitter();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null || player.isSneaking())
			return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null && stack.getItem() instanceof ItemToolWrench)
			return false;

		if (world.isRemote && te instanceof TileEntityAdvEmitter) {
			player.openGui(APM.instance, 4, world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IBlockHorizontal)
			((TileEntityFacing) te).setFacing(TileEntityFacing.getHorizontalFacing(player));
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icon[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityAdvEmitter) {
			int packetSize = ((TileEntityAdvEmitter) te).getPacketSize();
			if (packetSize <= TileEntityBatteryStation.OUTPUT[0])
				return icon[1];
			if (packetSize <= TileEntityBatteryStation.OUTPUT[1])
				return icon[2];
			if (packetSize <= TileEntityBatteryStation.OUTPUT[2])
				return icon[3];
			return icon[4];
		}
		return icon[0];
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icon[0] = iconRegister.registerIcon(APM.MODID + ":adj_emitter/side");
		for (int i = 0; i < 4; i++)
			icon[i + 1] = iconRegister.registerIcon(APM.MODID + ":adj_emitter/side_" + APM.PREFIX[i]);
	}
}
