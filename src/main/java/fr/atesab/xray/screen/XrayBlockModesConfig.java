package fr.atesab.xray.screen;

import java.util.Optional;
import java.util.stream.Stream;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.screen.page.AddPagedButton;
import fr.atesab.xray.screen.page.AddPagedElement;
import fr.atesab.xray.screen.page.PagedElement;
import fr.atesab.xray.screen.page.PagedScreen;
import fr.atesab.xray.screen.page.RemovePagedButton;
import fr.atesab.xray.utils.KeyData;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.view.ViewMode;
import fr.atesab.xray.widget.BlockConfigWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public abstract class XrayBlockModesConfig extends PagedScreen<BlockConfig> {
    private class PagedBlockMode extends PagedElement<BlockConfig> {
        private BlockConfig cfg;
        private boolean textHover = false;
        private BlockConfigWidget blocks;

        public PagedBlockMode(BlockConfig cfg) {
            super(XrayBlockModesConfig.this);
            this.cfg = cfg;
        }

        public PagedBlockMode() {
            this(new BlockConfig());
        }

        @Override
        public void init() {
            int x = width / 2 - 125;
            blocks = addSubWidget(new BlockConfigWidget(x, 0, 125, 20, cfg, XrayBlockModesConfig.this));
            x += 129;
            addSubWidget(new ButtonWidget(x, 0, 56, 20, KeyData.getName(cfg.getKey()), btn -> {
                client.setScreen(new KeySelector(XrayBlockModesConfig.this, cfg.getKey(), oKey -> {
                    cfg.setKey(oKey);
                    btn.setMessage(KeyData.getName(cfg.getKey()));
                }));
            }));
            x += 60;
            addSubWidget(new ButtonWidget(x, 0, 64, 20, cfg.getViewMode().getTitle(), btn -> {
                client.setScreen(new EnumSelector<ViewMode>(
                        Text.translatable("x13.mod.mode.view.title"), getParentScreen(), ViewMode.values()) {

                    @Override
                    protected void select(ViewMode element) {
                        cfg.setViewMode(element);
                        btn.setMessage(cfg.getViewMode().getTitle());
                    }

                });
            }));
            x += 68;
            addSubWidget(new ButtonWidget(x, 0, 20, 20, Text.translatable("x13.mod.template.little"), btn -> {
                client.setScreen(new EnumSelector<BlockConfig.Template>(
                        Text.translatable("x13.mod.template"), XrayBlockModesConfig.this,
                        BlockConfig.Template.values()) {

                    @Override
                    protected void select(BlockConfig.Template template) {
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

            addSubWidget(new AddPagedButton<>(XrayBlockModesConfig.this,
                    x, 0, 20, 20, PagedBlockMode::new));
            x += 24;
            addSubWidget(new RemovePagedButton(XrayBlockModesConfig.this,
                    x, 0, 20, 20));
            super.init();
        }

        @Override
        public void updateDelta(int delta, int index) {
            blocks.setDeltaY(delta);
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
            textHover = XrayUtils.isHover(mouseX, mouseY, width / 2 - 200, 0, width / 2 - 125 - 4, 20);
            fill(stack, width / 2 - 200, 0, width / 2 - 125 - 4, 20, textHover ? 0x33ffaa00 : 0x33ffffff);
            int w = textRenderer.getWidth(cfg.getModeName());
            textRenderer.draw(stack, cfg.getModeName(), width / 2 - (200 - 125 - 4) / 2 - 125 - 4 - w / 2,
                    10 - textRenderer.fontHeight / 2,
                    cfg.getColor());
            super.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (textHover) {
                playDownSound();
                client.setScreen(new XrayAbstractModeConfig(XrayBlockModesConfig.this, cfg));
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public BlockConfig save() {
            return cfg;
        }
    }

    public XrayBlockModesConfig(Screen parent, Stream<BlockConfig> stream) {
        super(Text.translatable("x13.mod.mode"), parent, 24, stream);
    }

    @Override
    protected void initElements(Stream<BlockConfig> stream) {
        stream.map(PagedBlockMode::new).forEach(this::addElement);
        addElement(new AddPagedElement<>(this, PagedBlockMode::new));
    }
}
