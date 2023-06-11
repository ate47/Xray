package fr.atesab.xray.widget;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.screen.XrayBlockMenu;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockConfigWidget extends XrayButton {
    private final BlockConfig cfg;
    private int deltaX;
    private int deltaY;

    public BlockConfigWidget(int x, int y, int width, int height, BlockConfig cfg, Screen menu) {
        this(x, y, width, height, cfg, menu, 0, 0);
    }

    public BlockConfigWidget(int x, int y, int width, int height, BlockConfig cfg, Screen menu, int deltaX,
            int deltaY) {
        super(x, y, width, height, Component.literal(""),
                b -> Minecraft.getInstance().setScreen(new XrayBlockMenu(menu, cfg)));
        this.cfg = cfg;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int fit = (width - 2) / 17;

        List<Block> blocks = cfg.getBlocks().getObjects();

        List<Block> view = blocks.subList(0, Math.min(fit, blocks.size()));
        Minecraft client = Minecraft.getInstance();

        int x = getX();
        int y = getY();

        if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
            graphics.fill(x, y, x + width, y + height, 0x33ffaa00);
        } else {
            graphics.fill(x, y, x + width, y + height, 0x33ffffff);
        }

        int left = x + this.width / 2 - view.size() * 17 / 2;
        int top = y + this.height / 2 - 15 / 2;
        for (Block b : view) {
            GuiUtils.renderItemIdentity(graphics, new ItemStack(b), left + deltaX, top + deltaY);
            left += 17;
        }
    }

}
