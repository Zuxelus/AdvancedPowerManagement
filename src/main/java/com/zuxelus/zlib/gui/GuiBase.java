package com.zuxelus.zlib.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiScreen {
	private static final int oX[] = {0, -1, 0, 1};
	private static final int oY[] = {-1, 0, 1, 0};
	private static final int MASKR = 0xFF0000;
	private static final int MASKG = 0x00FF00;
	private static final int MASKB = 0x0000FF;
	protected static final int GREEN = 0x55FF55;
	protected static final int RED = 0xFF5555;
	protected static final int GREENGLOW = multiplyColorComponents(GREEN, 0.16F);
	protected static final int REDGLOW = multiplyColorComponents(RED, 0.16F);

	protected ResourceLocation texture;
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	protected String name;

	public GuiBase(String name, int xSize, int ySize, String texture) {
		this.name = I18n.format(name);
		this.xSize = xSize;
		this.ySize = ySize;
		this.texture = new ResourceLocation(texture);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (this.width - xSize) / 2;
		guiTop = (this.height - ySize) / 2;
		buttonList.clear();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		int i = this.guiLeft;
		int j = this.guiTop;
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef((float) i, (float) j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {};

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texture);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void drawCenteredText(String text, int x, int y) {
		drawCenteredText(text, x, y, 0x404040);
	}

	public void drawCenteredText(String text, int x, int y, int color) {
		fontRendererObj.drawString(text, x - fontRendererObj.getStringWidth(text) / 2, y, color);
	}

	public void drawRightAlignedGlowingText(String text, int x, int y, int color, int glowColor) {
		drawGlowingText(text, x - fontRendererObj.getStringWidth(text), y, color, glowColor);
	}

	public void drawGlowingText(String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			fontRendererObj.drawString(text, x + oX[i], y + oY[i], glowColor);
		fontRendererObj.drawString(text, x, y, color);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}
}
