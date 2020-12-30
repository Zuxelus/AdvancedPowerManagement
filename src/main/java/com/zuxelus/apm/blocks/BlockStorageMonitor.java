package com.zuxelus.apm.blocks;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;
import com.zuxelus.apm.tileentities.TileEntityStorageMonitor;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
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

public class BlockStorageMonitor extends BlockContainer {
	private IIcon[] topBottom = new IIcon[2];
	private IIcon[][] sides = new IIcon[14][2];

	public BlockStorageMonitor() {
		super(Material.wood);
		setHardness(0.75F);
		setResistance(5F);
		setStepSound(soundTypeWood);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
			return new TileEntityStorageMonitor();
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null || player.isSneaking())
			return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null && stack.getItem() instanceof ItemToolWrench)
			return false;

		if (!world.isRemote && te instanceof TileEntityStorageMonitor) {
			player.openGui(APM.instance, 3, world, x, y, z);
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
	public int isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int direction) {
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityStorageMonitor)
			return ((TileEntityStorageMonitor) te).getPowered() ? 15 : 0;
		return 0;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side < 2)
			return topBottom[side];
		return sides[0][0];
	}

	@Override
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		int meta = blockaccess.getBlockMetadata(x, y, z);
		if (side < 2)
			return getIcon(side, meta);
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityStorageMonitor) {
			if (!((TileEntityStorageMonitor) te).getBlockState())
				return sides[13][0];
			return sides[((TileEntityStorageMonitor) te).getChargeLevel()][((TileEntityStorageMonitor) te).getPowered() ? 1 : 0];
		}
		return getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		topBottom[0] = ir.registerIcon(APM.MODID + ":storage_monitor/bottom");
		topBottom[1] = ir.registerIcon(APM.MODID + ":storage_monitor/top");
		for (int i = 0; i < 13; i++) {
			sides[i][0] = ir.registerIcon(APM.MODID + ":storage_monitor/monitor_off_" + i);
			sides[i][1] = ir.registerIcon(APM.MODID + ":storage_monitor/monitor_on_" + i);
		}
		sides[13][0] = ir.registerIcon(APM.MODID + ":storage_monitor/invalid");
		sides[13][1] = ir.registerIcon(APM.MODID + ":storage_monitor/invalid");
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory) te).dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}
}
