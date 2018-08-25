package fr.atesab.x13;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class XrayMenu extends GuiScreen {
	/**
	 * A button to set boolean value
	 */
	private class GuiBooleanButton extends GuiButton {
		private String lang;
		private Supplier<Boolean> getter;
		private Consumer<Boolean> setter;

		/**
		 * Create a button to set boolean value
		 * 
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 * @param lang
		 *            button name
		 * @param getter
		 *            a function to get the boolean value
		 * @param setter
		 *            a function to set the boolean value
		 */
		public GuiBooleanButton(int x, int y, int w, int h, String lang, Supplier<Boolean> getter,
				Consumer<Boolean> setter) {
			super(0, x, y, w, h, lang);
			this.lang = lang;
			this.getter = getter;
			this.setter = setter;
			setString();
			colorButtons.add(this);
		}

		@Override
		public void mouseClicked(double arg0, double arg1) {
			setter.accept(!getter.get());
			colorButtons.forEach(GuiBooleanButton::setString);
			super.mouseClicked(arg0, arg1);
		}

		private GuiButton setString() {
			boolean value = getter.get(); // the value of the boolean
			// set the display text COLOR(GREEN/RED) LANG_NAME (Enabled)?
			displayString = "\u00a7" + (value ? 'a' : 'c') + I18n.format(lang)
					+ (value ? " (" + I18n.format("x13.mod.enable") + ")" : "");
			return this;
		}
	}

	private GuiScreen parent;
	private GuiTextField xrayBlocks;
	private GuiTextField caveBlocks;
	private GuiTextField redstoneBlocks;
	private String title1;
	private String title2;
	private String title3;
	private String xrayText;
	private String caveText;
	private String redstoneText;
	private X13Main mod;
	private final boolean oldXrayConfig;
	private final boolean oldCaveConfig;
	private final boolean oldRedstoneConfig;
	private final boolean oldFullbrightConfig;
	private final boolean oldShowLocationConfig;

	private List<GuiBooleanButton> colorButtons = Lists.newArrayList();

	public XrayMenu(GuiScreen parent) {
		super();
		this.parent = parent;
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
		this.mod = X13Main.getX13();
		xrayText = X13Main.getBlocksNames(mod.getXrayBlocks()).collect(Collectors.joining(" "));
		caveText = X13Main.getBlocksNames(mod.getCaveBlocks()).collect(Collectors.joining(" "));
		redstoneText = X13Main.getBlocksNames(mod.getRedstoneBlocks()).collect(Collectors.joining(" "));
		oldXrayConfig = mod.isXrayEnable();
		oldCaveConfig = mod.isCaveEnable();
		oldRedstoneConfig = mod.isRedstoneEnable();
		oldFullbrightConfig = mod.isFullBrightEnable();
		oldShowLocationConfig = mod.isShowLocation();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		String s = "Xray 13";
		fontRenderer.drawStringWithShadow(s, width / 2 - fontRenderer.getStringWidth("Xray 13") / 2, height / 2 - 84,
				0xffffffff);
		fontRenderer.drawStringWithShadow(title1, width / 2 - 200, xrayBlocks.y - fontRenderer.FONT_HEIGHT / 2 + 9,
				0xffffffff);
		fontRenderer.drawStringWithShadow(title2, width / 2 - 200, caveBlocks.y - fontRenderer.FONT_HEIGHT / 2 + 9,
				0xffffffff);
		fontRenderer.drawStringWithShadow(title3, width / 2 - 200, redstoneBlocks.y - fontRenderer.FONT_HEIGHT / 2 + 9,
				0xffffffff);
		xrayBlocks.func_195608_a(mouseX, mouseY, partialTick);
		caveBlocks.func_195608_a(mouseX, mouseY, partialTick);
		redstoneBlocks.func_195608_a(mouseX, mouseY, partialTick);
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void initGui() {
		colorButtons.clear();
		addButton(new GuiButton(0, width / 2 - 200, height / 2, 198, 20, I18n.format("gui.cancel")) {
			@Override
			public void mouseClicked(double x, double y) {
				mod.xray(oldXrayConfig, false);
				mod.cave(oldCaveConfig, false);
				mod.redstone(oldRedstoneConfig, false);
				mod.fullBright(oldFullbrightConfig);
				mod.setShowLocation(oldShowLocationConfig);
				mod.reloadModules();
				mc.displayGuiScreen(parent);
				super.mouseClicked(x, y);
			}
		});
		addButton(new GuiButton(0, width / 2 + 2, height / 2, 198, 20, I18n.format("gui.done")) {
			@Override
			public void mouseClicked(double x, double y) {
				mod.setConfig(xrayText, caveText, redstoneText);
				mc.displayGuiScreen(parent);
				super.mouseClicked(x, y);
			}
		});
		addButton(new GuiBooleanButton(width / 2 - 200, height / 2 + 24, 130, 20, "x13.mod.xray", mod::isXrayEnable,
				b -> {
					mod.cave(false, false);
					mod.redstone(false, false);
					mod.xray();
				}));
		addButton(
				new GuiBooleanButton(width / 2 - 66, height / 2 + 24, 132, 20, "x13.mod.cave", mod::isCaveEnable, b -> {
					mod.xray(false, false);
					mod.redstone(false, false);
					mod.cave();
				}));
		addButton(new GuiBooleanButton(width / 2 + 70, height / 2 + 24, 130, 20, "x13.mod.redstone",
				mod::isRedstoneEnable, b -> {
					mod.xray(false, false);
					mod.cave(false, false);
					mod.redstone();
				}));
		addButton(new GuiBooleanButton(width / 2 - 200, height / 2 + 48, 198, 20, "x13.mod.fullbright",
				mod::isFullBrightEnable, mod::fullBright));
		addButton(new GuiBooleanButton(width / 2 + 2, height / 2 + 48, 198, 20, "x13.mod.showloc", mod::isShowLocation,
				mod::setShowLocation));
		title1 = I18n.format("x13.mod.xray.blocks") + ": ";
		title2 = I18n.format("x13.mod.cave.blocks") + ": ";
		title3 = I18n.format("x13.mod.redstone.blocks") + ": ";
		int l = Math.max(fontRenderer.getStringWidth(title1),
				Math.max(fontRenderer.getStringWidth(title2), fontRenderer.getStringWidth(title3)));
		int x = width / 2 - 195 + l;
		field_195124_j.add(xrayBlocks = new GuiTextField(0, fontRenderer, x, height / 2 - 70, 338 - l, 18));
		field_195124_j.add(caveBlocks = new GuiTextField(1, fontRenderer, x, height / 2 - 46, 338 - l, 18));
		field_195124_j.add(redstoneBlocks = new GuiTextField(2, fontRenderer, x, height / 2 - 22, 338 - l, 18));
		x = width / 2 + 150;
		String s = I18n.format("controls.reset");
		addButton(new GuiButton(3, x, height / 2 - 72, 50, 20, s) {
			@Override
			public void mouseClicked(double arg0, double arg1) {
				xrayBlocks.setText(xrayText = mod.getDefaultXrayBlocks());
				super.mouseClicked(arg0, arg1);
			}
		});
		addButton(new GuiButton(4, x, height / 2 - 48, 50, 20, s) {
			@Override
			public void mouseClicked(double arg0, double arg1) {
				caveBlocks.setText(caveText = mod.getDefaultCaveBlocks());
				super.mouseClicked(arg0, arg1);
			}
		});
		addButton(new GuiButton(5, x, height / 2 - 24, 50, 20, s) {
			@Override
			public void mouseClicked(double arg0, double arg1) {
				redstoneBlocks.setText(redstoneText = mod.getDefaultRedstoneBlocks());
				super.mouseClicked(arg0, arg1);
			}
		});
		xrayBlocks.setMaxStringLength(Integer.MAX_VALUE);
		xrayBlocks.setText(xrayText);
		caveBlocks.setMaxStringLength(Integer.MAX_VALUE);
		caveBlocks.setText(caveText);
		redstoneBlocks.setMaxStringLength(Integer.MAX_VALUE);
		redstoneBlocks.setText(redstoneText);
		super.initGui();
	}

	@Override
	public void updateScreen() {
		xrayText = xrayBlocks.getText();
		caveText = caveBlocks.getText();
		redstoneText = redstoneBlocks.getText();
		super.updateScreen();
	}

}
