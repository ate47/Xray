package fr.atesab.xray.color;

import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

public abstract class AbstractEnumElement implements EnumElement {
    private ItemStack icon;
    private Text title;

    public AbstractEnumElement(ItemStack icon, Text title) {
        this.icon = icon;
        this.title = title;
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
