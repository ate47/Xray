package fr.atesab.xray.screen;

import java.util.Optional;
import java.util.function.Consumer;

import fr.atesab.xray.utils.KeyData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class KeySelector extends XrayScreen {
    private static final Text NONE_KEY = Text.translatable("x13.mod.selector.key.none");

    private Consumer<Optional<KeyData>> keyConsumer;
    private Optional<KeyData> value;
    private boolean isWaitingKey = false;

    private ButtonWidget cancelButton;
    private ButtonWidget keyButton;
    private ButtonWidget doneButton;

    public KeySelector(Screen parent, KeyData key, Consumer<Optional<KeyData>> keyConsumer) {
        this(parent, Optional.of(key), keyConsumer);
    }

    public KeySelector(Screen parent, Consumer<Optional<KeyData>> keyConsumer) {
        this(parent, Optional.empty(), keyConsumer);
    }

    public KeySelector(Screen parent, Optional<KeyData> currentValue, Consumer<Optional<KeyData>> keyConsumer) {
        super(Text.translatable("x13.mod.selector.key.title"), parent);
        value = currentValue;
        this.keyConsumer = keyConsumer;
    }

    private void waitKey() {
        isWaitingKey = true;
        cancelButton.active = false;
        doneButton.active = false;
        keyButton.active = keyButton.visible = false;
    }

    private void setKey(Optional<KeyData> value) {
        this.value = value;
        keyButton.setMessage(KeyData.getName(value));

        cancelButton.active = true;
        doneButton.active = true;
        keyButton.active = keyButton.visible = true;

        isWaitingKey = false;
    }

    @Override
    protected void init() {
        keyButton = addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 - 24, 200, 20,
                NONE_KEY, b -> waitKey()));
        doneButton = addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2, 200, 20,
                Text.translatable("gui.done"), b -> {
                    keyConsumer.accept(value);
                    client.setScreen(parent);
                }));
        cancelButton = addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 + 24, 200, 20,
                Text.translatable("gui.cancel"), b -> {
                    client.setScreen(parent);
                }));
        setKey(value);
        super.init();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifier) {
        if (isWaitingKey) {
            if (key == 256) {
                setKey(Optional.empty());
                return true;
            }

            setKey(Optional.of(new KeyData(key, scanCode)));
            return true;
        }

        return super.keyPressed(key, scanCode, modifier);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !isWaitingKey;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredText(stack, textRenderer, getTitle(), width / 2, height / 2 - 30 - textRenderer.fontHeight,
                0xffffffff);

        if (isWaitingKey) {
            drawCenteredText(stack, textRenderer, Text.translatable("x13.mod.selector.key.presskey"), width / 2,
                    keyButton.y + keyButton.getHeight() / 2 - textRenderer.fontHeight, 0xffffff00);
        }

        super.render(stack, mouseX, mouseY, delta);
    }

}
