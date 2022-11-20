package fr.atesab.xray.config;

import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.color.EntityTypeIcon;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncedEntityTypeList extends SyncedRegistryList<EntityType<?>> {

    private SyncedEntityTypeList(SyncedRegistryList<EntityType<?>> other) {
        super(other);
    }

    public SyncedEntityTypeList() {
        super(ForgeRegistries.ENTITY_TYPES);
    }

    public SyncedEntityTypeList(EntityType<?>... objects) {
        super(objects, ForgeRegistries.ENTITY_TYPES);
    }

    public SyncedEntityTypeList(List<EntityType<?>> objects) {
        super(objects, ForgeRegistries.ENTITY_TYPES);
    }

    public Stream<ItemStack> getIcons() {
        return getObjects().stream().map(EntityTypeIcon::getIcon);
    }

    @Override
    public SyncedEntityTypeList clone() {
        return new SyncedEntityTypeList(this);
    }
}