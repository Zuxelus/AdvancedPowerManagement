package com.zuxelus.apm.blocks;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityAdjTransformer;
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

public class BlockAdjTransformer extends BlockContainer {
	private IIcon input;
	private IIcon[] output = new IIcon[4];

	public BlockAdjTransformer() {
		super(Material.wood);
		setHardness(0.75F);
		setResistance(5F);
		setStepSound(soundTypeWood);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAdjTransformer();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null || player.isSneaking())
			return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null && stack.getItem() instanceof ItemToolWrench)
			return false;

		if (!world.isRemote && te instanceof TileEntityAdjTransformer) {
			player.openGui(APM.instance, 5, world, x, y, z);
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
		return input;
	}

	@Override
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityAdjTransformer) {
			if (((TileEntityAdjTransformer) te).getSideSettings()[side] == 0)
				return input;
			int packetSize = ((TileEntityAdjTransformer) te).getPacketSize();
			if (packetSize <= TileEntityBatteryStation.OUTPUT[0])
				return output[0];
			if (packetSize <= TileEntityBatteryStation.OUTPUT[1])
				return output[1];
			if (packetSize <= TileEntityBatteryStation.OUTPUT[2])
				return output[2];
			return output[3];
		}
		return input;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		input = iconRegister.registerIcon(APM.MODID + ":adj_transformer/input");
		for (int i = 0; i < 4; i++)
			output[i] = iconRegister.registerIcon(APM.MODID + ":adj_transformer/output_" + APM.PREFIX[i] + "_1");
	}
}
