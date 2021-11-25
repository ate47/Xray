package fr.atesab.xray.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.LightTexture;

@Mixin(value = LightTexture.class)
public class MixinLightmapTextureManager {

	@Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;gamma*:D", opcode = Opcodes.GETFIELD), method = "updateLightTexture(F)V")
	private double getFieldValue(Options options) {
		if (XrayMain.getMod().isInternalFullbrightEnable()) {
			return XrayMain.getMod().getInternalFullbrightState();
		} else {
			return options.gamma;
		}
	}
}
