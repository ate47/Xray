package fr.atesab.xray.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderUtils {

        public static void renderSingleLine(PoseStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                        float x2, float y2,
                        float z2, float r, float g, float b, float a) {
                Vec3 normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize();
                renderSingleLine(stack, buffer, x1, y1, z1, x2, y2, z2, r, g, b, a, (float) normal.x, (float) normal.y,
                                (float) normal.z);
        }

        public static void renderSingleLine(PoseStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                        float x2, float y2,
                        float z2, float r, float g, float b, float a, float normalX, float normalY, float normalZ) {
                Matrix4f matrix4f = stack.last().pose();
                Matrix3f matrix3f = stack.last().normal();
                buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).endVertex();
                buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                                .normal(matrix3f, normalX, normalY, normalZ).endVertex();
        }

        // copied vanilla code for Sodium/Rubidium compatibility, because they overwrite and broke this vanilla code.
        public static void renderLineBoxVanillaStyle(PoseStack p_109647_, VertexConsumer p_109648_, AABB p_109649_, float p_109650_, float p_109651_, float p_109652_, float p_109653_) {
        	renderLineBoxVanillaStyle(p_109647_, p_109648_, p_109649_.minX, p_109649_.minY, p_109649_.minZ, p_109649_.maxX, p_109649_.maxY, p_109649_.maxZ, p_109650_, p_109651_, p_109652_, p_109653_, p_109650_, p_109651_, p_109652_);
         }

        public static void renderLineBoxVanillaStyle(PoseStack p_109609_, VertexConsumer p_109610_, double p_109611_, double p_109612_, double p_109613_, double p_109614_, double p_109615_, double p_109616_, float p_109617_, float p_109618_, float p_109619_, float p_109620_) {
        	renderLineBoxVanillaStyle(p_109609_, p_109610_, p_109611_, p_109612_, p_109613_, p_109614_, p_109615_, p_109616_, p_109617_, p_109618_, p_109619_, p_109620_, p_109617_, p_109618_, p_109619_);
        }

        public static void renderLineBoxVanillaStyle(PoseStack p_109622_, VertexConsumer p_109623_, double p_109624_, double p_109625_, double p_109626_, double p_109627_, double p_109628_, double p_109629_, float p_109630_, float p_109631_, float p_109632_, float p_109633_, float p_109634_, float p_109635_, float p_109636_) {
            Matrix4f matrix4f = p_109622_.last().pose();
            Matrix3f matrix3f = p_109622_.last().normal();
            float f = (float)p_109624_;
            float f1 = (float)p_109625_;
            float f2 = (float)p_109626_;
            float f3 = (float)p_109627_;
            float f4 = (float)p_109628_;
            float f5 = (float)p_109629_;
            p_109623_.vertex(matrix4f, f, f1, f2).color(p_109630_, p_109635_, p_109636_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109635_, p_109636_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f1, f2).color(p_109634_, p_109631_, p_109636_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f2).color(p_109634_, p_109631_, p_109636_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f1, f2).color(p_109634_, p_109635_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f1, f5).color(p_109634_, p_109635_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
            p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
            p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
         }

        private RenderUtils() {
        }
}
