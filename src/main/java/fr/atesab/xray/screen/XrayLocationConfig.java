package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.LocationConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.XrayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class XrayLocationConfig extends XrayScreen {

    private EditBox format;

    public XrayLocationConfig(Screen parent) {
        super(new TranslatableComponent("x13.mod.showloc"), parent);
    }

    @Override
    protected void init() {
        XrayMain mod = XrayMain.getMod();
        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 48, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(), "x13.mod.location"), b -> {
                    mod.getConfig().getLocationConfig().setEnabled(!mod.getConfig().getLocationConfig().isEnabled());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isEnabled(),
                            "x13.mod.location"));
                }));

        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 24, 200, 20,
                XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(), "x13.mod.location.showmodes"),
                b -> {
                    mod.getConfig().getLocationConfig().setShowMode(!mod.getConfig().getLocationConfig().isShowMode());
                    b.setMessage(XrayUtils.getToggleable(mod.getConfig().getLocationConfig().isShowMode(),
                            "x13.mod.location.showmodes"));
                }));

        format = new EditBox(font, width / 2 - 98, height / 2 + 2, 196, 16, new TextComponent(""));
        format.setMaxLength(256);
        format.setValue(mod.getConfig().getLocationConfig().getFormat());
        format.setResponder(mod.getConfig().getLocationConfig()::setFormat);
        format.setFocus(true);
        addWidget(format);
        setInitialFocus(format);

        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + 24, 98, 20,
                        new TranslatableComponent("x13.mod.location.option"),
                        btn -> {
                            minecraft.setScreen(new EnumSelector<>(
                                    new TranslatableComponent("x13.mod.location.option"), this,
                                    LocationFormatTool.values()) {
                                @Override
                                protected void select(LocationFormatTool e) {
                                    format.insertText(e.getOption());
                                }

                            });
                        }));
        addRenderableWidget(
                new Button(width / 2 + 2, height / 2 + 24, 98, 20,
                        new TranslatableComponent("x13.mod.location.reset"),
                        btn -> {
                            format.setValue(LocationConfig.DEFAULT_FORMAT);
                        }));

        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + 52, 200, 20, new TranslatableComponent("gui.done"),
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
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredString(stack, font, new TranslatableComponent("x13.mod.location"), width / 2,
                height / 2 - 52 - font.lineHeight, 0xffffffff);
        GuiUtils.drawRightString(stack, font, I18n.get("x13.mod.location.format") + ": ", format, 0xffffffff);
        format.render(stack, mouseX, mouseY, delta);
        super.render(stack, mouseX, mouseY, delta);
    }
    
    protected void save() {
    	// unimplemenets. save code is in XrayMenu.
    }
}