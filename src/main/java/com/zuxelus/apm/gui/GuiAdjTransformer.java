package com.zuxelus.apm.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.containers.ContainerAdjTransformer;
import com.zuxelus.apm.containers.ContainerBatteryStation;
import com.zuxelus.apm.containers.ContainerChargingBench;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonImage;
import com.zuxelus.zlib.network.NetworkHelper;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class GuiAdjTransformer extends GuiContainerBase {
	private static final String DISPLAYSTRINGS[] = {"+1", "+10", "+64", "x2", "-1", "-10", "-64", "/2"};
	public static final String[] KEY_DIRECTION_NAMES = {"AdvPwrMan.dir.down", "AdvPwrMan.dir.up", "AdvPwrMan.dir.north", "AdvPwrMan.dir.south", "AdvPwrMan.dir.west", "AdvPwrMan.dir.east"};
	private ContainerAdjTransformer container;

	public GuiAdjTransformer(ContainerAdjTransformer container) {
		super(container, "tile.adj_transformer.name", APM.MODID + ":textures/gui/gui_adj_transformer.png");
		this.container = container;
		xSize = 240;
		ySize = 140;
	}

	@Override
	public void initGui() {
		super.initGui();
		for (int i = 0; i < 8; i++)
			buttonList.add(new GuiButtonImage(i, guiLeft +  8 + 24 * (i % 4), guiTop + 63 + 13 * (i / 4) + 17 * (i / 8),
					24, 13, 1, 192, 15, texture).setText(DISPLAYSTRINGS[i % 8], 0xA0A0A0, 0xFFFFA0));
		for (int i = 8; i < 16; i++)
			buttonList.add(new GuiButtonImage(i, guiLeft +  8 + 24 * (i % 4), guiTop + 63 + 13 * (i / 4) + 17 * (i / 8),
					24, 13, 1, 192, 15, texture).setText(DISPLAYSTRINGS[i % 8], 0x404040, 0xFFFFA0));
		for (int i = 0; i < 6; i++)
			buttonList.add(new GuiButtonImage(i + 16, guiLeft + 173, guiTop + 54 + 13 * i, 32, 13, 27, 192, 15, texture).setText(I18n.format(KEY_DIRECTION_NAMES[i]), 0x404040, 0xFFFFA0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		drawCenteredText(name, width / 2, guiTop + 6);

		// Draw stats text
		drawRightAlignedText(I18n.format("AdvPwrMan.station.average"), guiLeft + 180, guiTop + 26);
		drawRightAlignedText(I18n.format("AdvPwrMan.station.packetIn"), guiLeft + 180, guiTop + 36);
		drawLeftAlignedText(I18n.format("AdvPwrMan.station.EUbuffered"), guiLeft + 49, guiTop + 26);

		drawRightAlignedGlowingText(Integer.toString((int) container.te.getEnergy()), guiLeft + 44, guiTop + 26, GREEN, GREENGLOW);
		drawRightAlignedGlowingText(fraction.format(container.te.outputTracker.getAverage()), guiLeft + 230, guiTop + 26, GREEN, GREENGLOW);
		drawRightAlignedGlowingText(fraction.format(container.te.inputTracker.getAverage()), guiLeft + 230, guiTop + 36, GREEN, GREENGLOW);

		// Packet size section text
		drawCenteredText(I18n.format("AdvPwrMan.emitter.packet"), guiLeft + 88, guiTop + 51, 0xB00000);
		drawRightAlignedGlowingText(Integer.toString(container.te.getPacketSize()), guiLeft + 146, guiTop + 79, GREEN, GREENGLOW);
		fontRendererObj.drawString("[" + container.te.MIN_PACKET + " - " + container.te.MAX_PACKET + "]", guiLeft + 110, guiTop + 65, 4210752);
		fontRendererObj.drawString(I18n.format("AdvPwrMan.misc.EU"), guiLeft + 152, guiTop + 79, 4210752);

		// Transfer rate section text
		drawCenteredText(I18n.format("AdvPwrMan.transformer.limit"), guiLeft + 88, guiTop + 94, 0xB00000);
		drawRightAlignedGlowingText(Integer.toString(container.te.getOutputRate()), guiLeft + 146, guiTop + 122, GREEN, GREENGLOW);
		fontRendererObj.drawString("[" + container.te.MIN_OUTPUT + " - " + container.te.MAX_OUTPUT + "]", guiLeft + 110, guiTop + 108, 4210752);
		fontRendererObj.drawString(I18n.format("AdvPwrMan.misc.EU"), guiLeft + 152, guiTop + 122, 4210752);

		// Side input/output settings text
		for (int i = 0; i < 6; i++)
			drawGlowingText(I18n.format((container.te.getSideSettings()[i] & 1) == 0 ? "AdvPwrMan.misc.in" : "AdvPwrMan.misc.out"), guiLeft + 214, guiTop + 57 + 13 * i, GREEN, GREENGLOW);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int packetSize = container.te.getPacketSize();
		int outputRate = container.te.getOutputRate();
		switch (button.id)
		{
		/*case 0:
			setPacketSize(packetSize + 1);
			break;
		case 1:
			setPacketSize(packetSize + 10);
			break;
		case 2:
			setPacketSize(packetSize + 64);
			break;
		case 3:
			setPacketSize(packetSize * 2);
			break;
		case 4:
			setPacketSize(packetSize - 1);
			break;
		case 5:
			setPacketSize(packetSize - 10);
			break;
		case 6:
			setPacketSize(packetSize - 64);
			break;
		case 7:
			setPacketSize(packetSize / 2);
			break;*/
		case 8:
			setOutputRate(outputRate + 1);
			break;
		case 9:
			setOutputRate(outputRate + 10);
			break;
		case 10:
			setOutputRate(outputRate + 64);
			break;
		case 11:
			setOutputRate(outputRate * 2);
			break;
		case 12:
			setOutputRate(outputRate - 1);
			break;
		case 13:
			setOutputRate(outputRate - 10);
			break;
		case 14:
			setOutputRate(outputRate - 64);
			break;
		case 15:
			setOutputRate(outputRate / 2);
			break;
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
			byte value = (byte) (container.te.getSideSettings()[button.id - 16] ^ 1);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 3);
			tag.setInteger("id", button.id - 16);
			tag.setByte("value", value);
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, tag);
			container.te.setSideSettings(button.id - 16, value);
			break;
		}
	}

	private void setPacketSize(int value) {
		NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, value);
		container.te.setPacketSize(value);
	}

	private void setOutputRate(int value) {
		NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 2, value);
		container.te.setOutputRate(value);
	}
}
