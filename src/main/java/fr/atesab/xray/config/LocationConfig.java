package fr.atesab.xray.config;

import com.google.gson.annotations.Expose;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class LocationConfig implements Cloneable {
    public enum TextAlignX {
        LEFT, CENTER, RIGHT
    }
    public enum TextAlignY {
        TOP, MIDDLE, BOTTOM
    }
    public enum LocationLocation {
        TOP_LEFT("x13.mod.location.hud.corner.top.left", TextAlignX.LEFT, TextAlignY.TOP, 0, 0),
        TOP_CENTER("x13.mod.location.hud.corner.top.center", TextAlignX.CENTER, TextAlignY.TOP, 0.5f, 0),
        TOP_RIGHT("x13.mod.location.hud.corner.top.right", TextAlignX.RIGHT, TextAlignY.TOP, 1, 0),

        MIDDLE_LEFT("x13.mod.location.hud.corner.middle.left", TextAlignX.LEFT, TextAlignY.MIDDLE, 0, 0.5f),
        MIDDLE_CENTER("x13.mod.location.hud.corner.middle.center", TextAlignX.CENTER, TextAlignY.MIDDLE, 0.5f, 0.5f),
        MIDDLE_RIGHT("x13.mod.location.hud.corner.middle.right", TextAlignX.RIGHT, TextAlignY.MIDDLE, 1, 0.5f),

        BOTTOM_LEFT("x13.mod.location.hud.corner.bottom.left", TextAlignX.LEFT, TextAlignY.BOTTOM, 0, 1),
        BOTTOM_CENTER("x13.mod.location.hud.corner.bottom.center", TextAlignX.CENTER, TextAlignY.BOTTOM, 0.5f, 1),
        BOTTOM_RIGHT("x13.mod.location.hud.corner.bottom.right", TextAlignX.RIGHT, TextAlignY.BOTTOM, 1, 1);

        private final String translation;
        private final TextAlignX alignX;
        private final TextAlignY alignY;
        private final float defaultShiftX;
        private final float defaultShiftY;

        LocationLocation(String translation, TextAlignX alignX, TextAlignY alignY, float defaultShiftX, float defaultShiftY) {
            this.translation = translation;
            this.alignX = alignX;
            this.alignY = alignY;
            this.defaultShiftX = defaultShiftX;
            this.defaultShiftY = defaultShiftY;
        }

        public String getTranslation() {
            return translation;
        }

        public TextAlignX getAlignX() {
            return alignX;
        }

        public TextAlignY getAlignY() {
            return alignY;
        }

        public MutableText getTranslationText() {
            return Text.translatable(getTranslation());
        }

        public float getDefaultShiftX() {
            return defaultShiftX;
        }

        public float getDefaultShiftY() {
            return defaultShiftY;
        }

        /**
         * @return next element in the enum
         */
        public LocationLocation next() {
            LocationLocation[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        /**
         * @return previous element in the enum
         */
        public LocationLocation prev() {
            LocationLocation[] values = values();
            return values[(ordinal() - 1 + values.length) % values.length];
        }
    }
    private static final float FONT_SIZE_MIN = 0.2f;
    private static final float FONT_SIZE_MAX = 10;
    public static final String DEFAULT_FORMAT = "XYZ: " + LocationFormatTool.PLAYER_LOCATION_X.getOption() + " / "+LocationFormatTool.PLAYER_LOCATION_Y.getOption()+" / "+LocationFormatTool.PLAYER_LOCATION_Z.getOption();

    @Expose
    private boolean enabled = true;
    @Expose
    private boolean showMode = true;
    @Expose
    private float shiftX = LocationLocation.TOP_LEFT.getDefaultShiftX();
    @Expose
    private float shiftY = LocationLocation.TOP_LEFT.getDefaultShiftY();
    @Expose
    private int location = LocationLocation.TOP_LEFT.ordinal();
    @Expose
    private float fontSizeMultiplier = 1;

    private LocationFormatTool.ToolFunction compiledFormat = new LocationFormatTool.StringToolFunction("???");

    @Expose
    private String format = DEFAULT_FORMAT;

    public LocationConfig() {
    }

    private LocationConfig(LocationConfig other) {
        this.enabled = other.enabled;
        this.format = other.format;
        this.showMode = other.showMode;
        this.shiftX = other.shiftX;
        this.shiftY = other.shiftY;
        this.location = other.location;
        this.fontSizeMultiplier = other.fontSizeMultiplier;
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

    public float getFontSizeMultiplier() {
        if (fontSizeMultiplier <= 0.01) {
            // bad config?
            setFontSizeMultiplier(1);
        }
        return fontSizeMultiplier;
    }
    public float getFontSizeMultiplierNormalized() {
        return (getFontSizeMultiplier() - FONT_SIZE_MIN) / (FONT_SIZE_MAX - FONT_SIZE_MIN);
    }

    public void setFontSizeMultiplier(float fontSizeMultiplier) {
        this.fontSizeMultiplier = fontSizeMultiplier;
    }

    public void setFontSizeMultiplierNormalized(float fontSizeMultiplier) {
        setFontSizeMultiplier(fontSizeMultiplier * (FONT_SIZE_MAX - FONT_SIZE_MIN) + FONT_SIZE_MIN);
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void setLocation(LocationLocation location) {
        if (this.location == location.ordinal()) {
            return;
        }
        this.location = location.ordinal();
        this.shiftX = location.getDefaultShiftX();
        this.shiftY = location.getDefaultShiftY();
    }

    public void setShiftX(float shiftX) {
        this.shiftX = shiftX;
    }

    public void setShiftY(float shiftY) {
        this.shiftY = shiftY;
    }

    public float getShiftX() {
        return shiftX;
    }

    public float getShiftY() {
        return shiftY;
    }

    public LocationLocation getLocation() {
        LocationLocation[] values = LocationLocation.values();
        if (location < 0 || location >= values.length) {
            setLocation(LocationLocation.TOP_LEFT);
        }
        return values[location];
    }

    @Override
    public LocationConfig clone() {
        try {
            return (LocationConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            return new LocationConfig(this);
        }
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
