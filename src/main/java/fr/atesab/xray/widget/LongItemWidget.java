package fr.atesab.xray.widget;

import fr.atesab.xray.widget.MenuWidget.OnPress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class LongItemWidget extends PressableWidget {

    private ItemStack itemStack;
    private OnPress oPress;
    private int deltaX;
    private int deltaY;

    public LongItemWidget(int x, int y, int w, int h, Text text, ItemStack stack, OnPress oPress) {
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean hovered = isHovered();
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        DrawableHelper.fill(stack, getX(), getY(), getX() + width, getY() + height, color);

        Text message = getMessage();
        TextRenderer textRenderer = client.textRenderer;
        ItemRenderer renderer = client.getItemRenderer();

        int deltaH = (getHeight() - 16);

        renderer.renderGuiItemIcon(itemStack, getX() + deltaH / 2 + deltaX, getY() + deltaH / 2 + deltaY);
        int textColor = this.active ? 16777215 : 10526880;
        textRenderer.draw(stack,
                message, getX() + deltaH + 16 + 2, getY() + getHeight() / 2f - textRenderer.fontHeight / 2f,
                textColor);
    }

    @Override
    public void onPress() {
        oPress.onPress();
    }
}
