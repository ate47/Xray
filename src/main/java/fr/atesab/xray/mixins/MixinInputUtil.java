package fr.atesab.xray.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.utils.KeyInput;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

@Mixin(value = Keyboard.class)
public class MixinInputUtil {

    @Inject(at = @At(value = "TAIL"), method = "onKey", cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getWindow().getHandle() != window)
            return;
        XrayMain.getMod().onKeyEvent(new KeyInput(key, scancode, action, modifiers));
    }
}
