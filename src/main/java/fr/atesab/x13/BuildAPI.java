package fr.atesab.x13;

import net.minecraft.client.settings.KeyBinding;

/**
 * A class to define what an API need to run XrayMod
 */
public interface BuildAPI {
	/**
	 * register keys in Minecraft
	 */
	public void registerKeys(KeyBinding... keys);

	/**
	 * the API name (Rift, Forge, (...))
	 */
	public String getAPIName();
}
