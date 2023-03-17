package fr.atesab.xray.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.GuiUtils.HSLResult;
import fr.atesab.xray.widget.XrayButton;
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
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class ColorSelector extends XrayScreen {

    private enum DragState {
        HL, S, NONE
    }

    private static final int[] buffer = new int[20];
    private static boolean filledBuffer;

    private static int getShiftY() {
        return filledBuffer ? 12 : 0;
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
    private static final ItemStack RANDOM_PICKER = Util.make(new ItemStack(Items.POTION), ItemStack::getOrCreateNbt);
    private static final int RANDOM_PICKER_FREQUENCY = 3600;

    private static ItemStack updatePicker() {
        NbtCompound tag = RANDOM_PICKER.getNbt();
        assert tag != null;
        tag.putInt("CustomPotionColor", GuiUtils.getTimeColor(RANDOM_PICKER_FREQUENCY, 100, 50));
        RANDOM_PICKER.setNbt(tag);
        return RANDOM_PICKER;
    }

    public static Identifier getPickerHlResource() {
        return PICKER_HL_RESOURCE;
    }

    public static Identifier getPickerSResource() {
        return PICKER_S_RESOURCE;
    }

    private static int pickerHue;
    private static int pickerSaturation;
    private static int pickerLightness;

    private static void setPickerState(int hue, int saturation, int lightness) {
        if (!pickerInit)
            registerPickerImage();
        // regen PICKER_IMAGE_S
        if (!(hue == pickerHue && lightness == pickerLightness)) {
            pickerHue = hue;
            pickerLightness = lightness;

            NativeImage pixels = PICKER_IMAGE_S.getImage();
            assert pixels != null;

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
            assert pixels != null;

            for (int x = 0; x < pixels.getWidth(); x++) // hue
                for (int y = 0; y < pixels.getHeight(); y++) // lightness
                    pixels.setColor(x, y, GuiUtils.blueToRed(
                            GuiUtils.fromHSL(x * 360 / pixels.getWidth(), saturation, y * 100 / pixels.getHeight())));

            PICKER_IMAGE_HL.upload();
        }

    }

    private static void registerPickerImage() {
        pickerInit = true;
        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
        setPickerState(0, 0, 100);
        tm.registerTexture(PICKER_S_RESOURCE, PICKER_IMAGE_S);
        tm.registerTexture(PICKER_HL_RESOURCE, PICKER_IMAGE_HL);
    }

    private int oldAlphaLayer;
    private final boolean transparentAsDefault;
    private int color;
    private DragState drag = DragState.NONE;
    private boolean advanced = false;
    private ButtonWidget advButton;
    private TextFieldWidget tfr, tfg, tfb, tfh, tfs, tfl, intColor, hexColor;
    private final int defaultColor;
    private int localHue;
    private int localSaturation;
    private int localLightness;
    private final Consumer<OptionalInt> setter;

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
        super(Text.translatable("x13.mod.color.title"), parent);
        int rgba = color.orElse(defaultColor);
        this.color = rgba & 0xFFFFFF; // remove alpha
        this.oldAlphaLayer = rgba & 0xFF000000;
        if (transparentAsDefault && color.isEmpty())
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
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getPickerSResource());
            GuiUtils.drawScaledCustomSizeModalRect(width / 2 + 180, height / 2 - 76 - getShiftY(), 0, 0, PICKER_S_SIZE_X,
                    PICKER_SIZE_Y, 20, 76 * 2, PICKER_S_SIZE_X, PICKER_SIZE_Y);

            // - S Index
            int saturationDelta = pickerSaturation * 76 * 2 / 100;
            GuiUtils.drawRect(matrixStack, width / 2 + 178, height / 2 - 76 + saturationDelta - 2 - getShiftY(), width / 2 + 178 + 22,
                    height / 2 - 76 + saturationDelta + 2 - getShiftY(), 0xff222222);
            GuiUtils.drawRect(matrixStack, width / 2 + 180, height / 2 - 76 + saturationDelta - 1 - getShiftY(), width / 2 + 180 + 20,
                    height / 2 - 76 + saturationDelta + 1 - getShiftY(), 0xff999999);

            // HL Picker
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getPickerHlResource());
            GuiUtils.drawScaledCustomSizeModalRect(width / 2 - 158, height / 2 - 76 - getShiftY(), 0, 0, PICKER_HL_SIZE_X,
                    PICKER_SIZE_Y, 158 + 176, 76 * 2, PICKER_HL_SIZE_X, PICKER_SIZE_Y);

            // - HL Index
            int hueDelta = pickerHue * (158 + 176) / 360;
            int lightnessDelta = pickerLightness * (76 * 2) / 100;
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 5, height / 2 - 76 + lightnessDelta - 2 - getShiftY(),
                    width / 2 - 158 + hueDelta - 5 + 10, height / 2 - 76 + lightnessDelta - 2 + 4 - getShiftY(), 0xff222222);
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 2, height / 2 - 76 + lightnessDelta - 5 - getShiftY(),
                    width / 2 - 158 + hueDelta - 2 + 4, height / 2 - 76 + lightnessDelta - 5 + 10 - getShiftY(), 0xff222222);

            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 4, height / 2 - 76 + lightnessDelta - 1 - getShiftY(),
                    width / 2 - 158 + hueDelta - 4 + 8, height / 2 - 76 + lightnessDelta - 1 + 2 - getShiftY(), 0xff999999);
            GuiUtils.drawRect(matrixStack, width / 2 - 158 + hueDelta - 1, height / 2 - 76 + lightnessDelta - 4 - getShiftY(),
                    width / 2 - 158 + hueDelta - 1 + 2, height / 2 - 76 + lightnessDelta - 4 + 8 - getShiftY(), 0xff999999);
        } else {
            GuiUtils.drawRect(matrixStack, width / 2 - 158, height / 2 - 76 - getShiftY(), width / 2 + 200, height / 2 + 76 - getShiftY(),
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
                    intColor.getX(),
                    intColor.getY() - 4 - 10, 0xffffffff, 10);
            GuiUtils.drawString(matrixStack, textRenderer, I18n.translate("x13.mod.color.hexColor") + ":",
                    hexColor.getX(),
                    hexColor.getY() - 4 - 10, 0xffffffff, 10);

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
            GuiUtils.drawRect(matrixStack, width / 2 - 158, height / 2 - 100 - getShiftY(), width / 2 + 176, height / 2 - 80 - getShiftY(),
                    color | 0xff000000);

        Runnable show = () -> {
        };
        for (int i = 0; i < DyeColor.values().length; ++i) {
            DyeColor color = DyeColor.values()[i];
            int x = width / 2 - 200 + (i % 2) * 19;
            int y = height / 2 - 76 + (i / 2) * 19 - getShiftY();
            GuiUtils.drawRect(matrixStack, x, y, x + 19, y + 19, 0xff000000 | color.getFireworkColor());
            if (GuiUtils.isHover(x, y, 19, 19, mouseX, mouseY)) {
                show = () -> GuiUtils.drawTextBox(matrixStack, textRenderer, mouseX, mouseY, width, height,
                        0,
                        I18n.translate("item.minecraft.firework_star." + color.getName()));
            }
            GuiUtils.drawItemStack(itemRenderer, new ItemStack(DyeItem.byColor(color)), x + (19 - 16) / 2,
                    y + (19 - 16) / 2);
        }

        // random
        GuiUtils.drawHoverableRect(matrixStack, width / 2 - 200, height / 2 - 100 - getShiftY(), width / 2 - 162, height / 2 - 80 - getShiftY(),
                0xFF444444, GuiUtils.getTimeColor(RANDOM_PICKER_FREQUENCY, 50, 15), mouseX, mouseY);
        GuiUtils.drawItemStack(itemRenderer, updatePicker(), width / 2 - 200 + 38 / 2 - 16 / 2,
                height / 2 - 100 + 20 / 2 - 16 / 2 - getShiftY());
        if (GuiUtils.isHover(width / 2 - 200, height / 2 - 100 - getShiftY(), 38, 20, mouseX, mouseY)) {
            show = () -> GuiUtils.drawTextBox(matrixStack, textRenderer, mouseX, mouseY, width, height, 0,
                    I18n.translate("x13.mod.color.random"));
        }

        // delete
        GuiUtils.drawHoverableRect(matrixStack, width / 2 + 180, height / 2 - 100 - getShiftY(), width / 2 + 200, height / 2 - 80 - getShiftY(),
                0xFFDD4444, 0xFFFF4444, mouseX, mouseY);
        GuiUtils.drawCenterString(matrixStack, textRenderer, "x", width / 2 + 190, height / 2 - 100 - getShiftY(), 0xFFFFFFFF, 20);

        // history
        for (int i = 0; i < buffer.length; i++) {
            int bx = width / 2 - 200 + 20 * i;
            int by = height / 2 + 104 - getShiftY();
            GuiUtils.drawHoverableRect(matrixStack, bx, by, bx + 19, by + 19, 0xFF_FF_FF_FF, 0xFF_BB_BB_BB, mouseX, mouseY);
            GuiUtils.drawRect(matrixStack, bx + 1, by + 1, bx + 18, by + 18, buffer[i] | 0xFF_00_00_00);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        show.run();
    }

    private void complete() {
        int endColor = color & 0x00FFFFFF;
        // find last index of the color (or length if not available)
        // this is done to put first the endColor if it was already in the history
        int start;
        for (start = 1; start < buffer.length; start++) {
            if (buffer[start - 1] == endColor) {
                break;
            }
        }
        System.arraycopy(buffer, 0, buffer, 1, start - 1);
        buffer[0] = endColor;
        filledBuffer = true;
        setter.accept((color & 0xFF000000) != 0 ? OptionalInt.empty() : OptionalInt.of(color | oldAlphaLayer));
    }

    @Override
    public void init() {
        addDrawableChild(XrayButton.builder(Text.translatable("gui.done"), b -> {
                    complete();
                    client.setScreen(parent);
                }).dimensions(width / 2 - 200, height / 2 + 80 - getShiftY(), 130, 20).build());

        advButton = addDrawableChild(XrayButton.builder(Text.translatable("x13.mod.color.advanced"), b -> {
            advanced ^= true;
            advButton.setMessage(Text.translatable(
                    advanced ? "x13.mod.color.picker" : "x13.mod.color.advanced"));
        }).dimensions(width / 2 - 66, height / 2 + 80 - getShiftY(), 132, 20).build());

        addDrawableChild(XrayButton.builder(Text.translatable("gui.cancel"), b -> client.setScreen(parent))
                .dimensions(width / 2 + 70, height / 2 + 80 - getShiftY(), 130, 20).build());

        int advWidth = 158 + 200;
        int midAdv = width / 2 + (-158 + 200) / 2;
        tfr = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 - 54 - getShiftY(), 56, 18, Text.literal(""));
        tfg = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 - 26 - getShiftY(), 56, 18, Text.literal(""));
        tfb = new TextFieldWidget(textRenderer, midAdv - 56, height / 2 + 2 - getShiftY(), 56, 18, Text.literal(""));

        int rightAdv = width / 2 + 200;
        tfh = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 - 54 - getShiftY(), 56, 18, Text.literal(""));
        tfl = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 - 26 - getShiftY(), 56, 18, Text.literal(""));
        tfs = new TextFieldWidget(textRenderer, rightAdv - 56, height / 2 + 2 - getShiftY(), 56, 18, Text.literal(""));

        int intHexWidth = (advWidth - 4 - 4) / 2;
        intColor = new TextFieldWidget(textRenderer, midAdv - intHexWidth, height / 2 + 40 - getShiftY(), intHexWidth, 18,
                Text.literal(""));
        hexColor = new TextFieldWidget(textRenderer, midAdv + 4, height / 2 + 40 - getShiftY(), intHexWidth, 18, Text.literal(""));

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
                    updateRed(tfr.getText().isEmpty() ? 0 : Integer.parseInt(tfr.getText()));
                } catch (Exception ignored) {
                }
            else if (tfg.isFocused())
                try {
                    updateGreen(tfg.getText().isEmpty() ? 0 : Integer.parseInt(tfg.getText()));
                } catch (Exception ignored) {
                }
            else if (tfb.isFocused())
                try {
                    updateBlue(tfb.getText().isEmpty() ? 0 : Integer.parseInt(tfb.getText()));
                } catch (Exception ignored) {
                }
            else if (tfh.isFocused())
                try {
                    updateHue(tfh.getText().isEmpty() ? 0 : Integer.parseInt(tfh.getText()));
                } catch (Exception ignored) {
                }
            else if (tfs.isFocused())
                try {
                    updateSaturation(tfs.getText().isEmpty() ? 0 : Integer.parseInt(tfs.getText()));
                } catch (Exception ignored) {
                }
            else if (tfl.isFocused())
                try {
                    updateLightness(tfl.getText().isEmpty() ? 0 : Integer.parseInt(tfl.getText()));
                } catch (Exception ignored) {
                }
            else if (hexColor.isFocused())
                try {
                    String s = hexColor.getText().substring(1);
                    updateColor(s.isEmpty() ? 0 : Integer.parseInt(s, 16));
                } catch (Exception ignored) {
                }
            else if (intColor.isFocused())
                try {
                    updateColor(intColor.getText().isEmpty() ? 0 : Integer.parseInt(intColor.getText()));
                } catch (Exception ignored) {
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
                    updateRed(tfr.getText().isEmpty() ? 0 : Integer.parseInt(tfr.getText()));
                } catch (Exception ignored) {

                }
            else if (tfg.isFocused())
                try {
                    updateGreen(tfg.getText().isEmpty() ? 0 : Integer.parseInt(tfg.getText()));
                } catch (Exception ignored) {

                }
            else if (tfb.isFocused())
                try {
                    updateBlue(tfb.getText().isEmpty() ? 0 : Integer.parseInt(tfb.getText()));
                } catch (Exception ignored) {

                }
            else if (tfh.isFocused())
                try {
                    updateHue(tfh.getText().isEmpty() ? 0 : Integer.parseInt(tfh.getText()));
                } catch (Exception ignored) {

                }
            else if (tfs.isFocused())
                try {
                    updateSaturation(tfs.getText().isEmpty() ? 0 : Integer.parseInt(tfs.getText()));
                } catch (Exception ignored) {

                }
            else if (tfl.isFocused())
                try {
                    updateLightness(tfl.getText().isEmpty() ? 0 : Integer.parseInt(tfl.getText()));
                } catch (Exception ignored) {

                }
            else if (hexColor.isFocused())
                try {
                    String s = hexColor.getText().substring(1);
                    updateColor(s.isEmpty() ? 0 : Integer.parseInt(s, 16));
                } catch (Exception ignored) {

                }
            else if (intColor.isFocused())
                try {
                    updateColor(intColor.getText().isEmpty() ? 0 : Integer.parseInt(intColor.getText()));
                } catch (Exception ignored) {

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
            if (!advanced && GuiUtils.isHover(width / 2 - 158, height / 2 - 76 - getShiftY(), 158 + 176, 76 * 2, (int) mouseX,
                    (int) mouseY)) {
                setColor((int) mouseX, (int) mouseY, DragState.HL);
            } else if (!advanced
                    && GuiUtils.isHover(width / 2 + 180, height / 2 - 76 - getShiftY(), 20, 76 * 2, (int) mouseX, (int) mouseY)) {
                setColor((int) mouseX, (int) mouseY, DragState.S);
            } else if (GuiUtils.isHover(width / 2 + 180, height / 2 - 100 - getShiftY(), 20, 20, (int) mouseX, (int) mouseY)) {
                if (transparentAsDefault) {
                    color |= 0xFF000000;
                } else {
                    oldAlphaLayer = defaultColor & 0xFF000000;
                    updateColor(defaultColor & 0xFFFFFF);
                }
                playDownSound();
                return true;
            } else if (GuiUtils.isHover(width / 2 - 200, height / 2 - 100 - getShiftY(), 38, 20, (int) mouseX, (int) mouseY)) {
                updateColor(GuiUtils.getRandomColor() & 0xffffff);
                playDownSound();
                return true;
            } else {
                for (int i = 0; i < DyeColor.values().length; ++i)
                    if (GuiUtils.isHover(width / 2 - 200 + (i % 2) * 19, height / 2 - 76 + (i / 2) * 19 - getShiftY(), 19, 19,
                            (int) mouseX, (int) mouseY)) {
                        updateColor(DyeColor.values()[i].getFireworkColor());
                        playDownSound();
                        return true;
                    }
                // history
                for (int i = 0; i < buffer.length; i++) {
                    int bx = width / 2 - 200 + 20 * i;
                    int by = height / 2 + 104 - getShiftY();
                    if (GuiUtils.isHover(bx, by, 19, 19,
                            (int) mouseX, (int) mouseY)) {
                        updateColor(buffer[i]);
                        playDownSound();
                        return true;
                    }
                }
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
        tfb.setText("" + (color & 0xFF));
        this.intColor.setText("" + color);
        this.hexColor.setText("#" + Integer.toHexString(color));
    }

    private void setColor(int mouseX, int mouseY, DragState dragState) {
        drag = dragState;
        if (drag == DragState.NONE)
            return;

        switch (drag) {
            case HL -> {
                // hue
                int hue = GuiUtils.clamp(mouseX - (width / 2 - 158), 0, 158 + 176) * 360 / (158 + 176 + 1);
                // lightness
                int lightness = GuiUtils.clamp(mouseY - (height / 2 - 76 - getShiftY()), 0, 76 * 2) * 100 / (76 * 2);
                updateColor(hue, pickerSaturation, lightness);
            }
            case S -> {
                int saturation = GuiUtils.clamp(mouseY - (height / 2 - 76 - getShiftY()), 0, 76 * 2) * 100 / (76 * 2);
                updateColor(pickerHue, saturation, pickerLightness);
            }
            default -> {
            }
        }
    }

    private void updateRed(int v) {
        updateColor((v & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | ((color & 0xFF) & 0xFF));
    }

    private void updateGreen(int v) {
        updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | (v & 0xFF) << 8 | ((color & 0xFF) & 0xFF));
    }

    private void updateBlue(int v) {
        updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | (v & 0xFF));
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
