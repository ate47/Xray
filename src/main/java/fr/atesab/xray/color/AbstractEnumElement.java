package fr.atesab.xray.color;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractEnumElement implements EnumElement {
    private ItemStack icon;
    private Component title;

    public AbstractEnumElement(ItemStack icon, Component title) {
        this.icon = icon;
        this.title = title;
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
