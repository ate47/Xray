package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.Minecraft;

@Mixin(value = Minecraft.class)
public class MixinMinecraftClient {

	@Inject(at = @At(value = "HEAD"), method = "useAmbientOcclusion()Z", cancellable = true)
	private static void isAmbientOcclusionEnabled(CallbackInfoReturnable<Boolean> ci) {
		if (XrayMain.getMod().isInternalFullbrightEnable()) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}

	private MixinMinecraftClient() {
	}
}
