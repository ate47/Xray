package fr.atesab.xray.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MenuWidget extends AbstractButton {
    public interface OnPress {
        void onPress();
    }

    private final ItemStack itemStack;
    private final OnPress onPress;

    public MenuWidget(int x, int y, int w, int h, Component text, ItemStack stack, OnPress onPress) {
        super(x, y, w, h, text);
        this.onPress = onPress;
        this.itemStack = stack;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        boolean hovered = isHoveredOrFocused();
        int centerX = getX() + width / 2;
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);

        Component message = getMessage();
        Font font = client.font;
        PoseStack modelStack = RenderSystem.getModelViewStack();

        int stackCenterX = getX() + width / 2;
        int stackCenterY = getY() + height * 2 / 5;

        modelStack.pushPose();
        modelStack.translate(stackCenterX, stackCenterY, 0);
        float scaleX = getWidth() * 3 / 4f / 16f;
        float scaleY = getHeight() * 3 / 4f / 16f;
        modelStack.scale(scaleX, scaleY, 1);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.assertOnRenderThread();
        GuiUtils.renderItemIdentity(graphics, itemStack, -8, -8);
        modelStack.popPose();
        RenderSystem.applyModelViewMatrix();

        PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.translate(centerX, getY() + getHeight(), 0);
        float scale = getHeight() / 7f / font.lineHeight;
        stack.scale(scale, scale, 1);
        graphics.drawCenteredString(font, message, 0, -font.lineHeight, packedFGColor);
        stack.scale(1 / scale, 1 / scale, 1);
        stack.translate(-centerX, -getY() - getHeight(), 0);
        stack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput elementOutput) {
        this.defaultButtonNarrationText(elementOutput);
    }


    @Override
    public void onPress() {
        onPress.onPress();
    }

}
