package com.zuxelus.apm.gui;

import com.zuxelus.apm.APM;
import com.zuxelus.apm.tileentities.TileEntityAdvEmitter;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiButtonImage;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

public class GuiAdvEmitter extends GuiBase {
	private static final String DISPLAYSTRINGS[] = {"+1", "+10", "+64", "x2", "-1", "-10", "-64", "/2"};
	private TileEntityAdvEmitter te;

	public GuiAdvEmitter(TileEntityAdvEmitter te) {
		super("tile.adv_emitter.name", 176, 110, APM.MODID + ":textures/gui/gui_adv_emitter.png");
		this.te = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		for (int i = 0; i < 8; i++)
			buttonList.add(new GuiButtonImage(i, guiLeft +  8 + 24 * (i % 4), guiTop + 33 + 13 * (i / 4) + 17 * (i / 8),
					24, 13, 1, 192, 15, texture).setText(DISPLAYSTRINGS[i % 8], 0xA0A0A0, 0xFFFFA0));
		for (int i = 8; i < 16; i++)
			buttonList.add(new GuiButtonImage(i, guiLeft +  8 + 24 * (i % 4), guiTop + 33 + 13 * (i / 4) + 17 * (i / 8),
					24, 13, 1, 192, 15, texture).setText(DISPLAYSTRINGS[i % 8], 0x404040, 0xFFFFA0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		drawCenteredText(name, width / 2, guiTop + 6);

		// Packet size section text
		drawCenteredText(I18n.format("AdvPwrMan.emitter.packet"), width / 2, guiTop + 21, 0xB00000);
		//drawRightAlignedGlowingText(Integer.toString(te.getPacketSize()), guiLeft + 146, guiTop + 49, GREEN, GREENGLOW);
		fontRendererObj.drawString("[" + te.MIN_PACKET + " - " + te.MAX_PACKET + "]", guiLeft + 110, guiTop + 35, 0x404040);
		fontRendererObj.drawString(I18n.format("AdvPwrMan.misc.EU"), guiLeft + 152, guiTop + 49, 0x404040);

		// Output rate section text
		drawCenteredText(I18n.format("AdvPwrMan.emitter.output"), width / 2, guiTop + 64, 0xB00000);
		drawRightAlignedGlowingText(Integer.toString(te.getOutputRate()), guiLeft + 146, guiTop + 92, GREEN, GREENGLOW);
		fontRendererObj.drawString("[" + te.MIN_OUTPUT + " - " + te.MAX_OUTPUT + "]", guiLeft + 110, guiTop + 78, 0x404040);
		fontRendererObj.drawString(I18n.format("AdvPwrMan.misc.EU"), guiLeft + 152, guiTop + 92, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int packetSize = te.getPacketSize();
		int outputRate = te.getOutputRate();
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
		}
	}

	private void setPacketSize(int value) {
		NetworkHelper.updateSeverTileEntity(te.xCoord, te.yCoord, te.zCoord, 1, value);
		te.setPacketSize(value);
	}

	private void setOutputRate(int value) {
		NetworkHelper.updateSeverTileEntity(te.xCoord, te.yCoord, te.zCoord, 2, value);
		te.setOutputRate(value);
	}
}
