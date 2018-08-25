package fr.atesab.x13;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.dimdev.rift.listener.client.OverlayRenderer;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class X13Main implements InitializationListener, KeybindHandler, OverlayRenderer {
	private static X13Main instance;
	private static final Logger log = LogManager.getLogger("X13");

	static Stream<String> getBlocksNames(List<Block> blocks) {
		return blocks.stream().map(Block.REGISTRY::getNameForObject).filter(Objects::nonNull).map(Objects::toString);
	}

	public static X13Main getX13() {
		return instance;
	}

	private static String significantNumbers(double d, int n) {
		boolean a;
		if (a = d < 0) {
			d *= -1;
		}
		int d1 = (int) (d);
		d %= 1;
		String s = String.format("%." + n + "G", d);
		if (s.length() > 0)
			s = s.substring(1);
		if (s.contains("E+")) {
			s = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%." + n + "G", d)));
		}
		return (a ? "-" : "") + d1 + s;
	}

	private double oldGama;
	private boolean xrayEnable = false;
	private boolean caveEnable = false;

	private boolean fullBrightEnable = false;

	private boolean showLocation = true;

	private List<Block> xrayBlocks;

	private List<Block> caveBlocks;

	private KeyBinding xray, cave, fullbright, config;

	public X13Main() {
		instance = this;
	}

	public void cave() {
		cave(!caveEnable);
	}

	public void cave(boolean enable) {
		caveEnable = enable;
		Minecraft mc = Minecraft.getMinecraft();
		if (!(xrayEnable || fullBrightEnable)) {
			if (enable) {
				oldGama = mc.gameSettings.gammaSetting;
				mc.gameSettings.gammaSetting = 30;
			} else
				mc.gameSettings.gammaSetting = oldGama;
		}
		mc.renderGlobal.loadRenderers();
	}

	public void fullBright() {
		fullBright(!fullBrightEnable);
	}

	public void fullBright(boolean enable) {
		fullBrightEnable = enable;
		if (!(xrayEnable || caveEnable)) {
			Minecraft mc = Minecraft.getMinecraft();
			if (enable) {
				oldGama = mc.gameSettings.gammaSetting;
				mc.gameSettings.gammaSetting = 30;
			} else
				mc.gameSettings.gammaSetting = oldGama;
		}
	}

	public List<Block> getCaveBlocks() {
		return caveBlocks;
	}

	public File getSaveFile() {
		return new File(Minecraft.getMinecraft().gameDir, "xray.json");
	}

	public List<Block> getXrayBlocks() {
		return xrayBlocks;
	}

	public void init() {
		log("Initialisation");
		Minecraft mc = Minecraft.getMinecraft();
		mc.gameSettings.keyBindings = ArrayUtils.addAll(mc.gameSettings.keyBindings,
				xray = new KeyBinding("x13.mod.xray", 88, "key.categories.misc"), // X
				cave = new KeyBinding("x13.mod.cave", 67, "key.categories.misc"), // C
				fullbright = new KeyBinding("x13.mod.fullbright", 72, "key.categories.misc"),
				config = new KeyBinding("x13.mod.config", 78, "key.categories.misc")); // N
		mc.gameSettings.loadOptions();
		caveBlocks = Lists.newArrayList(Blocks.DIRT, Blocks.GRASS, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.GRASS_PATH,
				Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SAND);
		xrayBlocks = Lists.newArrayList(Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.DIAMOND_ORE, Blocks.GOLD_ORE,
				Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.OBSIDIAN, Blocks.DIAMOND_BLOCK, Blocks.IRON_ORE,
				Blocks.GOLD_BLOCK, Blocks.EMERALD_BLOCK, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.PORTAL,
				Blocks.BEACON, Blocks.MOB_SPAWNER, Blocks.BOOKSHELF, Blocks.LAVA, Blocks.WATER, Blocks.NETHER_WART,
				Blocks.BLUE_ICE, Blocks.DRAGON_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG);
		loadConfigs();
	}

	public boolean isCaveEnable() {
		return caveEnable;
	}

	public boolean isFullBrightEnable() {
		return fullBrightEnable;
	}

	public boolean isShowLocation() {
		return showLocation;
	}

	public boolean isXrayEnable() {
		return xrayEnable;
	}

	@SuppressWarnings("unchecked")
	public void loadConfigs() {
		try {
			Map<String, Object> map = new GsonBuilder().create().fromJson(
					new InputStreamReader(new FileInputStream(getSaveFile()), Charset.forName("UTF-8")), HashMap.class);
			setConfig(xrayBlocks, ((List<String>) map.getOrDefault("xrayBlocks", Lists.newArrayList())).stream()
					.toArray(String[]::new));
			setConfig(caveBlocks, ((List<String>) map.getOrDefault("caveBlocks", Lists.newArrayList())).stream()
					.toArray(String[]::new));
			fullBrightEnable = (boolean) map.getOrDefault("fullBright", false);
			oldGama = (double) map.getOrDefault("oldGama", 0D);
			showLocation = (boolean) map.getOrDefault("showLocation", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveConfigs();
	}

	private void log(String message) {
		log.info("[X13] " + message);
	}

	@Override
	public void onInitialization() {
		log("Load Mixins...");
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.x13.json");
	}

	@Override
	public void processKeybinds() {
		if (xray.isPressed()) {
			cave(false);
			xray();
		} else if (cave.isPressed()) {
			xray(false);
			cave();
		} else if (fullbright.isPressed())
			fullBright();
		else if (config.isPressed())
			Minecraft.getMinecraft().displayGuiScreen(new XrayMenu(null));
	}

	public void reloadModules() {
		xray(isXrayEnable());
		cave(isCaveEnable());
		fullBright(isFullBrightEnable());
	}

	@Override
	public void renderOverlay() {
		Minecraft mc = Minecraft.getMinecraft();
		int c;
		String s;
		if (xrayEnable) {
			c = 0xff00ff00;
			s = "[" + I18n.format("x13.mod.xray") + "] ";
		} else if (caveEnable) {
			c = 0xffffff00;
			s = "[" + I18n.format("x13.mod.cave") + "] ";
		} else if (fullBrightEnable) {
			c = 0xff00ffff;
			s = "[" + I18n.format("x13.mod.fullbright") + "] ";
		} else {
			c = 0;
			s = "";

		}
		FontRenderer render = mc.fontRenderer;
		EntityPlayerSP player = mc.player;
		render.drawStringWithShadow(s, 5, 5, c);
		if (showLocation && player != null) {
			Vec3d pos = player.getPositionVector();
			render.drawStringWithShadow("XYZ: " + (significantNumbers(pos.x, 3) + " / " + significantNumbers(pos.y, 3)
					+ " / " + significantNumbers(pos.z, 3)), 5 + render.getStringWidth(s), 5, 0xffffffff);
		}
	}

	public void saveConfigs() {
		try {
			Writer writer = new FileWriterWithEncoding(getSaveFile(), Charset.forName("UTF-8"));
			new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create()
					.toJson(Util.acceptAndReturn(Maps.newHashMap(), m -> {
						m.put("xrayBlocks", getBlocksNames(xrayBlocks).collect(Collectors.toList()));
						m.put("caveBlocks", getBlocksNames(caveBlocks).collect(Collectors.toList()));
						m.put("showLocation", showLocation);
						m.put("oldGama", oldGama);
						m.put("fullBrightEnable", fullBrightEnable);
					}), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reloadModules();
	}

	private void setConfig(List<Block> blocks, String[] data) {
		blocks.clear();
		for (String d : data) {
			Block b = Block.REGISTRY.getObject(new ResourceLocation(d));
			if (b != null)
				blocks.add(b);
		}
	}

	void setConfig(String xrayData, String caveData) {
		setConfig(xrayBlocks, xrayData.split(" "));
		setConfig(caveBlocks, caveData.split(" "));
		saveConfigs();
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
	}

	public void xray() {
		xray(!xrayEnable);
	}

	public void xray(boolean enable) {
		xrayEnable = enable;
		Minecraft mc = Minecraft.getMinecraft();
		if (!(caveEnable || fullBrightEnable)) {
			if (enable) {
				oldGama = mc.gameSettings.gammaSetting;
				mc.gameSettings.gammaSetting = 30;
			} else
				mc.gameSettings.gammaSetting = oldGama;
		}
		mc.renderGlobal.loadRenderers();
	}
}
