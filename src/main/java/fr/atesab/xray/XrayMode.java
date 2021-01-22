package fr.atesab.xray;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;

public class XrayMode implements SideRenderer {
	@FunctionalInterface
	public static interface Viewer {
		public boolean shouldRenderSide(boolean blockInList, BlockState adjacentState, IBlockReader blockState,
				BlockPos blockAccess, Direction pos);
	}

	public static enum ViewMode {
		/**
		 * Default mode, like in Xray and Redstone mode
		 */
		EXCLUSIVE((il, v1, v2, v3, v4) -> il),
		/**
		 * Inclusive mode, like in Cave Mode
		 */
		@SuppressWarnings("deprecation")
		INCLUSIVE((il, v1, reader, pos, face) -> !il && reader.getBlockState(pos.offset(face)).isAir());

		private Viewer viewer;

		private ViewMode(Viewer viewer) {
			this.viewer = viewer;
		}

		public Viewer getViewer() {
			return viewer;
		}
	}

	private static final List<XrayMode> MODES = Lists.newArrayList();
	private List<Block> blocks;
	private final String defaultBlocks;
	private boolean enabled;
	private KeyBinding key;
	private final String name;
	private final int color;
	private ViewMode viewMode;
	private static int colorCursor = -1;
	private static final int[] COLORS = { 0xff00ffff, 0xff00ff00, 0xffff0000, 0xffffff00, 0xffff00ff, 0xff7aff00,
			0xffff7a00, 0xff00ff7a, 0xffff007a, 0xff7a00ff, 0xff7a7aff, 0xff7aff7a, 0xffff7a7a };
	public static final String CUSTOM_PREFIX = "Custom_";

	static int nextColor() {
		return COLORS[colorCursor = (colorCursor + 1) % COLORS.length];
	}

	public XrayMode(String name, int keyCode, ViewMode viewMode) {
		this.name = name;
		this.color = nextColor();
		this.enabled = false;
		this.blocks = Lists.newArrayList();
		this.key = new KeyBinding(name, keyCode, "key.categories.xray");
		this.viewMode = viewMode;
		this.defaultBlocks = "";
		MODES.add(this);
	}

	public XrayMode(String name, int keyCode, ViewMode viewMode, Block... defaultBlocks) {
		this.name = name;
		this.color = nextColor();
		this.enabled = false;
		this.blocks = Lists.newArrayList(defaultBlocks);
		this.key = new KeyBinding("x13.mod." + name, keyCode, "key.categories.xray");
		this.viewMode = viewMode;
		this.defaultBlocks = XrayMain.getBlockNamesToString(blocks);
		MODES.add(this);
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public String getDefaultBlocks() {
		return defaultBlocks;
	}

	public int getColor() {
		return color;
	}

	public KeyBinding getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getNameTranslate() {
		return name.startsWith(CUSTOM_PREFIX) ? name : I18n.format("x13.mod." + name);
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}

	public boolean toggleKey() {
		if (key.isPressed()) {
			toggle();
			return true;
		}
		return false;
	}

	public void setConfig(String[] data) {
		blocks.clear();
		for (String d : data) {
			@SuppressWarnings("deprecation")
			Block b = Registry.BLOCK.getOrDefault(new ResourceLocation(d));
			if (!b.equals(Blocks.AIR))
				blocks.add(b);
		}
	}

	public void toggle() {
		toggle(!enabled);
	}

	public void toggle(boolean enable) {
		toggle(enable, true);
	}

	public void toggle(boolean enable, boolean reloadRenderers) {
		MODES.forEach(m -> m.toggle0(false, false));
		toggle0(enable, reloadRenderers);
		XrayMain.internalFullbright();
		if (reloadRenderers)
			Minecraft.getInstance().worldRenderer.loadRenderers();
	}

	private void toggle0(boolean enable, boolean reloadRenderers) {
		enabled = enable;
	}

	@Override
	public void shouldSideBeRendered(BlockState adjacentState, IBlockReader blockState, BlockPos blockAccess,
			Direction pos, CallbackInfo<Boolean> ci) {
		if (isEnabled())
			ci.setReturnValue(viewMode.getViewer().shouldRenderSide(blocks.contains(adjacentState.getBlock()),
					adjacentState, blockState, blockAccess, pos));
	}
}
