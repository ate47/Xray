package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class XrayBlockModesConfig extends XrayScreen {

    public XrayBlockModesConfig(Screen parent) {
        super(new TranslatableComponent("x13.mod.modes"), parent);
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        addRenderableWidget(
                new Button(width / 2 - 100, height - 24, 200, 20, new TranslatableComponent("gui.done"),
                        btn -> {
                            minecraft.setScreen(parent);
                        }));
        super.init();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        // TODO Auto-generated method stub
        super.render(stack, mouseX, mouseY, delta);
    }
}
