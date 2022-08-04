package fr.atesab.xray.utils;

import java.util.Optional;

import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;

import net.minecraft.network.chat.Component;

public record KeyData(int keyCode, int keyScanCode, boolean alt, boolean ctrl, boolean shift) {
    public KeyData() {
        this(0, 0, false, false, false);
    }

    public Component getName() {
        MutableComponent text = Component.literal("" + GLFW.glfwGetKeyName(keyCode(), keyScanCode()));
        if (shift()) {
            text = Component.literal("SHIFT + ").append(text);
        }
        if (alt()) {
            text = Component.literal("ALT + ").append(text);
        }
        if (ctrl()) {
            text = Component.literal("CTRL + ").append(text);
        }
        return text;
    }

    public static Component getName(Optional<KeyData> data) {
        if (!data.isPresent())
            return Component.translatable("x13.mod.selector.key.none");

        return data.get().getName();
    }
}
