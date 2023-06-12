package fr.atesab.xray.screen;

import fr.atesab.xray.config.AbstractModeConfig;
import fr.atesab.xray.widget.ColorSelectorWidget;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class XrayAbstractModeConfig extends XrayScreen {
    private AbstractModeConfig cfg;
    private TextFieldWidget nameBox;
    private int color;

    protected XrayAbstractModeConfig(Screen parent, AbstractModeConfig cfg) {
        super(Text.translatable("x13.mod.mode.edit"), parent);
        this.cfg = cfg;
        this.color = cfg.getColor();
    }

    @Override
    public void resize(MinecraftClient p_96575_, int p_96576_, int p_96577_) {
        String s = nameBox.getText();
        super.resize(p_96575_, p_96576_, p_96577_);
        nameBox.setText(s);
    }

    @Override
    protected void init() {
        addDrawableChild(
                XrayButton.builder(Text.translatable("gui.done"), btn -> {
                    cfg.setName(nameBox.getText());
                    cfg.setColor(color);
                    client.setScreen(parent);
                }).dimensions(width / 2 - 100, height / 2 + 24, 200, 20).build());
        addDrawableChild(
                XrayButton.builder(Text.translatable("gui.cancel"), btn -> client.setScreen(parent)).dimensions(width / 2 - 100, height / 2 + 48, 200, 20).build());

        nameBox = new TextFieldWidget(textRenderer, width / 2 - 98, height / 2 - 22, 196, 16, Text.literal(""));
        nameBox.setMaxLength(128);
        nameBox.setText(cfg.getModeName());

        addDrawableChild(new ColorSelectorWidget(width / 2 - 100, height / 2, 200, 20,
                Text.translatable("x13.mod.color.title"), client, this, c -> color = c, () -> color));

        addSelectableChild(nameBox);
        setInitialFocus(nameBox);

        super.init();
    }

    @Override
    public void tick() {
        nameBox.tick();
        super.tick();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        nameBox.render(drawContext, mouseX, mouseY, delta);
        super.render(drawContext, mouseX, mouseY, delta);
    }
}
