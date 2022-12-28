package fr.atesab.xray.widget;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class SliderValueWidget extends SliderWidget {
    public SliderValueWidget(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text, value);
    }

    public void setValue0(double value) {
        double d = this.value;
        this.value = MathHelper.clamp(value, 0.0, 1.0);
        if (d != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }
}
