package com.zuxelus.apm.blocks;

import java.util.List;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityBatteryStation;
import com.zuxelus.apm.tileentities.TileEntityHVBatteryStation;
import com.zuxelus.apm.tileentities.TileEntityLVBatteryStation;
import com.zuxelus.apm.tileentities.TileEntityMVBatteryStation;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBatteryStation extends BlockContainer {
	public static final int SIZE = 3;
	private IIcon[][] topBottom = new IIcon[SIZE][2];
	private IIcon[][] sides = new IIcon[SIZE][2];

	public BlockBatteryStation() {
		super(Material.wood);
		setHardness(0.75F);
		setResistance(5F);
		setStepSound(soundTypeWood);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta) {
		case 0:
			return new TileEntityLVBatteryStation();
		case 1:
			return new TileEntityMVBatteryStation();
		case 2:
			return new TileEntityHVBatteryStation();
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null || player.isSneaking())
			return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null && stack.getItem() instanceof ItemToolWrench)
			return false;

		if (!world.isRemote && te instanceof TileEntityBatteryStation) {
			player.openGui(APM.instance, 2, world, x, y, z);
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
		if (side < 2)
			return topBottom[meta][side];
		return sides[meta][0];
	}

	@Override
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		int meta = blockaccess.getBlockMetadata(x, y, z);
		if (side < 2)
			return getIcon(side, meta);
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityBatteryStation)
			return sides[meta][((TileEntityBatteryStation) te).doingWork ? 1 : 0];
		return getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		for (int i = 0; i < SIZE; i++) {
			topBottom[i][0] = ir.registerIcon(APM.MODID + ":battery_station/" + APM.PREFIX[i] + "_bottom");
			topBottom[i][1] = ir.registerIcon(APM.MODID + ":battery_station/" + APM.PREFIX[i] + "_top");
			sides[i][0] = ir.registerIcon(APM.MODID + ":battery_station/" + APM.PREFIX[i] + "_battery_station_off");
			sides[i][1] = ir.registerIcon(APM.MODID + ":battery_station/" + APM.PREFIX[i] + "_battery_station_on");
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < SIZE; i++)
			list.add(new ItemStack(this, 1, i));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory) te).dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}
}
