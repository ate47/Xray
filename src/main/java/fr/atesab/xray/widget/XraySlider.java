package fr.atesab.xray.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class XraySlider extends AbstractSliderButton {

    public XraySlider(int x, int y, int w, int h, Component text, double value) {
        super(x, y, w, h, text, value);
    }

    @Override
    public void renderButton(PoseStack stack, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderSlider(this, stack, x, y, width, height)) {
            super.renderButton(stack, mx, my, delta);
        }
    }

    public double getValue() {
        return value;
    }
}
