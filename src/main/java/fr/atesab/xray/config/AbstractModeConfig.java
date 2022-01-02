package fr.atesab.xray.config;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.annotations.Expose;

import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;

public abstract class AbstractModeConfig implements IColorObject {
    private static final AtomicInteger IDS = new AtomicInteger();
    private boolean enabled = false;
    @Expose
    private int key = 0;
    @Expose
    private int color = ColorSupplier.DEFAULT.getColor();

    @Expose
    private String name = "Mode #" + IDS.incrementAndGet();

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getModeName() {
        return getName();
    }
}
