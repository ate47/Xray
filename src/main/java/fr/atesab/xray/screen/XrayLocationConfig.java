package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class XrayLocationConfig extends XrayScreen {

    private TextFieldWidget format;
    private int position;

    public XrayLocationConfig(Screen parent) {
        super(Text.translatable("x13.mod.showloc"), parent);
    }

    @Override
    protected void init() {
        XrayMain mod = XrayMain.getMod();
        addDrawableChild(XrayButton.builder(
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(), "x13.mod.location"), b -> {
            mod.getConfig().getLocationConfig().setEnabled(!mod.getConfig().getLocationConfig().isEnabled());
            b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(),
                    "x13.mod.location"));
        }).dimensions(width / 2 - 100, height / 2 - 48, 200, 20).build());

        addDrawableChild(XrayButton.builder(
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(), "x13.mod.location.showmodes"),
                b -> {
                    mod.getConfig().getLocationConfig().setShowMode(!mod.getConfig().getLocationConfig().isShowMode());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(),
                            "x13.mod.location.showmodes"));
                }).dimensions(width / 2 - 100, height / 2 - 24, 200, 20).build());

        format = new TextFieldWidget(textRenderer, width / 2 - 98, height / 2 + 2, 196, 16, Text.literal(""));
        format.setMaxLength(256);
        format.setText(mod.getConfig().getLocationConfig().getFormat());
        format.setChangedListener(mod.getConfig().getLocationConfig()::setFormat);
        if (position != 0) {
            format.setCursor(position, false);
            position = 0;
        }
        addSelectableChild(format);
        setInitialFocus(format);

        addDrawableChild(
                XrayButton.builder(
                        Text.translatable("x13.mod.location.option"),
                        btn -> client.setScreen(new EnumSelector<>(
                                Text.translatable("x13.mod.location.option"), this,
                                LocationFormatTool.values()) {
                            @Override
                            protected void select(LocationFormatTool e) {
                                format.write(e.getOption());
                                // store the position for the screen switch
                                position = format.getCursor();
                            }

                        })).dimensions(width / 2 - 100, height / 2 + 24, 98, 20).build());
        addDrawableChild(
                XrayButton.builder(
                        Text.translatable("x13.mod.location.reset"),
                        btn -> {
                            format.setText(LocationConfig.DEFAULT_FORMAT);
                        }).dimensions(width / 2 + 2, height / 2 + 24, 98, 20).build());

        addDrawableChild(
                XrayButton.builder(
                        Text.translatable("x13.mod.location.hud"),
                        btn -> {
                            client.setScreen(new XrayLocationConfigBoxScreen(this));
                        }).dimensions(width / 2 - 100, height / 2 + 48, 200, 20).build());

        addDrawableChild(
                XrayButton.builder(Text.translatable("gui.done"),
                        btn -> {
                            save();
                            client.setScreen(parent);
                        }).dimensions(width / 2 - 100, height / 2 + 76, 200, 20).build());

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
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("x13.mod.location"), width / 2,
                height / 2 - 52 - textRenderer.fontHeight, 0xffffffff);
        GuiUtils.drawRightString(context, textRenderer, I18n.translate("x13.mod.location.format") + ": ", format,
                0xffffffff);
        format.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    protected void save() {
        // unimplemenets. save code is in XrayMenu.
    }
}