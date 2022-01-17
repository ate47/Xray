package fr.atesab.xray.widget;

import net.minecraft.client.util.math.MatrixStack;

import fr.atesab.xray.widget.MenuWidget.OnPress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

public class LongItemWidget extends AbstractButton {

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
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
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

        DrawableHelper.fill(stack, x, y, x + width, y + height, color);

        Text message = getMessage();
        TextRenderer font = client.font;
        ItemRenderer renderer = client.getItemRenderer();

        int deltaH = (getHeight() - 16);

        renderer.renderGuiItem(itemStack, x + deltaH / 2 + deltaX, y + deltaH / 2 + deltaY);
        font.draw(stack,
                message, x + deltaH + 16 + 2, y + getHeight() / 2 - font.fontHeight / 2,
                packedFGColor);
    }

    @Override
    public void onPress() {
        oPress.onPress();
    }
}
