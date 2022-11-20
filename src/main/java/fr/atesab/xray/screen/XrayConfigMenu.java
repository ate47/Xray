package fr.atesab.xray.screen;

import java.net.URL;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.Skin;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.utils.XrayUtils;
import fr.atesab.xray.widget.LongItemWidget;
import fr.atesab.xray.widget.XrayButton;
import fr.atesab.xray.widget.XraySlider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class XrayConfigMenu extends XrayScreen {

    public XrayConfigMenu(Screen parent) {
        super(Component.translatable("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        XrayConfig cfg = XrayMain.getMod().getConfig();
        addRenderableWidget(new XraySlider(width / 2 - 100, height / 2 - 48, 200, 20,
                Component.translatable("x13.mod.esp.maxdistance"), cfg.getMaxTracerRangeNormalized()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                int range = cfg.getMaxTracerRange();
                MutableComponent distance = Component.translatable("x13.mod.esp.maxdistance").append(": ");
                if (range == 0)
                    setMessage(distance.append(Component.translatable("x13.mod.esp.maxdistance.infinite").withStyle(ChatFormatting.YELLOW)));
                else
                    setMessage(distance
                            .append(Component.translatable("x13.mod.esp.maxdistance.block", String.valueOf(range)).withStyle(ChatFormatting.YELLOW)));
            }

            @Override
            protected void applyValue() {
                cfg.setMaxTracerRangeNormalized(value);
            }

        });
        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 - 24, 200, 20,
                        XrayUtils.getToggleable(!cfg.isDamageIndicatorDisabled(), "x13.mod.config.espDamage"), button -> {
                    cfg.setDamageIndicatorDisabled(!cfg.isDamageIndicatorDisabled());
                    button.setMessage(XrayUtils.getToggleable(!cfg.isDamageIndicatorDisabled(), "x13.mod.config.espDamage"));
                })
        );
        addRenderableWidget(new XraySlider(width / 2 - 100, height / 2, 200, 20,
                Component.translatable("x13.mod.config.espline"), cfg.getEspLineWidthNormalized()) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                float range = cfg.getEspLineWidth();
                setMessage(Component.translatable("x13.mod.config.espline").append(": ")
                        .append(Component.literal(String.format("%.1f", range)).withStyle(ChatFormatting.YELLOW)));
            }

            @Override
            protected void applyValue() {
                cfg.setEspLineWidthNormalized((float) value);
            }

        });

        addRenderableWidget(new XrayButton(width / 2 - 100, height / 2 + 24, 200, 20, Component.translatable("x13.mod.config.skin").append(": ").append(Component.literal(cfg.getSkin().getTitle()).withStyle(ChatFormatting.YELLOW)), btn -> {
            Skin[] skins = Skin.values();
            cfg.setSkin(skins[(cfg.getSkin().ordinal() + 1) % skins.length]);
            btn.setMessage(Component.translatable("x13.mod.config.skin").append(": ").append(Component.literal(cfg.getSkin().getTitle()).withStyle(ChatFormatting.YELLOW)));
        }));

        addRenderableWidget(
                new XrayButton(width / 2 - 100, height / 2 + 52, 200, 20, Component.translatable("gui.done"),
                        btn -> {
                            minecraft.setScreen(parent);
                        }));

        addRenderableWidget(new LongItemWidget(width * 0 / 3, height - 20, width / 3, 20,
                Component.translatable("x13.mod.link.mod"), new ItemStack(Blocks.GOLD_ORE), () -> {
                    openLink(XrayMain.MOD_LINK);
                }));
        addRenderableWidget(new LongItemWidget(width * 1 / 3, height - 20, width / 3, 20,
                Component.translatable("x13.mod.link.issue"), new ItemStack(Blocks.TNT), () -> {
                    openLink(XrayMain.MOD_ISSUE);
                }));
        addRenderableWidget(new LongItemWidget(width * 2 / 3, height - 20, width - width * 2 / 3, 20,
                Component.translatable("x13.mod.link.source"), new ItemStack(Items.PAPER), () -> {
                    openLink(XrayMain.MOD_SOURCE);
                }));
        super.init();
    }

    private void openLink(URL url) {
        try {
            Util.getPlatform().openUrl(url);
        } catch (Exception e) {
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        drawCenteredString(stack, font, title, width / 2,
                height / 2 - 52 - font.lineHeight, 0xffffffff);
        super.render(stack, mouseX, mouseY, delta);
    }
}
