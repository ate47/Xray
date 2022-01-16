package fr.atesab.xray.color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import fr.atesab.xray.utils.RenderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record SideRendering(BlockPos state, Direction pos) {
    private static final float R = 0.5f;
    private static final float G = 0.5f;
    private static final float B = 0.5f;
    private static final float A = 0.2f;

    public void addVertex(VertexConsumer buffer, PoseStack stack) {
        int normalX = pos().getStepX();
        int normalY = pos().getStepY();
        int normalZ = pos().getStepZ();

        int x1 = state.getX() + (normalX + 1) / 2;
        int y1 = state.getY() + (normalY + 1) / 2;
        int z1 = state.getZ() + (normalZ + 1) / 2;

        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int z2 = z1 + 1;

        if (normalX != 0) {
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x1, y1, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x1, y2, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z2,
                    x1, y2, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y2, z1,
                    x1, y2, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
        } else if (normalY != 0) {
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x1, y1, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x2, y1, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z2,
                    x2, y1, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x2, y1, z1,
                    x2, y1, z2,
                    R, G, B, A,
                    normalX, normalY, normalZ);
        } else { // if (normalZ != 0)
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x1, y2, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y1, z1,
                    x2, y1, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x1, y2, z1,
                    x2, y2, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
            RenderUtils.renderSingleLine(stack, buffer,
                    x2, y1, z1,
                    x2, y2, z1,
                    R, G, B, A,
                    normalX, normalY, normalZ);
        }
    }
}
