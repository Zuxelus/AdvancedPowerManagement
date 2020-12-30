package com.zuxelus.apm.gui;

import java.text.DecimalFormat;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.containers.ContainerBatteryStation;
import com.zuxelus.apm.containers.ContainerChargingBench;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonImage;
import com.zuxelus.zlib.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiBatteryStation extends GuiContainerBase {
	private DecimalFormat time = new DecimalFormat("00");
	private DecimalFormat days = new DecimalFormat("#0");
	private DecimalFormat dayFrac = new DecimalFormat("0.#");
	private ContainerBatteryStation container;
	private GuiButtonImage button;

	public GuiBatteryStation(ContainerBatteryStation container) {
		super(container, "tile.battery_station.name", APM.MODID + ":textures/gui/gui_battery_station.png");
		this.container = container;
		ySize = 182;
	}

	@Override
	public void initGui() {
		super.initGui();
		button = new GuiButtonImage(1, guiLeft + 16, guiTop + 44, 20, 14, 30, container.te.getOpMode() ? 185 : 200, 0, texture);
		buttonList.add(button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		drawCenteredText(name, width / 2, guiTop + 6);
		drawLeftAlignedText(I18n.format("AdvPwrMan.station.modeline1"), guiLeft + 7, guiTop + 59);
		drawLeftAlignedText(I18n.format("AdvPwrMan.station.modeline2"), guiLeft + 7, guiTop + 70);
		drawCenteredText(I18n.format("AdvPwrMan.station.average"), guiLeft + 144, guiTop + 27);
		drawCenteredText(I18n.format("AdvPwrMan.station.remaining"), guiLeft + 144, guiTop + 65);

		double average = container.te.outputTracker.getAverage();
		drawRightAlignedGlowingText(fraction.format(average), guiLeft + 166, guiTop + 41, GREEN, GREENGLOW);

		String clock = I18n.format("AdvPwrMan.station.led.unknown");
		if (average > 0) {
			int timeScratch = (int) (container.te.getEnergyTotal() / average * 20);
			if (timeScratch <= 345600) { // 60 * 60 * 96 or 4 days
				final int sec = timeScratch % 60;
				timeScratch /= 60;
				final int min = timeScratch % 60;
				timeScratch /= 60;
				clock = time.format(timeScratch) + ":" + time.format(min) + ":" + time.format(sec);
			} else {
				float dayScratch = ((float)timeScratch) / 86400F; // 60 * 60 * 24 or 1 day
				clock = (dayScratch < 10F ? dayFrac.format(dayScratch) : dayScratch < 100 ? days.format((int)dayScratch) : "??") + I18n.format("AdvPwrMan.station.led.days");
			}
		}
		drawRightAlignedGlowingText(clock, guiLeft + 166, guiTop + 51, GREEN, GREENGLOW);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		button.setTextureY(container.te.getOpMode() ? 185 : 200);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			boolean mode = !container.te.getOpMode();
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, mode ? 1 : 0);
			container.te.setOpMode(mode);
		}
	}
}
