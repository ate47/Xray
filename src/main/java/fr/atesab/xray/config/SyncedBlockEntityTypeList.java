package fr.atesab.xray.config;

import fr.atesab.xray.color.BlockEntityTypeIcon;
import fr.atesab.xray.color.EntityTypeIcon;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Stream;

public class SyncedBlockEntityTypeList extends SyncedRegistryList<BlockEntityType<?>> {

    private SyncedBlockEntityTypeList(SyncedRegistryList<BlockEntityType<?>> other) {
        super(other);
    }

    public SyncedBlockEntityTypeList() {
        super(Registry.BLOCK_ENTITY_TYPE);
    }

    public SyncedBlockEntityTypeList(BlockEntityType<?>... objects) {
        super(objects, Registry.BLOCK_ENTITY_TYPE);
    }

    public SyncedBlockEntityTypeList(List<BlockEntityType<?>> objects) {
        super(objects, Registry.BLOCK_ENTITY_TYPE);
    }

    public Stream<ItemStack> getIcons() {
        return getObjects().stream().map(BlockEntityTypeIcon::getIcon);
    }

    @Override
    public SyncedBlockEntityTypeList clone() {
        return new SyncedBlockEntityTypeList(this);
    }
}