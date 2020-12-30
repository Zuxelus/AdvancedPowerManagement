package com.zuxelus.zlib.containers.slots;

import com.zuxelus.apm.APM;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class SlotDischargeable extends SlotFilter {
	public static IIcon[] slotIcon;
	private int tier;

	public SlotDischargeable(IInventory inventory, int slotIndex, int x, int y, int tier) {
		super(inventory, slotIndex, x, y);
		this.tier = tier;
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return tier > 3 ? slotIcon[2] : slotIcon[tier - 1];
	}
}
