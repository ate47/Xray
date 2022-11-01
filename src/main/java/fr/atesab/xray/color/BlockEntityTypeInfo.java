package fr.atesab.xray.color;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypeInfo extends AbstractEnumElement {
    private static Text fromType(BlockEntityType<?> type) {
        Identifier id = Registry.BLOCK_ENTITY_TYPE.getId(type);
        if (id == null) {
            return Text.literal(type.toString());
        }
        return Text.literal(id.toString());
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