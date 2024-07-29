package fr.atesab.xray.utils;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtils {

    public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
            float x2, float y2,
            float z2, float r, float g, float b, float a) {
        Vector3f normal = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
        normal.normalize();
        renderSingleLine(stack, buffer, x1, y1, z1, x2, y2, z2, r, g, b, a, normal.x(), normal.y(),
                normal.z());
    }

    public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
            float x2, float y2,
            float z2, float r, float g, float b, float a, float normalX, float normalY, float normalZ) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                .normal(stack.peek(), normalX, normalY, normalZ);
        buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                .normal(stack.peek(), normalX, normalY, normalZ);
    }

    private RenderUtils() {
    }
}
