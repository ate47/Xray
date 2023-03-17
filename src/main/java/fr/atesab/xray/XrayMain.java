package fr.atesab.xray;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.atesab.xray.color.ColorSupplier;
import fr.atesab.xray.color.IColorObject;
import fr.atesab.xray.color.TextHudBuffer;
import fr.atesab.xray.config.*;
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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class XrayMain implements ClientModInitializer, HudRenderCallback, EndTick, AfterEntities {
    public static final String MOD_ID = "atianxray";
    public static final String MOD_NAME = "Xray";
    public static final String[] MOD_AUTHORS = {"ATE47", "ThaEin", "ALFECLARE"};
    public static final URL MOD_SOURCE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray"));
    public static final URL MOD_ISSUE = XrayUtils.soWhat(() -> new URL("https://github.com/ate47/Xray/issues"));
    public static final URL MOD_LINK = XrayUtils
            .soWhat(() -> new URL("https://www.curseforge.com/minecraft/mc-mods/xray-1-13-rift-modloader"));
    private static final int maxFullbrightStates = 20;
    private static final Logger log = LogManager.getLogger(MOD_ID);

    private static XrayMain instance;

    private boolean fullBrightEnable = false;

    private int internalFullbrightState = 0;

    private KeyBinding configKey, fullbrightKey, locationEnableKey;

    private XrayConfig config;

    private int fullbrightColor = 0;

    private final SimpleOption<Double> gammaBypass = new SimpleOption<>("options.gamma", SimpleOption.emptyTooltip(), (optionText, value) -> Text.empty(), SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
            d -> (double) getInternalFullbrightState(), d -> 1
    ), 0.5, value -> {
    });

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
     * @return the gamma option
     */
    public SimpleOption<Double> getGammaBypass() {
        // force value
        gammaBypass.setValue(1.0);
        return gammaBypass;
    }

    /**
     * Set fullBright
     */
    public XrayMain fullBright(boolean enable) {
        fullBrightEnable = enable;
        return internalFullbright();
    }

    public static <T> T getBlockNamesCollected(Collection<Block> blocks, Collector<CharSequence, ?, T> collector) {
        // BLOCK
        return blocks.stream().filter(b -> !Blocks.AIR.equals(b)).map(Registries.BLOCK::getId).map(Objects::toString).collect(collector);
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

    public void shouldSideBeRendered(BlockState adjacentState, BlockView blockState, BlockPos blockAccess,
                                     Direction pos, CallbackInfoReturnable<Boolean> ci) {
        if (ci == null)
            ci = new CallbackInfoReturnable<>("shouldSideBeRendered", true);

        for (BlockConfig mode : getConfig().getBlockConfigs()) {
            mode.shouldSideBeRendered(adjacentState, blockState, blockAccess, pos, ci);
        }
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

        if (fullbrightKey.wasPressed()) {
            fullBright();
        }
        if (locationEnableKey.wasPressed()) {
            config.getLocationConfig().setEnabled(!config.getLocationConfig().isEnabled());
        }
        if (configKey.wasPressed()) {
            client.setScreen(new XrayMenu(null));
        }

    }

    @Override
    public void onHudRender(MatrixStack stack, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer render = mc.textRenderer;
        ClientPlayerEntity player = mc.player;

        if (!config.getLocationConfig().isEnabled() || player == null || mc.options.debugEnabled) {
            return;
        }

        TextHudBuffer buffer = new TextHudBuffer();

        // TODO: add option to render the modes one line/mode
        buffer.newLine();
        if (config.getLocationConfig().isShowMode()) {
            for (AbstractModeConfig cfg : config.getModes()) {
                if (!cfg.isEnabled()) {
                    continue;
                }
                buffer.append(
                        Text.literal("[" + cfg.getModeName() + "] ")
                        .styled(s -> s.withColor(cfg.getColor()))
                );
            }
            if (fullBrightEnable) {
                buffer.append(
                        Text.literal("[" + fullbrightMode.getModeName() + "] ")
                                .styled(s -> s.withColor(fullbrightMode.getColor()))
                );
            }
        }

        if (config.getLocationConfig().isEnabled()) {
			Text[] format = LocationFormatTool.applyColor(
					getConfig().getLocationConfig().getCompiledFormat().apply(mc, player, mc.world)
			);

            if (format.length > 0) {
                buffer.append(format[0]);

                for (int i = 1; i < format.length; i++) {
                    buffer.newLine();
                    buffer.append(format[i]);
                }
            }
        }

        buffer.draw(
                stack,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                config.getLocationConfig(),
                render
        );
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

        if (player == null || level == null || config.getEspConfigs().stream().noneMatch(ESPConfig::isEnabled)) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        // RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
		if (config.getEspConfigs().stream().noneMatch(ESPConfig::isEnabled)) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		// RenderSystem.depthMask(false);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE);
		GL11.glLineWidth(config.getEspLineWidth());
		RenderSystem.depthMask(false);
		RenderSystem.depthFunc(GL11.GL_NEVER);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

		stack.push();

		RenderSystem.applyModelViewMatrix();
		stack.translate(-camera.x, -camera.y, -camera.z);
		Vector3f look = mainCamera.getHorizontalPlane();
		float px = (float) (player.prevX + (player.getX() - player.prevX) * delta) + look.x();
		float py = (float) (player.prevY + (player.getY() - player.prevY) * delta) + player.getEyeHeight(player.getPose()) + look.y();
		float pz = (float) (player.prevZ + (player.getZ() - player.prevZ) * delta) + look.z();

		int maxDistanceSquared = (config.getMaxTracerRange() * config.getMaxTracerRange());
        int distance = minecraft.options.getClampedViewDistance();
        ChunkPos chunkPos = player.getChunkPos();
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

					Chunk chunk = level.getChunk(i, j, ChunkStatus.FULL, false);
					if (chunk != null) {
						chunk.getBlockEntityPositions().forEach(((blockPos) -> {
							if ((config.getMaxTracerRange() != 0 && blockPos.getSquaredDistance(player.getPos()) > maxDistanceSquared)) {
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

								Box aabb = new Box(
										blockPos.getX(), blockPos.getY(), blockPos.getZ(),
										blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1
								);

								WorldRenderer.drawBox(stack, buffer, aabb, r, g, b, a);

								if (esp.hasTracer()) {
									Vec3d center = aabb.getCenter();
									RenderUtils.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
											(float) center.y, (float) center.z, r, g, b, a);
								}
							});
						}));
					}
				}
			}
		}

        level.getEntities().forEach(e -> {

            if ((config.getMaxTracerRange() != 0 && e.squaredDistanceTo(player) > maxDistanceSquared) || player == e)
                return;

            boolean damage = !config.isDamageIndicatorDisabled() && e instanceof LivingEntity le && le.getRecentDamageSource() != null;

            EntityType<?> type = e.getType();

            config.getEspConfigs().stream().filter(esp -> esp.shouldTag(type)).forEach(esp -> {
                double x = e.prevX + (e.getX() - e.prevX) * delta;
                double y = e.prevY + (e.getY() - e.prevY) * delta;
                double z = e.prevZ + (e.getZ() - e.prevZ) * delta;

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
        RenderSystem.disableBlend();
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

    @Override
    public void onInitializeClient() {
        log("Initialization");
        fullbrightColor = ColorSupplier.DEFAULT.getColor();
        loadConfigs();

        fullbrightKey = new KeyBinding("x13.mod.fullbright", GLFW.GLFW_KEY_H, "key.categories.xray");
        KeyBindingHelper.registerKeyBinding(fullbrightKey);

        locationEnableKey = new KeyBinding("x13.mod.locationEnable", GLFW.GLFW_KEY_J, "key.categories.xray");
        KeyBindingHelper.registerKeyBinding(locationEnableKey);

        configKey = new KeyBinding("x13.mod.config", GLFW.GLFW_KEY_N, "key.categories.xray");
        KeyBindingHelper.registerKeyBinding(configKey);

        HudRenderCallback.EVENT.register(this);
        ClientTickEvents.END_CLIENT_TICK.register(this);
        WorldRenderEvents.AFTER_ENTITIES.register(this);
    }
}
