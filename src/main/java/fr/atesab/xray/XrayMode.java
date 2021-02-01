package fr.atesab.xray;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.loom.util.FabricApiExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.List;

public class XrayMode implements SideRenderer {
	@FunctionalInterface
	public static interface Viewer {
		public boolean shouldRenderSide(boolean blockInList, BlockState adjacentState, BlockView blockState,
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
	private FabricKeyBinding key;
	private final String name;
	private final int color;
	private ViewMode viewMode;
	private static int colorCursor = -1;
	private static final int[] COLORS = {
			0xff00ffff, 0xffff0000, 0xffffff00, 0xffff00ff,
			0xff7aff00, 0xffff7a00, 0xff00ff7a, 0xffff007a,
			0xff7a00ff, 0xff7a7aff, 0xff7aff7a, 0xffff7a7a
	};
	public static final String CUSTOM_PREFIX = "Custom_";

	static int nextColor() {
		return COLORS[colorCursor = (colorCursor + 1) % COLORS.length];
	}

	public XrayMode(String name, int keyCode, ViewMode viewMode) {
		this.name = name;
		this.color = nextColor();
		this.enabled = false;
		this.blocks = Lists.newArrayList();
		this.key = new FabricKeyBinding(
				new Identifier(name),
				InputUtil.Type.KEYSYM,
				keyCode,
				"key.categories.xray"
		) {
		};
		this.viewMode = viewMode;
		this.defaultBlocks = "";
		MODES.add(this);
	}

	public XrayMode(String name, int keyCode, ViewMode viewMode, Block... defaultBlocks) {
		this.name = name;
		this.color = nextColor();
		this.enabled = false;
		this.blocks = Lists.newArrayList(defaultBlocks);
		this.key = new FabricKeyBinding(
				new Identifier("x13.mod." + name),
				InputUtil.Type.KEYSYM,
				keyCode,
				"key.categories.xray"
		) {
		};
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

	public FabricKeyBinding getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getNameTranslate() {
		return name.startsWith(CUSTOM_PREFIX) ? name : I18n.translate("x13.mod." + name);
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
			Block b = Registry.BLOCK.get(new Identifier(d));
			if (b != null && !b.equals(Blocks.AIR))
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
		XrayMain.getMod().internalFullbright();
		if (reloadRenderers)
			MinecraftClient.getInstance().worldRenderer.reload();
	}

	private void toggle0(boolean enable, boolean reloadRenderers) {
		enabled = enable;
	}

	@Override
	public void shouldSideBeRendered(BlockState adjacentState, BlockView blockState, BlockPos blockAccess,
									 Direction pos, CallbackInfo<Boolean> ci) {
		if (isEnabled())
			ci.setReturnValue(viewMode.getViewer().shouldRenderSide(blocks.contains(adjacentState.getBlock()),
					adjacentState, blockState, blockAccess, pos));
	}
}
