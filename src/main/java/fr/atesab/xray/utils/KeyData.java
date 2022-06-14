package fr.atesab.xray.utils;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.minecraft.text.Text;


public record KeyData(int keyCode, int keyScanCode) {
    public KeyData() {
        this(0, 0);
    }

    public Text getName() {
        return Text.literal("" + GLFW.glfwGetKeyName(keyCode(), keyScanCode()));
    }

    public static Text getName(Optional<KeyData> data) {
        if (!data.isPresent())
            return Text.translatable("x13.mod.selector.key.none");

        return data.get().getName();
    }
}
