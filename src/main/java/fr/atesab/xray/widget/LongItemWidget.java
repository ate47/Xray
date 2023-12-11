package fr.atesab.xray.widget;

import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.widget.MenuWidget.OnPress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class LongItemWidget extends PressableWidget {

    private final ItemStack itemStack;
    private final OnPress oPress;
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
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean hovered = isHovered();
        int color;
        if (hovered) {
            color = 0x33ffffff;
        } else {
            color = 0x22ffffff;
        }

        context.fill(getX(), getY(), getX() + width, getY() + height, color);

        Text message = getMessage();
        TextRenderer textRenderer = client.textRenderer;

        int deltaH = (getHeight() - 16);

        GuiUtils.renderItemIdentity(context, itemStack, getX() + deltaH / 2 + deltaX, getY() + deltaH / 2 + deltaY);
        int textColor = this.active ? 16777215 : 10526880;
        context.drawText(textRenderer,
                message, getX() + deltaH + 16 + 2, getY() + (getHeight() - textRenderer.fontHeight) / 2,
                textColor, false);
    }

    @Override
    public void onPress() {
        oPress.onPress();
    }
}
