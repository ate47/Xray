package fr.atesab.xray.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.world.phys.Vec3;

public class RenderUtils {

    public static void renderSingleLine(PoseStack stack, VertexConsumer buffer, float x1, float y1, float z1,
            float x2, float y2,
            float z2, float r, float g, float b, float a) {
        Matrix4f matrix4f = stack.last().pose();
        Matrix3f matrix3f = stack.last().normal();
        Vec3 normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize();
        buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                .normal(matrix3f, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                .normal(matrix3f, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
    }

    private RenderUtils() {
    }
}
