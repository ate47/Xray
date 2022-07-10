package fr.atesab.xray.mixins;

import net.minecraft.client.OptionInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.LightTexture;

@Mixin(value = LightTexture.class)
public class MixinLightmapTextureManager {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;", opcode = Opcodes.INVOKEVIRTUAL), method = "updateLightTexture(F)V")
	private OptionInstance<Double> getFieldValue(Options options) {
		if (XrayMain.getMod().isInternalFullbrightEnable()) {
			return XrayMain.getMod().getGammaBypass();
		} else {
			return options.gamma();
		}
	}
}
