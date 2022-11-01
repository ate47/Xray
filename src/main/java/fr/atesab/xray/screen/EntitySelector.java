package fr.atesab.xray.screen;

import fr.atesab.xray.color.EntityTypeInfo;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import java.util.Comparator;
import java.util.stream.Stream;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class EntitySelector extends EnumSelector<XrayEntityMenu.EntityUnion> {

    @SuppressWarnings("deprecation")
    public EntitySelector(Screen parent) {
        super(Component.translatable("x13.mod.esp.selector"), parent,
                Stream.concat(
                        Registry.ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new),
                        Registry.BLOCK_ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new)
                ).sorted(Comparator.comparing(XrayEntityMenu.EntityUnion::text))
        );
    }


}
