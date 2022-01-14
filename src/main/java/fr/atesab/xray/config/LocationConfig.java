package fr.atesab.xray.config;

import com.google.gson.annotations.Expose;

public class LocationConfig implements Cloneable {
    public static final String DEFAULT_FORMAT = "%modes %x / %y / %z";

    @Expose
    private boolean enabled = true;

    @Expose
    private String format = DEFAULT_FORMAT;

    public LocationConfig() {
    }

    private LocationConfig(LocationConfig other) {
        this.enabled = other.enabled;
        this.format = other.format;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    @Override
    public LocationConfig clone() {
        return new LocationConfig(this);
    }
}
