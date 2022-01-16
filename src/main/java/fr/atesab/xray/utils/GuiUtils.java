package fr.atesab.xray.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

/**
 * Advanced creative tab GuiUtils
 * https://github.com/ate47/AdvancedCreativeTab/blob/1.18-forge/src/main/java/fr/atesab/act/utils/GuiUtils.java
 */
public class GuiUtils {
    private static final Random RANDOM = new Random();

    public static record HSLResult(int hue, int saturation, int lightness, int alpha) {
    };

    public static record RGBResult(int red, int green, int blue, int alpha) {
    };

    public static final int COLOR_CONTAINER_BORDER = 0xC2C2C2;
    public static final int COLOR_CONTAINER_SLOT = 0xDADADA;

    public static int blueToRed(int color) {
        return (color & 0xFF00FF00) | ((color & 0x000000FF) << 16) | ((color & 0x00FF0000) >> 16);
    }

    /**
     * @param rgba argb color
     * @return true if the rgba value has an alpha part or if the color is fully
     *         transparent
     */
    public static boolean hasAlpha(int rgba) {
        return (rgba & 0xFF000000) != 0;
    }

    /**
     * Convert r g b param to rgba int
     * 
     * @param r red
     * @param g green
     * @param b red
     * @param a alpha
     * @return (a << 24) | (r << 16) | (g << 8) | b
     */
    public static int asRGBA(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Convert r g b param to rgba int
     * 
     * @param r red
     * @param g green
     * @param b red
     * @param a alpha
     * @return (a << 24) | (r << 16) | (g << 8) | b
     */
    public static int asRGBA(float r, float g, float b, float a) {
        return ((int) (a * 0xFF) << 24) | ((int) (r * 0xFF) << 16) | ((int) (g * 0xFF) << 8) | (int) (b * 0xFF);
    }

    /**
     * convert hsl parm to rgba
     * 
     * @param h hue, angle [0-360[
     * @param s saturation, percentage [0-100]
     * @param l lightness, percentage [0-100]
     * @return rgba value
     */
    public static int fromHSL(int h, int s, int l) {
        return fromHSL(h, s / 100f, l / 100f);
    }

    /**
     * convert hsl parm to rgba
     * 
     * @param h hue, angle [0-360[
     * @param s saturation, percentage
     * @param l lightness, percentage
     * @return rgba value
     */
    public static int fromHSL(int h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float hh = h / 60f;
        float x = c * (1 - Math.abs(hh % 2 - 1));

        float m = l - c / 2;

        return switch ((int) hh) {
            case 0 -> asRGBA(c, x, 0, 1f);
            case 1 -> asRGBA(x, c, 0, 1f);
            case 2 -> asRGBA(0, c, x, 1f);
            case 3 -> asRGBA(0, x, c, 1f);
            case 4 -> asRGBA(x, 0, c, 1f);
            case 5 -> asRGBA(c, 0, x, 1f);
            default -> 0xFF000000; // happy compiler
        } + 0x010101 * (int) (m * 0xFF);
    }

    /**
     * get HSL from RGBA
     * 
     * @param rgba int rgba
     * @return HSL
     */
    public static HSLResult hslFromRGBA(int rgba) {
        return hslFromRGBA(rgba, 0, 0);
    }

    /**
     * @param rgba int rgba
     * @return RGB
     */
    public static RGBResult rgbaFromRGBA(int rgba) {
        int alpha = (rgba >> 24) & 0xff;
        int red = (rgba >> 16) & 0xff;
        int green = (rgba >> 8) & 0xff;
        int blue = rgba & 0xff;
        return new RGBResult(red, green, blue, alpha);
    }

    /**
     * get HSL from RGBA with an option to preserve hue/saturation with gray scale
     * 
     * @param rgba          int rgba
     * @param oldHue        old hue, to avoid loosing it
     * @param oldSaturation old saturation, to avoid loosing it
     * @return HSL
     */
    public static HSLResult hslFromRGBA(int rgba, int oldHue, int oldSaturation) {
        int alpha = (rgba >> 24) & 0xff;
        float red = ((rgba >> 16) & 0xff) / 255f;
        float green = ((rgba >> 8) & 0xff) / 255f;
        float blue = (rgba & 0xff) / 255f;

        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float chroma = max - min;

        int hue;

        if (chroma == 0) {
            hue = oldHue; // no color
        } else if (max == red) {
            hue = (int) ((((green - blue) / chroma) % 6) * 60);
        } else if (max == green) {
            hue = (int) ((((blue - red) / chroma + 2) % 6) * 60);
        } else { // max == blue
            hue = (int) ((((red - green) / chroma + 4) % 6) * 60);
        }

        if (hue < 0) {
            hue += 360;
        }

        float lightness = (max + min) / 2;
        float saturation = lightness == 1 ? oldSaturation : (chroma / (1 - Math.abs(2 * lightness - 1)));

        return new HSLResult(hue, (int) (saturation * 100), (int) (lightness * 100), alpha);
    }

    public static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(v, max));
    }

    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }

    public static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(v, max));
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color.
     * 
     * @param stack  the matrix stack
     * @param left   left location
     * @param top    top location
     * @param right  right location
     * @param bottom bottom location
     * @param color  the color
     */
    public static void drawRect(PoseStack stack, int left, int top, int right, int bottom, int color) {
        Gui.fill(stack, left, top, right, bottom, color);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color.
     * 
     * @param stack        the matrix stack
     * @param left         left location
     * @param top          top location
     * @param right        right location
     * @param bottom       bottom location
     * @param color        the color
     * @param colorHovered the color if the mouse is hover the rect
     * @param mouseX       the mouseX
     * @param mouseY       the mouseY
     */
    public static void drawHoverableRect(PoseStack stack, int left, int top, int right, int bottom, int color,
            int colorHovered, int mouseX, int mouseY) {
        int c = (isHover(left, top, right - left, bottom - top, mouseX, mouseY) ? colorHovered : color);
        Gui.fill(stack, left, top, right, bottom, c);
    }

    /**
     * @return a random ARGB with a full alpha
     */
    public static int getRandomColor() {
        return 0xff000000 | RANDOM.nextInt(0x1000000);
    }

    public static int getTimeColor(int frequency, int saturation, int lightness) {
        return 0xff000000 | fromHSL((int) ((System.currentTimeMillis() % (long) frequency) * 360 / frequency),
                saturation, lightness);
    }

    /**
     * Check if a {@link Widget} is hover by a location (mouse)
     * 
     * @param widget the widget
     * @param mouseX the mouse x location
     * @param mouseY the mouse y location
     * @return true if the button is hover, false otherwise
     * @see #isHover(int, int, int, int, int, int)
     * @since 2.0
     */
    public static boolean isHover(AbstractWidget widget, int mouseX, int mouseY) {
        return isHover(widget.x, widget.y, widget.getWidth(), widget.getHeight(), mouseX, mouseY);
    }

    /**
     * Check if a box is hover by a location (mouse)
     * 
     * @param x      the x location
     * @param y      the y location
     * @param sizeX  the width
     * @param sizeY  the height
     * @param mouseX the mouse x location
     * @param mouseY the mouse y location
     * @return true if the field is hover
     * @see #isHover(Widget, int, int)
     * @since 2.0
     */
    public static boolean isHover(int x, int y, int sizeX, int sizeY, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
    }

    /**
     * Draw an {@link ItemStack} on a {@link Screen}
     * 
     * @param itemRender the renderer
     * @param itemstack  the stack
     * @param x          the x location
     * @param y          the y location
     * 
     * @since 2.1.1
     */
    public static void drawItemStack(ItemRenderer itemRender, ItemStack itemstack, int x, int y) {
        if (itemstack == null || itemstack.isEmpty())
            return;
        RenderSystem.enableDepthTest();
        itemRender.renderAndDecorateItem(itemstack, x, y);
        itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, itemstack, x, y, null);
        RenderSystem.disableBlend();
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
     * anywhere in vanilla code.
     * 
     * @param x          x location
     * @param y          y location
     * @param u          x uv location
     * @param v          y uv location
     * @param uWidth     uv width
     * @param vHeight    uv height
     * @param width      width
     * @param height     height
     * @param tileWidth  tile width
     * @param tileHeight tile height
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
            int height, float tileWidth, float tileHeight) {
        drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight, 0xffffff);
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
     * anywhere in vanilla code.
     * 
     * @param x          x location
     * @param y          y location
     * @param u          x uv location
     * @param v          y uv location
     * @param uWidth     uv width
     * @param vHeight    uv height
     * @param width      width
     * @param height     height
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param color      tile color
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
            int height, float tileWidth, float tileHeight, int color) {
        drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight, color, false);
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
     * anywhere in vanilla code.
     * 
     * @param x          x location
     * @param y          y location
     * @param u          x uv location
     * @param v          y uv location
     * @param uWidth     uv width
     * @param vHeight    uv height
     * @param width      width
     * @param height     height
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param color      tile color
     * @param useAlpha   use the alpha of the color
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
            int height, float tileWidth, float tileHeight, int color, boolean useAlpha) {
        float scaleX = 1.0F / tileWidth;
        float scaleY = 1.0F / tileHeight;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = useAlpha ? (color >> 24) : 0xff;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex((double) x, (double) (y + height), 0.0D)
                .uv((float) (u * scaleX), (float) ((v + (float) vHeight) * scaleY)).color(red, green, blue, alpha)
                .endVertex();
        bufferbuilder.vertex((double) (x + width), (double) (y + height), 0.0D)
                .uv((float) ((u + (float) uWidth) * scaleX), (float) ((v + (float) vHeight) * scaleY))
                .color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex((double) (x + width), (double) y, 0.0D)
                .uv((float) ((u + (float) uWidth) * scaleX), (float) (v * scaleY)).color(red, green, blue, alpha)
                .endVertex();
        bufferbuilder.vertex((double) x, (double) y, 0.0D).uv((float) (u * scaleX), (float) (v * scaleY))
                .color(red, green, blue, alpha).endVertex();
        tesselator.end();
    }

    /**
     * Draw a String on the screen at middle of an height
     * 
     * @param stack  the stack
     * @param font   the font
     * @param text   the string to render
     * @param x      the x location
     * @param y      the y location
     * @param color  the color of the text
     * @param height the height of the text
     * 
     * @since 2.0
     * @see #drawCenterString(Font, String, int, int, int, int)
     * @see #drawRightString(Font, String, int, int, int, int)
     */
    public static void drawString(PoseStack stack, Font font, String text, int x, int y, int color, int height) {
        font.draw(stack, text, x, y + height / 2 - font.lineHeight / 2, color);
    }

    /**
     * Draw a String on the screen at middle of an height
     * 
     * @param stack the stack
     * @param font  the font
     * @param text  the string to render
     * @param x     the x location
     * @param y     the y location
     * @param color the color of the text
     * 
     * @since 2.0
     * @see #drawCenterString(Font, String, int, int, int, int)
     * @see #drawRightString(Font, String, int, int, int, int)
     */
    public static void drawString(PoseStack stack, Font font, String text, int x, int y, int color) {
        drawString(stack, font, text, x, y, color, font.lineHeight);
    }

    /**
     * Draw a String centered
     * 
     * @param stack the stack
     * @param font  the font
     * @param text  the text
     * @param x     x text location
     * @param y     y text location
     * @param color text color
     * @since 2.0
     * @see #drawCenterString(Font, String, int, int, int, int)
     * @see #drawRightString(Font, String, int, int, int)
     */
    public static void drawCenterString(PoseStack stack, Font font, String text, int x, int y, int color) {
        drawCenterString(stack, font, text, x, y, color, font.lineHeight);
    }

    /**
     * Draw a String centered of a vertical segment
     * 
     * @param stack  the stack
     * @param font   the font
     * @param text   the text
     * @param x      x text location
     * @param y      y text location
     * @param color  text color
     * @param height segment length
     * @since 2.0
     * @see #drawCenterString(Font, String, int, int, int)
     * @see #drawString(Font, String, int, int, int, int)
     */
    public static void drawCenterString(PoseStack stack, Font font, String text, int x, int y, int color, int height) {
        drawString(stack, font, text, x - font.width(text) / 2, y, color, height);
    }

    /**
     * Draw a String to the the right of a location
     * 
     * @param stack the stack
     * @param font  the font
     * @param text  the string to render
     * @param x     the x location
     * @param y     the y location
     * @param color the color of the text
     * 
     * @since 2.0
     * @see #drawCenterString(Font, String, int, int, int)
     * @see #drawRightString(Font, String, int, int, int, int)
     */

    public static void drawRightString(PoseStack stack, Font font, String text, int x, int y, int color) {
        drawRightString(stack, font, text, x, y, color, font.lineHeight);
    }

    /**
     * Draw a String on the screen at middle of an height to the right of location
     * 
     * @param stack  the stack
     * @param font   the font
     * @param text   the string to render
     * @param x      the x location
     * @param y      the y location
     * @param color  the color of the text
     * @param height the height of the text
     * 
     * @since 2.0
     * @see #drawString(Font, String, int, int, int, int)
     * @see #drawCenterString(Font, String, int, int, int, int)
     */
    public static void drawRightString(PoseStack stack, Font font, String text, int x, int y, int color, int height) {
        drawString(stack, font, text, x - font.width(text), y, color, height);
    }

    /**
     * Draw a String to the right of a {@link Widget}
     * 
     * @param stack the stack
     * @param font  the font
     * @param text  the string to render
     * @param field the widget
     * @param color the color of the text
     * 
     * @since 2.0
     * @see #drawRightString(Font, String, int, int, int)
     * @see #drawRightString(Font, String, int, int, int, int)
     */
    public static void drawRightString(PoseStack stack, Font font, String text, AbstractWidget field, int color) {
        drawRightString(stack, font, text, field.x, field.y, color, field.getHeight());
    }

    /**
     * Draw a String to the right of a {@link Widget} with offsets
     * 
     * @param stack   the stack
     * @param font    the font
     * @param text    the string to render
     * @param field   the widget
     * @param color   the color of the text
     * @param offsetX the x offset
     * @param offsetY the y offset
     * 
     * @since 2.0
     * @see #drawRightString(Font, String, Widget, int)
     */
    public static void drawRightString(PoseStack stack,
            Font font, String text, AbstractWidget field, int color, int offsetX,
            int offsetY) {
        drawRightString(stack, font, text, field.x + offsetX, field.y + offsetY, color, field.getHeight());
    }

    /**
     * Draw a text box on the screen
     * 
     * @param matrixStack  the matrixStack
     * @param font         the renderer
     * @param x            the x location
     * @param y            the y location
     * @param parentWidth  the parent width
     * @param parentHeight the parent height
     * @param zLevel       the zLevel of the screen
     * @param args         the lines to show
     * 
     * @since 2.1
     */
    public static void drawTextBox(PoseStack matrixStack, Font font, int x, int y, int parentWidth, int parentHeight,
            float zLevel, String... args) {
        List<String> text = Arrays.asList(args);
        int width = text.isEmpty() ? 0 : text.stream().mapToInt(font::width).max().getAsInt();
        int height = text.size() * (1 + font.lineHeight);
        Tuple<Integer, Integer> pos = getRelativeBoxPos(x, y, width, height, parentWidth, parentHeight);
        drawBox(matrixStack, pos.getA(), pos.getB(), width, height, zLevel);
        text.forEach(l -> {
            drawString(matrixStack, font, l, pos.getA(), pos.getB(), 0xffffffff);
            pos.setB(pos.getB() + (1 + font.lineHeight));
        });
    }

    /**
     * Draw a box on the screen
     * 
     * @param p      matrix stack
     * @param x      x tl location
     * @param y      y tl location
     * @param width  box width
     * @param height box height
     * @param z      zlevel of the screen
     * 
     * @since 2.0
     */
    public static void drawBox(PoseStack p, int x, int y, int width, int height, float z) {
        z -= 50F;
        // -267386864 0xF0100010 | 1347420415 0x505000FF | 1344798847 0x5028007F
        drawGradientRect(p, x - 3, y - 4, x + width + 3, y - 3, 0xF0100010, 0xF0100010, z);
        drawGradientRect(p, x - 3, y + height + 3, x + width + 3, y + height + 4, 0xF0100010, 0xF0100010, z);
        drawGradientRect(p, x - 3, y - 3, x + width + 3, y + height + 3, 0xF0100010, 0xF0100010, z);
        drawGradientRect(p, x - 4, y - 3, x - 3, y + height + 3, 0xF0100010, 0xF0100010, z);
        drawGradientRect(p, x + width + 3, y - 3, x + width + 4, y + height + 3, 0xF0100010, 0xF0100010, z);
        drawGradientRect(p, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 0x505000FF, 0x5028007F, z);
        drawGradientRect(p, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 0x505000FF, 0x5028007F, z);
        drawGradientRect(p, x - 3, y - 3, x + width + 3, y - 3 + 1, 0x505000FF, 0x505000FF, z);
        drawGradientRect(p, x - 3, y + height + 2, x + width + 3, y + height + 3, 0x5028007F, 0x5028007F, z);
    }

    /**
     * Draw a rectangle with a vertical gradient
     * 
     * @param stack      matrix stack
     * @param left       left location
     * @param top        top location
     * @param right      right location
     * @param bottom     bottom location
     * @param startColor startColor color
     * @param endColor   endColor color
     * @param zLevel     zLevel of the screen
     * 
     * @see #drawGradientRect(PoseStack, int, int, int, int, int, int, int, int,
     *      float)
     * @since 2.0
     */
    public static void drawGradientRect(PoseStack stack, int left, int top, int right, int bottom, int startColor,
            int endColor, float zLevel) {
        drawGradientRect(stack, left, top, right, bottom, startColor, startColor, endColor, endColor, zLevel);
    }

    /**
     * Draw a gradient rectangle
     * 
     * @param stack            matrix stack
     * @param left             left location
     * @param top              top location
     * @param right            right location
     * @param bottom           bottom location
     * @param rightTopColor    rightTopColor color (ARGB)
     * @param leftTopColor     leftTopColor color (ARGB)
     * @param leftBottomColor  leftBottomColor color (ARGB)
     * @param rightBottomColor rightBottomColor color (ARGB)
     * @param zLevel           zLevel of the screen
     * 
     * @see #drawGradientRect(PoseStack, int, int, int, int, int, int, float)
     * @since 2.0
     */
    public static void drawGradientRect(PoseStack stack, int left, int top, int right, int bottom, int rightTopColor,
            int leftTopColor, int leftBottomColor, int rightBottomColor, float zLevel) {
        float alphaRightTop = (float) (rightTopColor >> 24 & 255) / 255.0F;
        float redRightTop = (float) (rightTopColor >> 16 & 255) / 255.0F;
        float greenRightTop = (float) (rightTopColor >> 8 & 255) / 255.0F;
        float blueRightTop = (float) (rightTopColor & 255) / 255.0F;
        float alphaLeftTop = (float) (leftTopColor >> 24 & 255) / 255.0F;
        float redLeftTop = (float) (leftTopColor >> 16 & 255) / 255.0F;
        float greenLeftTop = (float) (leftTopColor >> 8 & 255) / 255.0F;
        float blueLeftTop = (float) (leftTopColor & 255) / 255.0F;
        float alphaLeftBottom = (float) (leftBottomColor >> 24 & 255) / 255.0F;
        float redLeftBottom = (float) (leftBottomColor >> 16 & 255) / 255.0F;
        float greenLeftBottom = (float) (leftBottomColor >> 8 & 255) / 255.0F;
        float blueLeftBottom = (float) (leftBottomColor & 255) / 255.0F;
        float alphaRightBottom = (float) (rightBottomColor >> 24 & 255) / 255.0F;
        float redRightBottom = (float) (rightBottomColor >> 16 & 255) / 255.0F;
        float greenRightBottom = (float) (rightBottomColor >> 8 & 255) / 255.0F;
        float blueRightBottom = (float) (rightBottomColor & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = stack.last().pose();
        bufferbuilder.vertex(mat, right, top, zLevel).color(redRightTop, greenRightTop, blueRightTop, alphaRightTop)
                .endVertex();
        bufferbuilder.vertex(mat, left, top, zLevel).color(redLeftTop, greenLeftTop, blueLeftTop, alphaLeftTop)
                .endVertex();
        bufferbuilder.vertex(mat, left, bottom, zLevel)
                .color(redLeftBottom, greenLeftBottom, blueLeftBottom, alphaLeftBottom).endVertex();
        bufferbuilder.vertex(mat, right, bottom, zLevel)
                .color(redRightBottom, greenRightBottom, blueRightBottom, alphaRightBottom).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /**
     * get a tuple of (x,y) location on the screen for a box to put it without
     * loosing it at borders
     * 
     * @param x            the x location
     * @param y            the y location
     * @param width        the width
     * @param height       the height
     * @param parentWidth  the parent width
     * @param parentHeight the parent height
     * 
     * @return (x,y) location
     * 
     * @since 2.0
     */
    public static Tuple<Integer, Integer> getRelativeBoxPos(int x, int y, int width, int height, int parentWidth,
            int parentHeight) {
        if (x + width > parentWidth) {
            x -= width + 5;
            if (x < 0)
                x = 0;
        } else
            x += 12;
        if (y + height > parentHeight) {
            y -= height + 5;
            if (y < 0)
                y = 0;
        } else
            y += 12;
        return new Tuple<>(x, y);
    }

}
