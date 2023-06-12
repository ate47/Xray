package fr.atesab.xray.widget;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class XrayButton extends ButtonWidget {
    @Environment(EnvType.CLIENT)
    public static class Builder extends ButtonWidget.Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private NarrationSupplier narrationSupplier;

        public Builder(Text message, PressAction onPress) {
            super(message, onPress);
            this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public XrayButton build() {
            XrayButton buttonWidget = new XrayButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }

    public static Builder builder(Text message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    private Integer bgColor = null;

    public XrayButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction press) {
        this(x, y, w, h, text, press, DEFAULT_NARRATION_SUPPLIER);
    }
    public XrayButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction press, NarrationSupplier narrationSupplier) {
        super(x, y, w, h, text, press, narrationSupplier);
    }

    public XrayButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction press, Tooltip tooltip) {
        this(x, y, w, h, text, press, DEFAULT_NARRATION_SUPPLIER, tooltip);
    }
    public XrayButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction press, NarrationSupplier narrationSupplier, Tooltip tooltip) {
        super(x, y, w, h, text, press, narrationSupplier);
        setTooltip(tooltip);
    }

    @Override
    public void renderButton(DrawContext drawContext, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderButton(this, drawContext.getMatrices(), getX(), getY(), width, height)) {
            super.renderButton(drawContext, mx, my, delta);
        }
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void removeBgColor() {
        this.bgColor = null;
    }

    public Integer getBgColor() {
        return bgColor;
    }
}
