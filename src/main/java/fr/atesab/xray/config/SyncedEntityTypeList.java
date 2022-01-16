package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import fr.atesab.xray.color.EntityTypeIcon;
import fr.atesab.xray.utils.TagOnWriteList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class SyncedEntityTypeList extends TagOnWriteList<String> implements Cloneable {

    private List<EntityType<?>> entities = new ArrayList<>();

    public SyncedEntityTypeList() {
        super(new ArrayList<>());
    }

    private SyncedEntityTypeList(SyncedEntityTypeList other) {
        super(new ArrayList<>(other));
        this.entities.addAll(other.entities);
    }

    public SyncedEntityTypeList(EntityType<?>... entities) {
        this(Arrays.asList(entities));
    }

    public SyncedEntityTypeList(List<EntityType<?>> entities) {
        super(new ArrayList<>());
        setEntities(entities);
    }

    public List<EntityType<?>> getEntities() {
        return entities;
    }

    @SuppressWarnings("deprecation")
    public void setEntities(List<EntityType<?>> entities) {
        setTagEnabled(false);
        this.entities = new ArrayList<>(entities);
        clear();
        entities.stream().filter(Objects::nonNull).map(Registry.ENTITY_TYPE::getKey).map(Object::toString)
                .forEach(this::add);
        setTagEnabled(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onUpdate() {
        entities.clear();
        stream().map(ResourceLocation::new).map(Registry.ENTITY_TYPE::get).filter(Objects::nonNull)
                .forEach(entities::add);
        removeUpdated();
    }

    @Override
    public SyncedEntityTypeList clone() {
        return new SyncedEntityTypeList(this);
    }

    public Stream<ItemStack> getIcons() {
        return getEntities().stream().map(EntityTypeIcon::getIcon);
    }
}