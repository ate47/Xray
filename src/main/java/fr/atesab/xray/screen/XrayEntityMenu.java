package fr.atesab.xray.screen;

import fr.atesab.xray.color.BlockEntityTypeIcon;
import fr.atesab.xray.color.EntityTypeIcon;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.widget.XrayButton;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class XrayEntityMenu extends Screen {
    public record EntityUnion(EntityType<?> type, BlockEntityType<?> blockType, String text) implements EnumElement {
        EntityUnion(EntityType<?> type) {
            this(type, null, computeText(type, null));
        }

        EntityUnion(BlockEntityType<?> blockType) {
            this(null, blockType, computeText(null, blockType));
        }

        @Override
        public ItemStack getIcon() {
            if (type != null) {
                return EntityTypeIcon.getIcon(type);
            } else {
                return BlockEntityTypeIcon.getIcon(blockType);
            }
        }

        @Override
        public Text getTitle() {
            return Text.translatable(text());
        }

        static String computeText(EntityType<?> type, BlockEntityType<?> blockType) {
            if (type != null) {
                return type.getTranslationKey();
            } else {
                Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(blockType);
                if (id == null) {
                    return blockType.getClass().getCanonicalName();
                }
                return id.toTranslationKey();
            }
        }
    }

    private static final Text ADD = Text.literal("+").formatted(Formatting.GREEN);
    private static final Text REPLACE = Text.translatable("x13.mod.menu.replace")
            .formatted(Formatting.YELLOW);
    private static final Text DELETE = Text.translatable("x13.mod.menu.delete")
            .formatted(Formatting.RED);
    private final Screen parent;
    private final ESPConfig mode;
    private final List<EntityUnion> config;
    private final List<EntityUnion> visible = new ArrayList<>();
    private TextFieldWidget searchBar;
    private ButtonWidget nextPage;
    private ButtonWidget lastPage;
    private int elementByPage = 1;
    private int elementsX = 1;
    private int elementsY = 1;
    private int page = 0;

    public XrayEntityMenu(Screen parent, ESPConfig mode) {
        super(Text.literal(mode.getName()));
        this.mode = mode;
        this.parent = parent;
        this.config = new ArrayList<>();
        this.config.addAll(mode.getEntities().getObjects().stream().map(EntityUnion::new).toList());
        this.config.addAll(mode.getBlockEntities().getObjects().stream().map(EntityUnion::new).toList());
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
        }).dimensions(width / 2 - 126, pageBottom, 20, 20).build();

        ButtonWidget doneBtn = XrayButton.builder(Text.translatable("gui.done"), button -> {
            mode.getEntities().setObjects(config.stream().map(EntityUnion::type).filter(Objects::nonNull).toList());
            mode.getBlockEntities().setObjects(config.stream().map(EntityUnion::blockType).filter(Objects::nonNull).toList());
            client.setScreen(parent);
        }).dimensions(width / 2 - 102, pageBottom, 100, 20).build();

        ButtonWidget cancelBtn = XrayButton.builder(Text.translatable("gui.cancel"), button -> client.setScreen(parent)).dimensions(width / 2 + 2, pageBottom, 100, 20).build();

        nextPage = XrayButton.builder(Text.literal("->"), button -> {
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
        String query = searchBar.getText().toLowerCase();
        visible.clear();
        config.stream().filter(block -> I18n.translate(block.text()).toLowerCase().contains(query))
                .forEach(visible::add);
        page = Math.min(visible.size(), page);
        updateArrows();
    }

    public List<EntityUnion> getView() {
        return visible.subList(page * elementByPage, Math.min((page + 1) * elementByPage, visible.size()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTick) {
        renderInGameBackground(context);
        searchBar.render(context, mouseX, mouseY, partialTick);

        int left = width / 2 - elementsX * 18 / 2;
        int top = height / 2 - elementsY * 18 / 2;

        List<EntityUnion> view = getView();
        ItemStack hovered = null;
        EntityUnion hoveredBlock = null;
        int i;
        for (i = 0; i < view.size(); i++) {
            EntityUnion et = view.get(i);
            int x = left + (i % elementsX) * 18;
            int y = top + (i / elementsX) * 18;

            int color;
            ItemStack stack = et.getIcon();

            if (hovered == null && mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                color = 0x446666ff;
                hovered = stack;
                hoveredBlock = et;
            } else {
                color = 0x44666699;
            }

            context.fill(x, y, x + 18, y + 18, color);
            GuiUtils.renderItemIdentity(context, stack, x + 1, y + 1);
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

        context.fill(x, y, x + 18, y + 18, color);
        context.drawText(textRenderer, ADD, x + 18 / 2 - textRenderer.getWidth(ADD) / 2,
                y + 18 / 2 - textRenderer.fontHeight / 2, color, true);

        super.render(context, mouseX, mouseY, partialTick);

        if (hovered != null) {
            context.drawTooltip(textRenderer,
                    Arrays.asList(Text.translatable(hoveredBlock.text()), REPLACE, DELETE),
                    mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        int left = width / 2 - elementsX * 18 / 2;
        int top = height / 2 - elementsY * 18 / 2;

        List<EntityUnion> view = getView();
        int i;
        for (i = 0; i < view.size(); i++) {
            EntityUnion b = view.get(i);
            int x = left + (i % elementsX) * 18;
            int y = top + (i / elementsX) * 18;
            if (mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                if (button == 0) { // left click: replace
                    client.setScreen(new EntitySelector(this) {
                        @Override
                        protected void select(EntityUnion selection) {
                            int index = config.indexOf(b);
                            if (index == -1) { // wtf?
                                config.add(selection);
                            } else {
                                config.set(index, selection);
                            }
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
            client.setScreen(new EntitySelector(this) {
                @Override
                protected void select(EntityUnion selection) {
                    config.add(selection);
                    updateSearch();
                }
            });
            return true;
        }
        return false;
    }
}
