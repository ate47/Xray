package fr.atesab.xray.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public abstract class XraySlider extends AbstractSliderButton {

    public XraySlider(int x, int y, int w, int h, Component text, double value) {
        super(x, y, w, h, text, value);
    }

    @Override
    public void renderButton(PoseStack stack, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderSlider(this, stack, getX(), getY(), width, height)) {
            super.renderButton(stack, mx, my, delta);
        }
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        double d = this.value;
        this.value = GuiUtils.clamp(value, 0.0, 1.0);
        if (d != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }
}
