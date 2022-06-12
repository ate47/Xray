package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.XrayUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class XrayLocationConfig extends XrayScreen {

    private TextFieldWidget format;

    public XrayLocationConfig(Screen parent) {
        super(Text.translatable("x13.mod.showloc"), parent);
    }

    @Override
    protected void init() {
        XrayMain mod = XrayMain.getMod();
        addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 - 48, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(), "x13.mod.location"), b -> {
                    mod.getConfig().getLocationConfig().setEnabled(!mod.getConfig().getLocationConfig().isEnabled());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(),
                            "x13.mod.location"));
                }));

        addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 - 24, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(), "x13.mod.location.showmodes"),
                b -> {
                    mod.getConfig().getLocationConfig().setShowMode(!mod.getConfig().getLocationConfig().isShowMode());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(),
                            "x13.mod.location.showmodes"));
                }));

        format = new TextFieldWidget(textRenderer, width / 2 - 98, height / 2 + 2, 196, 16, Text.literal(""));
        format.setMaxLength(128);
        format.setText(mod.getConfig().getLocationConfig().getFormat());
        format.setChangedListener(mod.getConfig().getLocationConfig()::setFormat);
        addSelectableChild(format);
        setInitialFocus(format);

        addDrawableChild(
                new ButtonWidget(width / 2 - 100, height / 2 + 24, 98, 20,
                        Text.translatable("x13.mod.location.option"),
                        btn -> {
                            client.setScreen(new EnumSelector<>(
                                    Text.translatable("x13.mod.location.option"), this,
                                    LocationFormatTool.values()) {
                                @Override
                                protected void select(LocationFormatTool e) {
                                    format.write(e.getOption());
                                }

                            });
                        }));
        addDrawableChild(
                new ButtonWidget(width / 2 + 2, height / 2 + 24, 98, 20,
                        Text.translatable("x13.mod.location.reset"),
                        btn -> {
                            format.setText(LocationConfig.DEFAULT_FORMAT);
                        }));

        addDrawableChild(
                new ButtonWidget(width / 2 - 100, height / 2 + 52, 200, 20, Text.translatable("gui.done"),
                        btn -> {
                            client.setScreen(parent);
                        }));

        super.init();
    }

    @Override
    public void resize(MinecraftClient client, int w, int h) {
        String s = format.getText();
        super.resize(client, w, h);
        format.setText(s);
    }

    @Override
    public void tick() {
        format.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredText(stack, textRenderer, Text.translatable("x13.mod.location"), width / 2,
                height / 2 - 52 - textRenderer.fontHeight, 0xffffffff);
        GuiUtils.drawRightString(stack, textRenderer, I18n.translate("x13.mod.location.format") + ": ", format,
                0xffffffff);
        format.render(stack, mouseX, mouseY, delta);
        super.render(stack, mouseX, mouseY, delta);
    }
}