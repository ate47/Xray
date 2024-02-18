package fr.atesab.xray.screen;

import fr.atesab.xray.config.AbstractModeConfig;
import fr.atesab.xray.widget.ColorSelectorWidget;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class XrayAbstractModeConfig extends XrayScreen {
    private final AbstractModeConfig cfg;
    private EditBox nameBox;
    private int color;

    protected XrayAbstractModeConfig(Screen parent, AbstractModeConfig cfg) {
        super(Component.translatable("x13.mod.mode.edit"), parent);
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
                new XrayButton(width / 2 - 100, height / 2 + 24, 200, 20, Component.translatable("gui.done"), btn -> {
                    cfg.setName(nameBox.getValue());
                    cfg.setColor(color);
                    minecraft.setScreen(parent);
                }));
        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 48, 200, 20, Component.translatable("gui.cancel"), btn -> {
                    minecraft.setScreen(parent);
                }));

        nameBox = new EditBox(font, width / 2 - 98, height / 2 - 22, 196, 16, Component.literal(""));
        nameBox.setMaxLength(128);
        nameBox.setValue(cfg.getModeName());
        nameBox.setFocused(true);

        addRenderableWidget(new ColorSelectorWidget(width / 2 - 100, height / 2, 200, 20,
                Component.translatable("x13.mod.color.title"), minecraft, this, c -> color = c, () -> color));

        addWidget(nameBox);
        setInitialFocus(nameBox);

        super.init();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        nameBox.render(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
    }
}
