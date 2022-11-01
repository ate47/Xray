package fr.atesab.xray.color;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeInfo extends AbstractEnumElement {
    private static Component fromType(BlockEntityType<?> type) {
        ResourceLocation id = Registry.BLOCK_ENTITY_TYPE.getKey(type);
        if (id == null) {
            return Component.literal(type.toString());
        }
        return Component.literal(id.toString());
    }

    private BlockEntityType<?> type;

    public BlockEntityTypeInfo(BlockEntityType<?> type) {
        super(BlockEntityTypeIcon.getIcon(type), fromType(type));
        this.type = type;
    }


    public BlockEntityType<?> getType() {
        return type;
    }
}