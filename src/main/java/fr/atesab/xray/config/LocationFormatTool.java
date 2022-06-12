package fr.atesab.xray.config;

import java.util.function.Function;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;


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
    FPS("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", mc -> mc.fpsDebugString);

    public static String applyAll(String old, MinecraftClient mc) {
        String s = old;
        for (LocationFormatTool tool : values())
            s = tool.apply(s, mc);

        return s.replaceAll("&([0-9a-fk-or])", "§$1");
    }

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

}
