package fr.atesab.xray.screen;

import java.util.stream.Stream;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.screen.page.PagedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class XrayBlockModesConfig extends PagedScreen<BlockConfig> {

    public XrayBlockModesConfig(Screen parent, Stream<BlockConfig> stream) {
        super(new TranslatableComponent("x13.mod.mode"), parent, 24, stream);
    }

    @Override
    protected void initElements(Stream<BlockConfig> stream) {
        // TODO Auto-generated method stub
    }
}
