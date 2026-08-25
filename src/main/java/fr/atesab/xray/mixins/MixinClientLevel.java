package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

@Mixin(value = ClientLevel.class)
public class MixinClientLevel {
	@Inject(method = "getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
	public void getSkyColorMixin(Vec3 pPos, float pPartialTick, CallbackInfoReturnable<Vec3> info) {
		if (XrayMain.getMod().isBlueBlueSkyEnable()) {
			info.setReturnValue(new Vec3(((double)121 / 255), ((double)167 / 255), (double)1));
		}
	}
	
	private MixinClientLevel() {
	}
}
