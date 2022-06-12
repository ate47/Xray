package fr.atesab.xray.widget;

import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.screen.XrayEntityMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


public class EntityConfigWidget extends ButtonWidget {
    private ESPConfig cfg;
    private int deltaX;
    private int deltaY;

    public EntityConfigWidget(int x, int y, int width, int height, ESPConfig cfg, Screen menu) {
        this(x, y, width, height, cfg, menu, 0, 0);
    }

    public EntityConfigWidget(int x, int y, int width, int height, ESPConfig cfg, Screen menu, int deltaX,
            int deltaY) {
        super(x, y, width, height, Text.literal(""),
                b -> MinecraftClient.getInstance().setScreen(new XrayEntityMenu(menu, cfg)));
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

        Stream<ItemStack> stacks = cfg.getEntities().getIcons();

        List<ItemStack> view = stacks.limit(fit).toList();
        MinecraftClient client = MinecraftClient.getInstance();

        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffaa00);
        } else {
            DrawableHelper.fill(matrices, x, y, x + width, y + height, 0x33ffffff);
        }

        int left = this.x + this.width / 2 - view.size() * 17 / 2;
        int top = this.y + this.height / 2 - 15 / 2;
        for (ItemStack b : view) {
            client.getItemRenderer().renderGuiItemIcon(b, left + deltaX, top + deltaY);
            left += 17;
        }
    }

}
