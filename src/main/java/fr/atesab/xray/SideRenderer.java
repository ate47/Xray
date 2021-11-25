package fr.atesab.xray;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface SideRenderer {
	public void shouldSideBeRendered(BlockState adjacentState, BlockGetter blockState, BlockPos blockAccess,
			Direction pos, CallbackInfoReturnable<Boolean> ci);
}
