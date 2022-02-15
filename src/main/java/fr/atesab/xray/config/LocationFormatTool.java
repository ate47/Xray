package fr.atesab.xray.config;

import java.util.function.Function;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LightLayer;

public enum LocationFormatTool implements EnumElement {
    PLAYER_LOCATION_X("x13.mod.location.opt.x", Items.BOOK, "x",
            mc -> XrayMain.significantNumbers(mc.player.getPos().x)),
    PLAYER_LOCATION_Y("x13.mod.location.opt.y", Items.BOOK, "y",
            mc -> XrayMain.significantNumbers(mc.player.getPos().y)),
    PLAYER_LOCATION_Z("x13.mod.location.opt.z", Items.BOOK, "z",
            mc -> XrayMain.significantNumbers(mc.player.getPos().z)),
    PLAYER_LOCATION_FLOOR_X("x13.mod.location.opt.fx", Items.BOOK, "fx",
            mc -> String.valueOf((int) mc.player.getPos().x)),
    PLAYER_LOCATION_FLOOR_Y("x13.mod.location.opt.fy", Items.BOOK, "fy",
            mc -> String.valueOf((int) mc.player.getPos().y)),
    PLAYER_LOCATION_FLOOR_Z("x13.mod.location.opt.fz", Items.BOOK, "fz",
            mc -> String.valueOf((int) mc.player.getPos().z)),
    PLAYER_NAME("x13.mod.location.opt.name", Items.NAME_TAG, "name", mc -> mc.player.getGameProfile().getName()),
    FPS("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", mc -> mc.fpsDebugString),
    BIOME_CATEGORY("x13.mod.location.opt.biomeCategory", Items.STRIPPED_OAK_LOG, "biocate",
    		mc -> mc.level.getPrimaryBiome(mc.player.chunkPosition()).getBiomeCategory().getName()),
    BIOME("x13.mod.location.opt.biome", Items.OAK_LOG, "bio",
    		mc -> mc.level.getPrimaryBiome(mc.player.chunkPosition()).getRegistryName().getPath()),
    PLAYER_CHUNK_X("x13.mod.location.opt.chunkX", Items.BOOK, "cx",
    		mc -> String.valueOf(mc.player.chunkPosition().x)),
    PLAYER_CHUNK_Z("x13.mod.location.opt.chunkZ", Items.BOOK, "cz",
    		mc -> String.valueOf(mc.player.chunkPosition().z)),
    BLOCK_LIGHT("x13.mod.location.opt.blockLight", Items.TORCH, "blocklight",
    		mc -> String.valueOf(mc.level.getBrightness(LightLayer.BLOCK,mc.player.blockPosition().offset(1,0,0)))),
    SKY_LIGHT("x13.mod.location.opt.skyLight", Items.ELYTRA, "skylight",
    		mc -> String.valueOf(mc.level.getBrightness(LightLayer.SKY,mc.player.blockPosition()))),
    LOOKINGBLOCK_LIGHT("x13.mod.location.opt.lookingBlockLight", Items.REDSTONE_TORCH, "lookinglight",
    		mc -> String.valueOf(mc.level.getBrightness(LightLayer.BLOCK,LocationUtils.getLookingFaceBlockPos(mc)))),
    LOOKINGBLOCK("x13.mod.location.opt.lookingBlock", Items.DIAMOND_ORE, "lookingblock",
    		mc -> mc.level.getBlockState(LocationUtils.getLookingBlockPos(mc)).getBlock().getRegistryName().getPath()),
    LOOKINGBLOCK_TRANSLATE("x13.mod.location.opt.lookingTranslate", Items.DIAMOND_ORE, "lookingtranslate",
    		mc -> new TranslatableComponent(mc.level.getBlockState(LocationUtils.getLookingBlockPos(mc))
    				.getBlock().getDescriptionId()).getString()),
    FACING("x13.mod.location.opt.facing", Items.COMPASS, "face",
    		mc -> mc.player.getDirection().getName()),
    DAYS_COUNT("x13.mod.location.opt.daysCount", Items.CLOCK, "d",
    		mc -> String.valueOf((mc.level.getDayTime() / 24000))),
    TIME_OF_DAY("x13.mod.location.opt.timeOfDay", Items.CLOCK, "timeday",
    		mc -> String.valueOf((mc.level.getDayTime() % 24000) / 24000.0)),
    TIME_HOURS_PADDING("x13.mod.location.opt.hoursPadding", Items.CLOCK, "hh",
    		mc -> LocationUtils.getTwoDigitNumberFormat().format(((mc.level.getDayTime() + 6000) % 24000) / 1000)),
    TIME_HOURS("x13.mod.location.opt.hours", Items.CLOCK, "h",
    		mc -> String.valueOf(((mc.level.getDayTime() + 6000) % 24000) / 1000)),
    TIME_MINUTES_PADDING("x13.mod.location.opt.minutesPadding", Items.CLOCK, "mm",
    		mc -> LocationUtils.getTwoDigitNumberFormat().format(((mc.level.getDayTime() % 1000) / 1000.0 * 60))),
    TIME_SECONDS_PADDING("x13.mod.location.opt.secondsPadding", Items.CLOCK, "ss",
    		mc -> LocationUtils.getTwoDigitNumberFormat().format(((mc.level.getDayTime() % 1000) / 1000.0 * 3600) % 60)),
    IS_SLIME("x13.mod.location.opt.isSlime", Items.SLIME_BALL, "slime",
    		mc -> String.valueOf(LocationUtils.isSlimeChunk(mc, mc.player.chunkPosition()))),
    LINEFEED("x13.mod.location.opt.lineFeed",Items.WRITABLE_BOOK, "lf",
    		mc -> getLocationLineSeparater()),
    VALUE_SEPARATE("x13.mod.location.opt.valueSeparate",Items.WRITABLE_BOOK, "separate",
    		mc -> getLocationValueSeparater())
    ;

    public static String applyAll(String old, MinecraftClient mc) {
        String s = old;
        if (mc.level == null) {return old;};
        for (LocationFormatTool tool : values())
            s = tool.apply(s, mc);

        return s.replaceAll("&([0-9a-fk-or])", "§$1");
    }

    public static final String LINE_SEPARATER = "\n";
    public static final String VALUE_SEPARATER = "\r";

    private String regex;
    private Function<MinecraftClient, String> action;
    private ItemStack icon;
    private Text title;

    LocationFormatTool(String translation, ItemConvertible icon, String txt, Function<MinecraftClient, String> action) {
        this.regex = "%" + txt;
        this.action = action;
        this.icon = new ItemStack(icon);
        this.title = Text.translatable(translation);
    }

    public String getOption() {
        return regex;
    }

    public String apply(String old, MinecraftClient mc) {
        return old.replaceAll(regex, action.apply(mc));
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public Text getTitle() {
        return title;
    }

    public static String getLocationLineSeparater() {
    	return LINE_SEPARATER;
    }

    public static String getLocationValueSeparater() {
    	return VALUE_SEPARATER;
    }
}
