package fr.atesab.xray.widget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.screen.ColorSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorSelectorWidget extends AbstractButton {

    private IntConsumer setter;
    private IntSupplier getter;
    private Minecraft minecraft;
    private Screen parent;

    public ColorSelectorWidget(int x, int y, int w, int h, Component text, Minecraft mc, Screen parent,
            IntConsumer setter, IntSupplier getter) {
        super(x, y, w, h, text);
        this.setter = setter;
        this.getter = getter;
        this.minecraft = mc;
        this.parent = parent;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        boolean hovered = isHoveredOrFocused();
        int color = getter.getAsInt() & 0xFFFFFF;
        if (hovered) {
            color |= 0xaa000000;
        } else {
            color |= 0x88000000;
        }

        Gui.fill(stack, x, y, x + width, y + height, color);

        Component message = getMessage();
        Font font = minecraft.font;
        drawCenteredString(stack, font, message, x + width / 2, y + height / 2 - font.lineHeight / 2, 0xFFFFFFFF);
    }

    @Override
    public void onPress() {
        minecraft.setScreen(new ColorSelector(parent, setter, getter.getAsInt()));
    }
}
