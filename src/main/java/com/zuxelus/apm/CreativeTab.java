package com.zuxelus.apm;

import com.zuxelus.apm.init.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs {
	private static ItemStack stack;

	public CreativeTab() {
		super(APM.NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		if (stack == null)
			stack = new ItemStack(ModItems.blockChargingBench);
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return Item.getItemFromBlock(ModItems.blockChargingBench);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return APM.NAME;
	}
}
