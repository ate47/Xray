package fr.atesab.xray.widget;

import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.screen.XrayEntityMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


public class EntityConfigWidget extends XrayButton {
    private final ESPConfig cfg;
    private int deltaX;
    private int deltaY;

    public EntityConfigWidget(int x, int y, int width, int height, ESPConfig cfg, Screen menu) {
        this(x, y, width, height, cfg, menu, 0, 0);
    }

    public EntityConfigWidget(int x, int y, int width, int height, ESPConfig cfg, Screen menu, int deltaX,
            int deltaY) {
        super(x, y, width, height, Text.literal(""),
                b -> MinecraftClient.getInstance().setScreen(new XrayEntityMenu(menu, cfg)), DEFAULT_NARRATION_SUPPLIER);
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

        Stream<ItemStack> stacks = Stream.concat(cfg.getEntities().getIcons(), cfg.getBlockEntities().getIcons());

        List<ItemStack> view = stacks.limit(fit).toList();
        MinecraftClient client = MinecraftClient.getInstance();

        if (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            DrawableHelper.fill(matrices, getX(), getY(), getX() + width, getY() + height, 0x33ffaa00);
        } else {
            DrawableHelper.fill(matrices, getX(), getY(), getX() + width, getY() + height, 0x33ffffff);
        }

        int left = this.getX() + this.width / 2 - view.size() * 17 / 2;
        int top = this.getY() + this.height / 2 - 15 / 2;
        for (ItemStack b : view) {
            client.getItemRenderer().renderGuiItemIcon(b, left + deltaX, top + deltaY);
            left += 17;
        }
    }

}
