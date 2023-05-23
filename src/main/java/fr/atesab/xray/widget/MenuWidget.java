package fr.atesab.xray.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MenuWidget extends AbstractButton {
    public interface OnPress {
        void onPress();
    }

    private ItemStack itemStack;
    private OnPress onPress;

    public MenuWidget(int x, int y, int w, int h, Component text, ItemStack stack, OnPress onPress) {
        super(x, y, w, h, text);
        this.onPress = onPress;
        this.itemStack = stack;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        boolean hovered = isHoveredOrFocused();
        int centerX = getX() + width / 2;
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        Gui.fill(stack, getX(), getY(), getX() + width, getY() + height, color);

        Component message = getMessage();
        Font font = client.font;
        ItemRenderer renderer = client.getItemRenderer();
        PoseStack modelStack = RenderSystem.getModelViewStack();

        int stackCenterX = getX() + width / 2;
        int stackCenterY = getY() + height * 2 / 5;

        modelStack.translate(stackCenterX, stackCenterY, 0);
        float scaleX = getWidth() * 3 / 4f / 16f;
        float scaleY = getHeight() * 3 / 4f / 16f;
        modelStack.scale(scaleX, scaleY, 1);
        renderer.renderGuiItem(new PoseStack(), itemStack, -8, -8);
        modelStack.scale(1 / scaleX, 1 / scaleY, 1);
        modelStack.translate(-stackCenterX, -stackCenterY, 0);
        RenderSystem.applyModelViewMatrix();

        stack.pushPose();
        stack.translate(centerX, getY() + getHeight(), 0);
        float scale = getHeight() * 1 / 7f / font.lineHeight;
        stack.scale(scale, scale, 1);
        drawCenteredString(stack, font, message, 0, -font.lineHeight, packedFGColor);
        stack.scale(1 / scale, 1 / scale, 1);
        stack.translate(-centerX, -getY() - getHeight(), 0);
        stack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        this.defaultButtonNarrationText(p_259858_);
    }


    @Override
    public void onPress() {
        onPress.onPress();
    }

}
