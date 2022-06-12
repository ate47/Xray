package fr.atesab.xray.screen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.widget.MenuWidget;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;


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
                new ButtonWidget(width / 2 - 100, height / 2 + 52, 200, 20, Text.translatable("gui.done"),
                        btn -> {
                            client.setScreen(parent);
                        }));

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
                /*Text.translatable("x13.mod.fullbright"), new ItemStack(Blocks.GLOWSTONE), () -> {
                    mod.fullBright();
                }));*/ //Todo 1.19 :/
                Text.literal("broken"), new ItemStack(Blocks.BARRIER), () -> {
                    client.player.sendMessage(Text.literal("Fullbright is currently broken."), false);
                }));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.showloc"), new ItemStack(Items.PAPER), () -> {
                    client.setScreen(new XrayLocationConfig(this));
                }));
        addDrawableChild(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                Text.translatable("x13.mod.config"), new ItemStack(Items.REDSTONE), () -> {
                    client.setScreen(new XrayConfigMenu(this));
                }));

        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        stack.push();
        stack.translate(width / 2, height / 2 - 70, 0);
        stack.scale(4, 4, 1);
        drawCenteredText(stack, client.textRenderer, XrayMain.MOD_NAME, 0, -client.textRenderer.fontHeight,
                0xffffff33);
        stack.pop();
        drawCenteredText(stack, client.textRenderer, Text.translatable("x13.mod.by",
                Arrays.stream(XrayMain.MOD_AUTHORS).collect(Collectors.joining(
                        ", "))),
                width / 2, height / 2 - 60, 0xffaaaaaa);
        int size = 400 / 5;
        DrawableHelper.fill(stack, 0, height / 2 - size / 2, width / 2 - 200, height / 2 + size / 2, 0x22ffffff);
        DrawableHelper.fill(stack, width / 2 + 200, height / 2 - size / 2, width, height / 2 + size / 2, 0x22ffffff);

        super.render(stack, mouseX, mouseY, delta);
    }
}
