package fr.atesab.xray.widget;

import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public abstract class SliderValueWidget extends AbstractSliderButton {
    public SliderValueWidget(int x, int y, int width, int height, Component text, double value) {
        super(x, y, width, height, text, value);
    }

    public void setValue0(double value) {
        double d = this.value;
        this.value = GuiUtils.clamp(value, 0.0, 1.0);
        if (d != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }
}
