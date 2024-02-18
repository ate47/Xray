package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class XrayLocationConfig extends XrayScreen {

    private EditBox format;
    private int position;

    public XrayLocationConfig(Screen parent) {
        super(Component.translatable("x13.mod.showloc"), parent);
    }

    @Override
    protected void init() {
        XrayMain mod = XrayMain.getMod();
        addRenderableWidget(new XrayButton(width / 2 - 100, height / 2 - 48, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(), "x13.mod.location"), b -> {
            mod.getConfig().getLocationConfig().setEnabled(!mod.getConfig().getLocationConfig().isEnabled());
            b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(),
                    "x13.mod.location"));
        }));

        addRenderableWidget(new XrayButton(width / 2 - 100, height / 2 - 24, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(), "x13.mod.location.showmodes"),
                b -> {
                    mod.getConfig().getLocationConfig().setShowMode(!mod.getConfig().getLocationConfig().isShowMode());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(),
                            "x13.mod.location.showmodes"));
                }));

        format = new EditBox(font, width / 2 - 98, height / 2 + 2, 196, 16, Component.literal(""));
        format.setMaxLength(1024);
        format.setValue(mod.getConfig().getLocationConfig().getFormat());
        format.setResponder(mod.getConfig().getLocationConfig()::setFormat);
        format.setFocused(true);
        if (position != 0) {
            format.setCursorPosition(position);
            position = 0;
        }

        addWidget(format);
        setInitialFocus(format);

        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 24, 98, 20,
                        Component.translatable("x13.mod.location.option"),
                        btn -> {
                            minecraft.setScreen(new EnumSelector<>(
                                    Component.translatable("x13.mod.location.option"), this,
                                    LocationFormatTool.values()) {
                                @Override
                                protected void select(LocationFormatTool e) {
                                    format.insertText(e.getOption());
                                    // store the position for the screen switch
                                    position = format.getCursorPosition();
                                }

                            });
                        }));
        addRenderableWidget(
                new XrayButton(width / 2 + 2, height / 2 + 24, 98, 20,
                        Component.translatable("x13.mod.location.reset"),
                        btn -> {
                            format.setValue(LocationConfig.DEFAULT_FORMAT);
                        }));

        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 48, 200, 20,
                        Component.translatable("x13.mod.location.hud"),
                        btn -> {
                            minecraft.setScreen(new XrayLocationConfigBoxScreen(this));
                        }));

        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 76, 200, 20, Component.translatable("gui.done"),
                        btn -> {
                            save();
                            minecraft.setScreen(parent);
                        }));

        super.init();
    }

    @Override
    public void resize(Minecraft client, int w, int h) {
        String s = format.getValue();
        super.resize(client, w, h);
        format.setValue(s);
    }

    @Override
    public void tick() {
        format.tick();
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, Component.translatable("x13.mod.location"), width / 2,
                height / 2 - 52 - font.lineHeight, 0xffffffff);
        GuiUtils.drawRightString(graphics, font, I18n.get("x13.mod.location.format") + ": ", format, 0xffffffff);
        format.render(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
    }

    protected void save() {
        // unimplemenets. save code is in XrayMenu.
    }
}