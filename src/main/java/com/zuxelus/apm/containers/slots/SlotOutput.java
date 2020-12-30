package com.zuxelus.apm.containers.slots;

import com.zuxelus.apm.APM;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class SlotOutput extends SlotFilter {
	public static IIcon slotIcon;

	public SlotOutput(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return slotIcon;
	}
}
