package net.minecraft.client.renderer.block.model;

import net.caffeinemc.sodium.client.model.quad.BakedQuadView;
import net.caffeinemc.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.sodium.client.model.quad.properties.ModelQuadFlags;
import net.caffeinemc.sodium.client.util.ModelQuadUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements BakedQuadView {
    protected final int[] vertices;
    protected final int tintIndex;
    protected final Direction direction;
    protected final TextureAtlasSprite sprite;
    private final boolean shade;
    private final int lightEmission;

    // Sodium
    private int flags;
    private int normal;
    private ModelQuadFacing normalFace = null;

    public BakedQuad(int[] vertices,
                     int tintIndex,
                     Direction face,
                     TextureAtlasSprite textureAtlasSprite,
                     boolean shade,
                     int lightEmission) {
        this.vertices = vertices;
        this.tintIndex = tintIndex;
        this.direction = face;
        this.sprite = textureAtlasSprite;
        this.shade = shade;
        this.lightEmission = lightEmission;

        // Sodium
        this.normal = this.calculateNormal();
        this.normalFace = ModelQuadFacing.fromPackedNormal(this.normal);

        this.flags = ModelQuadFlags.getQuadFlags(this, face);
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return this.sprite;
    }

    public int[] getVertices() {
        return this.vertices;
    }

    public boolean isTinted() {
        return this.tintIndex != -1;
    }

    @Override
    public int getTintIndex() {
        return this.tintIndex;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isShade() {
        return this.shade;
    }

    public int getLightEmission() {
        return this.lightEmission;
    }

    @Override
    public float getX(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX]);
    }

    @Override
    public float getY(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX + 1]);
    }

    @Override
    public float getZ(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX + 2]);
    }

    @Override
    public int getColor(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.COLOR_INDEX];
    }

    @Override
    public int getVertexNormal(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.NORMAL_INDEX];
    }

    @Override
    public int getLight(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.LIGHT_INDEX];
    }

    @Override
    public float getTexU(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX]);
    }

    @Override
    public float getTexV(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX + 1]);
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public ModelQuadFacing getNormalFace() {
        return this.normalFace;
    }

    @Override
    public int getFaceNormal() {
        return this.normal;
    }

    @Override
    public Direction getLightFace() {
        return this.direction;
    }

    @Override
    public int getMaxLightQuad(int idx) {
        return LightTexture.lightCoordsWithEmission(getLight(idx), getLightEmission());
    }

    @Override
    // The target class has a function with the same name in a remapped environment
    public boolean hasShade() {
        return this.shade;
    }

    @Override
    public boolean hasAO() {
        return true;
    }
}