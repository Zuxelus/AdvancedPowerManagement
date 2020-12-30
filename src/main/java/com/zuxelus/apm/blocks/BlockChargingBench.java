package com.zuxelus.apm.blocks;

import java.util.List;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;
import com.zuxelus.apm.tileentities.TileEntityHVChargingBench;
import com.zuxelus.apm.tileentities.TileEntityLVChargingBench;
import com.zuxelus.apm.tileentities.TileEntityMVChargingBench;
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

public class BlockChargingBench extends BlockContainer {
	public static final int SIZE = 3;
	private IIcon[][] topBottom = new IIcon[SIZE][2];
	private IIcon[][] sides = new IIcon[SIZE][26];

	public BlockChargingBench() {
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
			return new TileEntityLVChargingBench();
		case 1:
			return new TileEntityMVChargingBench();
		case 2:
			return new TileEntityHVChargingBench();
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

		if (!world.isRemote && te instanceof TileEntityChargingBench) {
			player.openGui(APM.instance, 1, world, x, y, z);
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (!world.isRemote)
			world.markBlockForUpdate(x, y, z);
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
		if (te instanceof TileEntityChargingBench)
			return sides[meta][(((TileEntityChargingBench) te).doingWork ? 13 : 0) + ((TileEntityChargingBench) te).getChargeLevel()];
		return getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		for (int i = 0; i < SIZE; i++) {
			topBottom[i][0] = ir.registerIcon(APM.MODID + ":" + APM.PREFIX[i] + "_charging_bench/bottom");
			topBottom[i][1] = ir.registerIcon(APM.MODID + ":" + APM.PREFIX[i] + "_charging_bench/top");
			for (int j = 0; j < 13; j++) {
				sides[i][j] = ir.registerIcon(APM.MODID + ":" + APM.PREFIX[i] + "_charging_bench/side_off_" + j);
				sides[i][j + 13] = ir.registerIcon(APM.MODID + ":" + APM.PREFIX[i] + "_charging_bench/side_on_" + j);
			}
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
