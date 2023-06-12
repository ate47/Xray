package fr.atesab.xray.color;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.widget.XraySlider;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public enum Skin {
    XRAY(XrayMain.MOD_NAME) {
        @Override
        public boolean renderSlider(XraySlider widget, DrawContext drawContext, int x, int y, int w, int h) {
            int bgColor;
            if (!widget.active) {
                bgColor = 0x99_000000;
            } else if (widget.isHovered()) {
                bgColor = 0x99_999999;
            } else {
                bgColor = 0x99_666666;
            }
            int location = (int) (widget.getValue() * (double) (w - 8));
            GuiUtils.drawRect(drawContext, x, y, x + location, y + h, 0x99_222222);
            GuiUtils.drawRect(drawContext, x + location + 8, y, x + w, y + h, 0x99_222222);
            GuiUtils.drawRect(drawContext, x + location, y, x + location + 8, y + h, bgColor);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer font = minecraft.textRenderer;
            int color = widget.active ? 16777215 : 10526880;
            GuiUtils.drawCenterString(drawContext, font, widget.getMessage(), x + w / 2, y + (h - 8) / 2, color | 0xFF000000);
            return false;
        }

        @Override
        public boolean renderButton(XrayButton widget, DrawContext drawContext, int x, int y, int w, int h) {
            int bgColor;

            if (!widget.active) {
                bgColor = 0x99_222222;
            } else if (widget.getBgColor() != null) {
                bgColor = widget.getBgColor();
            } else if (widget.isHovered()) {
                bgColor = 0x99_999999;
            } else {
                bgColor = 0x99_666666;
            }
            GuiUtils.drawRect(drawContext, x, y, x + w, y + h, bgColor);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer font = minecraft.textRenderer;
            int color = widget.active ? 16777215 : 10526880;
            GuiUtils.drawCenterString(drawContext, font, widget.getMessage(), x + w / 2, y + (h - 8) / 2, color | 0xFF000000);
            return false;
        }

        @Override
        public Integer getBackgroundColor() {
            return 0xFF101010;
        }
    },
    PSY("Psy") {
        /**
         * high frequency to reduce Epilepsy
         */
        private static final int FREQUENCY = 10_000;

        @Override
        public boolean renderSlider(XraySlider widget, DrawContext drawContext, int x, int y, int w, int h) {
            int bgColor;
            if (!widget.active) {
                bgColor = 0x99_000000;
            } else if (widget.isHovered()) {
                bgColor = 0x99_000000 | (0xFFFFFF & GuiUtils.getTimeColor(FREQUENCY, 100, 50));
            } else {
                bgColor = 0x99_666666 | (0xFFFFFF & GuiUtils.getTimeColor(FREQUENCY / 3, FREQUENCY, 100, 50));
            }
            int location = (int) (widget.getValue() * (double) (w - 8));
            GuiUtils.drawRect(drawContext, x, y, x + location, y + h, 0x99_222222);
            GuiUtils.drawRect(drawContext, x + location + 8, y, x + w, y + h, 0x99_222222);
            GuiUtils.drawRect(drawContext, x + location, y, x + location + 8, y + h, bgColor);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer font = minecraft.textRenderer;
            int color = widget.active ? 16777215 : 10526880;
            GuiUtils.drawCenterString(drawContext, font, widget.getMessage(), x + w / 2, y + (h - 8) / 2, color | 0xFF000000);
            return false;
        }

        @Override
        public boolean renderButton(XrayButton widget, DrawContext drawContext, int x, int y, int w, int h) {
            int bgColor;

            if (!widget.active) {
                bgColor = 0x99_222222;
            } else if (widget.getBgColor() != null) {
                bgColor = widget.getBgColor();
            } else if (widget.isHovered()) {
                bgColor = 0x99_000000 | (0xFFFFFF & GuiUtils.getTimeColor(FREQUENCY, 100, 50));
            } else {
                bgColor = 0x99_666666 | (0xFFFFFF & GuiUtils.getTimeColor(FREQUENCY / 3, FREQUENCY, 100, 50));
            }
            GuiUtils.drawRect(drawContext, x, y, x + w, y + h, bgColor);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer font = minecraft.textRenderer;
            int color = widget.active ? 16777215 : 10526880;
            GuiUtils.drawCenterString(drawContext, font, widget.getMessage(), x + w / 2, y + (h - 8) / 2, color | 0xFF000000);
            return false;
        }

        @Override
        public Integer getBackgroundColor() {
            return 0xFF000000 | GuiUtils.getTimeColor(FREQUENCY * 2 / 3, FREQUENCY, 100, 8);
        }
    },
    CLASSIC("Minecraft") {
        @Override
        public boolean renderSlider(XraySlider widget, DrawContext drawContext, int x, int y, int w, int h) {
            return true;
        }

        @Override
        public boolean renderButton(XrayButton widget, DrawContext drawContext, int x, int y, int w, int h) {
            return true;
        }
    };

    private final String title;

    Skin(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean renderSlider(XraySlider widget, MatrixStack stack, int x, int y, int w, int h) {
        return true;
    }

    public abstract boolean renderSlider(XraySlider widget, DrawContext drawContext, int x, int y, int w, int h);

    public boolean renderButton(XrayButton widget, MatrixStack stack, int x, int y, int w, int h) {
        return true;
    }

    public abstract boolean renderButton(XrayButton widget, DrawContext drawContext, int x, int y, int w, int h);

    public Integer getBackgroundColor() {
        return null;
    }
}
