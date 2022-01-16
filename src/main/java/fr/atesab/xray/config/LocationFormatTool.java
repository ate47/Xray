package fr.atesab.xray.config;

import java.util.function.Function;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public enum LocationFormatTool implements EnumElement {
    PLAYER_LOCATION_X("x13.mod.location.opt.x", Items.BOOK, "x",
            mc -> XrayMain.significantNumbers(mc.player.position().x)),
    PLAYER_LOCATION_Y("x13.mod.location.opt.y", Items.BOOK, "y",
            mc -> XrayMain.significantNumbers(mc.player.position().y)),
    PLAYER_LOCATION_Z("x13.mod.location.opt.z", Items.BOOK, "z",
            mc -> XrayMain.significantNumbers(mc.player.position().z)),
    PLAYER_LOCATION_FLOOR_X("x13.mod.location.opt.fx", Items.BOOK, "fx",
            mc -> String.valueOf((int) mc.player.position().x)),
    PLAYER_LOCATION_FLOOR_Y("x13.mod.location.opt.fy", Items.BOOK, "fy",
            mc -> String.valueOf((int) mc.player.position().y)),
    PLAYER_LOCATION_FLOOR_Z("x13.mod.location.opt.fz", Items.BOOK, "fz",
            mc -> String.valueOf((int) mc.player.position().z)),
    PLAYER_NAME("x13.mod.location.opt.name", Items.NAME_TAG, "name", mc -> mc.player.getGameProfile().getName()),
    FPS("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", mc -> mc.fpsString);

    public static String applyAll(String old, Minecraft mc) {
        String s = old;
        for (LocationFormatTool tool : values())
            s = tool.apply(s, mc);
        return s.replaceAll("&([0-9a-fk-or])", ChatFormatting.PREFIX_CODE + "$1");
    }

    private String regex;
    private Function<Minecraft, String> action;
    private ItemStack icon;
    private Component title;

    LocationFormatTool(String translation, ItemLike icon, String txt, Function<Minecraft, String> action) {
        this.regex = "%" + txt;
        this.action = action;
        this.icon = new ItemStack(icon);
        this.title = new TranslatableComponent(translation);
    }

    public String getOption() {
        return regex;
    }

    public String apply(String old, Minecraft mc) {
        return old.replaceAll(regex, action.apply(mc));
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
