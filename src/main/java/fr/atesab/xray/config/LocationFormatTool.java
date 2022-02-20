package fr.atesab.xray.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.config.BufferedFormat.BufferedComponent;
import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LightLayer;

public class LocationFormatTool implements EnumElement, BufferedComponent {
    public static final LocationFormatTool PLAYER_LOCATION_X = register(
            new LocationFormatTool("x13.mod.location.opt.x", Items.BOOK, "x",
                    mc -> XrayMain.significantNumbers(mc.player.position().x)));

    public static final LocationFormatTool PLAYER_LOCATION_Y = register(
            new LocationFormatTool("x13.mod.location.opt.y", Items.BOOK, "y",
                    mc -> XrayMain.significantNumbers(mc.player.position().y)));

    public static final LocationFormatTool PLAYER_LOCATION_Z = register(
            new LocationFormatTool("x13.mod.location.opt.z", Items.BOOK, "z",
                    mc -> XrayMain.significantNumbers(mc.player.position().z)));

    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_X = register(
            new LocationFormatTool("x13.mod.location.opt.fx", Items.BOOK, "fx",
                    mc -> String.valueOf((int) mc.player.position().x)));

    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_Y = register(
            new LocationFormatTool("x13.mod.location.opt.fy", Items.BOOK, "fy",
                    mc -> String.valueOf((int) mc.player.position().y)));

    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_Z = register(
            new LocationFormatTool("x13.mod.location.opt.fz", Items.BOOK, "fz",
                    mc -> String.valueOf((int) mc.player.position().z)));

    public static final LocationFormatTool PLAYER_NAME = register(
            new LocationFormatTool("x13.mod.location.opt.name", Items.NAME_TAG, "name",
                    mc -> mc.player.getGameProfile().getName()));

    public static final LocationFormatTool FPS = register(
            new LocationFormatTool("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps",
                    mc -> mc.fpsString));

    public static final LocationFormatTool BIOME_CATEGORY = register(
            new LocationFormatTool("x13.mod.location.opt.biomeCategory", Items.STRIPPED_OAK_LOG, "biocate",
                    mc -> mc.level.getPrimaryBiome(mc.player.chunkPosition()).getBiomeCategory()
                            .getName()));

    public static final LocationFormatTool BIOME = register(
            new LocationFormatTool("x13.mod.location.opt.biome", Items.OAK_LOG, "bio",
                    mc -> mc.level.getPrimaryBiome(mc.player.chunkPosition()).getRegistryName()
                            .getPath()));

    public static final LocationFormatTool PLAYER_CHUNK_X = register(
            new LocationFormatTool("x13.mod.location.opt.chunkX", Items.BOOK, "cx",
                    mc -> String.valueOf(mc.player.chunkPosition().x)));

    public static final LocationFormatTool PLAYER_CHUNK_Z = register(
            new LocationFormatTool("x13.mod.location.opt.chunkZ", Items.BOOK, "cz",
                    mc -> String.valueOf(mc.player.chunkPosition().z)));

    public static final LocationFormatTool BLOCK_LIGHT = register(
            new LocationFormatTool("x13.mod.location.opt.blockLight", Items.TORCH, "blocklight",
                    mc -> String.valueOf(
                            mc.level.getBrightness(LightLayer.BLOCK,
                                    mc.player.blockPosition().offset(1, 0, 0)))));

    public static final LocationFormatTool SKY_LIGHT = register(
            new LocationFormatTool("x13.mod.location.opt.skyLight", Items.ELYTRA, "skylight",
                    mc -> String.valueOf(mc.level.getBrightness(LightLayer.SKY,
                            mc.player.blockPosition()))));

    public static final LocationFormatTool LOOKINGBLOCK_LIGHT = register(
            new LocationFormatTool("x13.mod.location.opt.lookingBlockLight", Items.REDSTONE_TORCH,
                    "lookinglight",
                    mc -> String.valueOf(
                            mc.level.getBrightness(LightLayer.BLOCK,
                                    LocationUtils.getLookingFaceBlockPos(mc)))));

    public static final LocationFormatTool LOOKINGBLOCK = register(
            new LocationFormatTool("x13.mod.location.opt.lookingBlock", Items.DIAMOND_ORE, "lookingblock",
                    mc -> mc.level.getBlockState(LocationUtils.getLookingBlockPos(mc)).getBlock()
                            .getRegistryName()
                            .getPath()));

    public static final LocationFormatTool LOOKINGBLOCK_TRANSLATE = register(
            new LocationFormatTool("x13.mod.location.opt.lookingTranslate", Items.DIAMOND_ORE,
                    "lookingtranslate",
                    mc -> new TranslatableComponent(
                            mc.level.getBlockState(LocationUtils.getLookingBlockPos(mc))
                                    .getBlock().getDescriptionId()).getString()));

    public static final LocationFormatTool FACING = register(
            new LocationFormatTool("x13.mod.location.opt.facing", Items.COMPASS, "face",
                    mc -> mc.player.getDirection().getName()));

    public static final LocationFormatTool DAYS_COUNT = register(
            new LocationFormatTool("x13.mod.location.opt.daysCount", Items.CLOCK, "d",
                    mc -> String.valueOf((mc.level.getDayTime() / 24000))));

    public static final LocationFormatTool TIME_OF_DAY = register(
            new LocationFormatTool("x13.mod.location.opt.timeOfDay", Items.CLOCK, "timeday",
                    mc -> String.valueOf((mc.level.getDayTime() % 24000) / 24000.0)));

    public static final LocationFormatTool TIME_HOURS_PADDING = register(
            new LocationFormatTool("x13.mod.location.opt.hoursPadding", Items.CLOCK, "hh",
                    mc -> LocationUtils.getTwoDigitNumberFormat()
                            .format(((mc.level.getDayTime() + 6000) % 24000) / 1000)));

    public static final LocationFormatTool TIME_HOURS = register(
            new LocationFormatTool("x13.mod.location.opt.hours", Items.CLOCK, "h",
                    mc -> String.valueOf(((mc.level.getDayTime() + 6000) % 24000) / 1000)));

    public static final LocationFormatTool TIME_MINUTES_PADDING = register(
            new LocationFormatTool("x13.mod.location.opt.minutesPadding", Items.CLOCK, "mm",
                    mc -> LocationUtils.getTwoDigitNumberFormat()
                            .format(((mc.level.getDayTime() % 1000) / 1000.0 * 60))));

    public static final LocationFormatTool TIME_SECONDS_PADDING = register(
            new LocationFormatTool("x13.mod.location.opt.secondsPadding", Items.CLOCK, "ss",
                    mc -> LocationUtils.getTwoDigitNumberFormat()
                            .format(((mc.level.getDayTime() % 1000) / 1000.0 * 3600)
                                    % 60)));

    public static final LocationFormatTool IS_SLIME = register(
            new LocationFormatTool("x13.mod.location.opt.isSlime", Items.SLIME_BALL, "slime",
                    mc -> String.valueOf(
                            LocationUtils.isSlimeChunk(mc, mc.player.chunkPosition()))));

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-z]+");
    public static final Pattern OBJ_PATTERN = Pattern.compile("%([a-z]+)");
    private static final Map<String, LocationFormatTool> NAME_TO_LOCATION = new HashMap<>();

    /**
     * register a tool
     * 
     * @param <T>  the tool type
     * @param tool the tool
     * @return the tool
     */
    public static <T extends LocationFormatTool> T register(T tool) {
        LocationFormatTool old = getByName(tool.getName());
        if (old != null)
            throw new IllegalArgumentException("a tool with the name " + old.getName() + " already exists.");
        NAME_TO_LOCATION.put(tool.getName(), tool);
        return tool;
    }

    /**
     * find a tool by name
     * 
     * @param the name of the tool
     * @return the tool or null if this doesn't exist
     */
    public static LocationFormatTool getByName(String name) {
        return NAME_TO_LOCATION.get(name.toLowerCase());
    }

    /**
     * @return all the location format tool
     */
    public static Collection<LocationFormatTool> getValues() {
        return NAME_TO_LOCATION.values();
    }

    private String v1Regex;
    private String name;
    private String old = "";
    private Function<Minecraft, String> action;
    private ItemStack icon;
    private Component title;

    LocationFormatTool(String translation, ItemLike icon, String text, Function<Minecraft, String> action) {
        if (!NAME_PATTERN.matcher(text).matches())
            throw new IllegalArgumentException(
                    "LocationFormatTool name should be matching the pattern " + NAME_PATTERN);
        this.v1Regex = "%" + text;
        this.name = text;
        this.action = action;
        this.icon = new ItemStack(icon);
        this.title = new TranslatableComponent(translation);
    }

    public String getName() {
        return name;
    }

    public String getOption() {
        return v1Regex;
    }

    @Override
    public String apply(Minecraft mc) {
        String newValue = old.replaceAll(v1Regex, action.apply(mc));
        return old = newValue;
    }

    @Override
    public String getLastComputedValue() {
        return old;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public Component getTitle() {
        return title;
    }
}
