package fr.atesab.xray.screen;

import fr.atesab.xray.color.EntityTypeInfo;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class EntitySelector extends EnumSelector<XrayEntityMenu.EntityUnion> {

    public EntitySelector(Screen parent) {
        super(Text.translatable("x13.mod.esp.selector"), parent,
                Stream.concat(
                        Registry.ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new),
                        Registry.BLOCK_ENTITY_TYPE.stream().map(XrayEntityMenu.EntityUnion::new)
                ).sorted(Comparator.comparing(XrayEntityMenu.EntityUnion::text))
        );
    }

}
