package fr.atesab.xray.widget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import fr.atesab.xray.screen.ColorSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
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
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.method_37021(builder);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();
        int color = getter.getAsInt() & 0xFFFFFF;
        if (hovered) {
            color |= 0xaa000000;
        } else {
            color |= 0x88000000;
        }

        DrawableHelper.fill(stack, x, y, x + width, y + height, color);

        Text message = getMessage();
        TextRenderer textRenderer = client.textRenderer;
        drawCenteredText(stack, textRenderer, message, x + width / 2, y + height / 2 - textRenderer.fontHeight / 2,
                0xFFFFFFFF);
    }

    @Override
    public void onPress() {
        client.openScreen(new ColorSelector(parent, setter, getter.getAsInt()));
    }

}
