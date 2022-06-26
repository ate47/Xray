package fr.atesab.xray.config;

import java.util.function.BiFunction;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

public enum LocationFormatTool implements EnumElement {
    PLAYER_LOCATION_X("x13.mod.location.opt.x", Items.BOOK, "x",
            (mc, cpinfo) -> XrayMain.significantNumbers(cpinfo.getX())),
    PLAYER_LOCATION_Y("x13.mod.location.opt.y", Items.BOOK, "y",
    		(mc, cpinfo) -> XrayMain.significantNumbers(cpinfo.getY())),
    PLAYER_LOCATION_Z("x13.mod.location.opt.z", Items.BOOK, "z",
    		(mc, cpinfo) -> XrayMain.significantNumbers(cpinfo.getZ())),
    PLAYER_LOCATION_FLOOR_X("x13.mod.location.opt.fx", Items.BOOK, "fx",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getFloatedX())),
    PLAYER_LOCATION_FLOOR_Y("x13.mod.location.opt.fy", Items.BOOK, "fy",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getFloatedY())),
    PLAYER_LOCATION_FLOOR_Z("x13.mod.location.opt.fz", Items.BOOK, "fz",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getFloatedZ())),
    PLAYER_NAME("x13.mod.location.opt.name", Items.NAME_TAG, "name", (mc, cpinfo) -> cpinfo.getPlayerName()),
    FPS("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", (mc, cpinfo) -> mc.fpsString),
    @SuppressWarnings("deprecation")
	BIOME_CATEGORY("x13.mod.location.opt.biomeCategory", Items.STRIPPED_OAK_LOG, "biocate",
    		(mc, cpinfo) -> Biome.getBiomeCategory(cpinfo.getCurrentBiome()).getName()),
    BIOME("x13.mod.location.opt.biome", Items.OAK_LOG, "bio",
    		(mc, cpinfo) -> cpinfo.getCurrentBiome().value().getRegistryName().getPath()),
    PLAYER_CHUNK_X("x13.mod.location.opt.chunkX", Items.BOOK, "cx",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getPlayerChunkX())),
    PLAYER_CHUNK_Z("x13.mod.location.opt.chunkZ", Items.BOOK, "cz",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getPlayerChunkZ())),
    BLOCK_LIGHT("x13.mod.location.opt.blockLight", Items.TORCH, "blocklight",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getBrightness(LightLayer.BLOCK))),
    SKY_LIGHT("x13.mod.location.opt.skyLight", Items.ELYTRA, "skylight",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getBrightness(LightLayer.SKY))),
    LOOKINGBLOCK_LIGHT("x13.mod.location.opt.lookingBlockLight", Items.REDSTONE_TORCH, "lookinglight",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getLookingBrightness(LightLayer.BLOCK))), 
    LOOKINGBLOCK("x13.mod.location.opt.lookingBlock", Items.DIAMOND_ORE, "lookingblock",
    		(mc, cpinfo) -> cpinfo.getLookingBlock().getRegistryName().getPath()),
    LOOKINGBLOCK_TRANSLATE("x13.mod.location.opt.lookingTranslate", Items.DIAMOND_ORE, "lookingtranslate",
    		(mc, cpinfo) -> new TranslatableComponent(cpinfo.getLookingBlock().getDescriptionId()).getString()),
    FACING("x13.mod.location.opt.facing", Items.COMPASS, "face",
    		(mc, cpinfo) -> cpinfo.getPlayerDirection().getName()),
    DAYS_COUNT("x13.mod.location.opt.daysCount", Items.CLOCK, "d",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getDays())),
    TIME_OF_DAY("x13.mod.location.opt.timeOfDay", Items.CLOCK, "timeday",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getTimeOfDay())),
    TIME_HOURS_PADDING("x13.mod.location.opt.hoursPadding", Items.CLOCK, "hh",
    		(mc, cpinfo) -> LocationUtils.getTwoDigitNumberFormat().format(cpinfo.getHours())),
    TIME_HOURS("x13.mod.location.opt.hours", Items.CLOCK, "h",
    		(mc, cpinfo) -> String.valueOf(cpinfo.getHours())),
    TIME_MINUTES_PADDING("x13.mod.location.opt.minutesPadding", Items.CLOCK, "mm",
    		(mc, cpinfo) -> LocationUtils.getTwoDigitNumberFormat().format(cpinfo.getMinutes())),
    TIME_SECONDS_PADDING("x13.mod.location.opt.secondsPadding", Items.CLOCK, "ss",
    		(mc, cpinfo) -> LocationUtils.getTwoDigitNumberFormat().format(cpinfo.getSeconds())),
    IS_SLIME("x13.mod.location.opt.isSlime", Items.SLIME_BALL, "slime",
    		(mc, cpinfo) -> String.valueOf(LocationUtils.isSlimeChunk(mc, cpinfo.getPlayerChunkPos()))),
    LINEFEED("x13.mod.location.opt.lineFeed",Items.WRITABLE_BOOK, "lf",
    		(mc, cpinfo) -> getLocationLineSeparater()),
    VALUE_SEPARATE("x13.mod.location.opt.valueSeparate",Items.WRITABLE_BOOK, "separate",
    		(mc, cpinfo) -> getLocationValueSeparater())
    ;

    public static String applyAll(String old, Minecraft mc, CurrentPlayerInfoHolder cpinfo) {
        String s = old;
        if (cpinfo.getLevel() == null) {return old;};
        for (LocationFormatTool tool : values())
            s = tool.apply(s, mc, cpinfo);
        return s.replaceAll("&([0-9a-fk-or])", ChatFormatting.PREFIX_CODE + "$1");
    }

    private String regex;
    private BiFunction<Minecraft, CurrentPlayerInfoHolder, String> action;
    private ItemStack icon;
    private Component title;
    public static final String LINE_SEPARATER = "\n";
    public static final String VALUE_SEPARATER = "\r";
    
    LocationFormatTool(String translation, ItemLike icon, String txt, BiFunction<Minecraft, CurrentPlayerInfoHolder, String> action) {
        this.regex = "%" + txt;
        this.action = action;
        this.icon = new ItemStack(icon);
        this.title = new TranslatableComponent(translation);
    }

    public String getOption() {
        return regex;
    }

    public String apply(String old, Minecraft mc, CurrentPlayerInfoHolder cpinfo) {
        return old.replaceAll(regex, action.apply(mc, cpinfo));
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    public static String getLocationLineSeparater() {
    	return LINE_SEPARATER;
    }

    public static String getLocationValueSeparater() {
    	return VALUE_SEPARATER;
    }
}
