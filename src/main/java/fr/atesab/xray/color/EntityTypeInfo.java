package fr.atesab.xray.color;

import net.minecraft.world.entity.EntityType;

public class EntityTypeInfo extends AbstractEnumElement {
    private EntityType<?> type;

    public EntityTypeInfo(EntityType<?> type) {
        super(EntityTypeIcon.getIcon(type), type.getDescription());
        this.type = type;
    }

    public EntityType<?> getType() {
        return type;
    }
}