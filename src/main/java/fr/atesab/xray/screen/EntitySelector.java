package fr.atesab.xray.screen;

import fr.atesab.xray.color.EntityTypeInfo;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.text.TranslatableText;

public abstract class EntitySelector extends EnumSelector<EntityTypeInfo> {

    @SuppressWarnings("deprecation")
    public EntitySelector(Screen parent) {
        super(new TranslatableText("x13.mod.esp.selector"), parent,
                Registry.ENTITY_TYPE.stream().map(EntityTypeInfo::new));
    }

}
