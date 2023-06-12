package fr.atesab.xray.color;

import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Text buffer to write a hud
 *
 * @author ATE47
 */
public class TextHudBuffer {
    private static final int PADDING = 4;
    private final List<MutableText> lines = new ArrayList<>();

    /**
     * begin a new line
     */
    public void newLine() {
        lines.add(Text.empty());
    }

    /**
     * append a new text to this buffer
     *
     * @param text text
     */
    public void append(Text text) {
        if (lines.isEmpty()) {
            newLine();
        }
        lines.set(lines.size() - 1, lines.get(lines.size() - 1).append(text));
    }

    /**
     * draw the buffer
     *
     * @param drawContext  drawContext
     * @param screenWidth  screen width
     * @param screenHeight screen height
     * @param cfg    location config
     * @param render text renderer
     */
    public void draw(DrawContext drawContext, int screenWidth, int screenHeight, LocationConfig cfg, TextRenderer render) {
        if (lines.isEmpty()) {
            return; // ignore
        }
        // add padding to not be too close of the screen border
        int width = screenWidth - PADDING * 2;
        int height = screenHeight - PADDING * 2;

        int fontSize = (int) (cfg.getFontSizeMultiplier() * render.fontHeight);

        LocationConfig.LocationLocation loc = cfg.getLocation();
        LocationConfig.TextAlignX alignX = loc.getAlignX();
        LocationConfig.TextAlignY alignY = loc.getAlignY();

        int x = PADDING + (int) (cfg.getShiftX() * width);
        int y = PADDING + switch (alignY) {
            case TOP -> (int) (cfg.getShiftY() * height);
            case MIDDLE -> (int) (cfg.getShiftY() * height) - ((fontSize + 1) * lines.size()) / 2;
            case BOTTOM -> (int) (cfg.getShiftY() * height) - (fontSize + 1) * lines.size();
        };

        for (int i = 0; i < lines.size(); i++) {
            OrderedText text = lines.get(i).asOrderedText();

            // TODO: add color selector in the location config?
            switch (alignX) {
                case LEFT ->
                        GuiUtils.drawTextComponentScaled(drawContext, x, y + i * (fontSize + 1), fontSize, text, 0xffffffff);
                case CENTER ->
                        GuiUtils.drawCenteredTextComponentScaled(drawContext, x, y + i * (fontSize + 1), fontSize, text, 0xffffffff);
                case RIGHT ->
                        GuiUtils.drawRightTextComponentScaled(drawContext, x, y + i * (fontSize + 1), fontSize, text, 0xffffffff);
            }
        }
    }

}
