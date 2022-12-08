package fr.atesab.xray.config;

import fr.atesab.xray.color.BlockEntityTypeIcon;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.stream.Stream;

public class SyncedBlockEntityTypeList extends SyncedRegistryList<BlockEntityType<?>> {

    private SyncedBlockEntityTypeList(SyncedRegistryList<BlockEntityType<?>> other) {
        super(other);
    }

    public SyncedBlockEntityTypeList() {
        super(Registries.BLOCK_ENTITY_TYPE);
    }

    public SyncedBlockEntityTypeList(BlockEntityType<?>... objects) {
        super(objects, Registries.BLOCK_ENTITY_TYPE);
    }

    public SyncedBlockEntityTypeList(List<BlockEntityType<?>> objects) {
        super(objects, Registries.BLOCK_ENTITY_TYPE);
    }

    public Stream<ItemStack> getIcons() {
        return getObjects().stream().map(BlockEntityTypeIcon::getIcon);
    }

    @Override
    public SyncedBlockEntityTypeList clone() {
        return new SyncedBlockEntityTypeList(this);
    }
}