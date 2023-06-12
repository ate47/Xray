package fr.atesab.xray.widget;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class XraySlider extends SliderWidget {
    public XraySlider(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text, value);
    }

    @Override
    public void renderButton(DrawContext drawContext, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderSlider(this, drawContext.getMatrices(), getX(), getY(), width, height)) {
            super.renderButton(drawContext, mx, my, delta);
        }
    }
    public void setValue(double value) {
        double d = this.value;
        this.value = MathHelper.clamp(value, 0.0, 1.0);
        if (d != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }

    public double getValue() {
        return value;
    }
}
