package fr.atesab.xray;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import fr.atesab.xray.XrayMode.ViewMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod(XrayMain.MOD_ID)
public class XrayMain {
	public static final String MOD_ID = "atianxray";
	public static final String MOD_NAME = "Xray";
	private static final Logger log = LogManager.getLogger(MOD_ID);

	private static List<XrayMode> modes = Lists.newArrayList();

	private static List<String> customModes = Lists.newArrayList();

	private static double oldGama;

	private static boolean fullBrightEnable = false;

	private static boolean internalFullbrightEnable = false;

	private static boolean showLocation = true;
	private static KeyBinding fullbright, config;
	private static int fullbrightColor = 0;

	/**
	 * Toggle fullBright
	 */
	public static void fullBright() {
		fullBright(!fullBrightEnable);
	}

	/**
	 * Set fullBright
	 */
	public static void fullBright(boolean enable) {
		fullBrightEnable = enable;
		internalFullbright();
	}

	@SuppressWarnings("deprecation")
	public static <T> T getBlockNamesCollected(Collection<Block> blocks, Collector<CharSequence, ?, T> collector) {
		return blocks.stream().filter(b -> !Blocks.AIR.equals(b)).map(Registry.BLOCK::getKey) // BLOCK
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
	 * Get all registered modes
	 */
	public static List<XrayMode> getModes() {
		return modes;
	}

	/**
	 * load internal fullbright by checking if a mode is enabled
	 */
	public static void internalFullbright() {
		Minecraft mc = Minecraft.getInstance();
		boolean f = fullBrightEnable;
		for (XrayMode mode : modes)
			if (f = (f || mode.isEnabled()))
				break;
		if (f) {
			if (!internalFullbrightEnable) {
				oldGama = mc.gameSettings.gamma;
				mc.gameSettings.gamma = 30;
			}
		} else
			mc.gameSettings.gamma = oldGama;
		internalFullbrightEnable = f;
	}

	public static boolean isFullBrightEnable() {
		return fullBrightEnable;
	}

	public static boolean isInternalFullbrightEnable() {
		return internalFullbrightEnable;
	}

	public static boolean isShowLocation() {
		return showLocation;
	}

	private static void log(String message) {
		log.info("[" + log.getName() + "] " + message);
	}

	/**
	 * Reload modules
	 */
	public static void modules() {
		for (XrayMode mode : modes)
			mode.toggle(mode.isEnabled(), false);
		fullBright(isFullBrightEnable());
		try {
		if (Minecraft.getInstance().worldRenderer != null)
			Minecraft.getInstance().worldRenderer.loadRenderers(); // WorldRenderer
		} catch (IllegalStateException e) {
			e.printStackTrace();	
		}
	}

	/**
	 * Register mode(s) and keybinding(s)
	 */
	public static void registerXrayMode(XrayMode... modes) {
		for (XrayMode mode : modes) {
			XrayMain.modes.add(mode);
			ClientRegistry.registerKeyBinding(mode.getKey());
		}
	}

	public static void setShowLocation(boolean showLocation) {
		XrayMain.showLocation = showLocation;
		saveConfigs();
	}

	/**
	 * True if the side should be rendered, injected on
	 */
	public static int shouldSideBeRendered(
			BlockState adjacentState,
			IBlockReader blockState,
			BlockPos blockAccess,
			Direction pos
	) {
		CallbackInfo<Boolean> ci = new CallbackInfo<>();
		for (XrayMode mode : modes)
			mode.shouldSideBeRendered(adjacentState, blockState, blockAccess, pos, ci);
		if (ci.isCancelled())
			return ci.getReturnValue() ? 0 : 1;
		return 2;
	}

	/**
	 * Filler for OptiFine
	 */
	public static int shouldSideBeRendered(
			BlockState adjacentState,
			IBlockReader blockState,
			BlockPos blockAccess,
			Direction pos,
			Object arg
	) {
		return shouldSideBeRendered(adjacentState, blockState, blockAccess, pos);
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

	public XrayMain() {
		log("Register mod listener...");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Mod config file
	 */
	public static File getSaveFile() {
		File old_file = new File(Minecraft.getInstance().gameDir, "xray.json");
		File new_file = new File(Minecraft.getInstance().gameDir, "config/xray.json");

		// if old exists but new not
		if(old_file.exists() && !new_file.exists()) {
			try {
				Files.move(old_file, new_file);
			} catch (IOException e) {
				new_file = old_file;
			}
		}

		return new_file;
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

	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent ev) {
		if (Minecraft.getInstance().currentScreen == null) {
			for (XrayMode mode : modes)
				if (mode.toggleKey())
					return;
			if (fullbright.isPressed())
				fullBright();
			else if (config.isPressed())
				Minecraft.getInstance().displayGuiScreen(new XrayMenu(null));
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post ev) {

		// In other time it breaks armor/hunger/air hud #10
		if (ev.getType() != RenderGameOverlayEvent.ElementType.TEXT)
			return;

		int c;
		String s;
		nameFinder:
		{
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
		Minecraft mc = Minecraft.getInstance();
		FontRenderer render = mc.fontRenderer;
		ClientPlayerEntity player = mc.player;

		MatrixStack matrixStack = ev.getMatrixStack();

		if (!s.isEmpty())
			render.drawStringWithShadow(matrixStack, s = "[" + s + "] ", 5, 5, c);
		if (showLocation && player != null) {
			Vector3d pos = player.getPositionVec();
			render.drawStringWithShadow(matrixStack, "XYZ: " + (significantNumbers(pos.x) + " / " + significantNumbers(pos.y) + " / "
					+ significantNumbers(pos.z)), 5 + render.getStringWidth(s), 5, 0xffffffff);
		}
	}

	/**
	 * Save mod configs
	 */
	public static void saveConfigs() {
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
		modules();
	}

	private void setup(final FMLCommonSetupEvent event) {
		log("Initialization");
		registerXrayMode(
				// @formatter:off
				// Xray Mode
				new XrayMode(
					"xray",
					GLFW.GLFW_KEY_X,
					ViewMode.EXCLUSIVE,

						/* Ores */
						Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
						Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.NETHER_GOLD_ORE,
						Blocks.ANCIENT_DEBRIS, Blocks.NETHER_QUARTZ_ORE,

						// 1.17
						/* Blocks.COPPER_ORE */

						/* Ore Blocks */
						Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK,
						Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,

						/* Blocks */
						Blocks.OBSIDIAN, Blocks.BLUE_ICE, Blocks.CLAY, Blocks.BOOKSHELF,
						Blocks.SPONGE, Blocks.WET_SPONGE,

						/* Other */
						Blocks.NETHER_WART, Blocks.SPAWNER, Blocks.LAVA,Blocks.WATER,
						Blocks.TNT, Blocks.CONDUIT,

						/* Portals */
						Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.NETHER_PORTAL,

						/* Interactive */
						Blocks.BEACON, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
						Blocks.DISPENSER, Blocks.DROPPER,

						/* Useless */
						Blocks.DRAGON_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG,

						/* Infested (Silverfish inside) */
						Blocks.INFESTED_STONE, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS,
						Blocks.INFESTED_COBBLESTONE, Blocks.INFESTED_CHISELED_STONE_BRICKS,
						Blocks.INFESTED_MOSSY_STONE_BRICKS

				),

				// Cave Mode
				new XrayMode(
					"cave",
					GLFW.GLFW_KEY_C,
					ViewMode.INCLUSIVE,
					Blocks.DIRT,              Blocks.GRASS,            Blocks.GRAVEL,          Blocks.GRASS_BLOCK,
					Blocks.GRASS_PATH,        Blocks.SAND,             Blocks.SANDSTONE,       Blocks.RED_SAND
				),

				// Redstone mode
				new XrayMode(
					"redstone",
					GLFW.GLFW_KEY_R,
					ViewMode.EXCLUSIVE,
					Blocks.REDSTONE_BLOCK,                             Blocks.REDSTONE_LAMP,
					Blocks.REDSTONE_ORE,                               Blocks.REDSTONE_TORCH,
					Blocks.REDSTONE_WALL_TORCH,                        Blocks.REDSTONE_WIRE,
					Blocks.REPEATER,                                   Blocks.REPEATING_COMMAND_BLOCK,
					Blocks.COMMAND_BLOCK,                              Blocks.CHAIN_COMMAND_BLOCK,
					Blocks.COMPARATOR,                                 Blocks.ANVIL,
					Blocks.CHEST,                                      Blocks.TRAPPED_CHEST,
					Blocks.DROPPER,                                    Blocks.DISPENSER,
					Blocks.HOPPER,                                     Blocks.OBSERVER,
					Blocks.DRAGON_HEAD,                                Blocks.DRAGON_WALL_HEAD,
					Blocks.IRON_DOOR,                                  Blocks.ACACIA_DOOR,
					Blocks.BIRCH_DOOR,                                 Blocks.DARK_OAK_DOOR,
					Blocks.JUNGLE_DOOR,                                Blocks.OAK_DOOR,
					Blocks.SPRUCE_DOOR,                                Blocks.ACACIA_BUTTON,
					Blocks.BIRCH_BUTTON,                               Blocks.DARK_OAK_BUTTON,
					Blocks.JUNGLE_BUTTON,                              Blocks.OAK_BUTTON,
					Blocks.SPRUCE_BUTTON,                              Blocks.STONE_BUTTON,
					Blocks.LEVER,                                      Blocks.TNT,
					Blocks.PISTON,                                     Blocks.PISTON_HEAD,
					Blocks.MOVING_PISTON,                              Blocks.STICKY_PISTON,
					Blocks.NOTE_BLOCK,                                 Blocks.DAYLIGHT_DETECTOR,
					Blocks.IRON_TRAPDOOR,                              Blocks.ACACIA_TRAPDOOR,
					Blocks.BIRCH_TRAPDOOR,                             Blocks.DARK_OAK_TRAPDOOR,
					Blocks.JUNGLE_TRAPDOOR,                            Blocks.OAK_TRAPDOOR,
					Blocks.SPRUCE_TRAPDOOR,                            Blocks.ACACIA_PRESSURE_PLATE,
					Blocks.BIRCH_PRESSURE_PLATE,                       Blocks.DARK_OAK_PRESSURE_PLATE,
					Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,              Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
					Blocks.JUNGLE_PRESSURE_PLATE,                      Blocks.OAK_PRESSURE_PLATE,
					Blocks.SPRUCE_PRESSURE_PLATE,                      Blocks.STONE_PRESSURE_PLATE,
					Blocks.RAIL,                                       Blocks.ACTIVATOR_RAIL,
					Blocks.DETECTOR_RAIL,                              Blocks.POWERED_RAIL,
					Blocks.ENDER_CHEST
				));
				// @formatter:on

		// H
		ClientRegistry.registerKeyBinding(fullbright = new KeyBinding(
				"x13.mod.fullbright",
				GLFW.GLFW_KEY_H,
				"key.categories.xray"
		));

		// N
		ClientRegistry.registerKeyBinding(config = new KeyBinding(
				"x13.mod.config",
				GLFW.GLFW_KEY_N,
				"key.categories.xray"
		));

		fullbrightColor = XrayMode.nextColor();
		loadConfigs();
	}

}
