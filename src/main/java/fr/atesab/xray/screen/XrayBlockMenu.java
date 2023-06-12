package fr.atesab.xray.screen;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XrayBlockMenu extends Screen {
    private static final Text ADD = Text.literal("+").formatted(Formatting.GREEN);
    private static final Text REPLACE = Text.translatable("x13.mod.menu.replace")
            .formatted(Formatting.YELLOW);
    private static final Text DELETE = Text.translatable("x13.mod.menu.delete")
            .formatted(Formatting.RED);
    private Screen parent;
    private BlockConfig mode;
    private List<Block> config;
    private List<Block> visible = new ArrayList<>();
    private TextFieldWidget searchBar;
    private ButtonWidget nextPage;
    private ButtonWidget lastPage;
    private int elementByPage = 1;
    private int elementsX = 1;
    private int elementsY = 1;
    private int page = 0;

    public XrayBlockMenu(Screen parent, BlockConfig mode) {
        super(Text.literal(mode.getName()));
        this.mode = mode;
        this.parent = parent;
        this.config = new ArrayList<>();
        this.config.addAll(mode.getBlocks().getObjects());
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

        lastPage = XrayButton.builder(Text.literal("<-"), b -> {
            page--;
            updateArrows();
        }).dimensions(width / 2 - 126, pageBottom, 20, 20).build();

        ButtonWidget doneBtn = XrayButton.builder(Text.translatable("gui.done"), b -> {
            mode.getBlocks().setObjects(config);
            client.setScreen(parent);
        }).dimensions(width / 2 - 102, pageBottom, 100, 20).build();

        ButtonWidget cancelBtn = XrayButton.builder(
                Text.translatable("gui.cancel"), b -> client.setScreen(parent)).dimensions(width / 2 + 2, pageBottom, 100, 20).build();
        nextPage = XrayButton.builder(Text.literal("->"), b -> {
            page++;
            updateArrows();
        }).dimensions(width / 2 + 106, pageBottom, 20, 20).build();

        addSelectableChild(searchBar);
        addDrawableChild(lastPage);
        addDrawableChild(doneBtn);
        addDrawableChild(cancelBtn);
        addDrawableChild(nextPage);

        updateArrows();
        updateSearch();

        setFocused(searchBar);
    }

    public void updateArrows() {
        nextPage.active = (page + 1) * elementByPage < visible.size() + 1; // have last page (+1 = next button)
        lastPage.active = page * elementByPage > 0; // have next page
    }

    public void updateSearch() {
        String query = searchBar.getText().toString().toLowerCase();
        visible.clear();
        config.stream().filter(block -> I18n.translate(block.getTranslationKey()).toLowerCase().contains(query))
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
        ItemStack hovered = null;
        Block hoveredBlock = null;
        int i;
        for (i = 0; i < view.size(); i++) {
            Block b = view.get(i);
            int x = left + (i % elementsX) * 18;
            int y = top + (i / elementsX) * 18;

            int color;
            ItemStack stack = new ItemStack(b);

            if (hovered == null && mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                color = 0x446666ff;
                hovered = stack;
                hoveredBlock = b;
            } else {
                color = 0x44666699;
            }

            drawContext.fill(x, y, x + 18, y + 18, color);
            drawContext.drawItem(stack, x + 1, y + 1);
        }
        // add [+] button
        int x = left + (i % elementsX) * 18;
        int y = top + (i / elementsX) * 18;

        int color;

        if (hovered == null && mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
            color = 0x4466ff66;
        } else {
            color = 0x44669966;
        }

        drawContext.fill(x, y, x + 18, y + 18, color);
        drawContext.drawText(this.textRenderer, ADD, x + 18 / 2 - textRenderer.getWidth(ADD) / 2,
                y + 18 / 2 - textRenderer.fontHeight / 2, color, false);

        super.render(drawContext, mouseX, mouseY, partialTick);

        if (hovered != null) {
            drawContext.drawTooltip(this.textRenderer, Arrays.asList(Text.translatable(hoveredBlock.getTranslationKey()), REPLACE, DELETE), mouseX, mouseY);
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
                if (button == 0) { // left click: replace
                    client.setScreen(new BlockSelector(this) {
                        @Override
                        protected void save(Block selection) {
                            int index = config.indexOf(b);
                            if (index == -1) // wtf?
                                config.add(selection);
                            else
                                config.set(index, selection);
                            updateSearch();
                        }
                    });
                    return true;
                }
                if (button == 1) { // right click: delete
                    config.remove(b);
                    updateSearch();
                    return true;
                }
                return false;
            }
        }
        int x = left + (i % elementsX) * 18;
        int y = top + (i / elementsX) * 18;
        if (button == 0 && mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
            // add
            client.setScreen(new BlockSelector(this) {
                @Override
                protected void save(Block selection) {
                    config.add(selection);
                    updateSearch();
                }
            });
            return true;
        }
        return false;
    }
}
