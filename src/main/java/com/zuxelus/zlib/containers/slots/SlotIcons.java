package com.zuxelus.zlib.containers.slots;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class SlotIcons {

	public static void registerIcons(IIconRegister ir) {
		SlotArmor.slotIcon = new IIcon[] { 
				ir.registerIcon("zlib:slots/slot_armor_0"),
				ir.registerIcon("zlib:slots/slot_armor_1"),
				ir.registerIcon("zlib:slots/slot_armor_2"),
				ir.registerIcon("zlib:slots/slot_armor_3") };
		SlotCard.slotIcon = ir.registerIcon("zlib:slots/slot_card");
		SlotChargeable.slotIcon = ir.registerIcon("zlib:slots/slot_chargeable");
		SlotDischargeable.slotIcon = new IIcon[] { 
				ir.registerIcon("zlib:slots/slot_dischargable_0"),
				ir.registerIcon("zlib:slots/slot_dischargable_1"),
				ir.registerIcon("zlib:slots/slot_dischargable_2") };
	}
}
