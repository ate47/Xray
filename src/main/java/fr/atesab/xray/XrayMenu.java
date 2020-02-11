package fr.atesab.xray;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class XrayMenu extends Screen {
	/**
	 * A button to set boolean value
	 */
	private class GuiBooleanButton extends AbstractButtonWidget {
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
		public GuiBooleanButton(String lang, Supplier<Boolean> getter, Consumer<Boolean> setter) {
			super(0, 0, 130, 20, lang);
			this.lang = lang;
			this.getter = getter;
			this.setter = setter;
			setString();
			colorButtons.add(this);
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
			setter.accept(!getter.get());
			colorButtons.forEach(GuiBooleanButton::setString);
		}

		private GuiBooleanButton setString() {
			boolean value = getter.get(); // the value of the boolean
			// set the display text COLOR(GREEN/RED) LANG_NAME (Enabled)?
			setMessage("\u00a7" + (value ? 'a' : 'c') + I18n.translate(lang)
					+ (value ? " (" + I18n.translate("x13.mod.enable") + ")" : ""));
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

		private void draw(int mouseX, int mouseY, float partialTick) {
			font.drawWithShadow(title, width / 2 - 200, field.y - font.fontHeight / 2 + 9, 0xffffffff);
			field.render(mouseX, mouseY, partialTick);
		}

		private int getSizeX() {
			return font.getStringWidth(title = I18n.translate("x13.mod.blocks", mode.getNameTranslate()) + ": ");
		}

		private void init(int x, int y, int sizeX) {
			children.add(field = new TextFieldWidget(font, x, y + 2, 338 - sizeX, 16, ""));
			addButton(new GuiBooleanButton(mode.getNameTranslate(), mode::isEnabled, mode::toggle));
			addButton(new ButtonWidget(width / 2 + 150, y, 50, 20, I18n.translate("controls.reset"), b -> {
				field.setText(text = mode.getDefaultBlocks());
			}));
			field.setMaxLength(Integer.MAX_VALUE);
			field.setText(text);
		}

		private void save() {
			mode.setConfig(text.split(" "));
		}

		private void update() {
			text = field.getText();
		}
	}

	private static void setBlock(AbstractButtonWidget b, int x, int y, int width) {
		b.x = x;
		b.y = y;
		b.setWidth(width);
	}

	private Screen parent;
	private XrayMain mod;

	private final boolean oldFullbrightConfig;
	private final boolean oldShowLocationConfig;

	private List<GuiBooleanButton> colorButtons = Lists.newArrayList();

	private List<XrayModeElement> modeElements = Lists.newArrayList();

	public XrayMenu(Screen parent) {
		super(new TranslatableText("x13.mod.config"));
		this.parent = parent;
		font = MinecraftClient.getInstance().textRenderer;
		mod = XrayMain.getMod();
		mod.getModes().forEach(XrayModeElement::new);
		oldFullbrightConfig = mod.isFullBrightEnable();
		oldShowLocationConfig = mod.isShowLocation();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		renderBackground();
		String s = I18n.translate("x13.mod.config");
		font.drawWithShadow(s, width / 2 - font.getStringWidth("Xray 13") / 2, height / 2 - 84, 0xffffffff);
		modeElements.forEach(modeElement -> modeElement.draw(mouseX, mouseY, partialTick));
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	protected void init() {
		colorButtons.clear();
		addButton(new ButtonWidget(width / 2 - 200, height / 2, 198, 20, I18n.translate("gui.cancel"), b -> {
			modeElements.forEach(XrayModeElement::cancel);
			mod.fullBright(oldFullbrightConfig).modules().setShowLocation(oldShowLocationConfig);
			minecraft.openScreen(parent);
		}));
		addButton(new ButtonWidget(width / 2 + 2, height / 2, 198, 20, I18n.translate("gui.done"), b -> {
			modeElements.forEach(XrayModeElement::save);
			minecraft.openScreen(parent);
		}));
		OptionalInt max = modeElements.stream().mapToInt(XrayModeElement::getSizeX).max();
		int sizeX = (max.isPresent() ? max.getAsInt() : 0);
		int x = width / 2 - 195 + sizeX;
		int y = height / 2 - 22 - 24 * modeElements.size();
		for (XrayModeElement element : modeElements) {
			y += 24;
			element.init(x, y, sizeX);
		}
		addButton(new GuiBooleanButton("x13.mod.fullbright", mod::isFullBrightEnable, mod::fullBright));
		addButton(new GuiBooleanButton("x13.mod.showloc", mod::isShowLocation, mod::setShowLocation));
		int i, j = colorButtons.size() / 3;
		int y_ = height / 2;
		for (i = 0; i < j; i++) {
			y_ += 24;
			setBlock(colorButtons.get(i * 3), width / 2 - 200, y_, 130);
			setBlock(colorButtons.get(i * 3 + 1), width / 2 - 66, y_, 132);
			setBlock(colorButtons.get(i * 3 + 2), width / 2 + 70, y_, 130);
		}
		y_ += 24;
		switch (colorButtons.size() - (i * 3)) {
		case 1:
			setBlock(colorButtons.get(i * 3 - 1), width / 2 - 200, y_, 198);
			setBlock(colorButtons.get(i * 3), width / 2 + 2, y_, 198);
			y_ -= 24;
			setBlock(colorButtons.get(i * 3 - 3), width / 2 - 200, y_, 198);
			setBlock(colorButtons.get(i * 3 - 2), width / 2 + 2, y_, 198);
			break;
		case 2:
			setBlock(colorButtons.get(i * 3), width / 2 - 200, y_, 198);
			setBlock(colorButtons.get(i * 3 + 1), width / 2 + 2, y_, 198);
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
