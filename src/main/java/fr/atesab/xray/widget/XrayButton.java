package fr.atesab.xray.widget;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class XrayButton extends Button {
    private Integer bgColor = null;

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press) {
        super(x, y, w, h, text, press, DEFAULT_NARRATION);
    }

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press, CreateNarration narration) {
        super(x, y, w, h, text, press, narration);
    }

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press, CreateNarration narration, Tooltip tooltip) {
        this(x, y, w, h, text, press, narration);
        setTooltip(tooltip);
    }

    public XrayButton(int x, int y, int w, int h, Component text, OnPress press, Tooltip tooltip) {
        this(x, y, w, h, text, press);
        setTooltip(tooltip);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mx, int my, float delta) {
        Skin skin = XrayMain.getMod().getConfig().getSkin();
        if (skin.renderButton(this, graphics, getX(), getY(), width, height)) {
            super.renderWidget(graphics, mx, my, delta);
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
