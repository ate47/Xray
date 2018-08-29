package fr.atesab.x13.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.atesab.x13.XrayMain;
import net.minecraft.client.Minecraft;

@Mixin(value = Minecraft.class, priority = 500)
public abstract class MixinMinecraft {

	@Inject(at = @At("RETURN"), method = "init", cancellable = true)
	private void init(CallbackInfo ci) {
		XrayMain.getMod().init();
	}
}
