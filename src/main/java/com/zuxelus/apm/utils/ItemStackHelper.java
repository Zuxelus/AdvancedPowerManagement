package com.zuxelus.apm.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ItemStackHelper {

	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			stack.setTagCompound(nbtTagCompound);
		}
		return nbtTagCompound;
	}

	public static void setCoordinates(ItemStack stack, int x, int y, int z) {
		NBTTagCompound nbtTagCompound = getTagCompound(stack);
		nbtTagCompound.setInteger("x", x);
		nbtTagCompound.setInteger("y", y);
		nbtTagCompound.setInteger("z", z);
	}

	public static NBTTagCompound getOrCreateNbtData(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		return tag;
	}

	public static ItemStack getStackWithEnergy(Item item, String name, double energy) {
		ItemStack stack = new ItemStack(item);
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble(name, energy);
		return stack;
	}
}
