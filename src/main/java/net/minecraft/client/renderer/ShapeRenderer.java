package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.util.NormI8;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.sodium.api.vertex.format.common.LineVertex;
import net.caffeinemc.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.sodium.client.render.vertex.buffer.BufferBuilderExtension;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class ShapeRenderer {
    public static void renderShape(
        PoseStack p_362127_, VertexConsumer p_362290_, VoxelShape p_362784_, double p_360742_, double p_360770_, double p_368227_, int p_362030_
    ) {
        PoseStack.Pose posestack$pose = p_362127_.last();
        p_362784_.forAllEdges(
            (p_368095_, p_361366_, p_363660_, p_361928_, p_364145_, p_361311_) -> {
                Vector3f vector3f = new Vector3f((float)(p_361928_ - p_368095_), (float)(p_364145_ - p_361366_), (float)(p_361311_ - p_363660_)).normalize();
                p_362290_.addVertex(posestack$pose, (float)(p_368095_ + p_360742_), (float)(p_361366_ + p_360770_), (float)(p_363660_ + p_368227_))
                    .setColor(p_362030_)
                    .setNormal(posestack$pose, vector3f);
                p_362290_.addVertex(posestack$pose, (float)(p_361928_ + p_360742_), (float)(p_364145_ + p_360770_), (float)(p_361311_ + p_368227_))
                    .setColor(p_362030_)
                    .setNormal(posestack$pose, vector3f);
            }
        );
    }

    public static void renderLineBox(PoseStack p_367242_,
                                     VertexConsumer p_368944_,
                                     AABB p_369230_,
                                     float p_364083_,
                                     float p_362021_,
                                     float p_362124_,
                                     float p_367649_) {
        renderLineBox(
            p_367242_,
            p_368944_,
            p_369230_.minX,
            p_369230_.minY,
            p_369230_.minZ,
            p_369230_.maxX,
            p_369230_.maxY,
            p_369230_.maxZ,
            p_364083_,
            p_362021_,
            p_362124_,
            p_367649_,
            p_364083_,
            p_362021_,
            p_362124_
        );
    }

    public static void renderLineBox(
        PoseStack p_366452_,
        VertexConsumer p_365817_,
        double p_362632_,
        double p_362535_,
        double p_368825_,
        double p_363850_,
        double p_361520_,
        double p_367127_,
        float p_363525_,
        float p_365172_,
        float p_361957_,
        float p_362174_
    ) {
        renderLineBox(
            p_366452_,
            p_365817_,
            p_362632_,
            p_362535_,
            p_368825_,
            p_363850_,
            p_361520_,
            p_367127_,
            p_363525_,
            p_365172_,
            p_361957_,
            p_362174_,
            p_363525_,
            p_365172_,
            p_361957_
        );
    }

    public static void renderLineBox(PoseStack matrices,
                                     VertexConsumer vertexConsumer,
                                     double x1,
                                     double y1,
                                     double z1,
                                     double x2,
                                     double y2,
                                     double z2,
                                     float red,
                                     float green,
                                     float blue,
                                     float alpha,
                                     float xAxisRed,
                                     float yAxisGreen,
                                     float zAxisBlue) {
        // Sodium
        var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer != null) {
            Matrix4f position = matrices.last().pose();
            Matrix3f normal = matrices.last().normal();

            float x1f = (float) x1;
            float y1f = (float) y1;
            float z1f = (float) z1;
            float x2f = (float) x2;
            float y2f = (float) y2;
            float z2f = (float) z2;

            int color = ColorABGR.pack(red, green, blue, alpha);

            float v1x = org.joml.Math.fma(position.m00(), x1f, org.joml.Math.fma(position.m10(), y1f, org.joml.Math.fma(position.m20(), z1f, position.m30())));
            float v1y = org.joml.Math.fma(position.m01(), x1f, org.joml.Math.fma(position.m11(), y1f, org.joml.Math.fma(position.m21(), z1f, position.m31())));
            float v1z = org.joml.Math.fma(position.m02(), x1f, org.joml.Math.fma(position.m12(), y1f, org.joml.Math.fma(position.m22(), z1f, position.m32())));

            float v2x = org.joml.Math.fma(position.m00(), x2f, org.joml.Math.fma(position.m10(), y1f, org.joml.Math.fma(position.m20(), z1f, position.m30())));
            float v2y = org.joml.Math.fma(position.m01(), x2f, org.joml.Math.fma(position.m11(), y1f, org.joml.Math.fma(position.m21(), z1f, position.m31())));
            float v2z = org.joml.Math.fma(position.m02(), x2f, org.joml.Math.fma(position.m12(), y1f, org.joml.Math.fma(position.m22(), z1f, position.m32())));

            float v3x = org.joml.Math.fma(position.m00(), x1f, org.joml.Math.fma(position.m10(), y2f, org.joml.Math.fma(position.m20(), z1f, position.m30())));
            float v3y = org.joml.Math.fma(position.m01(), x1f, org.joml.Math.fma(position.m11(), y2f, org.joml.Math.fma(position.m21(), z1f, position.m31())));
            float v3z = org.joml.Math.fma(position.m02(), x1f, org.joml.Math.fma(position.m12(), y2f, org.joml.Math.fma(position.m22(), z1f, position.m32())));

            float v4x = org.joml.Math.fma(position.m00(), x1f, org.joml.Math.fma(position.m10(), y1f, org.joml.Math.fma(position.m20(), z2f, position.m30())));
            float v4y = org.joml.Math.fma(position.m01(), x1f, org.joml.Math.fma(position.m11(), y1f, org.joml.Math.fma(position.m21(), z2f, position.m31())));
            float v4z = org.joml.Math.fma(position.m02(), x1f, org.joml.Math.fma(position.m12(), y1f, org.joml.Math.fma(position.m22(), z2f, position.m32())));

            float v5x = org.joml.Math.fma(position.m00(), x2f, org.joml.Math.fma(position.m10(), y2f, org.joml.Math.fma(position.m20(), z1f, position.m30())));
            float v5y = org.joml.Math.fma(position.m01(), x2f, org.joml.Math.fma(position.m11(), y2f, org.joml.Math.fma(position.m21(), z1f, position.m31())));
            float v5z = org.joml.Math.fma(position.m02(), x2f, org.joml.Math.fma(position.m12(), y2f, org.joml.Math.fma(position.m22(), z1f, position.m32())));

            float v6x = org.joml.Math.fma(position.m00(), x1f, org.joml.Math.fma(position.m10(), y2f, org.joml.Math.fma(position.m20(), z2f, position.m30())));
            float v6y = org.joml.Math.fma(position.m01(), x1f, org.joml.Math.fma(position.m11(), y2f, org.joml.Math.fma(position.m21(), z2f, position.m31())));
            float v6z = org.joml.Math.fma(position.m02(), x1f, org.joml.Math.fma(position.m12(), y2f, org.joml.Math.fma(position.m22(), z2f, position.m32())));

            float v7x = org.joml.Math.fma(position.m00(), x2f, org.joml.Math.fma(position.m10(), y1f, org.joml.Math.fma(position.m20(), z2f, position.m30())));
            float v7y = org.joml.Math.fma(position.m01(), x2f, org.joml.Math.fma(position.m11(), y1f, org.joml.Math.fma(position.m21(), z2f, position.m31())));
            float v7z = org.joml.Math.fma(position.m02(), x2f, org.joml.Math.fma(position.m12(), y1f, org.joml.Math.fma(position.m22(), z2f, position.m32())));

            float v8x = org.joml.Math.fma(position.m00(), x2f, org.joml.Math.fma(position.m10(), y2f, org.joml.Math.fma(position.m20(), z2f, position.m30())));
            float v8y = org.joml.Math.fma(position.m01(), x2f, org.joml.Math.fma(position.m11(), y2f, org.joml.Math.fma(position.m21(), z2f, position.m31())));
            float v8z = org.joml.Math.fma(position.m02(), x2f, org.joml.Math.fma(position.m12(), y2f, Math.fma(position.m22(), z2f, position.m32())));

            if (vertexConsumer instanceof BufferBuilderExtension ext) {
                ext.duplicateVertex();
            }

            writeLineVertices(writer, v1x, v1y, v1z, ColorABGR.pack(red, yAxisGreen, zAxisBlue, alpha), NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v2x, v2y, v2z, ColorABGR.pack(red, yAxisGreen, zAxisBlue, alpha), NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v1x, v1y, v1z, ColorABGR.pack(xAxisRed, green, zAxisBlue, alpha), NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v3x, v3y, v3z, ColorABGR.pack(xAxisRed, green, zAxisBlue, alpha), NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v1x, v1y, v1z, ColorABGR.pack(xAxisRed, yAxisGreen, blue, alpha), NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            writeLineVertices(writer, v4x, v4y, v4z, ColorABGR.pack(xAxisRed, yAxisGreen, blue, alpha), NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            writeLineVertices(writer, v2x, v2y, v2z, color, NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v5x, v5y, v5z, color, NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v5x, v5y, v5z, color, NormI8.pack(-normal.m00(), -normal.m01(), -normal.m02()));
            writeLineVertices(writer, v3x, v3y, v3z, color, NormI8.pack(-normal.m00(), -normal.m01(), -normal.m02()));
            writeLineVertices(writer, v3x, v3y, v3z, color, NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            writeLineVertices(writer, v6x, v6y, v6z, color, NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            writeLineVertices(writer, v6x, v6y, v6z, color, NormI8.pack(-normal.m10(), -normal.m11(), -normal.m12()));
            writeLineVertices(writer, v4x, v4y, v4z, color, NormI8.pack(-normal.m10(), -normal.m11(), -normal.m12()));
            writeLineVertices(writer, v4x, v4y, v4z, color, NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v7x, v7y, v7z, color, NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v7x, v7y, v7z, color, NormI8.pack(-normal.m20(), -normal.m21(), -normal.m22()));
            writeLineVertices(writer, v2x, v2y, v2z, color, NormI8.pack(-normal.m20(), -normal.m21(), -normal.m22()));
            writeLineVertices(writer, v6x, v6y, v6z, color, NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v8x, v8y, v8z, color, NormI8.pack(normal.m00(), normal.m01(), normal.m02()));
            writeLineVertices(writer, v7x, v7y, v7z, color, NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v8x, v8y, v8z, color, NormI8.pack(normal.m10(), normal.m11(), normal.m12()));
            writeLineVertices(writer, v5x, v5y, v5z, color, NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            writeLineVertex(writer, v8x, v8y, v8z, color, NormI8.pack(normal.m20(), normal.m21(), normal.m22()));
            return;
        }

        PoseStack.Pose posestack$pose = matrices.last();
        float f = (float)x1;
        float f1 = (float)y1;
        float f2 = (float)z1;
        float f3 = (float)x2;
        float f4 = (float)y2;
        float f5 = (float)z2;
        vertexConsumer.addVertex(posestack$pose, f, f1, f2).setColor(red, yAxisGreen, zAxisBlue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, yAxisGreen, zAxisBlue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f1, f2).setColor(xAxisRed, green, zAxisBlue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f2).setColor(xAxisRed, green, zAxisBlue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f1, f2).setColor(xAxisRed, yAxisGreen, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(posestack$pose, f, f1, f5).setColor(xAxisRed, yAxisGreen, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
        vertexConsumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    }

    // Sodium
    private static void writeLineVertices(VertexBufferWriter writer, float x, float y, float z, int color, int normal) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(2 * LineVertex.STRIDE);
            long ptr = buffer;

            for (int i = 0; i < 2; i++) {
                LineVertex.put(ptr, x, y, z, color, normal);
                ptr += LineVertex.STRIDE;
            }

            writer.push(stack, buffer, 2, LineVertex.FORMAT);
        }
    }

    // Sodium
    private static void writeLineVertex(VertexBufferWriter writer, float x, float y, float z, int color, int normal) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(LineVertex.STRIDE);
            LineVertex.put(buffer, x, y, z, color, normal);

            writer.push(stack, buffer, 1, LineVertex.FORMAT);
        }
    }

    public static void addChainedFilledBoxVertices(
        PoseStack p_364970_,
        VertexConsumer p_368145_,
        double p_361406_,
        double p_360919_,
        double p_368183_,
        double p_369129_,
        double p_366679_,
        double p_368318_,
        float p_365390_,
        float p_360927_,
        float p_369810_,
        float p_368692_
    ) {
        addChainedFilledBoxVertices(
            p_364970_,
            p_368145_,
            (float)p_361406_,
            (float)p_360919_,
            (float)p_368183_,
            (float)p_369129_,
            (float)p_366679_,
            (float)p_368318_,
            p_365390_,
            p_360927_,
            p_369810_,
            p_368692_
        );
    }

    public static void addChainedFilledBoxVertices(
        PoseStack p_363033_,
        VertexConsumer p_368281_,
        float p_363400_,
        float p_368959_,
        float p_368839_,
        float p_363598_,
        float p_369683_,
        float p_364534_,
        float p_369605_,
        float p_364542_,
        float p_367457_,
        float p_362117_
    ) {
        Matrix4f matrix4f = p_363033_.last().pose();
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_368959_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363400_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_368839_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
        p_368281_.addVertex(matrix4f, p_363598_, p_369683_, p_364534_).setColor(p_369605_, p_364542_, p_367457_, p_362117_);
    }

    public static void renderFace(
        PoseStack p_361398_,
        VertexConsumer p_368208_,
        Direction p_364940_,
        float p_361821_,
        float p_366736_,
        float p_364720_,
        float p_369092_,
        float p_365269_,
        float p_361985_,
        float p_366223_,
        float p_362144_,
        float p_364969_,
        float p_369822_
    ) {
        Matrix4f matrix4f = p_361398_.last().pose();
        switch (p_364940_) {
            case DOWN:
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                break;
            case UP:
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                break;
            case NORTH:
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                break;
            case SOUTH:
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                break;
            case WEST:
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_361821_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                break;
            case EAST:
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_364720_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_365269_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
                p_368208_.addVertex(matrix4f, p_369092_, p_366736_, p_361985_).setColor(p_366223_, p_362144_, p_364969_, p_369822_);
        }
    }

    public static void renderVector(PoseStack p_366769_, VertexConsumer p_362011_, Vector3f p_367001_, Vec3 p_367730_, int p_363783_) {
        PoseStack.Pose posestack$pose = p_366769_.last();
        p_362011_.addVertex(posestack$pose, p_367001_)
            .setColor(p_363783_)
            .setNormal(posestack$pose, (float)p_367730_.x, (float)p_367730_.y, (float)p_367730_.z);
        p_362011_.addVertex(
                posestack$pose,
                (float)((double)p_367001_.x() + p_367730_.x),
                (float)((double)p_367001_.y() + p_367730_.y),
                (float)((double)p_367001_.z() + p_367730_.z)
            )
            .setColor(p_363783_)
            .setNormal(posestack$pose, (float)p_367730_.x, (float)p_367730_.y, (float)p_367730_.z);
    }
}