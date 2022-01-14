package fr.atesab.xray.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import org.lwjgl.glfw.GLFW;

import fr.atesab.xray.utils.MergedIterable;
import fr.atesab.xray.view.ViewMode;
import net.minecraft.world.level.block.Blocks;

public class XrayConfig implements Cloneable {
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

    public XrayConfig() {
    }

    private XrayConfig(XrayConfig other) {
        this.espConfigs = other.espConfigs.stream().map(ESPConfig::clone)
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        this.blockConfigs = other.blockConfigs.stream().map(
                BlockConfig::clone)
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        this.maxTracerRange = other.maxTracerRange;
        this.locationConfig = other.locationConfig.clone();
        this.saveFile = other.saveFile;
    }

    public Iterable<AbstractModeConfig> getModes() {
        return new MergedIterable<AbstractModeConfig>(getBlockConfigs(), getEspConfigs());
    }

    /**
     * @return the selected block mode, or null if no mode is selected
     */
    public @Nullable BlockConfig getSelectedBlockMode() {
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

    public File getSaveFile() {
        return saveFile;
    }

    /**
     * reset the config
     */
    public void reset() {
        // @formatter:off
        blockConfigs.add(
            // Xray Mode
            new BlockConfig(
                GLFW.GLFW_KEY_X,
                "xray",
                ViewMode.EXCLUSIVE,

                /* Ores */
                Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.NETHER_GOLD_ORE,
                Blocks.ANCIENT_DEBRIS, Blocks.NETHER_QUARTZ_ORE,

                // 1.17
                Blocks.COPPER_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_LAPIS_ORE,

                Blocks.RAW_COPPER_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.RAW_IRON_BLOCK, Blocks.CRYING_OBSIDIAN,

                /* Ore Blocks */
                Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK,
                Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,

                /* Blocks */
                Blocks.OBSIDIAN, Blocks.BLUE_ICE, Blocks.CLAY, Blocks.BOOKSHELF,
                Blocks.SPONGE, Blocks.WET_SPONGE,

                /* Other */
                Blocks.NETHER_WART, Blocks.SPAWNER, Blocks.LAVA,Blocks.WATER,
                Blocks.TNT, Blocks.CONDUIT,

                /* Portals */
                Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.NETHER_PORTAL,

                /* Interactive */
                Blocks.BEACON, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
                Blocks.DISPENSER, Blocks.DROPPER,

                /* Useless */
                Blocks.DRAGON_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG,

                /* Infested (Silverfish inside) */
                Blocks.INFESTED_STONE, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS,
                Blocks.INFESTED_COBBLESTONE, Blocks.INFESTED_CHISELED_STONE_BRICKS,
                Blocks.INFESTED_MOSSY_STONE_BRICKS
            ));
        blockConfigs.add(
            // Cave Mode
            new BlockConfig(
                GLFW.GLFW_KEY_C,
                "cave",
                ViewMode.INCLUSIVE,

                Blocks.DIRT,              Blocks.GRASS,            Blocks.GRAVEL,          Blocks.GRASS_BLOCK,
                Blocks.DIRT_PATH,         Blocks.SAND,             Blocks.SANDSTONE,       Blocks.RED_SAND
            ));
        blockConfigs.add(
            // Redstone mode
            new BlockConfig(
                GLFW.GLFW_KEY_R,
                "redstone",
                ViewMode.EXCLUSIVE,

                Blocks.REDSTONE_BLOCK,                             Blocks.REDSTONE_LAMP,
                Blocks.REDSTONE_ORE,                               Blocks.REDSTONE_TORCH, 
                Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.REDSTONE_WALL_TORCH,                        Blocks.REDSTONE_WIRE,
                Blocks.REPEATER,                                   Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.COMMAND_BLOCK,                              Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.COMPARATOR,                                 Blocks.ANVIL,
                Blocks.CHEST,                                      Blocks.TRAPPED_CHEST,
                Blocks.DROPPER,                                    Blocks.DISPENSER,
                Blocks.HOPPER,                                     Blocks.OBSERVER,
                Blocks.DRAGON_HEAD,                                Blocks.DRAGON_WALL_HEAD,
                Blocks.IRON_DOOR,                                  Blocks.ACACIA_DOOR,
                Blocks.BIRCH_DOOR,                                 Blocks.DARK_OAK_DOOR,
                Blocks.JUNGLE_DOOR,                                Blocks.OAK_DOOR,
                Blocks.SPRUCE_DOOR,                                Blocks.ACACIA_BUTTON,
                Blocks.BIRCH_BUTTON,                               Blocks.DARK_OAK_BUTTON,
                Blocks.JUNGLE_BUTTON,                              Blocks.OAK_BUTTON,
                Blocks.SPRUCE_BUTTON,                              Blocks.STONE_BUTTON,
                Blocks.LEVER,                                      Blocks.TNT,
                Blocks.PISTON,                                     Blocks.PISTON_HEAD,
                Blocks.MOVING_PISTON,                              Blocks.STICKY_PISTON,
                Blocks.NOTE_BLOCK,                                 Blocks.DAYLIGHT_DETECTOR,
                Blocks.IRON_TRAPDOOR,                              Blocks.ACACIA_TRAPDOOR,
                Blocks.BIRCH_TRAPDOOR,                             Blocks.DARK_OAK_TRAPDOOR,
                Blocks.JUNGLE_TRAPDOOR,                            Blocks.OAK_TRAPDOOR,
                Blocks.SPRUCE_TRAPDOOR,                            Blocks.ACACIA_PRESSURE_PLATE,
                Blocks.BIRCH_PRESSURE_PLATE,                       Blocks.DARK_OAK_PRESSURE_PLATE,
                Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,              Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
                Blocks.JUNGLE_PRESSURE_PLATE,                      Blocks.OAK_PRESSURE_PLATE,
                Blocks.SPRUCE_PRESSURE_PLATE,                      Blocks.STONE_PRESSURE_PLATE,
                Blocks.RAIL,                                       Blocks.ACTIVATOR_RAIL,
                Blocks.DETECTOR_RAIL,                              Blocks.POWERED_RAIL,
                Blocks.ENDER_CHEST,								   Blocks.TARGET
            ));
        // @formatter:on
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

    @Override
    public XrayConfig clone() {
        return new XrayConfig(this);
    }
}
