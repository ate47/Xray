package fr.atesab.xray;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.KeybindText;
import net.minecraft.text.TranslatableText;

public class XrayMenu extends Screen {
	/**
	 * A button to set boolean value
	 */
	private class GuiBooleanButton extends PressableWidget {
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
			super(0, 0, 130, 20, new KeybindText(lang));
			this.lang = lang;
			this.getter = getter;
			this.setter = setter;
			setString();
			colorButtons.add(this);
		}

		private GuiBooleanButton setString() {
			boolean value = getter.get(); // the value of the boolean
			// set the display text COLOR(GREEN/RED) LANG_NAME (Enabled)?
			String color = "\u00a7" + (value ? 'a' : 'c');
			// @formatter:off
			setMessage(new KeybindText(
					color +
					I18n.translate(lang) +
					(value ? " (" + I18n.translate("x13.mod.enable") + ")" : "")
			));
			// @formatter:on
			return this;
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
			// TODO: understand what we have to fill
		}

		@Override
		public void onPress() {
			setter.accept(!getter.get());
			colorButtons.forEach(GuiBooleanButton::setString);
		}
	}

	public class XrayModeElement {
		private ButtonWidget blockMenu;
		private String title;
		private XrayMode mode;

		public XrayModeElement(XrayMode mode) {
			this.mode = mode;
			modeElements.add(this);
		}

		private void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
			textRenderer.drawWithShadow(matrixStack, title, width / 2 - 200,
					blockMenu.y - textRenderer.fontHeight / 2 + 9, 0xffffffff);
		}

		private int getSizeX() {
			return textRenderer.getWidth(title = I18n.translate("x13.mod.blocks", mode.getNameTranslate()) + ": ");
		}

		private void init(int x, int y, int sizeX) {
			// field = new TextFieldWidget(
			// font,
			// x, y + 2, 338 - sizeX, 16,
			// new KeybindText("")
			// );
			// addSelectableChild(field);
			addDrawableChild(new GuiBooleanButton(mode.getNameTranslate(), mode::isEnabled, mode::toggle));
			blockMenu = new XrayModeWidget(x, y, 338 - sizeX, 20, mode, XrayMenu.this);
			ButtonWidget reset = (new ButtonWidget(width / 2 + 150, y, 50, 20,
					new KeybindText(I18n.translate("controls.reset")), b -> mode.reset()));
			addDrawableChild(reset);
			addDrawableChild(blockMenu);
		}
	}

	private static void setBlock(PressableWidget b, int x, int y, int width) {
		b.x = x;
		b.y = y;
		b.setWidth(width);
	}

	private Screen parent;
	private XrayMain mod;

	private List<GuiBooleanButton> colorButtons = Lists.newArrayList();

	private List<XrayModeElement> modeElements = Lists.newArrayList();

	public XrayMenu(Screen parent) {
		super(new TranslatableText("x13.mod.config"));
		this.parent = parent;
		mod = XrayMain.getMod();
		mod.getModes().forEach(XrayModeElement::new);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(matrixStack);
		String s = I18n.translate("x13.mod.config");
		textRenderer.drawWithShadow(matrixStack, new KeybindText(s), width / 2 - textRenderer.getWidth("Xray 13") / 2,
				height / 2 - 84, 0xffffffff);
		modeElements.forEach(modeElement -> modeElement.draw(matrixStack, mouseX, mouseY, partialTick));
		super.render(matrixStack, mouseX, mouseY, partialTick);
	}

	@Override
	protected void init() {
		colorButtons.clear();
		ButtonWidget doneBtn = new ButtonWidget(width / 2 - 200, height / 2, 198, 20,
				new KeybindText(I18n.translate("gui.done")), b -> {
					client.openScreen(parent);
				});
		addDrawableChild(doneBtn);
		ButtonWidget resetBtn = new ButtonWidget(width / 2 + 2, height / 2, 198, 20,
				new KeybindText(I18n.translate("controls.reset")), b -> {
					mod.getModes().forEach(XrayMode::reset);
				});
		addDrawableChild(resetBtn);
		OptionalInt max = modeElements.stream().mapToInt(XrayModeElement::getSizeX).max();
		int sizeX = (max.isPresent() ? max.getAsInt() : 0);
		int x = width / 2 - 195 + sizeX;
		int y = height / 2 - 22 - 24 * modeElements.size();
		for (XrayModeElement element : modeElements) {
			y += 24;
			element.init(x, y, sizeX);
		}
		addDrawableChild(new GuiBooleanButton("x13.mod.fullbright", mod::isFullBrightEnable, mod::fullBright));
		addDrawableChild(new GuiBooleanButton("x13.mod.showloc", mod::isShowLocation, mod::setShowLocation));
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
		super.tick();
	}

}