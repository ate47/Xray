package fr.atesab.xray.config;

import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.color.EntityTypeIcon;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class SyncedEntityTypeList extends SyncedRegistryList<EntityType<?>> {

    private SyncedEntityTypeList(SyncedRegistryList<EntityType<?>> other) {
        super(other);
    }

    public SyncedEntityTypeList() {
        super(Registry.ENTITY_TYPE);
    }

    public SyncedEntityTypeList(EntityType<?>... objects) {
        super(objects, Registry.ENTITY_TYPE);
    }

    public SyncedEntityTypeList(List<EntityType<?>> objects) {
        super(objects, Registry.ENTITY_TYPE);
    }

    public Stream<ItemStack> getIcons() {
        return getObjects().stream().map(EntityTypeIcon::getIcon);
    }

    @Override
    public SyncedEntityTypeList clone() {
        return new SyncedEntityTypeList(this);
    }
}