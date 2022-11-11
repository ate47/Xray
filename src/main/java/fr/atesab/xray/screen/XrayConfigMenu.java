package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.LongItemWidget;
import fr.atesab.xray.widget.XraySlider;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.net.URL;

public class XrayConfigMenu extends XrayScreen {

    public XrayConfigMenu(Screen parent) {
        super(Text.translatable("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        XrayConfig cfg = XrayMain.getMod().getConfig();
        addDrawableChild(new XraySlider(width / 2 - 100, height / 2 - 48, 200, 20,
                Text.translatable("x13.mod.esp.maxdistance"), cfg.getMaxTracerRangeNormalized()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                int range = cfg.getMaxTracerRange();
                MutableText distance = Text.translatable("x13.mod.esp.maxdistance").append(": ");
                if (range == 0)
                    setMessage(distance.append(Text.translatable("x13.mod.esp.maxdistance.infinite").formatted(Formatting.YELLOW)));
                else
                    setMessage(distance
                            .append(Text.translatable("x13.mod.esp.maxdistance.block", String.valueOf(range)).formatted(Formatting.YELLOW)));
            }

            @Override
            protected void applyValue() {
                cfg.setMaxTracerRangeNormalized(value);
            }

        });
        addDrawableChild(XrayButton.builder(XrayUtils.getToggleable(!cfg.isDamageIndicatorDisabled(), "x13.mod.config.espDamage"), button -> {
            cfg.setDamageIndicatorDisabled(!cfg.isDamageIndicatorDisabled());
            button.setMessage(XrayUtils.getToggleable(!cfg.isDamageIndicatorDisabled(), "x13.mod.config.espDamage"));
        }).dimensions(width / 2 - 100, height / 2 - 24, 200, 20).build());

        addDrawableChild(new XraySlider(width / 2 - 100, height / 2, 200, 20,
                Text.translatable("x13.mod.config.espline"), cfg.getEspLineWidthNormalized()) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                float range = cfg.getEspLineWidth();
                setMessage(Text.translatable("x13.mod.config.espline").append(": ")
                        .append(Text.literal(String.format("%.1f", range)).formatted(Formatting.YELLOW)));
            }

            @Override
            protected void applyValue() {
                cfg.setEspLineWidthNormalized((float) value);
            }

        });

        addDrawableChild(XrayButton.builder(Text.translatable("x13.mod.config.skin").append(": ").append(Text.literal(cfg.getSkin().getTitle()).formatted(Formatting.YELLOW)), btn -> {
            Skin[] skins = Skin.values();
            cfg.setSkin(skins[(cfg.getSkin().ordinal() + 1) % skins.length]);
            btn.setMessage(Text.translatable("x13.mod.config.skin").append(": ").append(Text.literal(cfg.getSkin().getTitle()).formatted(Formatting.YELLOW)));
        }).dimensions(width / 2 - 100, height / 2 + 24, 200, 20).build());

        addDrawableChild(
                XrayButton.builder(Text.translatable("gui.done"),
                        btn -> {
                            client.setScreen(parent);
                        }).dimensions(width / 2 - 100, height / 2 + 52, 200, 20).build());

        addDrawableChild(XrayButton.builder(Text.translatable("gui.done"), btn -> client.setScreen(parent)).dimensions(width / 2 - 100, height / 2 + 52, 200, 20).build());

        addDrawableChild(new LongItemWidget(width * 0 / 3, height - 20, width / 3, 20,
                Text.translatable("x13.mod.link.mod"), new ItemStack(Blocks.GOLD_ORE), () -> {
            openLink(XrayMain.MOD_LINK);
        }));

        addDrawableChild(new LongItemWidget(width * 1 / 3, height - 20, width / 3, 20,
                Text.translatable("x13.mod.link.issue"), new ItemStack(Blocks.TNT), () -> {
            openLink(XrayMain.MOD_ISSUE);
        }));

        addDrawableChild(new LongItemWidget(width * 2 / 3, height - 20, width - width * 2 / 3, 20,
                Text.translatable("x13.mod.link.source"), new ItemStack(Items.PAPER), () -> {
            openLink(XrayMain.MOD_SOURCE);
        }));

        super.init();
    }

    private void openLink(URL url) {
        try {
            Util.getOperatingSystem().open(url);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredText(stack, textRenderer, title, width / 2,
                height / 2 - 52 - textRenderer.fontHeight, 0xffffffff);
        super.render(stack, mouseX, mouseY, delta);
    }
}
