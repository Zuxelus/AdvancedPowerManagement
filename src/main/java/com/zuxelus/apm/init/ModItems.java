package com.zuxelus.apm.init;

import com.zuxelus.apm.blocks.*;
import com.zuxelus.apm.items.*;
import com.zuxelus.apm.items.ItemLinkCard;
import com.zuxelus.apm.recipes.APMRecipes;
import com.zuxelus.apm.tileentities.*;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModItems {
	public static Block blockChargingBench;
	public static Block blockBatteryStation;
	public static Block blockStorageMonitor;
	public static Block blockAdvEmitter;
	public static Block blockAdjTransformer;
	public static Item itemBenchTool;
	public static Item itemLinkCard;

	public static void onBlockRegistry() {
		blockChargingBench = new BlockChargingBench();
		setNames(blockChargingBench, "charging_bench");
		GameRegistry.registerBlock(blockChargingBench, ItemBlockChargingBench.class, "charging_bench");

		blockBatteryStation = new BlockBatteryStation();
		setNames(blockBatteryStation, "battery_station");
		GameRegistry.registerBlock(blockBatteryStation, ItemBlockBatteryStation.class, "battery_station");

		blockStorageMonitor = new BlockStorageMonitor();
		setNames(blockStorageMonitor, "storage_monitor");
		GameRegistry.registerBlock(blockStorageMonitor, ItemBlock.class, "storage_monitor");

		blockAdvEmitter = new BlockAdvEmitter();
		setNames(blockAdvEmitter, "adv_emitter");
		GameRegistry.registerBlock(blockAdvEmitter, ItemBlock.class, "adv_emitter");

		blockAdjTransformer = new BlockAdjTransformer();
		setNames(blockAdjTransformer, "adj_transformer");
		GameRegistry.registerBlock(blockAdjTransformer, ItemBlock.class, "adj_transformer");
	}

	public static void onItemRegistry() {
		itemBenchTool = register(new ItemBenchTool(), "item_bench_tool");
		itemLinkCard = register(new ItemLinkCard(), "item_link_card");
	}

	private static void setNames(Object obj, String name) {
		if (obj instanceof Block) {
			Block block = (Block) obj;
			block.setBlockName(name);
		} else if (obj instanceof Item) {
			Item item = (Item) obj;
			item.setUnlocalizedName(name);
		} else
			throw new IllegalArgumentException("Item or Block required");
	}

	public static Item register(Item item, String name) {
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);
		return item;
	}

	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityLVChargingBench.class, "lv_charging_bench");
		GameRegistry.registerTileEntity(TileEntityMVChargingBench.class, "mv_charging_bench");
		GameRegistry.registerTileEntity(TileEntityHVChargingBench.class, "hv_charging_bench");

		GameRegistry.registerTileEntity(TileEntityLVBatteryStation.class, "lv_battery_station");
		GameRegistry.registerTileEntity(TileEntityMVBatteryStation.class, "mv_battery_station");
		GameRegistry.registerTileEntity(TileEntityHVBatteryStation.class, "hv_battery_station");

		GameRegistry.registerTileEntity(TileEntityStorageMonitor.class, "storage_monitor");
		GameRegistry.registerTileEntity(TileEntityAdvEmitter.class, "adv_emitter");
		GameRegistry.registerTileEntity(TileEntityAdjTransformer.class, "adj_transformer");
	}

	public static void registerCraftingRecipes() {
		APMRecipes.addRecipes();
	}
}
