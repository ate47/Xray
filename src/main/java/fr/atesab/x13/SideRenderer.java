package fr.atesab.x13;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface SideRenderer {
	public void shouldSideBeRendered(IBlockState state, IBlockReader reader, BlockPos pos, EnumFacing face,
			CallbackInfoReturnable<Boolean> ci);
}
