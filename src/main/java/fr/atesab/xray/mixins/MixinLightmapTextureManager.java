package fr.atesab.xray.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.atesab.xray.XrayMain;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(value = LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;gamma*:D", opcode = Opcodes.GETFIELD), method = "update(F)V")
	private double getFieldValue(GameOptions options) {
		if (XrayMain.getMod().isInternalFullbrightEnable()) {
			return XrayMain.getMod().getInternalFullbrightState();
		} else {
			return options.gamma;
		}
	}
}