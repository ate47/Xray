package fr.atesab.xray.color;

import net.minecraft.entity.EntityType;
import net.minecraft.text.TranslatableText;

public class EntityTypeInfo extends AbstractEnumElement {
    private EntityType<?> type;

    public EntityTypeInfo(EntityType<?> type) {
        super(EntityTypeIcon.getIcon(type), new TranslatableText(type.getTranslationKey()));
        this.type = type;
    }

    public EntityType<?> getType() {
        return type;
    }
}