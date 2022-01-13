package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class XrayMenu extends Screen {
    private Screen parent;

    public XrayMenu(Screen parent) {
        super(new TranslatableComponent("x13.mod.config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        // TODO Auto-generated method stub
        super.render(stack, mouseX, mouseY, delta);
    }
}
