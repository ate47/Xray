package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.atesab.xray.utils.TagOnWriteList;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;

public class SyncedBlockList extends TagOnWriteList<String> {

    private List<Block> blocks = new ArrayList<>();

    public SyncedBlockList() {
        super(new ArrayList<>());
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @SuppressWarnings("deprecation")
    public void setBlocks(List<Block> blocks) {
        setTagEnabled(false);
        this.blocks = blocks;
        clear();
        blocks.stream().map(Registry.BLOCK::getKey).map(Object::toString).forEach(this::add);
        setTagEnabled(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onUpdate() {
        blocks.clear();
        stream().map(ResourceLocation::new).map(Registry.BLOCK::get).filter(Objects::nonNull).forEach(blocks::add);
        removeUpdated();
    }

}
