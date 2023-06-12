package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.widget.XrayButton;
import fr.atesab.xray.widget.XraySlider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class XrayLocationConfigBoxScreen extends XrayScreen {
    public XrayLocationConfigBoxScreen(Screen parent) {
        super(Text.translatable("x13.mod.location.hud"), parent);
    }

    private Text getLocationButtonText(LocationConfig cfg) {
        return Text.translatable("x13.mod.location.hud.corner")
                .append(": ")
                .append(cfg.getLocation().getTranslationText().styled(s -> s.withColor(Formatting.GOLD)));
    }

    @Override
    protected void init() {
        XrayConfig cfg = XrayMain.getMod().getConfig();

        XraySlider sliderShiftX = new XraySlider(width / 2 - 100, height / 2 - 24, 200, 20,
                Text.translatable("x13.mod.location.hud.shift.x"), cfg.getLocationConfig().getShiftX()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float percentage = cfg.getLocationConfig().getShiftX();
                MutableText shift = Text.translatable("x13.mod.location.hud.shift.x").append(": ");
                setMessage(shift.append(
                        Text.literal((int) (percentage * 100) + "%")
                                .styled(s -> s.withColor(Formatting.GOLD))
                ));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setShiftX((float) value);
            }

        };
        XraySlider sliderShiftY = new XraySlider(width / 2 - 100, height / 2, 200, 20,
                Text.translatable("x13.mod.location.hud.shift.y"), cfg.getLocationConfig().getShiftX()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float percentage = cfg.getLocationConfig().getShiftY();
                MutableText shift = Text.translatable("x13.mod.location.hud.shift.y").append(": ");
                setMessage(shift.append(
                        Text.literal((int) (percentage * 100) + "%")
                                .styled(s -> s.withColor(Formatting.GOLD))
                ));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setShiftY((float) value);
            }

        };

        ButtonWidget locationButton = XrayButton.builder(getLocationButtonText(cfg.getLocationConfig()), btn -> {
                    cfg.getLocationConfig().setLocation(cfg.getLocationConfig().getLocation().next());
                    sliderShiftX.setValue(cfg.getLocationConfig().getShiftX());
                    sliderShiftY.setValue(cfg.getLocationConfig().getShiftY());
                    btn.setMessage(getLocationButtonText(cfg.getLocationConfig()));
                })
                .dimensions(width / 2 - 100, height / 2 - 48, 200, 20)
                .build();

        addDrawableChild(sliderShiftX);
        addDrawableChild(sliderShiftY);
        addDrawableChild(locationButton);

        XraySlider fontSizeSlider = new XraySlider(width / 2 - 100, height / 2 + 24, 200, 20,
                Text.translatable("x13.mod.location.hud.fontSize"), cfg.getLocationConfig().getFontSizeMultiplierNormalized()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                float size = cfg.getLocationConfig().getFontSizeMultiplier();
                setMessage(Text.translatable("x13.mod.location.hud.fontSize").append(": ")
                        .append(Text.literal(String.format("%.1f", size)).styled(s -> s.withColor(Formatting.GOLD))));
            }

            @Override
            protected void applyValue() {
                cfg.getLocationConfig().setFontSizeMultiplierNormalized((float) value);
            }

        };
        addDrawableChild(fontSizeSlider);

        addDrawableChild(
                XrayButton.builder(
                        Text.translatable("x13.mod.location.reset"),
                        btn -> {
                            cfg.getLocationConfig().setLocation(LocationConfig.LocationLocation.TOP_LEFT);
                            cfg.getLocationConfig().setFontSizeMultiplier(1);
                            // reset the messages/values of the components, this is important if someone adds
                            // a new value!!!
                            sliderShiftX.setValue(cfg.getLocationConfig().getShiftX());
                            sliderShiftY.setValue(cfg.getLocationConfig().getShiftY());
                            fontSizeSlider.setValue(cfg.getLocationConfig().getFontSizeMultiplierNormalized());
                            locationButton.setMessage(getLocationButtonText(cfg.getLocationConfig()));
                        }).dimensions(width / 2 - 100, height / 2 + 48, 200, 20).build());

        assert client != null;
        addDrawableChild(XrayButton.builder(Text.translatable("gui.done"), btn -> client.setScreen(parent))
                .dimensions(width / 2 - 100, height / 2 + 76, 200, 20).build());

        super.init();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
    }
}
