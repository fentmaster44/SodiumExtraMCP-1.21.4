package com.mojang.blaze3d.vertex;

import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.util.NormI8;
import net.caffeinemc.sodium.api.vertex.attributes.common.ColorAttribute;
import net.caffeinemc.sodium.api.vertex.attributes.common.TextureAttribute;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class SheetedDecalTextureGenerator implements VertexConsumer, VertexBufferWriter {
    private final VertexConsumer delegate;
    private final Matrix4f cameraInversePose;
    private final Matrix3f normalInversePose;
    private final float textureScale;
    private final Vector3f worldPos = new Vector3f();
    private final Vector3f normal = new Vector3f();
    private float x;
    private float y;
    private float z;

    // Sodium
    private boolean canUseIntrinsics;

    public SheetedDecalTextureGenerator(VertexConsumer p_260211_, PoseStack.Pose p_332899_, float p_259312_) {
        this.delegate = p_260211_;
        this.cameraInversePose = new Matrix4f(p_332899_.pose()).invert();
        this.normalInversePose = new Matrix3f(p_332899_.normal()).invert();
        this.textureScale = p_259312_;

        // Sodium
        this.canUseIntrinsics = VertexBufferWriter.tryOf(this.delegate) != null;
    }

    @Override
    public VertexConsumer addVertex(float p_345104_, float p_342988_, float p_342152_) {
        this.x = p_345104_;
        this.y = p_342988_;
        this.z = p_342152_;
        this.delegate.addVertex(p_345104_, p_342988_, p_342152_);
        return this;
    }

    @Override
    public VertexConsumer setColor(int p_344386_, int p_345260_, int p_344616_, int p_345057_) {
        this.delegate.setColor(-1);
        return this;
    }

    @Override
    public VertexConsumer setUv(float p_343310_, float p_343059_) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int p_344277_, int p_343886_) {
        this.delegate.setUv1(p_344277_, p_343886_);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int p_342602_, int p_345062_) {
        this.delegate.setUv2(p_342602_, p_345062_);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float p_344306_, float p_342091_, float p_344579_) {
        this.delegate.setNormal(p_344306_, p_342091_, p_344579_);
        Vector3f vector3f = this.normalInversePose.transform(p_344306_, p_342091_, p_344579_, this.normal);
        Direction direction = Direction.getApproximateNearest(vector3f.x(), vector3f.y(), vector3f.z());
        Vector3f vector3f1 = this.cameraInversePose.transformPosition(this.x, this.y, this.z, this.worldPos);
        vector3f1.rotateY((float) Math.PI);
        vector3f1.rotateX((float) (-Math.PI / 2));
        vector3f1.rotate(direction.getRotation());
        this.delegate.setUv(-vector3f1.x() * this.textureScale, -vector3f1.y() * this.textureScale);
        return this;
    }

    // Sodium
    @Override
    public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
        transform(ptr, count, format,
                this.normalInversePose, this.cameraInversePose, this.textureScale);

        VertexBufferWriter.of(this.delegate)
                .push(stack, ptr, count, format);
    }

    @Override
    public boolean canUseIntrinsics() {
        return canUseIntrinsics;
    }

    /**
     * Transforms the overlay UVs element of each vertex to create a perspective-mapped effect.
     *
     * @param ptr    The buffer of vertices to transform
     * @param count  The number of vertices to transform
     * @param format The format of the vertices
     * @param inverseNormalMatrix The inverted normal matrix
     * @param inverseTextureMatrix The inverted texture matrix
     * @param textureScale The amount which the overlay texture should be adjusted
     */
    private static void transform(long ptr, int count, VertexFormat format,
                                  Matrix3f inverseNormalMatrix, Matrix4f inverseTextureMatrix, float textureScale) {
        long stride = format.getVertexSize();

        var offsetPosition = format.getOffset(VertexFormatElement.POSITION);
        var offsetColor = format.getOffset(VertexFormatElement.COLOR);
        var offsetNormal = format.getOffset(VertexFormatElement.NORMAL);
        var offsetTexture = format.getOffset(VertexFormatElement.UV0);

        int color = ColorABGR.pack(1.0f, 1.0f, 1.0f, 1.0f);

        var normal = new Vector3f(Float.NaN);
        var position = new Vector4f(Float.NaN);

        for (int vertexIndex = 0; vertexIndex < count; vertexIndex++) {
            position.x = MemoryUtil.memGetFloat(ptr + offsetPosition + 0);
            position.y = MemoryUtil.memGetFloat(ptr + offsetPosition + 4);
            position.z = MemoryUtil.memGetFloat(ptr + offsetPosition + 8);
            position.w = 1.0f;

            int packedNormal = MemoryUtil.memGetInt(ptr + offsetNormal);
            normal.x = NormI8.unpackX(packedNormal);
            normal.y = NormI8.unpackY(packedNormal);
            normal.z = NormI8.unpackZ(packedNormal);

            Vector3f transformedNormal = inverseNormalMatrix.transform(normal);
            Direction direction = Direction.getApproximateNearest(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());

            Vector4f transformedTexture = inverseTextureMatrix.transform(position);
            transformedTexture.rotateY(3.1415927F);
            transformedTexture.rotateX(-1.5707964F);
            transformedTexture.rotate(direction.getRotation());

            float textureU = -transformedTexture.x() * textureScale;
            float textureV = -transformedTexture.y() * textureScale;

            ColorAttribute.set(ptr + offsetColor, color);
            TextureAttribute.put(ptr + offsetTexture, textureU, textureV);

            ptr += stride;
        }
    }

    // Sodium end
}