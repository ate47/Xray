package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class LocationConfig implements Cloneable {
    public static final String NEW_FORMAT_TEXT = "%USE V2%";
    public static final String DEFAULT_FORMAT = "XYZ: %x / %y / %z";

    @Expose
    private boolean enabled = true;
    @Expose
    private boolean showMode = true;

    @Expose
    private String format = DEFAULT_FORMAT;

    @Expose
    private List<String> v2Format = new ArrayList<>();

    private BufferedFormat buffFormat = new BufferedFormat();

    public LocationConfig() {
    }

    private LocationConfig(LocationConfig other) {
        this.enabled = other.enabled;
        this.format = other.format;
        this.v2Format = new ArrayList<>(other.v2Format);
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

    public List<String> getV2Format() {
        return v2Format;
    }

    public void setV2Format(List<String> v2Format) {
        this.v2Format = v2Format;
    }

    public BufferedFormat getBufferedFormat() {
        return buffFormat;
    }

    @Override
    public LocationConfig clone() {
        return new LocationConfig(this);
    }

    /**
     * sync location config
     */
    public void syncConfig() {
        // convert V1 config to V2
        if (!format.equals(NEW_FORMAT_TEXT)) {
            this.v2Format.add(format);
            format = NEW_FORMAT_TEXT;
        }

        syncV2Format();
    }

    public void syncV2Format() {
        buffFormat.sync(v2Format);
    }
}
