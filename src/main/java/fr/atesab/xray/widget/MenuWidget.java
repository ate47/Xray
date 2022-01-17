package fr.atesab.xray.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

public class MenuWidget extends AbstractButton {
    public interface OnPress {
        void onPress();
    }

    private ItemStack itemStack;
    private OnPress onPress;

    public MenuWidget(int x, int y, int w, int h, Text text, ItemStack stack, OnPress onPress) {
        super(x, y, w, h, text);
        this.onPress = onPress;
        this.itemStack = stack;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean hovered = isHovered();
        int centerX = x + width / 2;
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        DrawableHelper.fill(stack, x, y, x + width, y + height, color);

        Text message = getMessage();
        TextRenderer font = client.font;
        ItemRenderer renderer = client.getItemRenderer();
        MatrixStack modelStack = RenderSystem.getModelViewStack();

        int stackCenterX = x + width / 2;
        int stackCenterY = y + height * 2 / 5;

        modelStack.translate(stackCenterX, stackCenterY, 0);
        float scaleX = getWidth() * 3 / 4f / 16f;
        float scaleY = getHeight() * 3 / 4f / 16f;
        modelStack.scale(scaleX, scaleY, 1);
        renderer.renderGuiItem(itemStack, -8, -8);
        modelStack.scale(1 / scaleX, 1 / scaleY, 1);
        modelStack.translate(-stackCenterX, -stackCenterY, 0);
        RenderSystem.applyModelViewMatrix();

        stack.pushPose();
        stack.translate(centerX, y + getHeight(), 0);
        float scale = getHeight() * 1 / 7f / font.fontHeight;
        stack.scale(scale, scale, 1);
        drawCenteredString(stack, font, message, 0, -font.fontHeight, packedFGColor);
        stack.scale(1 / scale, 1 / scale, 1);
        stack.translate(-centerX, -y - getHeight(), 0);
        stack.popPose();
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void onPress() {
        onPress.onPress();
    }

}
