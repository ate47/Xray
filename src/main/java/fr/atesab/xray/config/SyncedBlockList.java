package fr.atesab.xray.config;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

public class SyncedBlockList extends SyncedRegistryList<Block> {

    private SyncedBlockList(SyncedRegistryList<Block> other) {
        super(other);
    }

    public SyncedBlockList() {
        super(Registry.BLOCK);
    }

    public SyncedBlockList(Block... objects) {
        super(objects, Registry.BLOCK);
    }

    public SyncedBlockList(List<Block> objects) {
        super(objects, Registry.BLOCK);
    }

    @Override
    public SyncedBlockList clone() {
        return new SyncedBlockList(this);
    }
}