package fr.atesab.xray.color;

import fr.atesab.xray.utils.GuiUtils;

public class ColorSupplier {
    public static final ColorSupplier DEFAULT = new ColorSupplier();

    public int getColor() {
        return GuiUtils.getTimeColor(1000, 75, 50);
    }
}
