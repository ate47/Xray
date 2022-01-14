package fr.atesab.xray.utils;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public record KeyData(int keyCode, int keyScanCode) {
    public KeyData() {
        this(0, 0);
    }

    public Component getName() {
        return new TextComponent("" + GLFW.glfwGetKeyName(keyCode(), keyScanCode()));
    }

    public static Component getName(Optional<KeyData> data) {
        if (!data.isPresent())
            return new TranslatableComponent("x13.mod.selector.key.none");

        return data.get().getName();
    }
}
