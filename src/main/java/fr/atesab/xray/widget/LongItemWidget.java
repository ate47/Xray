package fr.atesab.xray.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.widget.MenuWidget.OnPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LongItemWidget extends AbstractButton {

    private ItemStack itemStack;
    private OnPress oPress;
    private int deltaX;
    private int deltaY;

    public LongItemWidget(int x, int y, int w, int h, Component text, ItemStack stack, OnPress oPress) {
        super(x, y, w, h, text);
        this.itemStack = stack;
        this.oPress = oPress;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }


    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        boolean hovered = isHoveredOrFocused();
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        int x = getX();
        int y = getY();
        graphics.fill(x, y, x + width, y + height, color);

        Component message = getMessage();
        Font font = client.font;
        ItemRenderer renderer = client.getItemRenderer();

        int deltaH = (getHeight() - 16);

        GuiUtils.renderItemIdentity(graphics, itemStack, x + deltaH / 2 + deltaX, y + deltaH / 2 + deltaY);
        graphics.drawString(font, message, x + deltaH + 16 + 2, y + getHeight() / 2 - font.lineHeight / 2, packedFGColor);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        this.defaultButtonNarrationText(p_259858_);
    }

    @Override
    public void onPress() {
        oPress.onPress();
    }
}
