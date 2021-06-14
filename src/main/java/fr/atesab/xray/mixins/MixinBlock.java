package fr.atesab.xray.mixins;

import fr.atesab.xray.XrayMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * net.minecraft.block.BlockState state, 
 * net.minecraft.world.BlockView world, 
 * net.minecraft.util.math.BlockPos pos,
 * net.minecraft.util.math.Direction side,
 * net.minecraft.util.math.BlockPos blockPos
 */
@Mixin(value = Block.class)
public class MixinBlock {
	@Inject(
			at = @At("RETURN"),
			method = "shouldDrawSide(" +
					"Lnet/minecraft/block/BlockState;" +
					"Lnet/minecraft/world/BlockView;" +
					"Lnet/minecraft/util/math/BlockPos;" +
					"Lnet/minecraft/util/math/Direction;" +
					"Lnet/minecraft/util/math/BlockPos;" +
					")Z",
			cancellable = true
	)
	private static void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face, 
			BlockPos blockPos, CallbackInfoReturnable<Boolean> ci) {
		XrayMain.shouldSideBeRendered(state, reader, pos, face, ci);
	}
}
