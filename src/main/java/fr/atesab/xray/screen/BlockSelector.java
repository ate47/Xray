package fr.atesab.xray.screen;

import fr.atesab.xray.widget.XrayButton;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockSelector extends Screen {
    private Screen parent;
    private List<Block> blocks;
    private List<Block> visible = new ArrayList<>();
    private TextFieldWidget searchBar;
    private ButtonWidget nextPage;
    private ButtonWidget lastPage;
    private int elementByPage = 1;
    private int elementsX = 1;
    private int elementsY = 1;
    private int page = 0;

    public BlockSelector(Screen parent) {
        super(Text.translatable("x13.mod.menu.selector"));
        this.parent = parent;
        blocks = new ArrayList<>();
        Registries.BLOCK.forEach(blocks::add);
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

        searchBar = new TextFieldWidget(textRenderer, width / 2 - sizeX / 2, pageTop + 2, sizeX, 16,
                Text.literal("")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 1 && mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY()
                        && mouseY <= this.getY() + this.height) {
                    setText("");
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public void setText(String text) {
                super.setText(text);
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

        lastPage = XrayButton.builder(Text.literal("<-"), button -> {
            page--;
            updateArrows();
        }).dimensions(width / 2 - 124, pageBottom, 20, 20).build();

        ButtonWidget cancelBtn = XrayButton.builder(Text.translatable("gui.cancel"), button -> client.setScreen(parent)).dimensions(width / 2 - 100, pageBottom, 200, 20).build();

        nextPage = XrayButton.builder(Text.literal("->"), button -> {
            page++;
            updateArrows();
        }).dimensions(width / 2 + 104, pageBottom, 20, 20).build();

        addSelectableChild(searchBar);
        addDrawableChild(lastPage);
        addDrawableChild(cancelBtn);
        addDrawableChild(nextPage);

        updateArrows();
        updateSearch();

        setFocused(searchBar);
    }

    public void updateArrows() {
        nextPage.active = (page + 1) * elementByPage < visible.size(); // have last page
        lastPage.active = page * elementByPage > 0; // have next page
    }

    public void updateSearch() {
        String query = searchBar.getText().toString().toLowerCase();
        visible.clear();
        blocks.stream().filter(block -> I18n.translate(block.getTranslationKey()).toLowerCase().contains(query))
                .forEach(visible::add);
        page = Math.min(visible.size(), page);
        updateArrows();
    }

    public List<Block> getView() {
        return visible.subList(page * elementByPage, Math.min((page + 1) * elementByPage, visible.size()));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTick) {
        renderBackground(drawContext);
        searchBar.render(drawContext, mouseX, mouseY, partialTick);
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

            drawContext.fill(x, y, x + 18, y + 18, color);
            drawContext.drawItem(stack, x + 1, y + 1);
        }
        super.render(drawContext, mouseX, mouseY, partialTick);

        if (hoveredBlock != null) {
            drawContext.drawTooltip(this.textRenderer, Text.translatable(hoveredBlock.getTranslationKey()), mouseX, mouseY);
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
                    client.setScreen(parent);
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
