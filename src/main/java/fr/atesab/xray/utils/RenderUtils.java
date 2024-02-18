package fr.atesab.xray.utils;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

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
                Matrix3f matrix3f = stack.peek().getNormalMatrix();
                buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).next();
                buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).next();
        }

        	
        // copied vanilla code for Sodium/Rubidium compatibility, because they overwrite and broke this vanilla code.
       public static void drawBoxVanillaStyle(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red, float green, float blue, float alpha) {
        	drawBoxVanillaStyle(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
        }

        public static void drawBoxVanillaStyle(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        	drawBoxVanillaStyle(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
        }

        public static void drawBoxVanillaStyle(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, float xAxisRed, float yAxisGreen, float zAxisBlue) {
        	Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        	Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        	float f = (float)x1;
        	float g = (float)y1;
        	float h = (float)z1;
        	float i = (float)x2;
        	float j = (float)y2;
        	float k = (float)z2;
        	
        	vertexConsumer.vertex(matrix4f, f, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, i, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, f, j, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	vertexConsumer.vertex(matrix4f, f, g, k).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	
        	
        	vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
        	vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
        	
        	
        	vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        	
        	vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        	}
        	
        	private RenderUtils() {
        }
}
