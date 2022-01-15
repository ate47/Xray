package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.config.AbstractModeConfig;
import fr.atesab.xray.widget.ColorSelectorWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class XrayAbstractModeConfig extends XrayScreen {
    private AbstractModeConfig cfg;
    private EditBox nameBox;
    private int color;

    protected XrayAbstractModeConfig(Screen parent, AbstractModeConfig cfg) {
        super(new TranslatableComponent("x13.mod.mode.edit"), parent);
        this.cfg = cfg;
        this.color = cfg.getColor();
    }

    @Override
    public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
        String s = nameBox.getValue();
        super.resize(p_96575_, p_96576_, p_96577_);
        nameBox.setValue(s);
    }

    @Override
    protected void init() {
        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + 24, 200, 20, new TranslatableComponent("gui.done"), btn -> {
                    cfg.setName(nameBox.getValue());
                    cfg.setColor(color);
                    minecraft.setScreen(parent);
                }));
        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + 48, 200, 20, new TranslatableComponent("gui.cancel"), btn -> {
                    minecraft.setScreen(parent);
                }));

        nameBox = new EditBox(font, width / 2 - 98, height / 2 - 22, 196, 16, new TextComponent(""));
        nameBox.setMaxLength(128);
        nameBox.setValue(cfg.getModeName());
        nameBox.setFocus(true);

        addRenderableWidget(new ColorSelectorWidget(width / 2 - 100, height / 2, 200, 20,
                new TranslatableComponent("x13.mod.color.title"), minecraft, this, c -> color = c, () -> color));

        addWidget(nameBox);
        setInitialFocus(nameBox);

        super.init();
    }

    @Override
    public void tick() {
        nameBox.tick();
        super.tick();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        nameBox.render(stack, mouseX, mouseY, delta);
        super.render(stack, mouseX, mouseY, delta);
    }
}
