package com.zuxelus.apm.items;

import java.util.List;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.containers.slots.SlotInput;
import com.zuxelus.apm.containers.slots.SlotOutput;
import com.zuxelus.apm.containers.slots.SlotUpgrade;
import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.network.ChannelHandler;
import com.zuxelus.apm.network.PacketBenchTool;
import com.zuxelus.apm.tileentities.TileEntityChargingBench;
import com.zuxelus.apm.tileentities.TileEntityHVChargingBench;
import com.zuxelus.apm.tileentities.TileEntityLVChargingBench;
import com.zuxelus.apm.tileentities.TileEntityMVChargingBench;
import com.zuxelus.zlib.containers.slots.SlotIcons;
import com.zuxelus.zlib.network.PacketTileEntity;

import ic2.api.tile.IEnergyStorage;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBenchTool extends Item {
	public static final int TOOLKIT = 0;
	public static final int LV_KIT = 1;
	public static final int MV_KIT = 2;
	public static final int HV_KIT = 3;

	private IIcon[] icon;

	public ItemBenchTool() {
		super();
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = new IIcon[] {
				ir.registerIcon(APM.MODID + ":" + "toolkit"),
				ir.registerIcon(APM.MODID + ":" + "lv_kit"),
				ir.registerIcon(APM.MODID + ":" + "mv_kit"),
				ir.registerIcon(APM.MODID + ":" + "hv_kit") };
		SlotIcons.registerIcons(ir);
		SlotInput.slotIcon = ir.registerIcon(APM.MODID + ":slots/slot_input");
		SlotOutput.slotIcon = ir.registerIcon(APM.MODID + ":slots/slot_output");
		SlotUpgrade.slotIcon = ir.registerIcon(APM.MODID + ":slots/slot_upgrade");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case TOOLKIT:
		default:
			return "item.toolkit";
		case LV_KIT:
			return "item.lv_kit";
		case MV_KIT:
			return "item.mv_kit";
		case HV_KIT:
			return "item.hv_kit";
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		switch (damage) {
		case TOOLKIT:
		default:
			return icon[TOOLKIT];
		case LV_KIT:
			return icon[LV_KIT];
		case MV_KIT:
			return icon[MV_KIT];
		case HV_KIT:
			return icon[HV_KIT];
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		items.add(new ItemStack(ModItems.itemLinkCard, 1, TOOLKIT));
		items.add(new ItemStack(ModItems.itemBenchTool, 1, LV_KIT));
		items.add(new ItemStack(ModItems.itemBenchTool, 1, MV_KIT));
		items.add(new ItemStack(ModItems.itemBenchTool, 1, HV_KIT));
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (player == null || !(player instanceof EntityPlayerMP))
			return false;
		if (stack == null || stack.getItemDamage() == TOOLKIT)
			return false;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityChargingBench && stack.getItemDamage() != ((TileEntityChargingBench) te).getBaseTier()) {
			int newTool = swapBenchComponents((TileEntityChargingBench) te, world, x, y, z, stack.getItemDamage());
			ChannelHandler.network.sendTo(new PacketBenchTool(x, y, z, stack.getItemDamage()), (EntityPlayerMP) player);
			ItemStack tool = new ItemStack(ModItems.itemBenchTool, 1, newTool);
			--stack.stackSize;
			EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, tool);
			dropItem.delayBeforeCanPickup = 0;
			world.spawnEntityInWorld(dropItem);
			return true;
		}
		return false;
	}

	public static int swapBenchComponents(TileEntityChargingBench te, World world, int x, int y, int z, int newTier) {
		int oldTier = te.getBaseTier();
		int eustored = te.getStored();
		int facing = te.getFacing();
		ItemStack[] items = new ItemStack[te.getSizeInventory()];
		for (int i = 0; i < items.length; i++)
			items[i] = te.getStackInSlot(i);
		world.removeTileEntity(x, y, z);
		world.setBlock(x, y, z, ModItems.blockChargingBench, newTier - 1, 2);

		TileEntityChargingBench te_new; 
		switch (newTier - 1) {
		default:
			te_new = new TileEntityLVChargingBench();
			break;
		case 1:
			te_new = new TileEntityMVChargingBench();
			break;
		case 2:
			te_new = new TileEntityHVChargingBench();
			break;
		}
		te_new.addEnergy(eustored);
		te_new.setFacing(facing);
		for (int j = 0; j < items.length; j++)
			te_new.setInventorySlotContents(j, items[j]);
		world.setTileEntity(x, y, z, te_new);
		te_new.markDirty();
		world.markBlockForUpdate(x, y, z);
		return oldTier;
	}
}
