package fr.atesab.xray.utils;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.minecraft.network.chat.Component;

public record KeyData(int keyCode, int keyScanCode) {
    public KeyData() {
        this(0, 0);
    }

    public Component getName() {
        return Component.literal("" + GLFW.glfwGetKeyName(keyCode(), keyScanCode()));
    }

    public static Component getName(Optional<KeyData> data) {
        if (!data.isPresent())
            return Component.translatable("x13.mod.selector.key.none");

        return data.get().getName();
    }
}
