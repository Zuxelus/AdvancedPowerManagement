package com.zuxelus.apm.recipes;

import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.utils.Info;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class APMRecipes {

	public static void addRecipes() {
		// Charging Bench recipes
		GameRegistry.addRecipe(new ItemStack(ModItems.blockChargingBench, 1, 0), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("batBox")});
		GameRegistry.addRecipe(new ItemStack(ModItems.blockChargingBench, 1, 1), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedGoldCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mfeUnit")});
		GameRegistry.addRecipe(new ItemStack(ModItems.blockChargingBench, 1, 2), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedIronCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mfsUnit")});

		// Battery Station recipes
		GameRegistry.addRecipe(new ItemStack(ModItems.blockBatteryStation, 1, 0), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("lvTransformer")});
		GameRegistry.addRecipe(new ItemStack(ModItems.blockBatteryStation, 1, 1), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedGoldCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mvTransformer")});
		GameRegistry.addRecipe(new ItemStack(ModItems.blockBatteryStation, 1, 2), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedIronCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("hvTransformer")});

		// Adjustable Transformer recipe
		GameRegistry.addRecipe(new ItemStack(ModItems.blockAdjTransformer), new Object[] {"L", "C", "H", 'L', IC2Items.getItem("lvTransformer"), 'C', IC2Items.getItem("advancedCircuit"), 'H', IC2Items.getItem("hvTransformer")});
		GameRegistry.addRecipe(new ItemStack(ModItems.blockAdjTransformer), new Object[] {"H", "C", "L", 'H', IC2Items.getItem("hvTransformer"), 'C', IC2Items.getItem("advancedCircuit"), 'L', IC2Items.getItem("lvTransformer")});

		// Storage Monitor recipe
		GameRegistry.addRecipe(new ItemStack(ModItems.blockStorageMonitor), new Object[] {"WUW", "GCG", "WRW", 'W', Blocks.planks, 'U', IC2Items.getItem("goldCableItem"), 'G', Blocks.glass, 'C', IC2Items.getItem("electronicCircuit"), 'R', Items.redstone});

		// Link Card Creator recipe
		GameRegistry.addRecipe(new ItemStack(ModItems.itemLinkCard, 1, 1), new Object[] {"U  ", " C ", "  V", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'C', IC2Items.getItem("electronicCircuit"), 'V', Items.paper});

		// Bench Toolkit recipe
		GameRegistry.addRecipe(new ItemStack(ModItems.itemBenchTool, 1, 0), new Object[] {" I ", "S S", 'I', Items.iron_ingot, 'S', Items.stick});

		// LV, MV, HV Charging Bench Components recipes
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.itemBenchTool, 1, 1), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.blockChargingBench, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.itemBenchTool, 1, 2), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.blockChargingBench, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.itemBenchTool, 1, 3), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.blockChargingBench, 1, 2));

		// LV, MV, HV Charging Bench reassembly recipes
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.blockChargingBench, 1, 0), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.itemBenchTool, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.blockChargingBench, 1, 1), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.itemBenchTool, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.blockChargingBench, 1, 2), new ItemStack(ModItems.itemBenchTool, 1, 0), new ItemStack(ModItems.itemBenchTool, 1, 3));
	}
}
