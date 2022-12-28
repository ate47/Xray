package fr.atesab.xray.view;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;

@FunctionalInterface
public interface Viewer {
    boolean shouldRenderSide(boolean blockInList, BlockState adjacentState, BlockView blockState,
            BlockPos blockAccess, Direction pos);
}
