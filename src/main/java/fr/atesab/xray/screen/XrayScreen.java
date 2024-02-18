package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderBackground(GuiGraphics graphics, int p_299421_, int p_298679_, float p_297268_) {
        if (minecraft.level != null) {
            super.renderBackground(graphics, p_299421_, p_298679_, p_297268_);
        } else {
            Skin skin = XrayMain.getMod().getConfig().getSkin();
            Integer bg = skin.getBackgroundColor();
            if (bg != null) {
                GuiUtils.drawRect(graphics, 0, 0, width, height, bg);
            } else {
                super.renderBackground(graphics, p_299421_, p_298679_, p_297268_);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
