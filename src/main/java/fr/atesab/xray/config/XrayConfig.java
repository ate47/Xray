package fr.atesab.xray.config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.MergedIterable;

public class XrayConfig implements Cloneable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization()
            .excludeFieldsWithoutExposeAnnotation().create();

    public static final int MAX_TRACER_RANGE = 256;

    /**
     * load a config file and save it
     * 
     * @param saveFile the save file
     * @return the config
     * @see #save()
     */
    public static XrayConfig sync(File saveFile) {
        XrayConfig cfg;
        try (Reader r = new FileReader(saveFile)) {
            cfg = GSON.fromJson(r, XrayConfig.class);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("Can't find config file, creating a new config...");
            } else {
                e.printStackTrace();
            }
            cfg = new XrayConfig();
            cfg.reset();
        }

        cfg.saveFile = saveFile;

        cfg.locationConfig.load();

        // save the config
        cfg.save();

        return cfg;
    }

    @Expose
    private List<ESPConfig> espConfigs = new ArrayList<>();
    @Expose
    private List<BlockConfig> blockConfigs = new ArrayList<>();
    @Expose
    private int maxTracerRange = 0;
    @Expose
    private LocationConfig locationConfig = new LocationConfig();
    private File saveFile;

    public XrayConfig() {
    }

    private XrayConfig(XrayConfig other) {
        this.espConfigs = other.espConfigs.stream().map(ESPConfig::clone)
                .collect(Collectors.toCollection(ArrayList::new));
        this.blockConfigs = other.blockConfigs.stream().map(
                BlockConfig::clone)
                .collect(Collectors.toCollection(ArrayList::new));
        this.maxTracerRange = other.maxTracerRange;
        this.locationConfig = other.locationConfig.clone();
        this.saveFile = other.saveFile;
    }

    public Iterable<AbstractModeConfig> getModes() {
        return new MergedIterable<>(getBlockConfigs(), getEspConfigs());
    }

    /**
     * @return the selected block mode, or null if no mode is selected
     */
    public BlockConfig getSelectedBlockMode() {
        return getBlockConfigs().stream().filter(BlockConfig::isEnabled).findAny().orElse(null);
    }

    public List<BlockConfig> getBlockConfigs() {
        return blockConfigs;
    }

    public List<ESPConfig> getEspConfigs() {
        return espConfigs;
    }

    public void setEspConfigs(List<ESPConfig> espConfigs) {
        this.espConfigs = espConfigs;
    }

    public void setBlockConfigs(List<BlockConfig> blockConfigs) {
        this.blockConfigs = blockConfigs;
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public int getMaxTracerRange() {
        return maxTracerRange;
    }

    public double getMaxTracerRangeNormalized() {
        return GuiUtils.clamp(maxTracerRange / (double) MAX_TRACER_RANGE, 0, 1.0);
    }

    public void setMaxTracerRangeNormalized(double maxTracerRange) {
        this.maxTracerRange = GuiUtils.clamp((int) (maxTracerRange * MAX_TRACER_RANGE), 0, MAX_TRACER_RANGE);
    }

    public File getSaveFile() {
        return saveFile;
    }

    /**
     * reset the config
     */
    public void reset() {
        blockConfigs.clear();
        blockConfigs.add(BlockConfig.Template.XRAY.create(-11607223));
        blockConfigs.add(BlockConfig.Template.CAVE.create(-1779896));
        blockConfigs.add(BlockConfig.Template.REDSTONE.create(-122077));
        espConfigs.clear();
        espConfigs.add(ESPConfig.Template.PLAYER.create(-11607223));
        espConfigs.add(ESPConfig.Template.WITHER.create(-1779896));
    }

    /**
     * save the config to a file
     * 
     * @see #sync(File)
     */
    public void save() {
        try (Writer w = new FileWriter(saveFile)) {
            GSON.toJson(this, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMaxTracerRange(int maxTracerRange) {
        this.maxTracerRange = maxTracerRange;
    }

    @Override
    public XrayConfig clone() {
        return new XrayConfig(this);
    }
}
