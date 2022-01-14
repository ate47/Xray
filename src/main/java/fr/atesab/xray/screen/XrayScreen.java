package fr.atesab.xray.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class XrayScreen extends Screen {
    public Screen parent;

    protected XrayScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

}
