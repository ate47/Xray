package fr.atesab.x13.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.x13.XrayMain;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@Mixin(value = Block.class, priority = 500)
public class MixinBlock {
	@Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
	private static void shouldSideBeRendered(IBlockState state, IBlockReader reader, BlockPos pos, EnumFacing face,
			CallbackInfoReturnable<Boolean> ci) {
		XrayMain.getMod().shouldSideBeRendered(state, reader, pos, face, ci);
	}
}
