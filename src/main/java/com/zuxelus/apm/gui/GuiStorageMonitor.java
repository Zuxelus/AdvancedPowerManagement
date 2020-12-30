package com.zuxelus.apm.gui;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.containers.ContainerStorageMonitor;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonImage;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiStorageMonitor extends GuiContainerBase {
	private static final String DISPLAYSTRINGS[] = {"-10", "-1", "+1", "+10"};
	private static final int HORIZONTALOFFSETS[] = {-57, -33, 25, 49};
	private ContainerStorageMonitor container;

	public GuiStorageMonitor(ContainerStorageMonitor container) {
		super(container, "tile.storage_monitor.name", APM.MODID + ":textures/gui/gui_storage_monitor.png");
		this.container = container;
		ySize = 190;
	}

	@Override
	public void initGui() {
		super.initGui();
		for (int i = 0; i < 8; i++)
			buttonList.add(new GuiButtonImage(i, width / 2 + HORIZONTALOFFSETS[i % 4], guiTop + 60 + 29 * (i / 4),
					24, 13, 1, 192, 15, texture).setText(DISPLAYSTRINGS[i % 4], 0x404040, 0xFFFFA0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// Energy bar
		if (container.te.getEnergy() > 0) {
			int barLength = 5 * (container.te.getChargeLevel() + 1);
			if (barLength > 0)
				barLength++;
			drawTexturedModalRect(guiLeft + 10, guiTop + 100 - barLength, container.te.getPowered() ? 188 : 176, 66 - barLength, 12, barLength);
		}

		drawCenteredText(name, width / 2, guiTop + 6);
		if (container.te.getBlockState()) {
			drawRightAlignedGlowingText(Integer.toString(container.te.getEnergy()), guiLeft + 90, guiTop + 35, GREEN, GREENGLOW);
			drawGlowingText(" / " + Integer.toString(container.te.getCapacity()), guiLeft + 90, guiTop + 35, GREEN, GREENGLOW);
		} else
			drawCenteredGlowingText(I18n.format("AdvPwrMan.monitor.invalid"), guiLeft + 96, guiTop + 35, RED, REDGLOW);

		// Draw control section labels and readouts
		drawCenteredText(I18n.format("AdvPwrMan.monitor.upper"), guiLeft + 96, guiTop + 49, 0xB00000);
		drawRightAlignedGlowingText(Integer.toString(container.te.getUpperBoundary()) + "%", guiLeft + 109, guiTop + 63, GREEN, GREENGLOW);
		
		drawCenteredText(I18n.format("AdvPwrMan.monitor.lower"), guiLeft + 96, guiTop + 78, 0xB00000);
		drawRightAlignedGlowingText(Integer.toString(container.te.getLowerBoundary()) + "%", guiLeft + 109, guiTop + 92, GREEN, GREENGLOW);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int upperBoundary = container.te.getUpperBoundary();
		int lowerBoundary = container.te.getLowerBoundary();
		switch (button.id) {
		case 0:
			setUpperBoundary(upperBoundary - 10);
			break;
		case 1:
			setUpperBoundary(upperBoundary - 1);
			break;
		case 2:
			setUpperBoundary(upperBoundary + 1);
			break;
		case 3:
			setUpperBoundary(upperBoundary + 10);
			break;
		case 4:
			setLowerBoundary(lowerBoundary - 10);
			break;
		case 5:
			setLowerBoundary(lowerBoundary - 1);
			break;
		case 6:
			setLowerBoundary(lowerBoundary + 1);
			break;
		case 7:
			setLowerBoundary(lowerBoundary + 10);
			break;
		}
	}

	private void setUpperBoundary(int value) {
		NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, value);
		container.te.setUpperBoundary(value);
	}

	private void setLowerBoundary(int value) {
		NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 2, value);
		container.te.setLowerBoundary(value);
	}
}
