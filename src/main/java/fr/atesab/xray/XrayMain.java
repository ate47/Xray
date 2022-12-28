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
import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
	public static final String[] MOD_AUTHORS = { "ATE47", "ThaEin", "ALFECLARE" };
	public static final URL MOD_SOURCE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray"));
	public static final URL MOD_ISSUE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray/issues"));
	public static final URL MOD_LINK = XrayUtils
			.soWhat(() -> new URL("https://www.curseforge.com/minecraft/mc-mods/xray-1-13-rift-modloader"));
	private static final int maxFullbrightStates = 20;
	private static final Logger log = LogManager.getLogger(MOD_ID);

	private static XrayMain instance;

	private final OptionInstance<Double> gammaBypass = new OptionInstance<>(
			"options.gamma", OptionInstance.noTooltip(), (optionText, value) -> Component.empty(), OptionInstance.UnitDouble.INSTANCE.xmap(
			d -> (double) getInternalFullbrightState(), d -> 1
	), 0.5, value -> {});

	private boolean fullBrightEnable = false;

	private int internalFullbrightState = 0;

	private KeyMapping configKey, fullbrightKey, locationEnableKey;

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
		// BLOCK
		return blocks.stream().filter(b -> !Blocks.AIR.equals(b)).map(BuiltInRegistries.BLOCK::getId).map(Objects::toString).collect(collector);
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
		return 20f * internalFullbrightState / maxFullbrightStates;
	}
	/**
	 * @return the gamma option
	 */
	public OptionInstance<Double> getGammaBypass() {
		// force value
		gammaBypass.set(1.0);
		return gammaBypass;
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
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		bus.addListener(this::registerKeyBinding);

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
	public void onKeyEvent(InputEvent.Key ev) {
		Minecraft client = Minecraft.getInstance();
		if (client.screen != null)
			return;

		KeyInput input = new KeyInput(ev.getKey(), ev.getScanCode(), ev.getAction(), ev.getModifiers());

		if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), input.key())) {
			config.getModes().forEach(mode -> mode.onKeyInput(input));
		}

		if (fullbrightKey.consumeClick()) {
			fullBright();
		}
		if (locationEnableKey.consumeClick()) {
			config.getLocationConfig().setEnabled(!config.getLocationConfig().isEnabled());
		}
		if (configKey.consumeClick()) {
			client.setScreen(new XrayMenu(null));
		}
	}

	@SubscribeEvent
	public void onHudRender(RenderGuiOverlayEvent ev) {
		PoseStack stack = ev.getPoseStack();
		Minecraft mc = Minecraft.getInstance();
		Font render = mc.font;
		LocalPlayer player = mc.player;

		if (!config.getLocationConfig().isEnabled() || player == null || mc.options.renderDebug) {
			return;
		}
		int w = 0;

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

		if (config.getLocationConfig().isEnabled()) {
			Component[] format = LocationFormatTool.applyColor(
					getConfig().getLocationConfig().getCompiledFormat().apply(mc, player, mc.level)
			);
			// write first line with the shift for the mode
			render.draw(stack, format[0],5 + w, 5, 0xffffffff);
			// write next lines
			for (int lineIndex = 1; lineIndex < format.length; lineIndex++) {
				render.draw(stack, format[lineIndex],
						5, 5 + render.lineHeight * lineIndex, 0xffffffff);
			}
		}
	}

	@SubscribeEvent
	public void onRenderWorld(RenderLevelStageEvent ev) {
		if (ev.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		LocalPlayer player = minecraft.player;
		if (level == null || player == null) {
			return;
		}
		PoseStack stack = ev.getPoseStack();
		float delta = ev.getPartialTick();
		Camera mainCamera = minecraft.gameRenderer.getMainCamera();
		Vec3 camera = mainCamera.getPosition();

		if (config.getEspConfigs().stream().noneMatch(ESPConfig::isEnabled)) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		// RenderSystem.depthMask(false);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE);
		GL11.glLineWidth(config.getEspLineWidth());
		RenderSystem.depthMask(false);
		RenderSystem.depthFunc(GL11.GL_NEVER);
		RenderSystem.disableTexture();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

		stack.pushPose();

		RenderSystem.applyModelViewMatrix();
		stack.setIdentity();
		stack.translate(-camera.x, -camera.y, -camera.z);
		Vector3f look = mainCamera.getLookVector();
		float px = (float) (player.xOld + (player.getX() - player.xOld) * delta) + look.x();
		float py = (float) (player.yOld + (player.getY() - player.yOld) * delta) + player.getEyeHeight() + look.y();
		float pz = (float) (player.zOld + (player.getZ() - player.zOld) * delta) + look.z();

		int maxDistanceSquared = (config.getMaxTracerRange() * config.getMaxTracerRange());
		int distance = minecraft.options.getEffectiveRenderDistance();
		ChunkPos chunkPos = player.chunkPosition();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;

		if (config.getEspConfigs().stream().anyMatch(ESPConfig::hasBlockEsp)) {
			for (int i = chunkX - distance; i <= chunkX + distance; i++) {
				for (int j = chunkZ - distance; j <= chunkZ + distance; j++) {
					int ccx = i << 4 + 8;
					int ccz = j << 4 + 8;

					int squaredDistanceToChunk = (int) ((px - ccx) * (px - ccx) + (pz - ccz) * (pz - ccz));

					// sqrt(2) ~= 3 / 2 "math"
					if (squaredDistanceToChunk + 8 * 3 / 2 > maxDistanceSquared) {
						// ignore this chunk, too far
						continue;
					}

					ChunkAccess chunk = level.getChunk(i, j, ChunkStatus.FULL, false);
					if (chunk != null) {
						chunk.getBlockEntitiesPos().forEach(((blockPos) -> {
							if ((config.getMaxTracerRange() != 0 && blockPos.distSqr(player.blockPosition()) > maxDistanceSquared)) {
								return;
							}

							BlockEntity blockEntity = chunk.getBlockEntity(blockPos);

							if (blockEntity == null) {
								return;
							}

							BlockEntityType<?> type = blockEntity.getType();

							config.getEspConfigs().stream().filter(esp -> esp.shouldTag(type)).forEach(esp -> {
								RGBResult c = GuiUtils.rgbaFromRGBA(esp.getColor());
								float r = c.red() / 255F;
								float g = c.green() / 255F;
								float b = c.blue() / 255F;
								float a = c.alpha() / 255F;

								AABB aabb = new AABB(
										blockPos.getX(), blockPos.getY(), blockPos.getZ(),
										blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1
								);

								LevelRenderer.renderLineBox(stack, buffer, aabb, r, g, b, a);

								if (esp.hasTracer()) {
									Vec3 center = aabb.getCenter();
									RenderUtils.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
											(float) center.y, (float) center.z, r, g, b, a);
								}
							});
						}));
					}
				}
			}
		}

		level.entitiesForRendering().forEach(e -> {

			if ((config.getMaxTracerRange() != 0 && e.distanceToSqr(player) > maxDistanceSquared) || player == e) {
				return;
			}

			EntityType<?> type = e.getType();

			boolean damage = !config.isDamageIndicatorDisabled() && e instanceof LivingEntity le && le.getLastDamageSource() != null;

			config.getEspConfigs().stream().filter(esp -> esp.shouldTag(type)).forEach(esp -> {
				double x = e.xOld + (e.getX() - e.xOld) * delta;
				double y = e.yOld + (e.getY() - e.yOld) * delta;
				double z = e.zOld + (e.getZ() - e.zOld) * delta;

				float r, g, b, a;

				if (damage) {
					r = 1;
					g = 0;
					b = 0;
					a = 1;
				} else {
					RGBResult c = GuiUtils.rgbaFromRGBA(esp.getColor());
					r = c.red() / 255F;
					g = c.green() / 255F;
					b = c.blue() / 255F;
					a = c.alpha() / 255F;
				}

				AABB aabb = type.getAABB(x, y, z);

				LevelRenderer.renderLineBox(stack, buffer, aabb, r, g, b, a);

				if (esp.hasTracer()) {
					Vec3 center = aabb.getCenter();
					RenderUtils.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
							(float) center.y, (float) center.z, r, g, b, a);
				}
			});
		});
		tessellator.end();
		stack.popPose();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.lineWidth(1.0F);
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	/**
	 * Save mod configs
	 */
	public void saveConfigs() {
		config.save();
		modules();
	}

	private void registerKeyBinding(final RegisterKeyMappingsEvent ev) {
		fullbrightKey = new KeyMapping("x13.mod.fullbright", GLFW.GLFW_KEY_H, "key.categories.xray");
		configKey = new KeyMapping("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
		locationEnableKey = new KeyMapping("x13.mod.locationEnable", GLFW.GLFW_KEY_J, "key.categories.xray");

		ev.register(fullbrightKey);
		ev.register(configKey);
		ev.register(locationEnableKey);
	}

	private void setup(final FMLCommonSetupEvent event) {
		log("Initialization");
		fullbrightColor = ColorSupplier.DEFAULT.getColor();
		loadConfigs();

		ModList.get().getModContainerById(MOD_ID).ifPresent(con -> {
			con.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new XrayMenu(parent)));
		});
	}
}
