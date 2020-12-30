package com.zuxelus.apm.containers.slots;

import com.zuxelus.apm.APM;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class SlotInput extends SlotFilter {
	public static IIcon slotIcon;

	public SlotInput(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return slotIcon;
	}
}

