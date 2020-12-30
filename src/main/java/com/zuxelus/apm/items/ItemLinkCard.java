package com.zuxelus.apm.items;

import java.util.List;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.init.ModItems;
import com.zuxelus.zlib.containers.slots.SlotIcons;

import ic2.api.tile.IEnergyStorage;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemLinkCard extends Item {
	public static final int DAMAGE_CARD = 0;
	public static final int DAMAGE_CARD_BLANK = 1;
	private IIcon[] icon;

	public ItemLinkCard() {
		super();
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(APM.creativeTab);
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = new IIcon[] {
				ir.registerIcon(APM.MODID + ":" + "link_card"),
				ir.registerIcon(APM.MODID + ":" + "link_card_blank") };
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case DAMAGE_CARD:
		default:
			return "item.link_card";
		case DAMAGE_CARD_BLANK:
			return "item.link_card_blank";
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		switch (damage) {
		case DAMAGE_CARD:
		default:
			return icon[DAMAGE_CARD];
		case DAMAGE_CARD_BLANK:
			return icon[DAMAGE_CARD_BLANK];
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		//items.add(new ItemStack(ModItems.itemLinkCard, 1, DAMAGE_CARD));
		items.add(new ItemStack(ModItems.itemLinkCard, 1, DAMAGE_CARD_BLANK));
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (player == null || !(player instanceof EntityPlayerMP))
			return false;
		if (stack == null || stack.stackSize != 1)
			return false;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEnergyStorage) {
			ItemStack card = new ItemStack(ModItems.itemLinkCard);
			setCoordinates(card, x, y, z);
			player.inventory.mainInventory[player.inventory.currentItem] = card;
			return true;
		}
		return false;
	}

	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		return tag;
	}

	public static int[] getCoordinates(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemLinkCard))
			return null;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return null;
		return new int[] { tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")};
	}

	public static void setCoordinates(ItemStack stack, int x, int y, int z) {
		NBTTagCompound tag = getTagCompound(stack);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
	}

	public String getTitle(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemLinkCard))
			return "";
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return "";
		return tag.getString("title");
	}

	public void setTitle(ItemStack stack, String title) {
		NBTTagCompound tag = getTagCompound(stack);
		tag.setString("title", title);
	}
}
