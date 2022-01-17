package fr.atesab.xray;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;

@FunctionalInterface
public interface SideRenderer {
	public void shouldSideBeRendered(
			BlockState adjacentState, BlockView blockState, BlockPos blockAccess,
			Direction pos, CallbackInfoReturnable<Boolean> ci);
}
