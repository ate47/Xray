package fr.atesab.x13;

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
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;

import fr.atesab.x13.XrayMode.ViewMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class XrayMain {
	private static final Logger log = LogManager.getLogger("X13");
	private static XrayMain instance;

	public static <T> T getBlockNamesCollected(Collection<Block> blocks, Collector<CharSequence, ?, T> collector) {
		return blocks.stream().filter(b -> !Blocks.AIR.equals(b)).map(Block.REGISTRY::getNameForObject)
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

	/**
	 * true if an API is already register for it
	 */
	public static boolean isAPIRegister() {
		return instance != null;
	}

	/**
	 * register an API for Xray
	 * 
	 * @throws IllegalStateException
	 *             if an API is already register for it
	 */
	public static XrayMain registerAPI(BuildAPI api) throws IllegalStateException {
		instance = new XrayMain(api);
		log("Starting Xray with " + api.getAPIName());
		return instance;
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

	private BuildAPI api;

	private List<XrayMode> modes = Lists.newArrayList();
	private List<String> customModes = Lists.newArrayList();
	private double oldGama;
	private boolean fullBrightEnable = false;
	private boolean internalFullbrightEnable = false;
	private boolean showLocation = true;
	private KeyBinding fullbright, config;
	private boolean isInit = false;
	private boolean isPreInit = false;
	private int fullbrightColor = 0;

	private XrayMain(BuildAPI api) {
		if (isAPIRegister())
			throw new IllegalStateException("An API is already register for this mod!");
		this.api = Objects.requireNonNull(api, "You must provide a non-null API");
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
		return new File(Minecraft.getMinecraft().gameDir, "xray.json");
	}

	/**
	 * init Xray
	 */
	public void init() {
		if (isInit)
			return;
		log("Initialization");
		Minecraft mc = Minecraft.getMinecraft();
		registerXrayMode(
				// Xray Mode
				new XrayMode("xray", 88, ViewMode.EXCLUSIVE, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.DIAMOND_ORE,
						Blocks.GOLD_ORE, Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.OBSIDIAN, Blocks.DIAMOND_BLOCK,
						Blocks.IRON_ORE, Blocks.GOLD_BLOCK, Blocks.EMERALD_BLOCK, Blocks.END_PORTAL,
						Blocks.END_PORTAL_FRAME, Blocks.PORTAL, Blocks.BEACON, Blocks.MOB_SPAWNER, Blocks.BOOKSHELF,
						Blocks.LAVA, Blocks.WATER, Blocks.NETHER_WART, Blocks.BLUE_ICE, Blocks.DRAGON_WALL_HEAD,
						Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG, Blocks.NETHER_QUARTZ_ORE, Blocks.CHEST,
						Blocks.TRAPPED_CHEST, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
						Blocks.TNT, Blocks.CLAY, Blocks.WET_SPONGE, Blocks.SPONGE, Blocks.OAK_PLANKS, Blocks.CONDUIT,
						Blocks.ENDER_CHEST),
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
						Blocks.DETECTOR_RAIL, Blocks.POWERED_RAIL, Blocks.ENDER_CHEST));
		api.registerKeys(fullbright = new KeyBinding("x13.mod.fullbright", 72, "key.categories.xray"), // H
				config = new KeyBinding("x13.mod.config", 78, "key.categories.xray")); // N
		fullbrightColor = XrayMode.nextColor();
		loadConfigs();
		mc.gameSettings.loadOptions();
		isInit = true;
	}

	/**
	 * load internal fullbright by checking if a mode is enabled
	 */
	public XrayMain internalFullbright() {
		Minecraft mc = Minecraft.getMinecraft();
		boolean f = fullBrightEnable;
		for (XrayMode mode : modes)
			if (f = (f || mode.isEnabled()))
				break;
		if (f) {
			if (!internalFullbrightEnable) {
				oldGama = mc.gameSettings.gammaSetting;
				mc.gameSettings.gammaSetting = 30;
			}
		} else
			mc.gameSettings.gammaSetting = oldGama;
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

	private static void log(String message) {
		log.info("[" + log.getName() + "] " + message);
	}

	/**
	 * Reload modules
	 */
	public XrayMain modules() {
		for (XrayMode mode : modes)
			mode.toggle(mode.isEnabled(), false);
		fullBright(isFullBrightEnable());
		Minecraft.getMinecraft().renderGlobal.loadRenderers();
		return this;
	}

	/**
	 * pre-init Xray
	 */

	public void preInit() {
		if (isPreInit)
			return;
		log("Load Mixins...");
		MixinBootstrap.init();
		log("Search Optifine...");
		try {
			Class.forName("net.optifine.Lang"); // search Optifine
			log("Load Mixins for Optifine...");
			Mixins.addConfiguration("mixins.x13.optifine.json");
		} catch (ClassNotFoundException e) {
			log("Load Mixins without Optifine...");
			Mixins.addConfiguration("mixins.x13.json");
		}
		isPreInit = true;
	}

	/**
	 * Process modes and Xray keys
	 */
	public void processKeybinds() {
		for (XrayMode mode : modes)
			if (mode.toggleKey())
				return;
		if (fullbright.isPressed())
			fullBright();
		else if (config.isPressed())
			Minecraft.getMinecraft().displayGuiScreen(new XrayMenu(null));
	}

	/**
	 * Register mode(s) and keybinding(s)
	 */
	public void registerXrayMode(XrayMode... modes) {
		api.registerKeys(Arrays.stream(modes).map(mode -> {
			this.modes.add(mode);
			return mode.getKey();
		}).toArray(KeyBinding[]::new));
	}

	/**
	 * Render the overlay
	 */
	public void renderOverlay() {
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
				s = I18n.format("x13.mod.fullbright");
			} else {
				c = 0xffffffff;
				s = "";
			}
		}
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer render = mc.fontRenderer;
		EntityPlayerSP player = mc.player;
		if (!s.isEmpty())
			render.drawStringWithShadow(s = "[" + s + "] ", 5, 5, c);
		if (showLocation && player != null) {
			Vec3d pos = player.getPositionVector();
			render.drawStringWithShadow("XYZ: " + (significantNumbers(pos.x) + " / " + significantNumbers(pos.y) + " / "
					+ significantNumbers(pos.z)), 5 + render.getStringWidth(s), 5, 0xffffffff);
		}
	}

	/**
	 * Save mod configs
	 */
	public void saveConfigs() {
		try {
			Writer writer = new FileWriterWithEncoding(getSaveFile(), Charset.forName("UTF-8"));
			new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create()
					.toJson(Util.acceptAndReturn(Maps.newHashMap(), m -> {
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
		modules();
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
	}

	/**
	 * True if the side should be rendered
	 */
	public void shouldSideBeRendered(IBlockState state, IBlockReader reader, BlockPos pos, EnumFacing face,
			CallbackInfoReturnable<Boolean> ci) {
		for (XrayMode mode : modes)
			mode.shouldSideBeRendered(state, reader, pos, face, ci);
	}

}
