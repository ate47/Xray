package fr.atesab.xray.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class XrayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization()
            .excludeFieldsWithoutExposeAnnotation().create();

    /**
     * load a config file and save it
     * 
     * @param saveFile the save file
     * @return the config
     * @see #save(File)
     */
    public static XrayConfig sync(File saveFile) {
        XrayConfig cfg;
        try (Reader r = new FileReader(saveFile)) {
            cfg = GSON.fromJson(r, XrayConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            cfg = new XrayConfig();
            cfg.reset();
        }

        cfg.saveFile = saveFile;

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

    public List<BlockConfig> getBlockConfigs() {
        return blockConfigs;
    }

    public List<ESPConfig> getEspConfigs() {
        return espConfigs;
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public int getMaxTracerRange() {
        return maxTracerRange;
    }

    public File getSaveFile() {
        return saveFile;
    }

    /**
     * reset the config
     */
    public void reset() {
        espConfigs.clear();
    }

    /**
     * save the config to a file
     * 
     * @param saveFile the save file
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
}
