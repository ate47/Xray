package fr.atesab.x13;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
		public GuiBooleanButton(String lang, Supplier<Boolean> getter, Consumer<Boolean> setter) {
			super(0, 0, 0, 130, 20, lang);
			this.lang = lang;
			this.getter = getter;
			this.setter = setter;
			setString();
			colorButtons.add(this);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			if (!super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
				return false;
			setter.accept(!getter.get());
			colorButtons.forEach(GuiBooleanButton::setString);
			return true;
		}

		private GuiButton setString() {
			boolean value = getter.get(); // the value of the boolean
			// set the display text COLOR(GREEN/RED) LANG_NAME (Enabled)?
			displayString = "\u00a7" + (value ? 'a' : 'c') + I18n.format(lang)
					+ (value ? " (" + I18n.format("x13.mod.enable") + ")" : "");
			return this;
		}
	}

	public class XrayModeElement {
		private GuiTextField field;
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
			fontRenderer.drawStringWithShadow(title, width / 2 - 200, field.y - fontRenderer.FONT_HEIGHT / 2 + 9,
					0xffffffff);
			field.drawTextField(mouseX, mouseY, partialTick);
		}

		private int getSizeX() {
			return fontRenderer.getStringWidth(title = I18n.format("x13.mod.blocks", mode.getNameTranslate()) + ": ");
		}

		private void init(int x, int y, int sizeX) {
			children.add(field = new GuiTextField(0, fontRenderer, x, y + 2, 338 - sizeX, 16));
			addButton(new GuiBooleanButton(mode.getNameTranslate(), mode::isEnabled, mode::toggle));
			addButton(new GuiButton(3, width / 2 + 150, y, 50, 20, I18n.format("controls.reset")) {
				@Override
				public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
					if (!super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
						return false;
					field.setText(text = mode.getDefaultBlocks());
					return true;
				}
			});
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

	private static void setBlock(GuiButton b, int x, int y, int width) {
		b.x = x;
		b.y = y;
		b.setWidth(width);
	}

	private GuiScreen parent;
	private XrayMain mod;

	private final boolean oldFullbrightConfig;
	private final boolean oldShowLocationConfig;

	private List<GuiBooleanButton> colorButtons = Lists.newArrayList();

	private List<XrayModeElement> modeElements = Lists.newArrayList();

	public XrayMenu(GuiScreen parent) {
		super();
		this.parent = parent;
		fontRenderer = Minecraft.getInstance().fontRenderer;
		mod = XrayMain.getMod();
		mod.getModes().forEach(XrayModeElement::new);
		oldFullbrightConfig = mod.isFullBrightEnable();
		oldShowLocationConfig = mod.isShowLocation();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		String s = "Xray 13";
		fontRenderer.drawStringWithShadow(s, width / 2 - fontRenderer.getStringWidth("Xray 13") / 2, height / 2 - 84,
				0xffffffff);
		modeElements.forEach(modeElement -> modeElement.draw(mouseX, mouseY, partialTick));
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	protected void initGui() {
		colorButtons.clear();
		addButton(new GuiButton(0, width / 2 - 200, height / 2, 198, 20, I18n.format("gui.cancel")) {
			@Override
			public boolean mouseClicked(double x, double y, int b) {
				if (!super.mouseClicked(x,y,b))
					return false;
				modeElements.forEach(XrayModeElement::cancel);
				mod.fullBright(oldFullbrightConfig).modules().setShowLocation(oldShowLocationConfig);
				mc.displayGuiScreen(parent);
				return true;
			}
		});
		addButton(new GuiButton(0, width / 2 + 2, height / 2, 198, 20, I18n.format("gui.done")) {
			@Override
			public boolean mouseClicked(double x, double y, int b) {
				if (!super.mouseClicked(x,y,b))
					return false;
				modeElements.forEach(XrayModeElement::save);
				mc.displayGuiScreen(parent);
				return true;
			}
		});
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
		super.initGui();
	}

	@Override
	public void tick() {
		modeElements.forEach(XrayModeElement::update);
		super.tick();
	}

}
