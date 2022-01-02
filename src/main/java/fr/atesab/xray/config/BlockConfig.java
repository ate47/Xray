package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.SideRenderer;
import fr.atesab.xray.view.ViewMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockConfig extends AbstractModeConfig implements SideRenderer {
    @Expose
    private SyncedBlockList blocks = new SyncedBlockList();

    @Expose
    private ViewMode viewMode = ViewMode.EXCLUSIVE;

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
}
