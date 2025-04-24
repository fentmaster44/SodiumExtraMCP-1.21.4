package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;

import lombok.val;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite {
    private final ResourceLocation atlasLocation;
    private final SpriteContents contents;
    final int x;
    final int y;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    // Sodium
    private boolean hasUnknownImageContents;

    protected TextureAtlasSprite(ResourceLocation p_250211_, SpriteContents p_248526_, int p_248950_, int p_249741_, int p_248672_, int p_248637_) {
        this.atlasLocation = p_250211_;
        this.contents = p_248526_;
        this.x = p_248672_;
        this.y = p_248637_;
        this.u0 = (float)p_248672_ / (float)p_248950_;
        this.u1 = (float)(p_248672_ + p_248526_.width()) / (float)p_248950_;
        this.v0 = (float)p_248637_ / (float)p_249741_;
        this.v1 = (float)(p_248637_ + p_248526_.height()) / (float)p_249741_;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public float getU0() {
        return this.u0;
    }

    public float getU1() {
        return this.u1;
    }

    public SpriteContents contents() {
        return this.contents;
    }

    @Nullable
    public TextureAtlasSprite.Ticker createTicker() {
        val ticker = contents.createTicker();

        if (ticker != null && !(ticker instanceof SpriteContents.Ticker)) {
            this.hasUnknownImageContents = true;
        }

        return ticker != null ? new TextureAtlasSprite.Ticker() {
            @Override
            public void tickAndUpload() {
                ticker.tickAndUpload(TextureAtlasSprite.this.x, TextureAtlasSprite.this.y);
            }

            @Override
            public void close() {
                ticker.close();
            }
        } : null;
    }

    public float getU(float p_298825_) {
        float f = this.u1 - this.u0;
        return this.u0 + f * p_298825_;
    }

    public float getUOffset(float p_174728_) {
        float f = this.u1 - this.u0;
        return (p_174728_ - this.u0) / f;
    }

    public float getV0() {
        return this.v0;
    }

    public float getV1() {
        return this.v1;
    }

    public float getV(float p_299087_) {
        float f = this.v1 - this.v0;
        return this.v0 + f * p_299087_;
    }

    public float getVOffset(float p_174742_) {
        float f = this.v1 - this.v0;
        return (p_174742_ - this.v0) / f;
    }

    public ResourceLocation atlasLocation() {
        return this.atlasLocation;
    }

    @Override
    public String toString() {
        return "TextureAtlasSprite{contents='"
            + this.contents
            + "', u0="
            + this.u0
            + ", u1="
            + this.u1
            + ", v0="
            + this.v0
            + ", v1="
            + this.v1
            + "}";
    }

    public void uploadFirstFrame() {
        this.contents.uploadFirstFrame(this.x, this.y);
    }

    private float atlasSize() {
        float f = (float)this.contents.width() / (this.u1 - this.u0);
        float f1 = (float)this.contents.height() / (this.v1 - this.v0);
        return Math.max(f1, f);
    }

    // Sodium
    public float uvShrinkRatio() {
        // Vanilla tries to apply a bias to texture coordinates to avoid texture bleeding (see FaceBakery#bakeQuad).
        // This is counterproductive with Sodium's terrain rendering, since the bias is applied in the shader instead.
        // Overwrite this method instead of adjusting its return value in FaceBakery as other mods may use it to
        // manually apply the bias.
        return 0.0f;
    }

    public VertexConsumer wrap(VertexConsumer p_118382_) {
        return new SpriteCoordinateExpander(p_118382_, this);
    }

    @OnlyIn(Dist.CLIENT)
    public interface Ticker extends AutoCloseable {
        void tickAndUpload();

        @Override
        void close();
    }
}