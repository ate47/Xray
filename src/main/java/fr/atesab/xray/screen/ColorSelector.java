package fr.atesab.xray.screen;

import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import com.mojang.blaze3d.systems.RenderSystem;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.GuiUtils.HSLResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ColorSelector extends XrayScreen {

    private enum DragState {
        HL, S, NONE;
    }

    private static final int PICKER_SIZE_Y = 200;
    private static final int PICKER_S_SIZE_X = 20;
    private static final int PICKER_HL_SIZE_X = 200;
    private static boolean pickerInit = false;
    private static final Identifier PICKER_S_RESOURCE = new Identifier(XrayMain.MOD_ID, "picker_hl");
    private static final Identifier PICKER_HL_RESOURCE = new Identifier(XrayMain.MOD_ID, "picker_s");
    private static final NativeImageBackedTexture PICKER_IMAGE_S = new NativeImageBackedTexture(
            new NativeImage(NativeImage.Format.RGBA, PICKER_S_SIZE_X, PICKER_SIZE_Y, false));
    private static final NativeImageBackedTexture PICKER_IMAGE_HL = new NativeImageBackedTexture(
            new NativeImage(NativeImage.Format.RGBA, PICKER_HL_SIZE_X, PICKER_SIZE_Y, false));
    private static final ItemStack RANDOM_PICKER = Util.make(new ItemStack(Items.POTION), it -> {
        it.getOrCreateNbt();
    });
    private static final int RANDOM_PICKER_FREQUENCY = 3600;

    private static ItemStack updatePicker() {
        pickerInit = true;
        NbtCompound tag = RANDOM_PICKER.getNbt();
        tag.putInt("CustomPotionColor", GuiUtils.getTimeColor(RANDOM_PICKER_FREQUENCY, 100, 50));
        RANDOM_PICKER.setNbt(tag);
        return RANDOM_PICKER;
    }

    public static Identifier getPickerHlResource() {
        if (pickerInit)
            registerPickerImage();
        return PICKER_HL_RESOURCE;
    }

    public static Identifier getPickerSResource() {
        if (pickerInit)
            registerPickerImage();
        return PICKER_S_RESOURCE;
    }

    private static int pickerHue;
    private static int pickerSaturation;
    private static int pickerLightness;

    private static void setPickerState(int hue, int saturation, int lightness) {
        // regen PICKER_IMAGE_S
        if (!(hue == pickerHue && lightness == pickerLightness)) {
            pickerHue = hue;
            pickerLightness = lightness;

            NativeImage pixels = PICKER_IMAGE_S.getImage();

            for (int y = 0; y < pixels.getHeight(); y++) { // saturation
                int color = GuiUtils.fromHSL(hue, y * 100 / pixels.getHeight(), lightness);
                for (int x = 0; x < pixels.getWidth(); x++)
                    pixels.setColor(x, y, GuiUtils.blueToRed(color));
            }

            PICKER_IMAGE_S.upload();
        }

        // regen PICKER_IMAGE_HL
        if (saturation != pickerSaturation) {
            pickerSaturation = saturation;

            NativeImage pixels = PICKER_IMAGE_HL.getImage();

            for (int x = 0; x < pixels.getWidth(); x++) // hue
                for (int y = 0; y < pixels.getHeight(); y++) // lightness
                    pixels.setColor(x, y, GuiUtils.blueToRed(
                            GuiUtils.fromHSL(x * 360 / pixels.getWidth(), saturation, y * 100 / pixels.getHeight())));

            PICKER_IMAGE_HL.upload();
        }

    }

    public static void registerPickerImage() {
        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
        setPickerState(0, 0, 100);
        tm.registerTexture(PICKER_S_RESOURCE, PICKER_IMAGE_S);
        tm.registerTexture(PICKER_HL_RESOURCE, PICKER_IMAGE_HL);
    }

    private int oldAlphaLayer;
    private boolean transparentAsDefault;
    private int color;
    private DragState drag = DragState.NONE;
    private boolean advanced = false;
    private ButtonWidget advButton;
    private TextFieldWidget tfr, tfg, tfb, tfh, tfs, tfl, intColor, hexColor;
    private int defaultColor;
    private int localHue;
    private int localSaturation;
    private int localLightness;
    private Consumer<OptionalInt> setter;

    public ColorSelector(Screen parent, IntConsumer setter, int color) {
        this(parent, cd -> setter.accept(cd.orElse(0)), OptionalInt.of(color), 0xa06540, false);
    }

    public ColorSelector(Screen parent, IntConsumer setter, int color, int defaultColor) {
        this(parent, cd -> setter.accept(cd.orElse(0)), OptionalInt.of(color), defaultColor, false);
    }

    public ColorSelector(Screen parent, Consumer<OptionalInt> setter, OptionalInt color,
            boolean transparentAsDefault) {
        this(parent, setter, color, color.orElse(0), transparentAsDefault);
    }

    public ColorSelector(Screen parent, Consumer<OptionalInt> setter, OptionalInt color, int defaultColor,
            boolean transparentAsDefault) {
        super(new TranslatableText("x13.mod.color.title"), parent);
        int rgba = color.orElse(defaultColor);
        this.color = rgba & 0xFFFFFF; // remove alpha
        this.oldAlphaLayer = rgba & 0xFF000000;
        if (transparentAsDefault && !color.isPresent())
            this.color |= 0xFF000000;
        this.defaultColor = defaultColor;
        this.transparentAsDefault = transparentAsDefault;
        HSLResult hsl = GuiUtils.hslFromRGBA(rgba, pickerHue, pickerSaturation);
        boolean fullblack = (rgba & 0xFFFFFF) == 0;
        localHue = hsl.hue();
        localSaturation = fullblack ? 100 : hsl.saturation();
        localLightness = hsl.lightness();
        this.setter = setter;
    }

    @Override
    public void tick() {
        tfr.tick();
        tfg.tick();
        tfb.tick();
        tfh.tick();
        tfs.tick();
        tfl.tick();
        hexColor.tick();
        intColor.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // allow multiple color modifiers
        setPickerState(localHue, localSaturation, localLightness);

        renderBackground(matrixStack);

        if (!advanced) {
            // S PICKER
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getPickerSResource());
            GuiUtils.drawScaledCustomSizeModalRect(width / 2 + 180, height / 2 - 76, 0, 0, PICKER_S_SIZE_X,
                    PICKER_SIZE_Y, 20, 76 * 2, PICKER_S_SIZE_X, PICKER_SIZE_Y);

            // - S Index
            int saturationDelta = pickerSaturation * 76 * 2 / 100;
            GuiUtils.drawRect(matrixStack, width / 2 + 178, height / 2 - 76 + saturationDelta - 2, width / 2 + 178 + 22,
                    height / 2 - 76 + saturationDelta + 2, 0xff222222);
            GuiUtils.drawRect(matrixStack, width / 2 + 180, height / 2 - 76 + saturationDelta - 1, width / 2 + 180 + 20,
                    height / 2 - 76 + saturationDelta + 1, 0xff999999);

            // HL Picker
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getPickerHlResource());
            GuiUtils.drawScaledCustomSizeModalRect(width / 2 - 158, height / 2 - 76, 0, 0, PICKER_HL_SIZE_X,
                    PICKER_SIZE_Y, 158 + 176, 76 * 2, PICKER_HL_SIZE_X, PICKER_SIZE_Y);

            // - HL Index
            int hueDelta = pickerHue * (158 + 176) / 360;
            int lightnessDelta = pickerLightness * (76 * 2) / 100;
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 5, height / 2 - 76 + lightnessDelta - 2,
                    width / 2 - 158 + hueDelta - 5 + 10, height / 2 - 76 + lightnessDelta - 2 + 4, 0xff222222);
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 2, height / 2 - 76 + lightnessDelta - 5,
                    width / 2 - 158 + hueDelta - 2 + 4, height / 2 - 76 + lightnessDelta - 5 + 10, 0xff222222);

            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 4, height / 2 - 76 + lightnessDelta - 1,
                    width / 2 - 158 + hueDelta - 4 + 8, height / 2 - 76 + lightnessDelta - 1 + 2, 0xff999999);
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 1, height / 2 - 76 + lightnessDelta - 4,
                    width / 2 - 158 + hueDelta - 1 + 2, height / 2 - 76 + lightnessDelta - 4 + 8, 0xff999999);
        } else {
            GuiUtils.drawRect(matrixStack, width / 2 - 158, height / 2 - 76, width / 2 + 200, height / 2 + 76,
                    0x88000000);
            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.red") + ": ", tfr,
                    0xffffffff);
            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.green") + ": ", tfg,
                    0xffffffff);
            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.blue") + ": ", tfb,
                    0xffffffff);

            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.hue") + ": ", tfh,
                    0xffffffff);
            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.lightness") + ": ",
                    tfl,
                    0xffffffff);
            GuiUtils.drawRightString(matrixStack, textRenderer, I18n.translate("x13.mod.color.saturation") + ": ",
                    tfs,
                    0xffffffff);

            GuiUtils.drawString(matrixStack, textRenderer, I18n.translate("x13.mod.color.intColor") + ":",
                    intColor.x,
                    intColor.y - 4 - 10, 0xffffffff, 10);
            GuiUtils.drawString(matrixStack, textRenderer, I18n.translate("x13.mod.color.hexColor") + ":",
                    hexColor.x,
                    hexColor.y - 4 - 10, 0xffffffff, 10);

            tfr.render(matrixStack, mouseX, mouseY, partialTicks);
            tfg.render(matrixStack, mouseX, mouseY, partialTicks);
            tfb.render(matrixStack, mouseX, mouseY, partialTicks);
            tfh.render(matrixStack, mouseX, mouseY, partialTicks);
            tfl.render(matrixStack, mouseX, mouseY, partialTicks);
            tfs.render(matrixStack, mouseX, mouseY, partialTicks);
            intColor.render(matrixStack, mouseX, mouseY, partialTicks);
            hexColor.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        if ((color & 0xFF000000) == 0)
            GuiUtils.drawRect(matrixStack, width / 2 - 158, height / 2 - 100, width / 2 + 176, height / 2 - 80,
                    color | 0xff000000);

        Runnable show = () -> {
        };
        for (int i = 0; i < DyeColor.values().length; ++i) {
            DyeColor color = DyeColor.values()[i];
            int x = width / 2 - 200 + (i % 2) * 19;
            int y = height / 2 - 76 + (i / 2) * 19;
            GuiUtils.drawRect(matrixStack, x, y, x + 19, y + 19, 0xff000000 | color.getFireworkColor());
            if (GuiUtils.isHover(x, y, 19, 19, mouseX, mouseY)) {
                show = () -> GuiUtils.drawTextBox(matrixStack, textRenderer, mouseX, mouseY, width, height,
                        getZOffset(),
                        I18n.translate("item.minecraft.firework_star." + color.getName()));
            }
            GuiUtils.drawItemStack(itemRenderer, new ItemStack(DyeItem.byColor(color)), x + (19 - 16) / 2,
                    y + (19 - 16) / 2);
        }

        // random
        GuiUtils.drawHoverableRect(matrixStack, width / 2 - 200, height / 2 - 100, width / 2 - 162, height / 2 - 80,
                0xFF444444, GuiUtils.getTimeColor(RANDOM_PICKER_FREQUENCY, 50, 15), mouseX, mouseY);
        GuiUtils.drawItemStack(itemRenderer, updatePicker(), width / 2 - 200 + 38 / 2 - 16 / 2,
                height / 2 - 100 + 20 / 2 - 16 / 2);
        if (GuiUtils.isHover(width / 2 - 200, height / 2 - 100, 38, 20, mouseX, mouseY)) {
            show = () -> GuiUtils.drawTextBox(matrixStack, textRenderer, mouseX, mouseY, width, height, getZOffset(),
                    I18n.translate("x13.mod.color.random"));
        }

        // delete
        GuiUtils.drawHoverableRect(matrixStack, width / 2 + 180, height / 2 - 100, width / 2 + 200, height / 2 - 80,
                0xFFDD4444, 0xFFFF4444, mouseX, mouseY);
        GuiUtils.drawCenterString(matrixStack, textRenderer, "x", width / 2 + 190, height / 2 - 100, 0xFFFFFFFF, 20);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        setZOffset(getZOffset() + 75);
        show.run();
        setZOffset(getZOffset() - 75);
    }

    private void complete() {
        setter.accept((color & 0xFF000000) != 0 ? OptionalInt.empty() : OptionalInt.of(color | oldAlphaLayer));
    }

    @Override
    public void init() {
        addDrawableChild(
                new ButtonWidget(width / 2 - 200, height / 2 + 80, 130, 20, new TranslatableText("gui.done"), b -> {
                    complete();
                    client.setScreen(parent);
                }));
        advButton = addDrawableChild(new ButtonWidget(width / 2 - 66, height / 2 + 80, 132, 20,
                new TranslatableText("x13.mod.color.advanced"), b -> {
                    advanced ^= true;
                    advButton.setMessage(new TranslatableText(
                            advanced ? "x13.mod.color.picker" : "x13.mod.color.advanced"));
                }));
        addDrawableChild(
                new ButtonWidget(width / 2 + 70, height / 2 + 80, 130, 20, new TranslatableText("gui.cancel"), b -> {
                    client.setScreen(parent);
                }));

        int advWidth = 158 + 200;
        int midAdv = width / 2 + (-158 + 200) / 2;
        tfr = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 - 54, 56, 18, new LiteralText(""));
        tfg = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 - 26, 56, 18, new LiteralText(""));
        tfb = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 + 2, 56, 18, new LiteralText(""));

        int rightAdv = width / 2 + 200;
        tfh = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 - 54, 56, 18, new LiteralText(""));
        tfl = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 - 26, 56, 18, new LiteralText(""));
        tfs = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 + 2, 56, 18, new LiteralText(""));

        int intHexWidth = (advWidth - 4 - 4) / 2;
        intColor = new TextFieldWidget(textRenderer, midAdv - intHexWidth, height / 2 + 40, intHexWidth, 18,
                new LiteralText(""));
        hexColor = new TextFieldWidget(textRenderer, midAdv + 4, height / 2 + 40, intHexWidth, 18, new LiteralText(""));

        tfr.setMaxLength(4);
        tfg.setMaxLength(4);
        tfb.setMaxLength(4);
        tfh.setMaxLength(4);
        tfl.setMaxLength(4);
        tfs.setMaxLength(4);

        updateColor(color); // sync picker color
        super.init();
    }

    @Override
    public boolean charTyped(char key, int modifiers) {
        if (advanced) {
            tfr.charTyped(key, modifiers);
            tfg.charTyped(key, modifiers);
            tfb.charTyped(key, modifiers);
            tfh.charTyped(key, modifiers);
            tfl.charTyped(key, modifiers);
            tfs.charTyped(key, modifiers);
            hexColor.charTyped(key, modifiers);
            intColor.charTyped(key, modifiers);
            if (tfr.isFocused())
                try {
                    updateRed(tfr.getText().isEmpty() ? 0 : Integer.valueOf(tfr.getText()));
                } catch (Exception e) {
                }
            else if (tfg.isFocused())
                try {
                    updateGreen(tfg.getText().isEmpty() ? 0 : Integer.valueOf(tfg.getText()));
                } catch (Exception e) {
                }
            else if (tfb.isFocused())
                try {
                    updateBlue(tfb.getText().isEmpty() ? 0 : Integer.valueOf(tfb.getText()));
                } catch (Exception e) {
                }
            else if (tfh.isFocused())
                try {
                    updateHue(tfh.getText().isEmpty() ? 0 : Integer.valueOf(tfh.getText()));
                } catch (Exception e) {
                }
            else if (tfs.isFocused())
                try {
                    updateSaturation(tfs.getText().isEmpty() ? 0 : Integer.valueOf(tfs.getText()));
                } catch (Exception e) {
                }
            else if (tfl.isFocused())
                try {
                    updateLightness(tfl.getText().isEmpty() ? 0 : Integer.valueOf(tfl.getText()));
                } catch (Exception e) {
                }
            else if (hexColor.isFocused())
                try {
                    String s = hexColor.getText().substring(1);
                    updateColor(s.isEmpty() ? 0 : Integer.valueOf(s, 16));
                } catch (Exception e) {
                }
            else if (intColor.isFocused())
                try {
                    updateColor(intColor.getText().isEmpty() ? 0 : Integer.valueOf(intColor.getText()));
                } catch (Exception e) {
                }
        }
        return super.charTyped(key, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (advanced) {
            tfr.keyPressed(key, scanCode, modifiers);
            tfg.keyPressed(key, scanCode, modifiers);
            tfb.keyPressed(key, scanCode, modifiers);
            tfh.keyPressed(key, scanCode, modifiers);
            tfl.keyPressed(key, scanCode, modifiers);
            tfs.keyPressed(key, scanCode, modifiers);
            hexColor.keyPressed(key, scanCode, modifiers);
            intColor.keyPressed(key, scanCode, modifiers);
            if (tfr.isFocused())
                try {
                    updateRed(tfr.getText().isEmpty() ? 0 : Integer.valueOf(tfr.getText()));
                } catch (Exception e) {
                }
            else if (tfg.isFocused())
                try {
                    updateGreen(tfg.getText().isEmpty() ? 0 : Integer.valueOf(tfg.getText()));
                } catch (Exception e) {
                }
            else if (tfb.isFocused())
                try {
                    updateBlue(tfb.getText().isEmpty() ? 0 : Integer.valueOf(tfb.getText()));
                } catch (Exception e) {
                }
            else if (tfh.isFocused())
                try {
                    updateHue(tfh.getText().isEmpty() ? 0 : Integer.valueOf(tfh.getText()));
                } catch (Exception e) {
                }
            else if (tfs.isFocused())
                try {
                    updateSaturation(tfs.getText().isEmpty() ? 0 : Integer.valueOf(tfs.getText()));
                } catch (Exception e) {
                }
            else if (tfl.isFocused())
                try {
                    updateLightness(tfl.getText().isEmpty() ? 0 : Integer.valueOf(tfl.getText()));
                } catch (Exception e) {
                }
            else if (hexColor.isFocused())
                try {
                    String s = hexColor.getText().substring(1);
                    updateColor(s.isEmpty() ? 0 : Integer.valueOf(s, 16));
                } catch (Exception e) {
                }
            else if (intColor.isFocused())
                try {
                    updateColor(intColor.getText().isEmpty() ? 0 : Integer.valueOf(intColor.getText()));
                } catch (Exception e) {
                }
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (advanced) {
            if (mouseButton == 1) {
                if (GuiUtils.isHover(tfr, (int) mouseX, (int) mouseY)) {
                    tfr.setText("");
                    return true;
                } else if (GuiUtils.isHover(tfg, (int) mouseX, (int) mouseY)) {
                    tfg.setText("");
                    return true;
                } else if (GuiUtils.isHover(tfb, (int) mouseX, (int) mouseY)) {
                    tfb.setText("");
                    return true;
                } else if (GuiUtils.isHover(tfh, (int) mouseX, (int) mouseY)) {
                    tfh.setText("");
                    return true;
                } else if (GuiUtils.isHover(tfl, (int) mouseX, (int) mouseY)) {
                    tfl.setText("");
                    return true;
                } else if (GuiUtils.isHover(tfs, (int) mouseX, (int) mouseY)) {
                    tfs.setText("");
                    return true;
                } else if (GuiUtils.isHover(intColor, (int) mouseX, (int) mouseY)) {
                    intColor.setText("");
                    return true;
                } else if (GuiUtils.isHover(hexColor, (int) mouseX, (int) mouseY)) {
                    hexColor.setText("#");
                    return true;
                }
            }
            tfr.mouseClicked(mouseX, mouseY, mouseButton);
            tfg.mouseClicked(mouseX, mouseY, mouseButton);
            tfb.mouseClicked(mouseX, mouseY, mouseButton);
            tfh.mouseClicked(mouseX, mouseY, mouseButton);
            tfl.mouseClicked(mouseX, mouseY, mouseButton);
            tfs.mouseClicked(mouseX, mouseY, mouseButton);
            intColor.mouseClicked(mouseX, mouseY, mouseButton);
            hexColor.mouseClicked(mouseX, mouseY, mouseButton);
        }
        drag = DragState.NONE;
        if (mouseButton == 0) {
            if (!advanced && GuiUtils.isHover(width / 2 - 158, height / 2 - 76, 158 + 176, 76 * 2, (int) mouseX,
                    (int) mouseY)) {
                setColor((int) mouseX, (int) mouseY, DragState.HL);
            } else if (!advanced
                    && GuiUtils.isHover(width / 2 + 180, height / 2 - 76, 20, 76 * 2, (int) mouseX, (int) mouseY)) {
                setColor((int) mouseX, (int) mouseY, DragState.S);
            } else if (GuiUtils.isHover(width / 2 + 180, height / 2 - 100, 20, 20, (int) mouseX, (int) mouseY)) {
                if (transparentAsDefault) {
                    color |= 0xFF000000;
                } else {
                    oldAlphaLayer = defaultColor & 0xFF000000;
                    updateColor(defaultColor & 0xFFFFFF);
                }
                playDownSound();
                return true;
            } else if (GuiUtils.isHover(width / 2 - 200, height / 2 - 100, 38, 20, (int) mouseX, (int) mouseY)) {
                updateColor(GuiUtils.getRandomColor() & 0xffffff);
                playDownSound();
                return true;
            } else
                for (int i = 0; i < DyeColor.values().length; ++i)
                    if (GuiUtils.isHover(width / 2 - 200 + (i % 2) * 19, height / 2 - 76 + (i / 2) * 19, 19, 19,
                            (int) mouseX, (int) mouseY)) {
                        updateColor(DyeColor.values()[i].getFireworkColor());
                        playDownSound();
                        return true;
                    }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
        setColor((int) mouseX, (int) mouseY, drag);
        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy);
    }

    private void updateColor(int h, int s, int l) {
        updateColor(h % 360, s, l, GuiUtils.fromHSL(h % 360, s, l));
    }

    private void updateColor(int rgba) {
        HSLResult hsl = GuiUtils.hslFromRGBA(rgba, localHue, localSaturation);
        updateColor(hsl.hue(), hsl.saturation(), hsl.lightness(), rgba);
    }

    private void updateColor(int h, int s, int l, int rgba) {
        localHue = h;
        localSaturation = s;
        localLightness = l;
        tfh.setText("" + localHue);
        tfs.setText("" + localSaturation);
        tfl.setText("" + localLightness);
        setPickerState(localHue, localSaturation, localLightness);

        color = rgba & 0xffffff;
        tfr.setText("" + (color >> 16 & 0xFF));
        tfg.setText("" + (color >> 8 & 0xFF));
        tfb.setText("" + (color >> 0 & 0xFF));
        this.intColor.setText("" + color);
        this.hexColor.setText("#" + Integer.toHexString(color));
    }

    private void setColor(int mouseX, int mouseY, DragState dragState) {
        drag = dragState;
        if (drag == DragState.NONE)
            return;

        switch (drag) {
            case HL: {
                // hue
                int hue = GuiUtils.clamp(mouseX - (width / 2 - 158), 0, 158 + 176) * 360 / (158 + 176 + 1);
                // lightness
                int lightness = GuiUtils.clamp(mouseY - (height / 2 - 76), 0, 76 * 2) * 100 / (76 * 2);
                updateColor(hue, pickerSaturation, lightness);
            }
                break;
            case S: {
                int saturation = GuiUtils.clamp(mouseY - (height / 2 - 76), 0, 76 * 2) * 100 / (76 * 2);
                updateColor(pickerHue, saturation, pickerLightness);
            }
                break;
            default:
                break;
        }
    }

    private void updateRed(int v) {
        updateColor((v & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | ((color >> 0 & 0xFF) & 0xFF) << 0);
    }

    private void updateGreen(int v) {
        updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | (v & 0xFF) << 8 | ((color >> 0 & 0xFF) & 0xFF) << 0);
    }

    private void updateBlue(int v) {
        updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | (v & 0xFF) << 0);
    }

    private void updateHue(int v) {
        v %= 360;
        if (v < 0)
            v += 360;
        updateColor(v, pickerSaturation, pickerLightness);
    }

    private void updateSaturation(int v) {
        v = GuiUtils.clamp(v, 0, 100);
        updateColor(pickerHue, v, pickerLightness);
    }

    private void updateLightness(int v) {
        v = GuiUtils.clamp(v, 0, 100);
        updateColor(pickerHue, pickerSaturation, v);
    }

}
