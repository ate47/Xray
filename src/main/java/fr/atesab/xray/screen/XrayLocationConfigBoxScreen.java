package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.widget.SliderValueWidget;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class XrayLocationConfigBoxScreen extends XrayScreen {
    public XrayLocationConfigBoxScreen(Screen parent) {
        super(Component.translatable("x13.mod.location.hud"), parent);
    }

    private Component getLocationButtonText(LocationConfig cfg) {
        return Component.translatable("x13.mod.location.hud.corner")
                .append(": ")
                .append(cfg.getLocation().getTranslationText().withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        XrayConfig cfg = XrayMain.getMod().getConfig();

        SliderValueWidget sliderShiftX = new SliderValueWidget(width / 2 - 100, height / 2 - 24, 200, 20,
                Component.translatable("x13.mod.location.hud.shift.x"), cfg.getLocationConfig().getShiftX()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float percentage = cfg.getLocationConfig().getShiftX();
                MutableComponent shift = Component.translatable("x13.mod.location.hud.shift.x").append(": ");
                setMessage(shift.append(
                        Component.literal((int) (percentage * 100) + "%")
                                .withStyle(ChatFormatting.GOLD)
                ));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setShiftX((float) value);
            }

        };
        SliderValueWidget sliderShiftY = new SliderValueWidget(width / 2 - 100, height / 2, 200, 20,
                Component.translatable("x13.mod.location.hud.shift.y"), cfg.getLocationConfig().getShiftX()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float percentage = cfg.getLocationConfig().getShiftY();
                MutableComponent shift = Component.translatable("x13.mod.location.hud.shift.y").append(": ");
                setMessage(shift.append(
                        Component.literal((int) (percentage * 100) + "%")
                                .withStyle(ChatFormatting.GOLD)
                ));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setShiftY((float) value);
            }

        };

        XrayButton locationButton = new XrayButton(width / 2 - 100, height / 2 - 48, 200, 20,
                getLocationButtonText(cfg.getLocationConfig()), btn -> {
                    cfg.getLocationConfig().setLocation(cfg.getLocationConfig().getLocation().next());
                    sliderShiftX.setValue0(cfg.getLocationConfig().getShiftX());
                    sliderShiftY.setValue0(cfg.getLocationConfig().getShiftY());
                    btn.setMessage(getLocationButtonText(cfg.getLocationConfig()));
                });

        addRenderableWidget(sliderShiftX);
        addRenderableWidget(sliderShiftY);
        addRenderableWidget(locationButton);

        SliderValueWidget fontSizeSlider = new SliderValueWidget(width / 2 - 100, height / 2 + 24, 200, 20,
                Component.translatable("x13.mod.location.hud.fontSize"), cfg.getLocationConfig().getFontSizeMultiplierNormalized()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float size = cfg.getLocationConfig().getFontSizeMultiplier();
                setMessage(Component.translatable("x13.mod.location.hud.fontSize").append(": ")
                        .append(Component.literal(String.format("%.1f", size)).withStyle(ChatFormatting.GOLD)));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setFontSizeMultiplierNormalized((float) value);
            }

        };
        addRenderableWidget(fontSizeSlider);

        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 48, 200, 20,
                        Component.translatable("x13.mod.location.reset"),
                        btn -> {
                            cfg.getLocationConfig().setLocation(LocationConfig.LocationLocation.TOP_LEFT);
                            cfg.getLocationConfig().setFontSizeMultiplier(1);
                            // reset the messages/values of the components, this is important if someone adds
                            // a new value!!!
                            sliderShiftX.setValue0(cfg.getLocationConfig().getShiftX());
                            sliderShiftY.setValue0(cfg.getLocationConfig().getShiftY());
                            fontSizeSlider.setValue0(cfg.getLocationConfig().getFontSizeMultiplierNormalized());
                            locationButton.setMessage(getLocationButtonText(cfg.getLocationConfig()));
                        }));

        assert minecraft != null;
        addRenderableWidget(new XrayButton(width / 2 - 100, height / 2 + 76, 200, 20,
                Component.translatable("gui.done"), btn -> minecraft.setScreen(parent)));

        super.init();
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
