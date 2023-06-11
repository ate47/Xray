package fr.atesab.xray.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class MenuWidget extends PressableWidget {
    public interface OnPress {
        void onPress();
    }

    private final ItemStack itemStack;
    private final OnPress onPress;

    public MenuWidget(int x, int y, int w, int h, Text text, ItemStack stack, OnPress onPress) {
        super(x, y, w, h, text);
        this.onPress = onPress;
        this.itemStack = stack;
    }

    @Override
    public void renderButton(DrawContext graphics, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean hovered = isHovered();
        int centerX = getX() + width / 2;
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);

        Text message = getMessage();
        TextRenderer textRenderer = client.textRenderer;
        ItemRenderer renderer = client.getItemRenderer();
        MatrixStack modelStack = RenderSystem.getModelViewStack();
        modelStack.push();

        int stackCenterX = getX() + width / 2;
        int stackCenterY = getY() + height * 2 / 5;
        modelStack.translate(stackCenterX, stackCenterY, 0);
        float scaleX = getWidth() * 3 / 4f / 16f;
        float scaleY = getHeight() * 3 / 4f / 16f;
        modelStack.scale(scaleX, scaleY, 1);
        RenderSystem.applyModelViewMatrix();
        GuiUtils.renderItemIdentity(graphics, itemStack, -8, -8);
        modelStack.pop();
        RenderSystem.applyModelViewMatrix();

        MatrixStack stack = graphics.getMatrices();
        stack.push();
        stack.translate(centerX, getY() + getHeight(), 0);
        float scale = getHeight() / 7f / textRenderer.fontHeight;
        stack.scale(scale, scale, 1);
        int textColor = this.active ? 16777215 : 10526880;
        graphics.drawCenteredTextWithShadow(textRenderer, message, 0, -textRenderer.fontHeight, textColor);
        stack.scale(1 / scale, 1 / scale, 1);
        stack.translate(-centerX, -getY() - getHeight(), 0);
        stack.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public void onPress() {
        onPress.onPress();
    }

}
