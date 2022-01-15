package fr.atesab.xray.view;

import fr.atesab.xray.color.EnumElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public enum ViewMode implements EnumElement {
    /**
     * Default mode, like in Xray and Redstone mode
     */
    EXCLUSIVE("x13.mod.mode.view.exclusive", new ItemStack(Blocks.DIAMOND_ORE), (il, v1, v2, v3, v4) -> il),
    /**
     * Inclusive mode, like in Cave Mode
     */
    INCLUSIVE("x13.mod.mode.view.inclusive", new ItemStack(Blocks.STONE), (il, v1, reader, pos, face) -> !il
            && reader.getBlockState(pos.offset(face.getStepX(), face.getStepY(), face.getStepZ())).isAir());

    private Viewer viewer;
    private Component title;
    private ItemStack icon;

    private ViewMode(String translation, ItemStack icon, Viewer viewer) {
        this.viewer = viewer;
        this.icon = icon;
        this.title = new TranslatableComponent(translation);
    }

    @Override
    public Component getTitle() {
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
