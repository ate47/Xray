package fr.atesab.xray.screen;

import fr.atesab.xray.color.EntityTypeInfo;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.stream.Stream;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class EntitySelector extends EnumSelector<XrayEntityMenu.EntityUnion> {

    public EntitySelector(Screen parent) {
        super(Component.translatable("x13.mod.esp.selector"), parent,
                Stream.concat(
                        ForgeRegistries.ENTITY_TYPES.getValues().stream().map(XrayEntityMenu.EntityUnion::new),
                        ForgeRegistries.BLOCK_ENTITY_TYPES.getValues().stream().map(XrayEntityMenu.EntityUnion::new)
                ).sorted(Comparator.comparing(XrayEntityMenu.EntityUnion::text))
        );
    }


}
