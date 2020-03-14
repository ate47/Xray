package fr.atesab.xray;


import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface SideRenderer {
	public void shouldSideBeRendered(BlockState adjacentState, IBlockReader blockState, BlockPos blockAccess, Direction pos,
			CallbackInfo<Boolean> ci);
}
