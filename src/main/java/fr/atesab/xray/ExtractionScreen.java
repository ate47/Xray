package fr.atesab.xray;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ExtractionScreen extends Screen {
    private Screen parent;
    private TextFieldWidget data;
    private XrayMode mode;

    public ExtractionScreen(Screen parent, XrayMode mode) {
        super(new TranslatableText("x13.mod.menu.extract"));
        this.parent = parent;
        this.mode = mode;
    }

    @Override
    protected void init() {
        data = new TextFieldWidget(textRenderer, width / 2 - 198, height / 2 - 22, 396, 16, new LiteralText(""));
        data.setMaxLength(Integer.MAX_VALUE);
        data.setText(XrayMain.getBlockNamesToString(mode.getBlocks()));

        addSelectableChild(data);
        addDrawableChild(new ButtonWidget(width / 2 - 200, height / 2, 198, 20, new TranslatableText("gui.done"), b -> {
            mode.setConfig(data.getText().split(" "));
            client.setScreen(parent);
        }));
        addDrawableChild(new ButtonWidget(width / 2 + 2, height / 2, 198, 20, new TranslatableText("gui.cancel"), b -> {
            client.setScreen(parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        String name = mode.getNameTranslate();
        textRenderer.draw(matrices, name, width / 2 - textRenderer.getWidth(name) / 2,
                height / 2 - 24 - 4 - textRenderer.fontHeight, 0xffffaa00);
        data.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
