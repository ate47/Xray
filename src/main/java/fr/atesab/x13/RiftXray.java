package fr.atesab.x13;

import org.apache.commons.lang3.ArrayUtils;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.dimdev.rift.listener.client.OverlayRenderer;
import org.dimdev.riftloader.listener.InitializationListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * A class to interact between Xray and Rift
 */
public class RiftXray implements KeybindHandler, InitializationListener, OverlayRenderer, BuildAPI {
	private XrayMain mod;

	public RiftXray() {
		if (!XrayMain.isAPIRegister())
			this.mod = XrayMain.registerAPI(this);
	}

	@Override
	public void onInitialization() {
		if (mod != null)
			mod.preInit();
	}

	@Override
	public void renderOverlay() {
		if (mod != null)
			mod.renderOverlay();
	}

	@Override
	public void processKeybinds() {
		if (mod != null)
			mod.processKeybinds();
	}

	@Override
	public void registerKeys(KeyBinding... keys) {
		Minecraft mc = Minecraft.getInstance();
		mc.gameSettings.keyBindings = ArrayUtils.addAll(mc.gameSettings.keyBindings, keys);

	}

	@Override
	public String getAPIName() {
		return "Rift";
	}

}
