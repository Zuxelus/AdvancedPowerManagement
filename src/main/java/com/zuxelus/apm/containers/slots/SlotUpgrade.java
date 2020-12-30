package com.zuxelus.apm.containers.slots;

import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.IIcon;

public class SlotUpgrade extends SlotFilter {
	public static IIcon slotIcon;

	public SlotUpgrade(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return slotIcon;
	}
}