package fr.atesab.xray;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class XrayMenu extends Screen {
	/**
	 * A button to set boolean value
	 */
	private class GuiBooleanButton extends AbstractButton {
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
		 * @param lang   button name
		 * @param getter a function to get the boolean value
		 * @param setter a function to set the boolean value
		 */
		public GuiBooleanButton(String lang, Supplier<Boolean> getter, Consumer<Boolean> setter) {
			super(0, 0, 130, 20, new StringTextComponent(lang));
			this.lang = lang;
			this.getter = getter;
			this.setter = setter;
			setString();
			colorButtons.add(this);
		}

		@Override
		public void onPress() {
			setter.accept(!getter.get());
			colorButtons.forEach(GuiBooleanButton::setString);
		}

		private GuiBooleanButton setString() {
			boolean value = getter.get(); // the value of the boolean
			// set the display text COLOR(GREEN/RED) LANG_NAME (Enabled)?
			StringTextComponent txt = new StringTextComponent(
					"\u00a7" + (value ? 'a' : 'c') + I18n.format(lang)
							+ (value ? " (" + I18n.format("x13.mod.enable") + ")" : "")
			);
			setMessage(txt);
			return this;
		}
	}

	public class XrayModeElement {
		private TextFieldWidget field;
		private String text;
		private String title;
		private final boolean oldConfig;
		private XrayMode mode;

		public XrayModeElement(XrayMode mode) {
			text = XrayMain.getBlockNamesToString(mode.getBlocks());
			oldConfig = mode.isEnabled();
			this.mode = mode;
			modeElements.add(this);
		}

		private void cancel() {
			mode.toggle(oldConfig, false);
		}

		private void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
			font.drawStringWithShadow(matrixStack, title, width / 2 - 200, field.y - font.FONT_HEIGHT / 2 + 9, 0xffffffff);
			field.render(matrixStack, mouseX, mouseY, partialTick);
		}

		private int getSizeX() {
			return font.getStringWidth(title = I18n.format("x13.mod.blocks", mode.getNameTranslate()) + ": ");
		}

		private void init(int x, int y, int sizeX) {
			children.add(field = new TextFieldWidget(font, x, y + 2, 338 - sizeX, 16, new StringTextComponent("")));
			addButton(new GuiBooleanButton(mode.getNameTranslate(), mode::isEnabled, mode::toggle));
			addButton(new Button(width / 2 + 150, y, 50, 20, new StringTextComponent(I18n.format("controls.reset")), b -> {
				field.setText(text = mode.getDefaultBlocks());
			}));
			field.setMaxStringLength(Integer.MAX_VALUE);
			field.setText(text);
		}

		private void save() {
			mode.setConfig(text.split(" "));
		}

		private void update() {
			text = field.getText();
		}
	}

	private static void setBlock(GuiBooleanButton b, int x, int y, int width) {
		b.x = x;
		b.y = y;
		b.setWidth(width);
	}

	private Screen parent;

	private final boolean oldFullbrightConfig;
	private final boolean oldShowLocationConfig;

	private List<GuiBooleanButton> colorButtons = Lists.newArrayList();

	private List<XrayModeElement> modeElements = Lists.newArrayList();
	private Minecraft mc;

	public XrayMenu(Screen parent) {
		super(new TranslationTextComponent("x13.mod.config"));
		this.parent = parent;
		font = Minecraft.getInstance().fontRenderer;
		XrayMain.getModes().forEach(XrayModeElement::new);
		oldFullbrightConfig = XrayMain.isFullBrightEnable();
		oldShowLocationConfig = XrayMain.isShowLocation();
		mc = Minecraft.getInstance();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		font.drawStringWithShadow(matrixStack, XrayMain.MOD_NAME, width / 2 - font.getStringWidth(XrayMain.MOD_NAME) / 2,
				height / 2 - 84, 0xffffffff);
		modeElements.forEach(modeElement -> modeElement.draw(matrixStack, mouseX, mouseY, partialTicks));

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}


	@Override
	protected void init() {
		colorButtons.clear();
		addButton(new Button(width / 2 - 200, height / 2, 198, 20, new StringTextComponent(I18n.format("gui.cancel")), b -> {
			modeElements.forEach(XrayModeElement::cancel);
			XrayMain.fullBright(oldFullbrightConfig);
			XrayMain.modules();
			XrayMain.setShowLocation(oldShowLocationConfig);
			mc.displayGuiScreen(parent);
		}));
		addButton(new Button(width / 2 + 2, height / 2, 198, 20, new StringTextComponent(I18n.format("gui.done")), b -> {
			modeElements.forEach(XrayModeElement::save);
			mc.displayGuiScreen(parent);
		}));
		OptionalInt max = modeElements.stream().mapToInt(XrayModeElement::getSizeX).max();
		int sizeX = (max.isPresent() ? max.getAsInt() : 0);
		int x = width / 2 - 195 + sizeX;
		int y = height / 2 - 22 - 24 * modeElements.size();
		for (XrayModeElement element : modeElements) {
			y += 24;
			element.init(x, y, sizeX);
		}
		addButton(new GuiBooleanButton("x13.mod.fullbright", XrayMain::isFullBrightEnable, XrayMain::fullBright));
		addButton(new GuiBooleanButton("x13.mod.showloc", XrayMain::isShowLocation, XrayMain::setShowLocation));
		int i, j = colorButtons.size() / 3;
		int middle = height / 2;
		for (i = 0; i < j; i++) {
			middle += 24;
			setBlock(colorButtons.get(i * 3), width / 2 - 200, middle, 130);
			setBlock(colorButtons.get(i * 3 + 1), width / 2 - 66, middle, 132);
			setBlock(colorButtons.get(i * 3 + 2), width / 2 + 70, middle, 130);
		}
		middle += 24;
		switch (colorButtons.size() - (i * 3)) {
			case 1:
				setBlock(colorButtons.get(i * 3 - 1), width / 2 - 200, middle, 198);
				setBlock(colorButtons.get(i * 3), width / 2 + 2, middle, 198);
				middle -= 24;
				setBlock(colorButtons.get(i * 3 - 3), width / 2 - 200, middle, 198);
				setBlock(colorButtons.get(i * 3 - 2), width / 2 + 2, middle, 198);
				break;
			case 2:
				setBlock(colorButtons.get(i * 3), width / 2 - 200, middle, 198);
				setBlock(colorButtons.get(i * 3 + 1), width / 2 + 2, middle, 198);
				break;
		}
		super.init();
	}

	@Override
	public void tick() {
		modeElements.forEach(XrayModeElement::update);
		super.tick();
	}

}
