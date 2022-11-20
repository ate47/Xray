package fr.atesab.xray.config;

import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncedBlockList extends SyncedRegistryList<Block> {

    private SyncedBlockList(SyncedRegistryList<Block> other) {
        super(other);
    }

    public SyncedBlockList() {
        super(ForgeRegistries.BLOCKS);
    }

    public SyncedBlockList(Block... objects) {
        super(objects, ForgeRegistries.BLOCKS);
    }

    public SyncedBlockList(List<Block> objects) {
        super(objects, ForgeRegistries.BLOCKS);
    }

    @Override
    public SyncedBlockList clone() {
        return new SyncedBlockList(this);
    }
}