package fr.atesab.xray.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class XrayScreen extends Screen {
    public Screen parent;

    protected XrayScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public void playDownSound() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
