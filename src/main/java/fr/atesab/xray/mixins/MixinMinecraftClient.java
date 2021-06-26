package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.MinecraftClient;

@Mixin(value = MinecraftClient.class)
public class MixinMinecraftClient {

	@Inject(at = @At(value = "HEAD"), method = "isAmbientOcclusionEnabled()Z", cancellable = true)
	private static void isAmbientOcclusionEnabled(CallbackInfoReturnable<Boolean> ci) {
		if (XrayMain.getMod().getSelectedMode() != null) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}

	private MixinMinecraftClient() {
	}
}
