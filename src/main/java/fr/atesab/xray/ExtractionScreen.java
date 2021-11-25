package fr.atesab.xray;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ExtractionScreen extends Screen {
    private Screen parent;
    private EditBox data;
    private XrayMode mode;

    public ExtractionScreen(Screen parent, XrayMode mode) {
        super(new TranslatableComponent("x13.mod.menu.extract"));
        this.parent = parent;
        this.mode = mode;
    }

    @Override
    protected void init() {
        data = new EditBox(font, width / 2 - 198, height / 2 - 22, 396, 16, new TextComponent(""));
        data.setMaxLength(Integer.MAX_VALUE);
        data.setMessage(new TextComponent(XrayMain.getBlockNamesToString(mode.getBlocks())));

        addWidget(data);
        addRenderableWidget(
                new Button(width / 2 - 200, height / 2, 198, 20, new TranslatableComponent("gui.done"), b -> {
                    mode.setConfig(data.getMessage().getString().split(" "));
                    getMinecraft().setScreen(parent);
                }));
        addRenderableWidget(
                new Button(width / 2 + 2, height / 2, 198, 20, new TranslatableComponent("gui.cancel"), b -> {
                    getMinecraft().setScreen(parent);
                }));
        super.init();
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        String name = mode.getNameTranslate();
        font.draw(matrices, name, width / 2 - font.width(name) / 2, height / 2 - 24 - 4 - font.lineHeight, 0xffffaa00);
        data.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
