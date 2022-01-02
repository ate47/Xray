package fr.atesab.xray.view;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface Viewer {
    public boolean shouldRenderSide(boolean blockInList, BlockState adjacentState, BlockGetter blockState,
            BlockPos blockAccess, Direction pos);
}
