package fr.atesab.xray.widget;

import java.util.List;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.screen.XrayBlockMenu;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


public class BlockConfigWidget extends ButtonWidget {
    private BlockConfig cfg;
    private int deltaX;
    private int deltaY;

    public BlockConfigWidget(int x, int y, int width, int height, BlockConfig cfg, Screen menu) {
        this(x, y, width, height, cfg, menu, 0, 0);
    }

    public BlockConfigWidget(int x, int y, int width, int height, BlockConfig cfg, Screen menu, int deltaX,
            int deltaY) {
        super(x, y, width, height, Text.empty(),
                b -> MinecraftClient.getInstance().setScreen(new XrayBlockMenu(menu, cfg)));
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
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int fit = (width - 2) / 17;

        List<Block> blocks = cfg.getBlocks().getObjects();

        List<Block> view = blocks.subList(0, Math.min(fit, blocks.size()));
        MinecraftClient client = MinecraftClient.getInstance();

        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffaa00);
        } else {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffffff);
        }

        int left = this.x + this.width / 2 - view.size() * 17 / 2;
        int top = this.y + this.height / 2 - 15 / 2;
        for (Block b : view) {
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(b), left + deltaX, top + deltaY);
            left += 17;
        }
    }

}
