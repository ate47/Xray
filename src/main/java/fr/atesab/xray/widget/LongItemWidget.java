package fr.atesab.xray.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.widget.MenuWidget.OnPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LongItemWidget extends AbstractButton {

    private ItemStack itemStack;
    private OnPress oPress;

    public LongItemWidget(int x, int y, int w, int h, Component text, ItemStack stack, OnPress oPress) {
        super(x, y, w, h, text);
        this.itemStack = stack;
        this.oPress = oPress;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        boolean hovered = isHovered();
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        Gui.fill(stack, x, y, x + width, y + height, color);

        Component message = getMessage();
        Font font = client.font;
        ItemRenderer renderer = client.getItemRenderer();

        int deltaH = (getHeight() - 16);

        renderer.renderGuiItem(itemStack, x + deltaH / 2, y + deltaH / 2);
        font.draw(stack,
                message, x + deltaH + 16, y + getHeight() / 2 - font.lineHeight / 2,
                packedFGColor);
    }

    @Override
    public void onPress() {
        oPress.onPress();
    }
}
