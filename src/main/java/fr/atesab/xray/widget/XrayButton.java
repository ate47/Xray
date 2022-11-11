package fr.atesab.xray.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class XrayButton extends Button {
    private Integer bgColor = null;

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press) {
        super(x, y, w, h, text, press);
    }

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press, OnTooltip tooltip) {
        super(x, y, w, h, text, press, tooltip);
    }

    @Override
    public void renderButton(PoseStack stack, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderButton(this, stack, x, y, width, height)) {
            super.renderButton(stack, mx, my, delta);
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
