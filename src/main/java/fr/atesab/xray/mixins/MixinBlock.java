package fr.atesab.xray.mixins;

import fr.atesab.xray.XrayMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class)
public class MixinBlock {
	@Inject(at = @At("RETURN"), method = "shouldDrawSide(" + "Lnet/minecraft/block/BlockState;" + // state
			"Lnet/minecraft/block/BlockState;" + // adjacentState
			"Lnet/minecraft/util/math/Direction;" + // side (unused)
			")Z", // ci
			cancellable = true)
	private static void shouldDrawSide(BlockState state, BlockState adjacentState, Direction side,
			CallbackInfoReturnable<Boolean> ci) {
		XrayMain.getMod().shouldSideBeRendered(state, adjacentState, ci);
	}

	private MixinBlock() {
	}
}