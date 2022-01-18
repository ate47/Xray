package fr.atesab.xray;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;
import fr.atesab.xray.config.AbstractModeConfig;
import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.config.ESPConfig;
import fr.atesab.xray.config.LocationFormatTool;
import fr.atesab.xray.config.XrayConfig;
import fr.atesab.xray.screen.ColorSelector;
import fr.atesab.xray.screen.XrayMenu;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.GuiUtils.RGBResult;
import fr.atesab.xray.utils.KeyInput;
import fr.atesab.xray.utils.RenderUtils;
import fr.atesab.xray.utils.XrayUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class XrayMain implements ClientModInitializer, HudRenderCallback, EndTick, AfterEntities {
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

	private KeyBinding configKey, fullbrightKey;

	private XrayConfig config;

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
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.worldRenderer != null)
				mc.worldRenderer.reload();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return this;
	}

	public int shouldSideBeRendered(BlockState adjacentState, BlockView blockState, BlockPos blockAccess,
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
		return new File(mc.runDirectory, "config/xray2.json");
	}

	/**
	 * Load mod configs
	 */
	public void loadConfigs() {
		config = XrayConfig.sync(getSaveFile());
	}

	@Override
	public void onEndTick(MinecraftClient client) {
		if (internalFullbrightState != 0 && internalFullbrightState < maxFullbrightStates) {
			internalFullbrightState++;
		}
	}

	public void onKeyEvent(KeyInput input) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.currentScreen != null)
			return;

		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), input.key())) {
			config.getModes().forEach(mode -> mode.onKeyInput(input));
		}

		if (fullbrightKey.wasPressed())
			fullBright();
		if (configKey.wasPressed())
			client.setScreen(new XrayMenu(null));

	}

	@Override
	public void onHudRender(MatrixStack stack, float tickDelta) {
		int w = 0;
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer render = mc.textRenderer;
		ClientPlayerEntity player = mc.player;

		if (config.getLocationConfig().isShowMode()) {
			for (AbstractModeConfig cfg : config.getModes()) {
				if (!cfg.isEnabled())
					continue;
				String s = "[" + cfg.getModeName() + "] ";
				render.draw(stack, s, 5 + w, 5, cfg.getColor());
				w += render.getWidth(s);
			}
			if (w == 0 && fullBrightEnable) {
				String s = "[" + fullbrightMode.getModeName() + "] ";
				render.draw(stack, s, 5 + w, 5, fullbrightMode.getColor());
				w += render.getWidth(s);
			}
		}

		if (config.getLocationConfig().isEnabled() && player != null) {
			String format = getConfig().getLocationConfig().getFormat();
			render.draw(stack, LocationFormatTool.applyAll(format, mc), 5 + w, 5, 0xffffffff);
		}
	}

	@Override
	public void afterEntities(WorldRenderContext context) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		ClientWorld level = minecraft.world;
		ClientPlayerEntity player = minecraft.player;
		MatrixStack stack = context.matrixStack();
		float delta = context.tickDelta();
		Camera mainCamera = minecraft.gameRenderer.getCamera();
		Vec3d camera = mainCamera.getPos();

		if (!config.getEspConfigs().stream().filter(ESPConfig::isEnabled).findAny().isPresent()) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();

		stack.push();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

		RenderSystem.applyModelViewMatrix();
		stack.translate(-camera.x, -camera.y, -camera.z);
		Vec3f look = mainCamera.getHorizontalPlane();
		float px = (float) (player.prevX + (player.getX() - player.prevX) * delta) + look.getX();
		float py = (float) (player.prevY + (player.getY() - player.prevY) * delta) + look.getY()
				+ player.getStandingEyeHeight();
		float pz = (float) (player.prevZ + (player.getZ() - player.prevZ) * delta) + look.getZ();

		int maxDistanceSquared = (config.getMaxTracerRange() * config.getMaxTracerRange());
		level.getEntities().forEach(e -> {

			if ((config.getMaxTracerRange() != 0 && e.squaredDistanceTo(player) > maxDistanceSquared) || player == e)
				return;

			EntityType<?> type = e.getType();

			config.getEspConfigs().stream().filter(esp -> esp.shouldTag(type)).forEach(esp -> {
				double x = e.prevX + (e.getX() - e.prevX) * delta;
				double y = e.prevY + (e.getY() - e.prevY) * delta;
				double z = e.prevZ + (e.getZ() - e.prevZ) * delta;
				RGBResult c = GuiUtils.rgbaFromRGBA(esp.getColor());
				float r = c.red() / 255F;
				float g = c.green() / 255F;
				float b = c.blue() / 255F;
				float a = c.alpha() / 255F;

				Box aabb = type.createSimpleBoundingBox(x, y, z);

				WorldRenderer.drawBox(stack, buffer, aabb, r, g, b, a);

				if (esp.hasTracer()) {
					Vec3d center = aabb.getCenter();
					RenderUtils.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
							(float) center.y, (float) center.z, r, g, b, a);
				}
			});
		});
		tessellator.draw();
		stack.pop();
		RenderSystem.applyModelViewMatrix();
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

	@Override
	public void onInitializeClient() {
		log("Initialization");
		fullbrightColor = ColorSupplier.DEFAULT.getColor();
		loadConfigs();

		fullbrightKey = new KeyBinding("x13.mod.fullbright", GLFW.GLFW_KEY_H, "key.categories.xray");
		KeyBindingHelper.registerKeyBinding(fullbrightKey);

		configKey = new KeyBinding("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
		KeyBindingHelper.registerKeyBinding(configKey);

		HudRenderCallback.EVENT.register(this);
		ClientTickEvents.END_CLIENT_TICK.register(this);
		WorldRenderEvents.AFTER_ENTITIES.register(this);
	}
}
