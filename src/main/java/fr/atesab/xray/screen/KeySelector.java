package fr.atesab.xray.screen;

import java.util.OptionalInt;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class KeySelector extends XrayScreen {
    private static final TranslatableComponent NONE_KEY = new TranslatableComponent("x13.mod.selector.key.none");

    private Consumer<OptionalInt> keyConsumer;
    private OptionalInt value;
    private boolean isWaitingKey = false;

    private Button cancelButton;
    private Button keyButton;
    private Button doneButton;

    public KeySelector(Screen parent, int key, Consumer<OptionalInt> keyConsumer) {
        this(parent, OptionalInt.of(key), keyConsumer);
    }

    public KeySelector(Screen parent, Consumer<OptionalInt> keyConsumer) {
        this(parent, OptionalInt.empty(), keyConsumer);
    }

    public KeySelector(Screen parent, OptionalInt currentValue, Consumer<OptionalInt> keyConsumer) {
        super(new TranslatableComponent("x13.mod.selector.key.title"), parent);
        value = currentValue;
        this.keyConsumer = keyConsumer;
    }

    private void waitKey() {
        isWaitingKey = true;
        cancelButton.active = false;
        doneButton.active = false;
        keyButton.active = keyButton.visible = false;
    }

    private void setKey(OptionalInt value) {
        this.value = value;

        cancelButton.active = true;
        doneButton.active = true;
        keyButton.active = keyButton.visible = true;

        isWaitingKey = false;
    }

    @Override
    protected void init() {
        keyButton = addRenderableWidget(new Button(width / 2 - 100, height / 2 - 24, 200, 20,
                NONE_KEY, b -> waitKey()));
        doneButton = addRenderableWidget(new Button(width / 2 - 100, height / 2, 200, 20,
                new TranslatableComponent("gui.done"), b -> {
                    keyConsumer.accept(value);
                    minecraft.setScreen(parent);
                }));
        doneButton = addRenderableWidget(new Button(width / 2 - 100, height / 2 + 24, 200, 20,
                new TranslatableComponent("gui.cancel"), b -> {
                    minecraft.setScreen(parent);
                }));
        setKey(value);
        super.init();
    }

    @Override
    public boolean keyPressed(int key, int p_96553_, int p_96554_) {
        if (isWaitingKey) {
            if (key == 256) {
                setKey(OptionalInt.empty());
                return true;
            }

            setKey(OptionalInt.of(key));
            return true;
        }

        return super.keyPressed(key, p_96553_, p_96554_);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !isWaitingKey;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredString(stack, font, getTitle(), width / 2, height / 2 - 28 - font.lineHeight, 0xffffffff);

        if (isWaitingKey) {
            drawCenteredString(stack, font, new TranslatableComponent("x13.mod.selector.key.presskey"), width / 2,
                    keyButton.y + keyButton.getWidth() / 2 - font.lineHeight, 0xffffff00);
        }

        super.render(stack, mouseX, mouseY, delta);
    }

}
