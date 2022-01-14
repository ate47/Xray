package fr.atesab.xray.screen;

import java.util.stream.Stream;

import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.screen.page.PagedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class XrayESPModesConfig extends PagedScreen<ESPConfig> {

    public XrayESPModesConfig(Screen parent, Stream<ESPConfig> stream) {
        super(new TranslatableComponent("x13.mod.esp"), parent, 24, stream);
    }

    @Override
    protected void initElements(Stream<ESPConfig> stream) {
        // TODO Auto-generated method stub
    }

}
