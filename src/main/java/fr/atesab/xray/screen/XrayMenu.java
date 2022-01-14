package fr.atesab.xray.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.widget.MenuWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class XrayMenu extends XrayScreen {

    public XrayMenu(Screen parent) {
        super(new TranslatableComponent("x13.mod.config"), parent);
    }

    @Override
    protected void init() {
        int size = 400 / 5;
        int x = width / 2 - 200;
        int i = 0;

        XrayMain mod = XrayMain.getMod();

        addRenderableWidget(
                new Button(width / 2 - 100, height / 2 + size / 2 + 20, 200, 20, new TranslatableComponent("gui.done"),
                        btn -> {
                            minecraft.setScreen(parent);
                        }));

        addRenderableWidget(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                new TranslatableComponent("Xray"), new ItemStack(Blocks.DIAMOND_ORE), btn -> {
                    minecraft.setScreen(new XrayBlockModesConfig(this));
                }));
        addRenderableWidget(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                new TranslatableComponent("ESP"), new ItemStack(Blocks.CREEPER_HEAD), btn -> {
                    minecraft.setScreen(new XrayESPModesConfig(this));
                }));
        addRenderableWidget(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                new TranslatableComponent("Fullbright"), new ItemStack(Blocks.GLOWSTONE), btn -> {
                    mod.fullBright();
                }));
        addRenderableWidget(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                new TranslatableComponent("Location"), new ItemStack(Items.PAPER), btn -> {
                    minecraft.setScreen(new XrayLocationConfig(this));
                }));
        addRenderableWidget(new MenuWidget(x + size * i++, height / 2 - size / 2, size, size,
                new TranslatableComponent("Config"), new ItemStack(Items.REDSTONE), btn -> {
                    minecraft.setScreen(new XrayConfigMenu(this));
                }));

        super.init();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        stack.pushPose();
        stack.translate(width / 2, height / 2 - 60, 0);
        stack.scale(4, 4, 1);
        drawCenteredString(stack, minecraft.font, XrayMain.MOD_NAME, 0, -minecraft.font.lineHeight, 0xffffff33);
        stack.popPose();
        int size = 400 / 5;
        Gui.fill(stack, 0, height / 2 - size / 2, width / 2 - 200, height / 2 + size / 2, 0x22ffffff);
        Gui.fill(stack, width / 2 + 200, height / 2 - size / 2, width, height / 2 + size / 2, 0x22ffffff);

        super.render(stack, mouseX, mouseY, delta);
    }
}
