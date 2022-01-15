package fr.atesab.xray.config;

import com.google.gson.annotations.Expose;

public class LocationConfig implements Cloneable {
    public static final String DEFAULT_FORMAT = "XYZ: %x / %y / %z";

    @Expose
    private boolean enabled = true;
    @Expose
    private boolean showMode = true;

    @Expose
    private String format = DEFAULT_FORMAT;

    public LocationConfig() {
    }

    private LocationConfig(LocationConfig other) {
        this.enabled = other.enabled;
        this.format = other.format;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isShowMode() {
        return showMode;
    }

    public void setShowMode(boolean showMode) {
        this.showMode = showMode;
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
