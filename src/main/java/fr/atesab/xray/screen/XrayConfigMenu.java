package fr.atesab.xray.screen;

import java.net.URL;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.widget.LongItemWidget;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class XrayConfigMenu extends XrayScreen {

    public XrayConfigMenu(Screen parent) {
        super(new TranslatableComponent("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + 76, 200, 20, new TranslatableComponent("gui.done"),
                        btn -> {
                            minecraft.setScreen(parent);
                        }));

        addRenderableWidget(new LongItemWidget(width * 0 / 3, height - 20, width / 3, 20,
                new TranslatableComponent("x13.mod.link.mod"), new ItemStack(Blocks.GOLD_ORE), () -> {
                    openLink(XrayMain.MOD_LINK);
                }));
        addRenderableWidget(new LongItemWidget(width * 1 / 3, height - 20, width / 3, 20,
                new TranslatableComponent("x13.mod.link.issue"), new ItemStack(Blocks.TNT), () -> {
                    openLink(XrayMain.MOD_ISSUE);
                }));
        addRenderableWidget(new LongItemWidget(width * 2 / 3, height - 20, width - width * 2 / 3, 20,
                new TranslatableComponent("x13.mod.link.source"), new ItemStack(Items.PAPER), () -> {
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
        // TODO Auto-generated method stub
        super.render(stack, mouseX, mouseY, delta);
    }
}
