package fr.atesab.xray.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class XrayScreen extends Screen {
    public Screen parent;

    protected XrayScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public void playDownSound() {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
