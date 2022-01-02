package fr.atesab.xray.view;

public enum ViewMode {
    /**
     * Default mode, like in Xray and Redstone mode
     */
    EXCLUSIVE((il, v1, v2, v3, v4) -> il),
    /**
     * Inclusive mode, like in Cave Mode
     */
    INCLUSIVE((il, v1, reader, pos, face) -> !il
            && reader.getBlockState(pos.offset(face.getStepX(), face.getStepY(), face.getStepZ())).isAir());

    private Viewer viewer;

    private ViewMode(Viewer viewer) {
        this.viewer = viewer;
    }

    public Viewer getViewer() {
        return viewer;
    }
}
