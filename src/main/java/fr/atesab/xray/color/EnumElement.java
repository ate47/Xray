package fr.atesab.xray.color;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface EnumElement {
    ItemStack getIcon();

    Component getTitle();
}
