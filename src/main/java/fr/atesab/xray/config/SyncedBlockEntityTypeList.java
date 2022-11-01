package fr.atesab.xray.config;

import fr.atesab.xray.color.BlockEntityTypeIcon;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Stream;

public class SyncedBlockEntityTypeList extends SyncedRegistryList<BlockEntityType<?>> {

    private SyncedBlockEntityTypeList(SyncedRegistryList<BlockEntityType<?>> other) {
        super(other);
    }

    public SyncedBlockEntityTypeList() {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES);
    }

    public SyncedBlockEntityTypeList(BlockEntityType<?>... objects) {
        super(objects, ForgeRegistries.BLOCK_ENTITY_TYPES);
    }

    public SyncedBlockEntityTypeList(List<BlockEntityType<?>> objects) {
        super(objects, ForgeRegistries.BLOCK_ENTITY_TYPES);
    }

    public Stream<ItemStack> getIcons() {
        return getObjects().stream().map(BlockEntityTypeIcon::getIcon);
    }

    @Override
    public SyncedBlockEntityTypeList clone() {
        return new SyncedBlockEntityTypeList(this);
    }
}