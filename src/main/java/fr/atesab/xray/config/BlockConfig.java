package fr.atesab.xray.config;

import java.util.Objects;

import com.google.gson.annotations.Expose;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.SideRenderer;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.view.ViewMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockConfig extends AbstractModeConfig implements SideRenderer, Cloneable {
    @Expose
    private SyncedBlockList blocks;

    @Expose
    private ViewMode viewMode;

    public BlockConfig() {
        this(ViewMode.EXCLUSIVE);
    }

    private BlockConfig(BlockConfig other) {
        super(other);
        this.viewMode = other.viewMode;
        this.blocks = other.blocks.clone();
    }

    public BlockConfig(ViewMode viewMode, Block... blocks) {
        super();
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    public BlockConfig(int key, int ScanCode, String name, ViewMode viewMode, Block... blocks) {
        super(key, ScanCode, name);
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    public SyncedBlockList getBlocks() {
        return blocks;
    }

    @Override
    public void shouldSideBeRendered(BlockState adjacentState, BlockGetter blockState, BlockPos blockAccess,
            Direction pos, CallbackInfoReturnable<Boolean> ci) {
        if (!isEnabled())
            return;

        String name = Registry.BLOCK.getKey(adjacentState.getBlock()).toString();
        boolean present = blocks.contains(name);
        boolean shouldRender = viewMode.getViewer().shouldRenderSide(present, adjacentState, blockState,
                blockAccess, pos);
        ci.setReturnValue(shouldRender);
    }

    @Override
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enable, boolean reloadRenderers) {
        XrayMain mod = XrayMain.getMod();

        if (enable) {
            // disable the previous mode
            BlockConfig old = mod.getConfig().getSelectedBlockMode();
            if (old != null)
                old.setEnabled(false);
        }

        super.setEnabled(enable);

        mod.internalFullbright();

        if (reloadRenderers)
            Minecraft.getInstance().levelRenderer.allChanged();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public BlockConfig clone() {
        return new BlockConfig(this);
    }

}
