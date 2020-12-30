package com.zuxelus.apm.items;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.blocks.BlockBatteryStation;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBatteryStation extends ItemBlock {

	public ItemBlockBatteryStation(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(APM.creativeTab);
	}

	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		int meta = item.getItemDamage();
		if (meta < BlockBatteryStation.SIZE)
			return "tile." + APM.PREFIX[meta] + "_battery_station";
		return "";
	}
}