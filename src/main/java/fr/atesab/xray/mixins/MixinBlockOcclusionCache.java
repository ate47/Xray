package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

//@Pseudo
@Mixin(value = BlockOcclusionCache.class)
public class MixinBlockOcclusionCache {
	@Inject(at = @At("RETURN"), method = "shouldDrawSide", cancellable = true, remap = false)
    public void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face,
            CallbackInfoReturnable<Boolean> ci) {
        XrayMain.getMod().shouldSideBeRendered(state, reader, pos, face, ci);
    }
}