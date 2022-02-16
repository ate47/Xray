package fr.atesab.xray.config;

import com.google.gson.annotations.Expose;

public class LocationConfig implements Cloneable {
    public static final String DEFAULT_FORMAT = "XYZ: " + LocationFormatTool.PLAYER_LOCATION_X.getOption() + " / "+LocationFormatTool.PLAYER_LOCATION_Y.getOption()+" / "+LocationFormatTool.PLAYER_LOCATION_Z.getOption();

    @Expose
    private boolean enabled = true;
    @Expose
    private boolean showMode = true;

    private LocationFormatTool.ToolFunction compiledFormat = new LocationFormatTool.StringToolFunction("???");

    @Expose
    private String format = DEFAULT_FORMAT;

    public LocationConfig() {
    }

    private LocationConfig(LocationConfig other) {
        this.enabled = other.enabled;
        this.format = other.format;
        this.showMode = other.showMode;
        syncFormat();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
        syncFormat();
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

    private void syncFormat() {
        this.compiledFormat = LocationFormatTool.construct(format);
    }

    public LocationFormatTool.ToolFunction getCompiledFormat() {
        return compiledFormat;
    }

    public void load() {
        syncFormat();
    }
}
