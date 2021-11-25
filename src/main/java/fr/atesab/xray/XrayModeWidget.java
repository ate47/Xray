package fr.atesab.xray;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class XrayModeWidget extends Button {
    private XrayMode mode;

    public XrayModeWidget(int x, int y, int width, int height, XrayMode mode, Screen menu) {
        super(x, y, width, height, new TextComponent(""),
                b -> Minecraft.getInstance().setScreen(new XrayBlockMenu(menu, mode)));
        this.mode = mode;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        int fit = (width - 2) / 17;

        List<Block> blocks = mode.getBlocks();

        List<Block> view = blocks.subList(0, Math.min(fit, blocks.size()));
        var client = Minecraft.getInstance();

        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            Gui.fill(matrices, x, y, x + width, y + height, 0x33ffaa00);
        } else {
            Gui.fill(matrices, x, y, x + width, y + height, 0x33ffffff);
        }

        int left = this.x + this.width / 2 - view.size() * 17 / 2;
        int top = this.y + this.height / 2 - 15 / 2;
        for (Block b : view) {
            client.getItemRenderer().renderGuiItem(new ItemStack(b), left, top);
            left += 17;
        }
    }

}
