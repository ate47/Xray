package fr.atesab.xray;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@FunctionalInterface
public interface SideRenderer {
	public void shouldSideBeRendered(BlockState state, BlockView reader, BlockPos pos, Direction face,
			CallbackInfoReturnable<Boolean> ci);
}
