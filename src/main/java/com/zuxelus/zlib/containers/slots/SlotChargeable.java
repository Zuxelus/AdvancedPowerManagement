package com.zuxelus.zlib.containers.slots;

import com.zuxelus.apm.APM;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class SlotChargeable extends SlotFilter {
	public static IIcon slotIcon;

	public SlotChargeable(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return slotIcon;
	}
}

