package fr.atesab.xray.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class EntitySelector extends EnumSelector<XrayEntityMenu.EntityUnion> {

    public EntitySelector(Screen parent) {
        super(Text.translatable("x13.mod.esp.selector"), parent,
                Stream.concat(
                        Registries.ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new),
                        Registries.BLOCK_ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new)
                ).sorted(Comparator.comparing(XrayEntityMenu.EntityUnion::text))
        );
    }

}
