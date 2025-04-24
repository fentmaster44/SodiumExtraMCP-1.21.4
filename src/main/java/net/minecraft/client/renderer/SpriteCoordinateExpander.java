package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.sodium.api.vertex.attributes.common.TextureAttribute;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class SpriteCoordinateExpander implements VertexConsumer, VertexBufferWriter {
    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    // Sodium
    private boolean canUseIntrinsics;
    private float minU, minV;
    private float maxU, maxV;

    public SpriteCoordinateExpander(VertexConsumer delegate, TextureAtlasSprite sprite) {
        this.delegate = delegate;
        this.sprite = sprite;

        // Sodium
        this.minU = sprite.getU0();
        this.minV = sprite.getV0();

        this.maxU = sprite.getU1();
        this.maxV = sprite.getV1();

        this.canUseIntrinsics = VertexBufferWriter.tryOf(this.delegate) != null;
    }

    @Override
    public VertexConsumer addVertex(float p_342932_, float p_342886_, float p_342696_) {
        return this.delegate.addVertex(p_342932_, p_342886_, p_342696_);
    }

    @Override
    public VertexConsumer setColor(int p_344589_, int p_342555_, int p_344320_, int p_345258_) {
        return this.delegate.setColor(p_344589_, p_342555_, p_344320_, p_345258_);
    }

    @Override
    public VertexConsumer setUv(float p_343856_, float p_344420_) {
        return this.delegate.setUv(this.sprite.getU(p_343856_), this.sprite.getV(p_344420_));
    }

    @Override
    public VertexConsumer setUv1(int p_343784_, int p_344827_) {
        return this.delegate.setUv1(p_343784_, p_344827_);
    }

    @Override
    public VertexConsumer setUv2(int p_345257_, int p_344124_) {
        return this.delegate.setUv2(p_345257_, p_344124_);
    }

    @Override
    public VertexConsumer setNormal(float p_342779_, float p_342534_, float p_344783_) {
        return this.delegate.setNormal(p_342779_, p_342534_, p_344783_);
    }

    @Override
    public void addVertex(
        float p_342812_,
        float p_344058_,
        float p_343304_,
        int p_343913_,
        float p_344339_,
        float p_343349_,
        int p_344262_,
        int p_345265_,
        float p_344296_,
        float p_345357_,
        float p_343817_
    ) {
        this.delegate
            .addVertex(
                p_342812_,
                p_344058_,
                p_343304_,
                p_343913_,
                this.sprite.getU(p_344339_),
                this.sprite.getV(p_343349_),
                p_344262_,
                p_345265_,
                p_344296_,
                p_345357_,
                p_343817_
            );
    }

    @Override
    public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
        transform(ptr, count, format,
                this.minU, this.minV, this.maxU, this.maxV);

        VertexBufferWriter.of(this.delegate)
                .push(stack, ptr, count, format);
    }

    /**
     * Transforms the texture UVs for each vertex from their absolute coordinates into the sprite area specified
     * by the parameters.
     *
     * @param ptr    The buffer of vertices to transform
     * @param count  The number of vertices to transform
     * @param format The format of the vertices
     * @param minU   The minimum X-coordinate of the sprite bounds
     * @param minV   The minimum Y-coordinate of the sprite bounds
     * @param maxU   The maximum X-coordinate of the sprite bounds
     * @param maxV   The maximum Y-coordinate of the sprite bounds
     */
    private static void transform(long ptr, int count, VertexFormat format,
                                  float minU, float minV, float maxU, float maxV) {
        long stride = format.getVertexSize();
        long offsetUV = format.getOffset(VertexFormatElement.UV0);

        // The width/height of the sprite
        float w = maxU - minU;
        float h = maxV - minV;

        for (int vertexIndex = 0; vertexIndex < count; vertexIndex++) {
            // The texture coordinates relative to the sprite bounds
            float u = TextureAttribute.getU(ptr + offsetUV);
            float v = TextureAttribute.getV(ptr + offsetUV);

            // The texture coordinates in absolute space on the sprite sheet
            float ut = minU + (w * u);
            float vt = minV + (h * v);

            TextureAttribute.put(ptr + offsetUV, ut, vt);

            ptr += stride;
        }
    }

    @Override
    public boolean canUseIntrinsics() {
        return canUseIntrinsics;
    }
}