package fr.atesab.xray.config;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.annotations.Expose;

import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;
import fr.atesab.xray.utils.KeyData;
import fr.atesab.xray.utils.KeyInput;
import net.minecraft.client.gui.screen.Screen;

public abstract class AbstractModeConfig implements IColorObject {
    private static final AtomicInteger IDS = new AtomicInteger();
    private boolean enabled = false;
    @Expose
    private int key;
    @Expose
    private int scanCode;

    @Expose
    private boolean altModifier;

    @Expose
    private boolean ctrlModifier;

    @Expose
    private boolean shiftModifier;

    @Expose
    private int color = ColorSupplier.DEFAULT.getColor();

    @Expose
    private String name;

    protected AbstractModeConfig(AbstractModeConfig other) {
        cloneInto(other);
    }

    public AbstractModeConfig() {
        this(0, 0, "Mode #" + IDS.incrementAndGet());
    }

    public AbstractModeConfig(int key, int scanCode, String name) {
        this.key = key;
        this.scanCode = scanCode;
        this.name = Objects.requireNonNull(name, "name can't be null!");
    }

    public void cloneInto(AbstractModeConfig cfg) {
        this.key = cfg.key;
        this.enabled = cfg.enabled;
        this.color = cfg.color;
        this.name = cfg.name;
        this.altModifier = cfg.altModifier;
        this.ctrlModifier = cfg.ctrlModifier;
        this.shiftModifier = cfg.shiftModifier;
    }

    public void onKeyInput(KeyInput input) {
        if (key == input.key() && !((altModifier && !Screen.hasAltDown())
                || (ctrlModifier && !Screen.hasControlDown())
                || (shiftModifier && !Screen.hasShiftDown()))) {
            toggle();
        }
    }

    public int getKeyCode() {
        return key;
    }

    public int getKeyScanCode() {
        return scanCode;
    }

    public void setKey(Optional<KeyData> key) {
        KeyData data = key.orElseGet(KeyData::new);
        this.key = data.keyCode();
        this.scanCode = data.keyScanCode();
        this.altModifier = data.alt();
        this.ctrlModifier = data.ctrl();
        this.shiftModifier = data.shift();
    }

    public Optional<KeyData> getKey() {
        return key == 0 ? Optional.empty() : Optional.of(new KeyData(key, scanCode, altModifier, ctrlModifier, shiftModifier));
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
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

    public void setKey(int key) {
        this.key = key;
    }

    public int getScanCode() {
        return scanCode;
    }

    public void setScanCode(int scanCode) {
        this.scanCode = scanCode;
    }

    public boolean isAltModifier() {
        return altModifier;
    }

    public void setAltModifier(boolean altModifier) {
        this.altModifier = altModifier;
    }

    public boolean isCtrlModifier() {
        return ctrlModifier;
    }

    public void setCtrlModifier(boolean ctrlModifier) {
        this.ctrlModifier = ctrlModifier;
    }

    public boolean isShiftModifier() {
        return shiftModifier;
    }

    public void setShiftModifier(boolean shiftModifier) {
        this.shiftModifier = shiftModifier;
    }

    @Override
    public String getModeName() {
        return getName();
    }
}
