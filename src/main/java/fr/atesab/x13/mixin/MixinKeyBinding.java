package fr.atesab.x13.mixin;

import java.util.Map;
import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

@Mixin(value = KeyBinding.class, priority = 500)
public class MixinKeyBinding {
	@Shadow
	@Final
	public static Map<String, Integer> CATEGORY_ORDER;

	@Inject(method = "<init>*", at = @At("RETURN"))
	public void onConstruct(String name, InputMappings.Type type, int key, String cat, CallbackInfo ci) {
		// allow create new Category
		if (!CATEGORY_ORDER.containsKey(cat))
			CATEGORY_ORDER.put(cat, getInt(CATEGORY_ORDER.entrySet().stream().mapToInt(e -> e.getValue()).max()) + 1);
	}

	private static int getInt(OptionalInt i) {
		return i.isPresent() ? i.getAsInt() : 0;
	}
}
