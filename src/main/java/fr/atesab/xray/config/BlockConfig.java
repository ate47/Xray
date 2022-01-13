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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockConfig extends AbstractModeConfig implements SideRenderer {
    @Expose
    private SyncedBlockList blocks;

    @Expose
    private ViewMode viewMode;

    public BlockConfig() {
        this(ViewMode.EXCLUSIVE);
    }

    public BlockConfig(ViewMode viewMode, Block... blocks) {
        super();
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    public BlockConfig(int key, String name, ViewMode viewMode, Block... blocks) {
        super(key, name);
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    public SyncedBlockList getBlocks() {
        return blocks;
    }

    @Override
    public void shouldSideBeRendered(BlockState adjacentState, BlockGetter blockState, BlockPos blockAccess,
            Direction pos, CallbackInfoReturnable<Boolean> ci) {
        if (isEnabled())
            ci.setReturnValue(
                    viewMode.getViewer().shouldRenderSide(blocks.contains(adjacentState.getBlock().getDescriptionId()),
                            adjacentState, blockState, blockAccess, pos));
    }

    public void setEnabled(boolean enable, boolean reloadRenderers) {
        XrayMain mod = XrayMain.getMod();

        if (enable) {
            // disable the previous mode
            BlockConfig old = mod.getConfig().getSelectedBlockMode();
            if (old != null)
                old.setEnabled(false);
        }

        setEnabled(enable);

        mod.internalFullbright();

        if (reloadRenderers)
            Minecraft.getInstance().levelRenderer.allChanged();
    }
}
