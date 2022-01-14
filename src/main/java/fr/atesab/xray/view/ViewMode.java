package fr.atesab.xray.view;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum ViewMode {
    /**
     * Default mode, like in Xray and Redstone mode
     */
    EXCLUSIVE("x13.mod.mode.view.exclusive", (il, v1, v2, v3, v4) -> il),
    /**
     * Inclusive mode, like in Cave Mode
     */
    INCLUSIVE("x13.mod.mode.view.inclusive", (il, v1, reader, pos, face) -> !il
            && reader.getBlockState(pos.offset(face.getStepX(), face.getStepY(), face.getStepZ())).isAir());

    private Viewer viewer;
    private Component title;

    private ViewMode(String translation, Viewer viewer) {
        this.viewer = viewer;
        this.title = new TranslatableComponent(translation);
    }

    public Component getTitle() {
        return title;
    }

    public Viewer getViewer() {
        return viewer;
    }
}
