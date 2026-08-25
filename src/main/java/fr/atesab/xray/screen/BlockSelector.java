package fr.atesab.xray.screen;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BlockSelector extends XrayScreen {
    private final List<Block> blocks;
    private final List<Block> visible = new ArrayList<>();
    private EditBox searchBar;
    private XrayButton nextPage;
    private XrayButton lastPage;
    private int elementByPage = 1;
    private int elementsX = 1;
    private int elementsY = 1;
    private int page = 0;

    public BlockSelector(Screen parent) {
        super(Component.translatable("x13.mod.menu.selector"), parent);
        blocks = new ArrayList<>();
        ForgeRegistries.BLOCKS.forEach(blocks::add);
    }

    @Override
    protected void init() {
        int sizeX = Math.min(width, 400);
        int sizeY = Math.min(height - 48, 400);

        elementsX = sizeX / 18;
        elementsY = sizeY / 18;
        elementByPage = elementsX * elementsY;

        int pageTop = height / 2 - sizeY / 2 - 24;
        int pageBottom = height / 2 + sizeY / 2 + 2;

        searchBar = new EditBox(font, width / 2 - sizeX / 2, pageTop + 2, sizeX, 16, Component.literal("")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 1 && mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY()
                        && mouseY <= this.getY() + this.height) {
                    setValue("");
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public void setValue(String text) {
                super.setValue(text);
                updateSearch();
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (super.keyPressed(keyCode, scanCode, modifiers)) {
                    updateSearch();
                    return true;
                }
                return false;
            }

            @Override
            public boolean charTyped(char chr, int modifiers) {
                if (super.charTyped(chr, modifiers)) {
                    updateSearch();
                    return true;
                }
                return false;
            }
        };

        lastPage = new XrayButton(width / 2 - 124, pageBottom, 20, 20, Component.literal("<-"), b -> {
            page--;
            updateArrows();
        });
        XrayButton cancelBtn = new XrayButton(width / 2 - 100, pageBottom, 200, 20, Component.translatable("gui.cancel"),
                b -> {
                    getMinecraft().setScreen(parent);
                });
        nextPage = new XrayButton(width / 2 + 104, pageBottom, 20, 20, Component.literal("->"), b -> {
            page++;
            updateArrows();
        });

        addWidget(searchBar);
        addRenderableWidget(lastPage);
        addRenderableWidget(cancelBtn);
        addRenderableWidget(nextPage);

        updateArrows();
        updateSearch();

        setFocused(searchBar);
    }

    public void updateArrows() {
        nextPage.active = (page + 1) * elementByPage < visible.size(); // have last page
        lastPage.active = page * elementByPage > 0; // have next page
    }

    public void updateSearch() {
        String query = searchBar.getValue().toString().toLowerCase();
        visible.clear();
        blocks.stream().filter(block -> I18n.get(block.getDescriptionId()).toLowerCase().contains(query))
                .forEach(visible::add);
        page = Math.min(visible.size() / elementByPage, page);
        updateArrows();
    }

    public List<Block> getView() {
        return visible.subList(page * elementByPage, Math.min((page + 1) * elementByPage, visible.size()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        searchBar.render(graphics, mouseX, mouseY, partialTick);
        int left = width / 2 - elementsX * 18 / 2;
        int top = height / 2 - elementsY * 18 / 2;

        List<Block> view = getView();
        Block hoveredBlock = null;
        int i;
        for (i = 0; i < view.size(); i++) {
            Block b = view.get(i);
            int x = left + (i % elementsX) * 18;
            int y = top + (i / elementsX) * 18;

            int color;
            ItemStack stack = new ItemStack(b);

            if (hoveredBlock == null && mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                color = 0x4466ffff;
                hoveredBlock = b;
            } else {
                color = 0x44666699;
            }

            graphics.fill(x, y, x + 18, y + 18, color);
            GuiUtils.renderItemIdentity(graphics, stack, x + 1, y + 1);
        }
        super.render(graphics, mouseX, mouseY, partialTick);

        if (hoveredBlock != null) {
            graphics.renderTooltip(font, Component.translatable(hoveredBlock.getDescriptionId()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        int left = width / 2 - elementsX * 18 / 2;
        int top = height / 2 - elementsY * 18 / 2;

        List<Block> view = getView();
        int i;
        for (i = 0; i < view.size(); i++) {
            Block b = view.get(i);
            int x = left + (i % elementsX) * 18;
            int y = top + (i / elementsX) * 18;
            if (mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                if (button == 0) { // left click: select
                    save(b);
                    getMinecraft().setScreen(parent);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * save the selected block (only call when a Block is selected, doesn't call
     * after a cancel)
     * 
     * @param selection the selected block
     */
    protected abstract void save(Block selection);
}
