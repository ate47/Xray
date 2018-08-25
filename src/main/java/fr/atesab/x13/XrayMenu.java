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

	private class GuiColorButton extends GuiButton {
		private String lang;

		private Supplier<Boolean> getter;
		private Consumer<Boolean> setter;

		public GuiColorButton(int x, int y, int w, int h, String lang, Supplier<Boolean> getter,
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
			colorButtons.forEach(GuiColorButton::setString);
			super.mouseClicked(arg0, arg1);
		}

		private GuiButton setString() {
			boolean value = getter.get();
			displayString = "\u00a7" + (value ? 'a' : 'c') + I18n.format(lang)
					+ (value ? " (" + I18n.format("x13.mod.enable") + ")" : "");
			return this;
		}
	}

	private GuiScreen parent;
	private GuiTextField xrayBlocks;
	private GuiTextField caveBlocks;
	private String title1;
	private String title2;
	private String xrayText;
	private String caveText;
	private X13Main mod;
	private final boolean oldXrayConfig;
	private final boolean oldCaveConfig;
	private final boolean oldFullbrightConfig;

	private final boolean oldShowLocationConfig;

	private List<GuiColorButton> colorButtons = Lists.newArrayList();

	public XrayMenu(GuiScreen parent) {
		super();
		this.parent = parent;
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
		this.mod = X13Main.getX13();
		xrayText = X13Main.getBlocksNames(mod.getXrayBlocks()).collect(Collectors.joining(" "));
		caveText = X13Main.getBlocksNames(mod.getCaveBlocks()).collect(Collectors.joining(" "));
		oldXrayConfig = mod.isXrayEnable();
		oldCaveConfig = mod.isCaveEnable();
		oldFullbrightConfig = mod.isFullBrightEnable();
		oldShowLocationConfig = mod.isShowLocation();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		String s = "Xray 13";
		fontRenderer.drawStringWithShadow(s, width / 2 - fontRenderer.getStringWidth("Xray 13") / 2, height / 2 - 60,
				0xffffffff);
		fontRenderer.drawStringWithShadow(title1, width / 2 - 200, xrayBlocks.y - fontRenderer.FONT_HEIGHT / 2 + 9,
				0xffffffff);
		fontRenderer.drawStringWithShadow(title2, width / 2 - 200, caveBlocks.y - fontRenderer.FONT_HEIGHT / 2 + 9,
				0xffffffff);
		xrayBlocks.func_195608_a(mouseX, mouseY, partialTick);
		caveBlocks.func_195608_a(mouseX, mouseY, partialTick);
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void initGui() {
		colorButtons.clear();
		addButton(new GuiButton(0, width / 2 - 200, height / 2, 198, 20, I18n.format("gui.cancel")) {
			@Override
			public void mouseClicked(double x, double y) {
				mod.xray(oldXrayConfig);
				mod.cave(oldCaveConfig);
				mod.fullBright(oldFullbrightConfig);
				mod.setShowLocation(oldShowLocationConfig);
				mc.displayGuiScreen(parent);
				super.mouseClicked(x, y);
			}
		});
		addButton(new GuiButton(0, width / 2 + 2, height / 2, 198, 20, I18n.format("gui.done")) {
			@Override
			public void mouseClicked(double x, double y) {
				mod.setConfig(xrayText, caveText);
				mc.displayGuiScreen(parent);
				super.mouseClicked(x, y);
			}
		});
		addButton(
				new GuiColorButton(width / 2 - 200, height / 2 + 24, 198, 20, "x13.mod.xray", mod::isXrayEnable, b -> {
					mod.cave(false);
					mod.xray(b);
				}));
		addButton(new GuiColorButton(width / 2 + 2, height / 2 + 24, 198, 20, "x13.mod.cave", mod::isCaveEnable, b -> {
			mod.xray(false);
			mod.cave(b);
		}));
		addButton(new GuiColorButton(width / 2 - 200, height / 2 + 48, 198, 20, "x13.mod.fullbright",
				mod::isFullBrightEnable, mod::fullBright));
		addButton(new GuiColorButton(width / 2 + 2, height / 2 + 48, 198, 20, "x13.mod.showloc", mod::isShowLocation,
				mod::setShowLocation));
		title1 = I18n.format("x13.mod.xray.blocks") + ": ";
		title2 = I18n.format("x13.mod.cave.blocks") + ": ";
		int l = Math.max(fontRenderer.getStringWidth(title1), fontRenderer.getStringWidth(title2));
		int x = width / 2 - 195 + l;
		field_195124_j.add(xrayBlocks = new GuiTextField(0, fontRenderer, x, height / 2 - 46, 393 - l, 18));
		field_195124_j.add(caveBlocks = new GuiTextField(1, fontRenderer, x, height / 2 - 22, 393 - l, 18));
		xrayBlocks.setMaxStringLength(Integer.MAX_VALUE);
		xrayBlocks.setText(xrayText);
		caveBlocks.setMaxStringLength(Integer.MAX_VALUE);
		caveBlocks.setText(caveText);
		super.initGui();
	}

	@Override
	public void updateScreen() {
		xrayText = xrayBlocks.getText();
		caveText = caveBlocks.getText();
		super.updateScreen();
	}

}
