package fr.atesab.xray.screen;

import java.util.stream.Stream;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.screen.page.AddPagedButton;
import fr.atesab.xray.screen.page.AddPagedElement;
import fr.atesab.xray.screen.page.PagedElement;
import fr.atesab.xray.screen.page.PagedScreen;
import fr.atesab.xray.screen.page.RemovePagedButton;
import fr.atesab.xray.utils.KeyData;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.BlockConfigWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class XrayBlockModesConfig extends PagedScreen<BlockConfig> {
    private class PagedBlockMode extends PagedElement<BlockConfig> {
        private BlockConfig cfg;

        public PagedBlockMode(BlockConfig cfg) {
            super(XrayBlockModesConfig.this);
            this.cfg = cfg;
        }

        public PagedBlockMode() {
            this(new BlockConfig());
        }

        @Override
        public void init(int deltaY) {
            int x = width / 2 - 200 + 24;
            addWidget(new BlockConfigWidget(x, 0, 200, 20, cfg, XrayBlockModesConfig.this));
            x += 204;
            addWidget(new Button(x, 0, 80, 20, KeyData.getName(cfg.getKey()), btn -> {
                minecraft.setScreen(new KeySelector(XrayBlockModesConfig.this, cfg.getKey(), oKey -> {
                    cfg.setKey(oKey);
                    btn.setMessage(KeyData.getName(cfg.getKey()));
                }));
            }));
            x += 84;
            // TODO: exl/inc button
            x += 40;
            addWidget(new AddPagedButton<>(XrayBlockModesConfig.this,
                    x, 0, 20, 20, PagedBlockMode::new));
            x += 24;
            addWidget(new RemovePagedButton(XrayBlockModesConfig.this,
                    x, 0, 20, 20));
            super.init(deltaY);
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            fill(stack, width / 2 - 200, 0, width / 2 - 180, 20, cfg.getColor());
            super.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (XrayUtils.isHover(mouseX, mouseY, width / 2 - 200, 0, width / 2 - 180, 20)) {
                playDownSound();
                // TODO: Set color
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public BlockConfig save() {
            return cfg;
        }
    }

    public XrayBlockModesConfig(Screen parent, Stream<BlockConfig> stream) {
        super(new TranslatableComponent("x13.mod.mode"), parent, 24, stream);
    }

    @Override
    protected void initElements(Stream<BlockConfig> stream) {
        stream.map(PagedBlockMode::new).forEach(this::addElement);
        addElement(new AddPagedElement<>(this, PagedBlockMode::new));
    }
}
