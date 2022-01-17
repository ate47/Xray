package fr.atesab.xray.screen;

import java.net.URL;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.widget.LongItemWidget;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class XrayConfigMenu extends XrayScreen {

    public XrayConfigMenu(Screen parent) {
        super(new TranslatableText("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        XrayConfig cfg = XrayMain.getMod().getConfig();
        addDrawableChild(new SliderWidget(width / 2 - 100, height / 2 - 48, 200, 20,
                new TranslatableText("x13.mod.esp.maxdistance"), cfg.getMaxTracerRangeNormalized()) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                int range = cfg.getMaxTracerRange();
                MutableText distance = new TranslatableText("x13.mod.esp.maxdistance").append(": ");
                if (range == 0)
                    setMessage(distance.append(new TranslatableText("x13.mod.esp.maxdistance.infinite")));
                else
                    setMessage(distance
                            .append(new TranslatableText("x13.mod.esp.maxdistance.block", String.valueOf(range))));
            }

            @Override
            protected void applyValue() {
                cfg.setMaxTracerRangeNormalized(value);
            }

        });
        addDrawableChild(
                new ButtonWidget(width / 2 - 100, height / 2 + 52, 200, 20, new TranslatableText("gui.done"),
                        btn -> {
                            client.openScreen(parent);
                        }));

        addDrawableChild(new LongItemWidget(width * 0 / 3, height - 20, width / 3, 20,
                new TranslatableText("x13.mod.link.mod"), new ItemStack(Blocks.GOLD_ORE), () -> {
                    openLink(XrayMain.MOD_LINK);
                }));
        addDrawableChild(new LongItemWidget(width * 1 / 3, height - 20, width / 3, 20,
                new TranslatableText("x13.mod.link.issue"), new ItemStack(Blocks.TNT), () -> {
                    openLink(XrayMain.MOD_ISSUE);
                }));
        addDrawableChild(new LongItemWidget(width * 2 / 3, height - 20, width - width * 2 / 3, 20,
                new TranslatableText("x13.mod.link.source"), new ItemStack(Items.PAPER), () -> {
                    openLink(XrayMain.MOD_SOURCE);
                }));
        super.init();
    }

    private void openLink(URL url) {
        try {
            Util.getOperatingSystem().open(url);
        } catch (Exception e) {
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
