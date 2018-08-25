package fr.atesab.x13.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.x13.X13Main;
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
		X13Main mod = X13Main.getX13();
		if (mod.isXrayEnable()) {
			ci.setReturnValue(mod.getXrayBlocks().contains(state.getBlock()));
		} else if (mod.isCaveEnable()) {
			ci.setReturnValue(!(mod.getCaveBlocks().contains(state.getBlock()))
					&& reader.getBlockState(pos.offset(face)).isAir());
		}
	}
}
