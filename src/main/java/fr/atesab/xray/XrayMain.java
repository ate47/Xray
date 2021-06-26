package fr.atesab.xray;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import fr.atesab.xray.XrayMode.ViewMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class XrayMain implements ClientModInitializer, HudRenderCallback, EndTick {
	public static final String MOD_ID = "atianxray";
	public static final String MOD_NAME = "Xray";
	private static int maxFullbrightStates = 20;
	private static final Logger log = LogManager.getLogger(MOD_ID);

	private static XrayMain instance;

	private List<XrayMode> modes = Lists.newArrayList();

	private XrayMode selectedMode = null;

	private List<String> customModes = Lists.newArrayList();

	private boolean fullBrightEnable = false;

	private int internalFullbrightState = 0;

	private boolean showLocation = true;
	private KeyBinding fullbright;
	private KeyBinding config;
	private int fullbrightColor = 0;

	private final IColorObject fullbrightMode = new IColorObject() {
		public int getColor() {
			return fullbrightColor;
		}

		public String getModeName() {
			return I18n.translate("x13.mod.fullbright");
		}
	};

	/**
	 * Toggle fullBright
	 */
	public XrayMain fullBright() {
		return fullBright(!fullBrightEnable);
	}

	/**
	 * @param selectedMode the selectedMode to set
	 */
	public void setSelectedMode(XrayMode selectedMode) {
		this.selectedMode = selectedMode;
	}

	/**
	 * @return the selectedMode
	 */
	public XrayMode getSelectedMode() {
		return selectedMode;
	}

	/**
	 * Set fullBright
	 */
	public XrayMain fullBright(boolean enable) {
		fullBrightEnable = enable;
		return internalFullbright();
	}

	public static <T> T getBlockNamesCollected(Collection<Block> blocks, Collector<CharSequence, ?, T> collector) {
		return blocks.stream().filter(b -> !Blocks.AIR.equals(b)).map(Registry.BLOCK::getId) // BLOCK
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
	public List<XrayMode> getModes() {
		return modes;
	}

	/**
	 * load internal fullbright by checking if a mode is enabled
	 */
	public XrayMain internalFullbright() {
		if (fullBrightEnable) {
			if (internalFullbrightState == 0)
				internalFullbrightState = 1;
			return this;
		}
		boolean f = false;
		for (XrayMode mode : modes)
			if (mode.isEnabled()) {
				f = true;
				break;
			}
		if (f) {
			internalFullbrightState = maxFullbrightStates;
		} else {
			internalFullbrightState = 0;
		}
		return this;
	}

	public boolean isFullBrightEnable() {
		return fullBrightEnable;
	}

	public boolean isInternalFullbrightEnable() {
		return getInternalFullbrightState() != 0;
	}

	/**
	 * @return the internalFullbrightEnable
	 */
	public float getInternalFullbrightState() {
		return 20 * internalFullbrightState / maxFullbrightStates;
	}

	public boolean isShowLocation() {
		return showLocation;
	}

	private static void log(String message) {
		log.info("[{}] {}", log.getName(), message);
	}

	/**
	 * Reload modules
	 */
	public XrayMain modules() {
		for (XrayMode mode : modes)
			mode.toggle(mode.isEnabled(), false);
		fullBright(isFullBrightEnable());
		try {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.worldRenderer != null)
				mc.worldRenderer.reload();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Register mode(s) and keybinding(s)
	 */
	public void registerXrayMode(XrayMode... modes) {
		for (XrayMode mode : modes) {
			this.modes.add(mode);
			KeyBindingHelper.registerKeyBinding(mode.getKey());
		}
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
		saveConfigs();
	}

	public int shouldSideBeRendered(BlockState adjacentState, BlockView blockState, BlockPos blockAccess, Direction pos,
			@Nullable CallbackInfoReturnable<Boolean> ci) {
		ci = ci != null ? ci : new CallbackInfoReturnable<>("shouldSideBeRendered", true);
		for (XrayMode mode : modes) {
			mode.shouldSideBeRendered(adjacentState, blockState, blockAccess, pos, ci);
		}
		if (ci.isCancelled())
			return ci.getReturnValue().booleanValue() ? 0 : 1;
		return 2;
	}

	private static String significantNumbers(double d) {
		boolean a = d < 0;
		if (a) {
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
		instance = this;
	}

	/**
	 * get this mod
	 */
	public static XrayMain getMod() {
		return instance;
	}

	/**
	 * Mod config file
	 */
	public static File getSaveFile() {
		MinecraftClient mc = MinecraftClient.getInstance();
		File oldFile = new File(mc.runDirectory, "xray.json");
		File newFile = new File(mc.runDirectory, "config/xray.json");

		// if old exists but new not
		if (oldFile.exists() && !newFile.exists()) {
			try {
				Files.move(oldFile, newFile);
			} catch (IOException e) {
				newFile = oldFile;
			}
		}

		return newFile;
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

	@Override
	public void onEndTick(MinecraftClient client) {
		if (internalFullbrightState != 0 && internalFullbrightState < maxFullbrightStates) {
			internalFullbrightState++;
		}
		if (client.currentScreen != null)
			return;
		for (XrayMode mode : modes)
			if (mode.toggleKey())
				return;
		if (fullbright.wasPressed())
			fullBright();
		if (config.wasPressed())
			client.openScreen(new XrayMenu(null));

	}

	private IColorObject findCurrentMode() {
		for (XrayMode mode : modes)
			if (mode.isEnabled()) {
				return mode;
			}
		if (fullBrightEnable) {
			return fullbrightMode;
		} else {
			return IColorObject.EMPTY;
		}
	}

	@Override
	public void onHudRender(MatrixStack matrixStack, float v) {

		IColorObject color = findCurrentMode();
		int c = color.getColor();
		String s = color.getModeName();
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer render = mc.textRenderer;
		ClientPlayerEntity player = mc.player;

		if (!s.isEmpty())
			render.drawWithShadow(matrixStack, s = "[" + s + "] ", 5, 5, c);
		if (showLocation && player != null) {
			Vec3d pos = player.getPos();
			render.drawWithShadow(matrixStack, "XYZ: " + (significantNumbers(pos.x) + " / " + significantNumbers(pos.y)
					+ " / " + significantNumbers(pos.z)), 5 + render.getWidth(s), 5, 0xffffffff);
		}
	}

	/**
	 * Save mod configs
	 */
	public void saveConfigs() {
		try (Writer writer = new FileWriterWithEncoding(getSaveFile(), StandardCharsets.UTF_8)) {
			new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create()
					.toJson(Util.make(Maps.newHashMap(), m -> {
						modes.forEach(mode -> m.put(mode.getName() + "Blocks", getBlockNamesToList(mode.getBlocks())));
						m.put("showLocation", showLocation);
						m.put("internalFullbrightEnable", internalFullbrightState);
						m.put("fullBrightEnable", fullBrightEnable);
						m.put("customModes", customModes.stream().map(
								s -> s.split(":", 2).length == 2 ? s : s + ":" + XrayMode.ViewMode.EXCLUSIVE.name())
								.collect(Collectors.toList()));
					}), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		modules();
	}

	@Override
	public void onInitializeClient() {
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
						Blocks.COPPER_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
						Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_LAPIS_ORE,

						Blocks.RAW_COPPER_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.RAW_IRON_BLOCK, Blocks.CRYING_OBSIDIAN,

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
					Blocks.DIRT_PATH,        Blocks.SAND,             Blocks.SANDSTONE,       Blocks.RED_SAND
				),

				// Redstone mode
				new XrayMode(
					"redstone",
					GLFW.GLFW_KEY_R,
					ViewMode.EXCLUSIVE,
					Blocks.REDSTONE_BLOCK,                             Blocks.REDSTONE_LAMP,
					Blocks.REDSTONE_ORE,                               Blocks.REDSTONE_TORCH, 
					Blocks.DEEPSLATE_REDSTONE_ORE,
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
					Blocks.ENDER_CHEST,								   Blocks.TARGET
				));
				// @formatter:on

		fullbrightColor = XrayMode.nextColor();
		loadConfigs();

		HudRenderCallback.EVENT.register(this);
		ClientTickEvents.END_CLIENT_TICK.register(this);

		fullbright = new KeyBinding("x13.mod.fullbright", GLFW.GLFW_KEY_H, "key.categories.xray");
		KeyBindingHelper.registerKeyBinding(fullbright);

		config = new KeyBinding("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
		KeyBindingHelper.registerKeyBinding(config);

	}

}
