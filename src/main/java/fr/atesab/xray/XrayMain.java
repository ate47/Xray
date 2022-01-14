package fr.atesab.xray;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.screen.XrayMenu;
import fr.atesab.xray.utils.KeyInput;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

@Mod(XrayMain.MOD_ID)
public class XrayMain {
	public static final String MOD_ID = "atianxray";
	public static final String MOD_NAME = "Xray";
	private static int maxFullbrightStates = 20;
	private static final Logger log = LogManager.getLogger(MOD_ID);

	private static XrayMain instance;

	private boolean fullBrightEnable = false;

	private int internalFullbrightState = 0;

	private KeyMapping configKey, fullbrightKey;

	private XrayConfig config;

	private int fullbrightColor = 0;

	private final IColorObject fullbrightMode = new IColorObject() {
		public int getColor() {
			return fullbrightColor;
		}

		public String getModeName() {
			return I18n.get("x13.mod.fullbright");
		}
	};

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

	@SuppressWarnings("deprecation")
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
	 * load internal fullbright by checking if a mode is enabled
	 */
	public XrayMain internalFullbright() {
		if (fullBrightEnable) {
			if (internalFullbrightState == 0)
				internalFullbrightState = 1;
			return this;
		}
		boolean modeEnabled = config.getSelectedBlockMode() != null;

		if (modeEnabled) {
			internalFullbrightState = maxFullbrightStates;
		} else {
			internalFullbrightState = 0;
		}
		return this;
	}

	public XrayConfig getConfig() {
		return config;
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

	private static void log(String message) {
		log.info("[{}] {}", log.getName(), message);
	}

	/**
	 * Reload modules
	 */
	public XrayMain modules() {
		fullBright(isFullBrightEnable());
		try {
			Minecraft mc = Minecraft.getInstance();
			if (mc.levelRenderer != null)
				mc.levelRenderer.allChanged();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return this;
	}

	public int shouldSideBeRendered(BlockState adjacentState, BlockGetter blockState, BlockPos blockAccess,
			Direction pos, CallbackInfoReturnable<Boolean> ci) {
		if (ci != null)
			ci = new CallbackInfoReturnable<>("shouldSideBeRendered", true);

		for (BlockConfig mode : getConfig().getBlockConfigs()) {
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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
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
		Minecraft mc = Minecraft.getInstance();
		return new File(mc.gameDirectory, "config/xray2.json");
	}

	/**
	 * Load mod configs
	 */
	public void loadConfigs() {
		config = XrayConfig.sync(getSaveFile());
	}

	@SubscribeEvent
	public void onEndTickEvent(TickEvent.ClientTickEvent ev) {
		if (ev.phase != Phase.END)
			return;
		if (internalFullbrightState != 0 && internalFullbrightState < maxFullbrightStates) {
			internalFullbrightState++;
		}
	}

	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent ev) {
		var client = Minecraft.getInstance();
		if (client.screen != null)
			return;
		KeyInput input = new KeyInput(ev.getKey(), ev.getScanCode(), ev.getAction(), ev.getModifiers());

		config.getModes().forEach(mode -> mode.onKeyInput(input));

		if (fullbrightKey.consumeClick())
			fullBright();
		if (configKey.consumeClick())
			client.setScreen(new XrayMenu(null));

	}

	private IColorObject findCurrentMode() {
		BlockConfig config = getConfig().getSelectedBlockMode();

		if (config != null)
			return config;

		if (fullBrightEnable)
			return fullbrightMode;

		return IColorObject.EMPTY;
	}

	@SubscribeEvent
	public void onHudRender(RenderGameOverlayEvent ev) {
		IColorObject color = findCurrentMode();
		int c = color.getColor();
		String s = color.getModeName();
		var matrixStack = ev.getMatrixStack();
		var mc = Minecraft.getInstance();
		var render = mc.font;
		var player = mc.player;

		if (!s.isEmpty())
			render.draw(matrixStack, s = "[" + s + "] ", 5, 5, c);
		if (config.getLocationConfig().isEnabled() && player != null) {
			var pos = player.position();
			render.draw(matrixStack, "XYZ: " + (significantNumbers(pos.x) + " / " + significantNumbers(pos.y) + " / "
					+ significantNumbers(pos.z)), 5 + render.width(s), 5, 0xffffffff);
		}
	}

	/**
	 * Save mod configs
	 */
	public void saveConfigs() {
		config.save();
		modules();
	}

	private void setup(final FMLCommonSetupEvent event) {
		log("Initialization");
		fullbrightColor = ColorSupplier.DEFAULT.getColor();
		loadConfigs();

		fullbrightKey = new KeyMapping("x13.mod.fullbright", GLFW.GLFW_KEY_H, "key.categories.xray");
		ClientRegistry.registerKeyBinding(fullbrightKey);

		configKey = new KeyMapping("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
		ClientRegistry.registerKeyBinding(configKey);

		ModList.get().getModContainerById(MOD_ID).ifPresent(con -> {
			con.registerExtensionPoint(ConfigGuiFactory.class,
					() -> new ConfigGuiFactory((mc, parent) -> new XrayMenu(parent)));
		});
	}
}
