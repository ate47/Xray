package fr.atesab.xray;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;
import fr.atesab.xray.config.AbstractModeConfig;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.config.CurrentPlayerInfoHolder;
import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.screen.XrayMenu;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.GuiUtils.RGBResult;
import fr.atesab.xray.utils.KeyInput;
import fr.atesab.xray.utils.RenderUtils;
import fr.atesab.xray.utils.XrayUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(XrayMain.MOD_ID)
public class XrayMain {
	public static final String MOD_ID = "atianxray";
	public static final String MOD_NAME = "Xray";
	public static final String[] MOD_AUTHORS = { "ATE47", "ThaEin" };
	public static final URL MOD_SOURCE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray"));
	public static final URL MOD_ISSUE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray/issues"));
	public static final URL MOD_LINK = XrayUtils
			.soWhat(() -> new URL("https://www.curseforge.com/minecraft/mc-mods/xray-1-13-rift-modloader"));
	private static int maxFullbrightStates = 20;
	private static final Logger log = LogManager.getLogger(MOD_ID);

	private static XrayMain instance;

	private boolean fullBrightEnable = false;

	private int internalFullbrightState = 0;

	private KeyMapping configKey, fullbrightKey, locationEnableKey;

	private XrayConfig config;

	private int fullbrightColor = 0;
	
	private static CurrentPlayerInfoHolder cpinfo = new CurrentPlayerInfoHolder();

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
		if (ci == null)
			ci = new CallbackInfoReturnable<>("shouldSideBeRendered", true);

		for (BlockConfig mode : getConfig().getBlockConfigs()) {
			mode.shouldSideBeRendered(adjacentState, blockState, blockAccess, pos, ci);
		}
		if (ci.isCancelled())
			return ci.getReturnValue().booleanValue() ? 0 : 1;
		return 2;
	}

	public static String significantNumbers(double d) {
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
		Minecraft client = Minecraft.getInstance();
		if (client.screen != null)
			return;

		KeyInput input = new KeyInput(ev.getKey(), ev.getScanCode(), ev.getAction(), ev.getModifiers());

		if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), input.key())) {
			config.getModes().forEach(mode -> mode.onKeyInput(input));
		}

		if (fullbrightKey.consumeClick())
			fullBright();
		if (locationEnableKey.consumeClick())
			config.getLocationConfig().setEnabled(!config.getLocationConfig().isEnabled());
		if (configKey.consumeClick())
			client.setScreen(new XrayMenu(null));

	}

	@SubscribeEvent
	public void onHudRender(RenderGameOverlayEvent ev) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		
		if (!config.getLocationConfig().isEnabled() || player == null || mc.options.renderDebug)
			return;

		int w = 0;
		PoseStack stack = ev.getMatrixStack();
		Font render = mc.font;

		if (config.getLocationConfig().isShowMode()) {
			for (AbstractModeConfig cfg : config.getModes()) {
				if (!cfg.isEnabled())
					continue;
				String s = "[" + cfg.getModeName() + "] ";
				render.draw(stack, s, 5 + w, 5, cfg.getColor());
				w += render.width(s);
			}
			if (w == 0 && fullBrightEnable) {
				String s = "[" + fullbrightMode.getModeName() + "] ";
				render.draw(stack, s, 5 + w, 5, fullbrightMode.getColor());
				w += render.width(s);
			}
		}

		String format = getConfig().getLocationConfig().getFormat();
		cpinfo.update();
		String[] renderStrings = LocationFormatTool.applyAll(format, mc , cpinfo).split(LocationFormatTool.LINE_SEPARATER);
		for (int lineIndex = 0;lineIndex < renderStrings.length;lineIndex++) {
			render.draw(stack, renderStrings[lineIndex].replace(LocationFormatTool.VALUE_SEPARATER,""), 
					5, 5 + render.lineHeight * (lineIndex + (w > 0 ? 1 : 0)), 0xffffffff);
		}
	}

	@SubscribeEvent
	public void onRenderWorld(RenderLevelLastEvent ev) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		LocalPlayer player = minecraft.player;
		PoseStack stack = ev.getPoseStack();
		float delta = ev.getPartialTick();
		Camera mainCamera = minecraft.gameRenderer.getMainCamera();
		Vec3 camera = mainCamera.getPosition();

		if (!config.getEspConfigs().stream().filter(ESPConfig::isEnabled).findAny().isPresent()) {
			return;
		}
		BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();

		VertexConsumer buffer = source.getBuffer(RenderType.LINES);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		stack.pushPose();
		stack.translate(-camera.x, -camera.y, -camera.z);
		Vector3f look = mainCamera.getLookVector();
		float px = (float) (player.xOld + (player.getX() - player.xOld) * delta) + look.x();
		float py = (float) (player.yOld + (player.getY() - player.yOld) * delta) + player.getEyeHeight() + look.y();
		float pz = (float) (player.zOld + (player.getZ() - player.zOld) * delta) + look.z();

		int maxDistanceSquared = (config.getMaxTracerRange() * config.getMaxTracerRange());
		level.entitiesForRendering().forEach(e -> {

			if ((config.getMaxTracerRange() != 0 && e.distanceToSqr(player) > maxDistanceSquared) || player == e)
				return;

			EntityType<?> type = e.getType();

			config.getEspConfigs().stream().filter(esp -> esp.shouldTag(type)).forEach(esp -> {
				double x = e.xOld + (e.getX() - e.xOld) * delta;
				double y = e.yOld + (e.getY() - e.yOld) * delta;
				double z = e.zOld + (e.getZ() - e.zOld) * delta;
				RGBResult c = GuiUtils.rgbaFromRGBA(esp.getColor());
				float r = c.red() / 255F;
				float g = c.green() / 255F;
				float b = c.blue() / 255F;
				float a = c.alpha() / 255F;

				AABB aabb = type.getAABB(x, y, z);

				LevelRenderer.renderLineBox(stack, buffer, aabb, r, g, b, a);

				if (esp.hasTracer()) {
					Vec3 center = aabb.getCenter();
					RenderUtils.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
							(float) center.y, (float) center.z, r, g, b, a);
				}
			});
		});
		source.endBatch(RenderType.LINES);
		stack.popPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
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

		locationEnableKey = new KeyMapping("x13.mod.locationEnable", GLFW.GLFW_KEY_J, "key.categories.xray");
		ClientRegistry.registerKeyBinding(locationEnableKey);

		configKey = new KeyMapping("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
		ClientRegistry.registerKeyBinding(configKey);

		ModList.get().getModContainerById(MOD_ID).ifPresent(con -> {
			con.registerExtensionPoint(ConfigGuiFactory.class,
					() -> new ConfigGuiFactory((mc, parent) -> new XrayMenu(parent)));
		});
	}
}
