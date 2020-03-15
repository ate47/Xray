package fr.atesab.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.optifine.render.RenderEnv;

@Pseudo
@Mixin(targets = "net.optifine.util.BlockUtils")
public class MixinBlockUtils {
	@Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true, remap = false)
	private static void shouldSideBeRendered(BlockState state, BlockView reader, BlockPos pos, Direction face,
			RenderEnv env, CallbackInfoReturnable<Boolean> ci) {
		XrayMain.getMod().shouldDrawSide(state, reader, pos, face, ci);
	}
}
