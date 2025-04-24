package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Setter;
import net.caffeinemc.sodium.api.texture.SpriteUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TextureSheetParticle extends SingleQuadParticle {
    protected TextureAtlasSprite sprite;

    // Sodium
    @Setter private boolean shouldTickSprite;

    protected TextureSheetParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    protected TextureSheetParticle(
        ClientLevel p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_
    ) {
        super(p_108328_, p_108329_, p_108330_, p_108331_, p_108332_, p_108333_, p_108334_);
    }

    protected void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;

        // Sodium
        this.shouldTickSprite = sprite != null && SpriteUtil.INSTANCE.hasAnimation(sprite);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        // Sodium
        if (this.shouldTickSprite) {
            SpriteUtil.INSTANCE.markSpriteActive(this.sprite);
        }

        super.render(vertexConsumer, camera, tickDelta);
    }

    @Override
    protected float getU0() {
        return this.sprite.getU0();
    }

    @Override
    protected float getU1() {
        return this.sprite.getU1();
    }

    @Override
    protected float getV0() {
        return this.sprite.getV0();
    }

    @Override
    protected float getV1() {
        return this.sprite.getV1();
    }

    public void pickSprite(SpriteSet p_108336_) {
        this.setSprite(p_108336_.get(this.random));
    }

    public void setSpriteFromAge(SpriteSet p_108340_) {
        if (!this.removed) {
            this.setSprite(p_108340_.get(this.age, this.lifetime));
        }
    }
}