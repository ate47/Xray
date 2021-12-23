package fr.atesab.xray;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

public class XrayModeWidget extends ButtonWidget {
    private XrayMode mode;

    public XrayModeWidget(int x, int y, int width, int height, XrayMode mode, Screen menu) {
        super(x, y, width, height, new LiteralText(""),
                b -> MinecraftClient.getInstance().setScreen(new XrayBlockMenu(menu, mode)));
        this.mode = mode;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int fit = (width - 2) / 17;

        List<Block> blocks = mode.getBlocks();

        List<Block> view = blocks.subList(0, Math.min(fit, blocks.size()));
        MinecraftClient client = MinecraftClient.getInstance();

        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y
                && mouseY <= this.y + this.height) {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffaa00);
        } else {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffffff);
        }

        int left = this.x + this.width / 2 - view.size() * 17 / 2;
        int top = this.y + this.height / 2 - 15 / 2;
        for (Block b : view) {
            client.getItemRenderer().renderInGui(new ItemStack(b), left, top);
            left += 17;
        }
    }

}
