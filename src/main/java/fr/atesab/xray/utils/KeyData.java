package fr.atesab.xray.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;


public record KeyData(int keyCode, int keyScanCode, boolean alt, boolean ctrl, boolean shift) {
    public KeyData() {
        this(0, 0, false, false, false);
    }

    public Text getName() {
        MutableText text = Text.literal("" + GLFW.glfwGetKeyName(keyCode(), keyScanCode()));
        if (shift()) {
            text = Text.literal("SHIFT + ").append(text);
        }
        if (alt()) {
            text = Text.literal("ALT + ").append(text);
        }
        if (ctrl()) {
            text = Text.literal("CTRL + ").append(text);
        }
        return text;
    }

    public static Text getName(Optional<KeyData> data) {
        return data.map(KeyData::getName).orElseGet(() -> Text.translatable("x13.mod.selector.key.none"));
    }
}
