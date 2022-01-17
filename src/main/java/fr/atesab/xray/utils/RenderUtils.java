package fr.atesab.xray.utils;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderUtils {

        public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                        float x2, float y2,
                        float z2, float r, float g, float b, float a) {
                Vec3f normal = new Vec3f(x2 - x1, y2 - y1, z2 - z1);
                normal.normalize();
                renderSingleLine(stack, buffer, x1, y1, z1, x2, y2, z2, r, g, b, a, normal.getX(), normal.getY(),
                                normal.getZ());
        }

        public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                        float x2, float y2,
                        float z2, float r, float g, float b, float a, float normalX, float normalY, float normalZ) {
                Matrix4f matrix4f = stack.peek().getPositionMatrix();
                Matrix3f matrix3f = stack.peek().getNormalMatrix();
                buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).next();
                buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).next();
        }

        private RenderUtils() {
        }
}
