package fr.atesab.xray.color;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockEntityTypeInfo extends AbstractEnumElement {
    private static Component fromType(BlockEntityType<?> type) {
        ResourceLocation id = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(type);
        if (id == null) {
            return Component.literal(type.toString());
        }
        return Component.literal(id.toString());
    }

    private final BlockEntityType<?> type;

    public BlockEntityTypeInfo(BlockEntityType<?> type) {
        super(BlockEntityTypeIcon.getIcon(type), fromType(type));
        this.type = type;
    }


    public BlockEntityType<?> getType() {
        return type;
    }
}