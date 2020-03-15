package fr.atesab.xray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;

import fr.atesab.xray.XrayMode.ViewMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class XrayMain implements ClientModInitializer, HudRenderCallback, ClientTickCallback {
	private static final Logger log = LogManager.getLogger("XrayMod");
	private static XrayMain instance;

	public static <T> T getBlockNamesCollected(Collection<Block> blocks, Collector<CharSequence, ?, T> collector) {
		return blocks.stream().filter(b -> !net.minecraft.block.Blocks.AIR.equals(b)).map(Registry.BLOCK::getId)
				.filter(Objects::nonNull).map(Objects::toString).collect(collector);
	}

	/**
	 * get a list of block names from a list of blocks
	 */
	public static List<CharSequence> getBlockNamesToList(Collection<Block> blocks) {
		return getBlockNamesCollected(blocks, Collectors.toList());
	}

	/**
	 * get a String of a list of block names join by space from a list of blocks
	 */
	public static String getBlockNamesToString(Collection<Block> blocks) {
		return getBlockNamesCollected(blocks, Collectors.joining(" "));
	}

	/**
	 * get this mod
	 */
	public static XrayMain getMod() {
		return instance;
	}

	private static void log(String message) {
		log.info("[" + log.getName() + "] " + message);
	}

	private static String significantNumbers(double d) {
		boolean a;
		if (a = d < 0) {
			d *= -1;
		}
		int d1 = (int) (d);
		d %= 1;
		String s = String.format("%.3G", d);
		if (s.length() > 0)
			s = s.substring(1);
		if (s.contains("E+"))
			s = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%.3G", d)));
		return (a ? "-" : "") + d1 + s;
	}

	private List<XrayMode> modes = Lists.newArrayList();
	private List<String> customModes = Lists.newArrayList();
	private double oldGama;
	private boolean fullBrightEnable = false;
	private boolean internalFullbrightEnable = false;
	private boolean showLocation = true;
	private FabricKeyBinding fullbright, config;

	private int fullbrightColor = 0;

	public XrayMain() {
		instance = this;
	}

	/**
	 * Toggle fullBright
	 */
	public XrayMain fullBright() {
		return fullBright(!fullBrightEnable);
	}

	/**
	 * Set fullBright
	 */
	public XrayMain fullBright(boolean enable) {
		fullBrightEnable = enable;
		return internalFullbright();
	}

	/**
	 * Get all registered modes
	 */
	public List<XrayMode> getModes() {
		return modes;
	}

	/**
	 * Mod config file
	 */
	public File getSaveFile() {
		return new File(MinecraftClient.getInstance().runDirectory, "xray.json");
	}

	/**
	 * load internal fullbright by checking if a mode is enabled
	 */
	public XrayMain internalFullbright() {
		MinecraftClient mc = MinecraftClient.getInstance();
		boolean f = fullBrightEnable;
		for (XrayMode mode : modes)
			if (f = (f || mode.isEnabled()))
				break;
		if (f) {
			if (!internalFullbrightEnable) {
				oldGama = mc.options.gamma;
				mc.options.gamma = 30;
			}
		} else
			mc.options.gamma = oldGama;
		internalFullbrightEnable = f;
		return this;
	}

	public boolean isFullBrightEnable() {
		return fullBrightEnable;
	}

	public boolean isInternalFullbrightEnable() {
		return internalFullbrightEnable;
	}

	public boolean isShowLocation() {
		return showLocation;
	}

	/**
	 * Load mod configs
	 */
	@SuppressWarnings("unchecked")
	public void loadConfigs() {
		try {
			Map<String, Object> map = new GsonBuilder().create().fromJson(
					new InputStreamReader(new FileInputStream(getSaveFile()), Charset.forName("UTF-8")), HashMap.class);
			for (XrayMode mode : modes) {
				Object blocks = map.get(mode.getName() + "Blocks");
				if (blocks != null)
					mode.setConfig(((List<String>) blocks).stream().toArray(String[]::new));
			}
			fullBrightEnable = (boolean) map.getOrDefault("fullBright", false);
			internalFullbrightEnable = fullBrightEnable
					|| (boolean) map.getOrDefault("internalFullbrightEnable", false);
			oldGama = (double) map.getOrDefault("oldGama", 0D);
			showLocation = (boolean) map.getOrDefault("showLocation", true);
			customModes = ((List<String>) map.getOrDefault("customModes", Lists.<String>newArrayList()));
			registerXrayMode(customModes.stream().map(customMode -> {
				String[] d = customMode.split(":", 2);
				ViewMode view = null;
				if (d.length == 2)
					view = XrayMode.ViewMode.valueOf(d[1]);
				Object blocks = map.get(d[0] + "Blocks");
				XrayMode mode = new XrayMode(XrayMode.CUSTOM_PREFIX + d[0], -1,
						view == null ? XrayMode.ViewMode.EXCLUSIVE : view);
				if (blocks != null)
					mode.setConfig(((List<String>) blocks).stream().toArray(String[]::new));
				return mode;
			}).toArray(XrayMode[]::new));
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveConfigs();
	}

	/**
	 * Reload modules
	 */
	public XrayMain modules() {
		for (XrayMode mode : modes)
			mode.toggle(mode.isEnabled(), false);
		fullBright(isFullBrightEnable());
		MinecraftClient.getInstance().worldRenderer.reload();
		return this;
	}

	/**
	 * Render the overlay
	 */
	@Override
	public void onHudRender(float tickDelta) {
		int c;
		String s;
		nameFinder: {
			for (XrayMode mode : modes)
				if (mode.isEnabled()) {
					c = mode.getColor();
					s = mode.getNameTranslate();
					break nameFinder;
				}
			if (fullBrightEnable) {
				c = fullbrightColor;
				s = I18n.translate("x13.mod.fullbright");
			} else {
				c = 0xffffffff;
				s = "";
			}
		}
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer render = mc.textRenderer;
		ClientPlayerEntity player = mc.player;
		if (!s.isEmpty())
			render.drawWithShadow(s = "[" + s + "] ", 5, 5, c);
		if (showLocation && player != null) {
			Vec3d pos = player.getPosVector();
			render.drawWithShadow("XYZ: " + (significantNumbers(pos.x) + " / " + significantNumbers(pos.y) + " / "
					+ significantNumbers(pos.z)), 5 + render.getStringWidth(s), 5, 0xffffffff);
		}
	}

	@Override
	public void onInitializeClient() {
		log("Initialization");

		KeyBindingRegistry.INSTANCE.addCategory("key.categories.xray");

		KeyBindingRegistry.INSTANCE.register(fullbright = FabricKeyBinding.Builder
				.create(new Identifier("xray:fullbright"), Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.xray")
				.build());
		KeyBindingRegistry.INSTANCE.register(config = FabricKeyBinding.Builder
				.create(new Identifier("xray:config"), Type.KEYSYM, GLFW.GLFW_KEY_N, "key.categories.xray").build());

		registerXrayMode(
				// Xray Mode
				new XrayMode("xray", 88, ViewMode.EXCLUSIVE, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.DIAMOND_ORE,
						Blocks.GOLD_ORE, Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.OBSIDIAN, Blocks.DIAMOND_BLOCK,
						Blocks.IRON_ORE, Blocks.GOLD_BLOCK, Blocks.EMERALD_BLOCK, Blocks.END_PORTAL,
						Blocks.END_PORTAL_FRAME, Blocks.NETHER_PORTAL, Blocks.BEACON, Blocks.SPAWNER, Blocks.BOOKSHELF,
						Blocks.LAVA, Blocks.WATER, Blocks.NETHER_WART, Blocks.BLUE_ICE, Blocks.DRAGON_WALL_HEAD,
						Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG, Blocks.NETHER_QUARTZ_ORE, Blocks.CHEST,
						Blocks.TRAPPED_CHEST, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
						Blocks.TNT, Blocks.CLAY, Blocks.WET_SPONGE, Blocks.SPONGE, Blocks.OAK_PLANKS, Blocks.CONDUIT,
						Blocks.ENDER_CHEST, Blocks.MAGMA_BLOCK, Blocks.SCAFFOLDING, Blocks.HONEY_BLOCK,
						Blocks.HONEYCOMB_BLOCK, Blocks.BEE_NEST, Blocks.BEEHIVE, Blocks.BEETROOTS),
				// Cave Mode
				new XrayMode("cave", 67, ViewMode.INCLUSIVE, Blocks.DIRT, Blocks.GRASS, Blocks.GRAVEL,
						Blocks.GRASS_BLOCK, Blocks.GRASS_PATH, Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SAND),
				// Redstone mode
				new XrayMode("redstone", 82, ViewMode.EXCLUSIVE, Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_LAMP,
						Blocks.REDSTONE_ORE, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.REDSTONE_WIRE,
						Blocks.REPEATER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK,
						Blocks.CHAIN_COMMAND_BLOCK, Blocks.COMPARATOR, Blocks.ANVIL, Blocks.CHEST, Blocks.TRAPPED_CHEST,
						Blocks.DROPPER, Blocks.DISPENSER, Blocks.HOPPER, Blocks.OBSERVER, Blocks.DRAGON_HEAD,
						Blocks.DRAGON_WALL_HEAD, Blocks.IRON_DOOR, Blocks.ACACIA_DOOR, Blocks.BIRCH_DOOR,
						Blocks.DARK_OAK_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
						Blocks.ACACIA_BUTTON, Blocks.BIRCH_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.JUNGLE_BUTTON,
						Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.STONE_BUTTON, Blocks.LEVER, Blocks.TNT,
						Blocks.PISTON, Blocks.PISTON_HEAD, Blocks.MOVING_PISTON, Blocks.STICKY_PISTON,
						Blocks.NOTE_BLOCK, Blocks.DAYLIGHT_DETECTOR, Blocks.IRON_TRAPDOOR, Blocks.ACACIA_TRAPDOOR,
						Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.OAK_TRAPDOOR,
						Blocks.SPRUCE_TRAPDOOR, Blocks.ACACIA_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE,
						Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
						Blocks.JUNGLE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE,
						Blocks.SPRUCE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL,
						Blocks.DETECTOR_RAIL, Blocks.POWERED_RAIL, Blocks.ENDER_CHEST, Blocks.LECTERN));
		fullbrightColor = XrayMode.nextColor();
		loadConfigs();
		HudRenderCallback.EVENT.register(this);
		ClientTickCallback.EVENT.register(this);

		MixinBootstrap.init();
		log("Search Optifine...");
		try {
			Class.forName("net.optifine.Lang"); // search Optifine
			log("Load Mixins for Optifine...");
			Mixins.addConfiguration("optiray.mixins.json");
		} catch (ClassNotFoundException e) {
			log("Load Mixins without Optifine...");
			Mixins.addConfiguration("xray.mixins.json");
		}

	}

	/**
	 * Process modes and Xray keys
	 */
	@Override
	public void tick(MinecraftClient client) {
		if (client.currentScreen != null)
			return;
		for (XrayMode mode : modes)
			if (mode.toggleKey())
				return;
		if (fullbright.isPressed()) {
			fullBright();
			fullbright.setPressed(false);
		} else if (config.isPressed()) {
			MinecraftClient.getInstance().openScreen(new XrayMenu(null));
			config.setPressed(false);
		}
	}

	/**
	 * Register mode(s) and keybinding(s)
	 */
	public void registerXrayMode(XrayMode... modes) {
		Arrays.stream(modes).forEach(mode -> {
			this.modes.add(mode);
			KeyBindingRegistry.INSTANCE.register(mode.getKey());
		});
	}

	/**
	 * Save mod configs
	 */
	public void saveConfigs() {
		try {
			Writer writer = new FileWriterWithEncoding(getSaveFile(), Charset.forName("UTF-8"));
			new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create()
					.toJson(Util.make(Maps.newHashMap(), m -> {
						modes.forEach(mode -> m.put(mode.getName() + "Blocks", getBlockNamesToList(mode.getBlocks())));
						m.put("showLocation", showLocation);
						m.put("oldGama", oldGama);
						m.put("internalFullbrightEnable", internalFullbrightEnable);
						m.put("fullBrightEnable", fullBrightEnable);
						m.put("customModes", customModes.stream().map(
								s -> s.split(":", 2).length == 2 ? s : s + ":" + XrayMode.ViewMode.EXCLUSIVE.name())
								.collect(Collectors.toList()));
					}), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
	}

	/**
	 * True if the side should be rendered
	 */
	public void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face,
			CallbackInfoReturnable<Boolean> ci) {
		for (XrayMode mode : modes)
			mode.shouldSideBeRendered(state, reader, pos, face, ci);
	}

}
