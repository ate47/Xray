package fr.atesab.xray.config;

import com.google.gson.annotations.Expose;

public class LocationConfig {
    public static final String DEFAULT_FORMAT = "%modes %x / %y / %z";

    @Expose
    private boolean enabled = true;

    @Expose
    private String format = DEFAULT_FORMAT;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }
}
