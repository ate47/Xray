package fr.atesab.xray.widget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import fr.atesab.xray.screen.ColorSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class ColorSelectorWidget extends PressableWidget {

    private IntConsumer setter;
    private IntSupplier getter;
    private MinecraftClient client;
    private Screen parent;

    public ColorSelectorWidget(int x, int y, int w, int h, Text text, MinecraftClient mc, Screen parent,
            IntConsumer setter, IntSupplier getter) {
        super(x, y, w, h, text);
        this.setter = setter;
        this.getter = getter;
        this.client = mc;
        this.parent = parent;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();
        int color = getter.getAsInt() & 0xFFFFFF;
        if (hovered) {
            color |= 0xaa000000;
        } else {
            color |= 0x88000000;
        }

        drawContext.fill(getX(), getY(), getX() + width, getY() + height, color);

        Text message = getMessage();
        TextRenderer textRenderer = client.textRenderer;
        drawContext.drawCenteredTextWithShadow(textRenderer, message, getX() + width / 2, getY() + height / 2 - textRenderer.fontHeight / 2,
                0xFFFFFFFF);
    }

    @Override
    public void onPress() {
        client.setScreen(new ColorSelector(parent, setter, getter.getAsInt()));
    }

}
