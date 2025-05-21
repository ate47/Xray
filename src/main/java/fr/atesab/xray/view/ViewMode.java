package fr.atesab.xray.view;

import fr.atesab.xray.color.EnumElement;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


public enum ViewMode implements EnumElement {
    /**
     * Default mode, like in Xray and Redstone mode
     */
    EXCLUSIVE("x13.mod.mode.view.exclusive", new ItemStack(Blocks.DIAMOND_ORE), (inList, state, adjacentState) -> inList),
    /**
     * Inclusive mode, like in Cave Mode
     */
    INCLUSIVE("x13.mod.mode.view.inclusive", new ItemStack(Blocks.STONE), (inList, state, adjacentState) -> !inList && adjacentState.isAir());

    private Viewer viewer;
    private Text title;
    private ItemStack icon;

    private ViewMode(String translation, ItemStack icon, Viewer viewer) {
        this.viewer = viewer;
        this.icon = icon;
        this.title = Text.translatable(translation);
    }

    @Override
    public Text getTitle() {
        return title;
    }

    public Viewer getViewer() {
        return viewer;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }
}
