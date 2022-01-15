package fr.atesab.xray.screen;

import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.screen.page.AddPagedButton;
import fr.atesab.xray.screen.page.AddPagedElement;
import fr.atesab.xray.screen.page.PagedElement;
import fr.atesab.xray.screen.page.PagedScreen;
import fr.atesab.xray.screen.page.RemovePagedButton;
import fr.atesab.xray.utils.KeyData;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.EntityConfigWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class XrayESPModesConfig extends PagedScreen<ESPConfig> {
    private class PagedESPMode extends PagedElement<ESPConfig> {
        private ESPConfig cfg;
        private boolean textHover = false;
        private EntityConfigWidget entities;

        public PagedESPMode(ESPConfig cfg) {
            super(XrayESPModesConfig.this);
            this.cfg = cfg;
        }

        public PagedESPMode() {
            this(new ESPConfig());
        }

        @Override
        public void init() {
            int x = width / 2 - 125;
            entities = addSubWidget(new EntityConfigWidget(x, 0, 115, 20, cfg, XrayESPModesConfig.this));
            x += 119;
            addSubWidget(new Button(x, 0, 56, 20, KeyData.getName(cfg.getKey()), btn -> {
                minecraft.setScreen(new KeySelector(XrayESPModesConfig.this, cfg.getKey(), oKey -> {
                    cfg.setKey(oKey);
                    btn.setMessage(KeyData.getName(cfg.getKey()));
                }));
            }));
            x += 60;
            addSubWidget(
                    new Button(x, 0, 74, 20, XrayUtils.getToggleable(cfg.hasTracer(), "x13.mod.esp.tracer"), btn -> {
                        cfg.setTracer(!cfg.hasTracer());
                        btn.setMessage(XrayUtils.getToggleable(cfg.hasTracer(), "x13.mod.esp.tracer"));
                    }));
            x += 78;
            addSubWidget(new Button(x, 0, 20, 20, new TranslatableComponent("x13.mod.template.little"), btn -> {
                minecraft.setScreen(new EnumSelector<ESPConfig.Template>(
                        new TranslatableComponent("x13.mod.template"), XrayESPModesConfig.this,
                        ESPConfig.Template.values()) {

                    @Override
                    protected void select(ESPConfig.Template template) {
                        String oldName = cfg.getModeName();
                        int color = cfg.getColor();
                        Optional<KeyData> key = cfg.getKey();
                        template.cloneInto(cfg);
                        cfg.setName(oldName);
                        cfg.setColor(color);
                        cfg.setKey(key);
                    }

                });
            }));
            x += 24;

            addSubWidget(new AddPagedButton<>(XrayESPModesConfig.this,
                    x, 0, 20, 20, PagedESPMode::new));
            x += 24;
            addSubWidget(new RemovePagedButton(XrayESPModesConfig.this,
                    x, 0, 20, 20));
            super.init();
        }

        @Override
        public void updateDelta(int delta, int index) {
            entities.setDeltaY(delta);
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            textHover = XrayUtils.isHover(mouseX, mouseY, width / 2 - 200, 0, width / 2 - 125 - 4, 20);
            fill(stack, width / 2 - 200, 0, width / 2 - 125 - 4, 20, textHover ? 0x33ffaa00 : 0x33ffffff);
            int w = font.width(cfg.getModeName());
            font.draw(stack, cfg.getModeName(), width / 2 - (200 - 125 - 4) / 2 - 125 - 4 - w / 2,
                    10 - font.lineHeight / 2,
                    cfg.getColor());
            super.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (textHover) {
                playDownSound();
                minecraft.setScreen(new XrayAbstractModeConfig(XrayESPModesConfig.this, cfg));
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public ESPConfig save() {
            return cfg;
        }
    }

    public XrayESPModesConfig(Screen parent, Stream<ESPConfig> stream) {
        super(new TranslatableComponent("x13.mod.esp"), parent, 24, stream);
    }

    @Override
    protected void initElements(Stream<ESPConfig> stream) {
        stream.map(PagedESPMode::new).forEach(this::addElement);
        addElement(new AddPagedElement<>(this, PagedESPMode::new));
    }

}
