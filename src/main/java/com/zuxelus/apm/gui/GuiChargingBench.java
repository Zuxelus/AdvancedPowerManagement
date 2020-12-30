package com.zuxelus.apm.gui;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.containers.ContainerChargingBench;
import com.zuxelus.zlib.gui.GuiContainerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiChargingBench extends GuiContainerBase {
	private DecimalFormat time = new DecimalFormat("00");
	private DecimalFormat days = new DecimalFormat("#0");
	private DecimalFormat dayFrac = new DecimalFormat("0.#");
	private ContainerChargingBench container;

	public GuiChargingBench(ContainerChargingBench container) {
		super(container, "tile.charging_bench.name", APM.MODID + ":textures/gui/gui_charging_bench.png");
		this.container = container;
		ySize = 226;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// Energy bar
		if (container.te.getEnergy() > 0) {
			int barLength = 5 * (container.te.getChargeLevel() + 1);
			if (barLength > 0)
				barLength++;
			drawTexturedModalRect(guiLeft + 32, guiTop + 136 - barLength, 176, 66 - barLength, 12, barLength);
		}

		// Redstone power indicator
		drawTexturedModalRect(guiLeft + 129, guiTop + 48, container.te.getPowered() ? 188 : 206, 0, 18, 15);

		// Draw labels
		drawCenteredText(name, width / 2, guiTop + 6);

		drawRightAlignedText(I18n.format("AdvPwrMan.misc.EU"), guiLeft + 25, guiTop + 23);
		drawLeftAlignedText(I18n.format("AdvPwrMan.charger.maxEU"), guiLeft + 151, guiTop + 23);

		drawRightAlignedText(I18n.format("AdvPwrMan.charger.requiredEU"), guiLeft + 25, guiTop + 33);
		drawLeftAlignedText(I18n.format("AdvPwrMan.charger.estimatedTime"), guiLeft + 151, guiTop + 33);

		drawRightAlignedText(I18n.format("AdvPwrMan.charger.averageInput"), guiLeft + 70, guiTop + 52);
		drawLeftAlignedText(I18n.format("AdvPwrMan.charger.redstonePower"), guiLeft + 151, guiTop + 52);

		// Draw current and max storage
		drawRightAlignedGlowingText(Integer.toString((int) container.te.getEnergy()), width / 2 - 7, guiTop + 23, GREEN, GREENGLOW);
		drawGlowingText(" / " + Integer.toString((int) container.te.getCapacity()), width / 2 - 7, guiTop + 23, GREEN, GREENGLOW);

		drawRightAlignedGlowingText(fraction.format(container.te.inputTracker.getAverage()), guiLeft + 122, guiTop + 52, GREEN, GREENGLOW);

		// Charging stats (only displayed while charging items)
		if (container.te.getEnergyRequired() > 0) {
			String clock = I18n.format("AdvPwrMan.station.led.unknown");
			if (container.te.getTicksRequired() > 0) {
				int timeScratch = container.te.getTicksRequired() / 20;
				if (timeScratch <= 345600) { // 60 * 60 * 96 or 4 days
					int sec = timeScratch % 60;
					timeScratch /= 60;
					int min = timeScratch % 60;
					timeScratch /= 60;
					clock = time.format(timeScratch) + ":" + time.format(min) + ":" + time.format(sec);
				} else {
					float dayScratch = ((float)timeScratch) / 86400F; // 60 * 60 * 24 or 1 day
					clock = (dayScratch < 10F ? dayFrac.format(dayScratch) : dayScratch < 100 ? days.format((int)dayScratch) : "??") + I18n.format("AdvPwrMan.station.led.days");
				}
			}
			final String energyReq = container.te.getEnergyRequired() > 9999999 ? dayFrac.format(((float)container.te.getEnergyRequired()) / 1000000F) + "M" : Integer.toString((int)container.te.getEnergyRequired());
			drawRightAlignedGlowingText(energyReq, width / 2 - 7, guiTop + 33, GREEN, GREENGLOW);
			drawRightAlignedGlowingText(clock, guiLeft + 144, guiTop + 33, GREEN, GREENGLOW);
		}
	}
}
