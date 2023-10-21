package fr.atesab.xray.screen;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.widget.MenuWidget;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;


public class XrayMenu extends XrayScreen {

    public XrayMenu(Screen parent) {
        super(Text.translatable("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        int size = 400 / 5;
        int x = width / 2 - 200;
        int i = 0;

        XrayMain mod = XrayMain.getMod();

        addDrawableChild(
                XrayButton.builder(Text.translatable("gui.done"),
                        btn -> {
                            client.setScreen(parent);
                        }).dimensions(width / 2 - 100, height / 2 + 52, 200, 20).build());

        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.mode"), new ItemStack(Blocks.DIAMOND_ORE), () -> {
            client.setScreen(new XrayBlockModesConfig(this, mod.getConfig().getBlockConfigs().stream()) {
                @Override
                protected void save(List<BlockConfig> list) {
                    mod.getConfig().setBlockConfigs(list);
                    mod.saveConfigs();
                }
            });
        }));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.esp"), new ItemStack(Blocks.CREEPER_HEAD), () -> {
            client.setScreen(new XrayESPModesConfig(this, mod.getConfig().getEspConfigs().stream()) {
                @Override
                protected void save(List<ESPConfig> list) {
                    mod.getConfig().setEspConfigs(list);
                    mod.saveConfigs();
                }
            });
        }));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.fullbright"), new ItemStack(Blocks.GLOWSTONE), mod::fullBright));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.showloc"), new ItemStack(Items.PAPER), () -> {
            client.setScreen(new XrayLocationConfig(this) {
                @Override
                protected void save() {
                    mod.saveConfigs();
                }
            });
        }));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.config"), new ItemStack(Items.REDSTONE), () -> {
            client.setScreen(new XrayConfigMenu(this));
        }));

        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(width / 2f, height / 2f - 70, 0);
        stack.scale(4, 4, 1);
        context.drawCenteredTextWithShadow(client.textRenderer, XrayMain.MOD_NAME, 0, -client.textRenderer.fontHeight,
                0xffffff33);
        stack.pop();
        context.drawCenteredTextWithShadow(client.textRenderer, Text.translatable("x13.mod.by",
                        String.join(", ", XrayMain.MOD_AUTHORS)),
                width / 2, height / 2 - 60, 0xffaaaaaa);
        int size = 400 / 5;
        context.fill(0, height / 2 - size / 2, width / 2 - 200, height / 2 + size / 2, 0x22ffffff);
        context.fill(width / 2 + 200, height / 2 - size / 2, width, height / 2 + size / 2, 0x22ffffff);

        super.render(context, mouseX, mouseY, delta);
    }
}
