package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = Block.class)
public class MixinBlock {
	@Inject(at = @At("RETURN"), method = "shouldRenderFace(" + "Lnet/minecraft/world/level/block/state/BlockState;" + // state
			"Lnet/minecraft/world/level/BlockGetter;" + // reader
			"Lnet/minecraft/core/BlockPos;" + // pos
			"Lnet/minecraft/core/Direction;" + // face
			"Lnet/minecraft/core/BlockPos;" + // blockPosaaa
			")Z", // ci
			cancellable = true)
	private static void shouldRenderFace(BlockState state, BlockGetter reader, BlockPos pos, Direction face,
			BlockPos blockPosaaa, CallbackInfoReturnable<Boolean> ci) {
		XrayMain.getMod().shouldSideBeRendered(state, reader, pos, face, ci);
	}

	private MixinBlock() {
	}
}
