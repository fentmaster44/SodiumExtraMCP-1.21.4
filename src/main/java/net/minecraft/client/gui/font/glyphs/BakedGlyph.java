package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.sodium.api.math.MatrixHelper;
import net.caffeinemc.sodium.api.util.ColorARGB;
import net.caffeinemc.sodium.api.vertex.format.common.GlyphVertex;
import net.caffeinemc.sodium.client.render.vertex.VertexConsumerUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class BakedGlyph {
    public static final float Z_FIGHTER = 0.001F;
    private final GlyphRenderTypes renderTypes;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;

    public BakedGlyph(
        GlyphRenderTypes p_285527_,
        float p_285271_,
        float p_284970_,
        float p_285098_,
        float p_285023_,
        float p_285242_,
        float p_285043_,
        float p_285100_,
        float p_284948_) {

        this.renderTypes = p_285527_;
        this.u0 = p_285271_;
        this.u1 = p_284970_;
        this.v0 = p_285098_;
        this.v1 = p_285023_;
        this.left = p_285242_;
        this.right = p_285043_;
        this.up = p_285100_;
        this.down = p_284948_;
    }

    public void renderChar(BakedGlyph.GlyphInstance p_368554_, Matrix4f p_365625_, VertexConsumer p_370130_, int p_369456_) {
        Style style = p_368554_.style();
        boolean flag = style.isItalic();
        float f = p_368554_.x();
        float f1 = p_368554_.y();
        int i = p_368554_.color();
        int j = p_368554_.shadowColor();
        boolean flag1 = style.isBold();
        if (p_368554_.hasShadow()) {
            this.render(flag, f + p_368554_.shadowOffset(), f1 + p_368554_.shadowOffset(), p_365625_, p_370130_, j, flag1, p_369456_);
            this.render(flag, f, f1, 0.03F, p_365625_, p_370130_, i, flag1, p_369456_);
        } else {
            this.render(flag, f, f1, p_365625_, p_370130_, i, flag1, p_369456_);
        }

        if (flag1) {
            if (p_368554_.hasShadow()) {
                this.render(
                    flag, f + p_368554_.boldOffset() + p_368554_.shadowOffset(), f1 + p_368554_.shadowOffset(), 0.001F, p_365625_, p_370130_, j, true, p_369456_
                );
                this.render(flag, f + p_368554_.boldOffset(), f1, 0.03F, p_365625_, p_370130_, i, true, p_369456_);
            } else {
                this.render(flag, f + p_368554_.boldOffset(), f1, p_365625_, p_370130_, i, true, p_369456_);
            }
        }
    }

    private void render(boolean p_95227_,
                        float p_95228_,
                        float p_95229_,
                        Matrix4f p_253706_,
                        VertexConsumer p_95231_,
                        int p_95236_,
                        boolean p_378824_,
                        int p_365126_) {
        this.render(p_95227_, p_95228_, p_95229_, 0.0F, p_253706_, p_95231_, p_95236_, p_378824_, p_365126_);
    }

    private void render(boolean italic,
                        float x,
                        float y,
                        float z,
                        Matrix4f matrix,
                        VertexConsumer vertexConsumer,
                        int c,
                        boolean bl2,
                        int light) {

        var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer != null) {
            float x1 = x + this.left;
            float x2 = x + this.right;
            float h1 = y + this.up;
            float h2 = y + this.down;
            float w1 = italic ? 1.0F - 0.25F * this.up : 0.0F;
            float w2 = italic ? 1.0F - 0.25F * this.down : 0.0F;
            float offset = bl2 ? 0.1F : 0.0F;

            int color = ColorARGB.toABGR(c);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                long buffer = stack.nmalloc(4 * GlyphVertex.STRIDE);
                long ptr = buffer;

                write(ptr, matrix, x1 + w1 - offset, h1 - offset, z, color, this.u0, this.v0, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x1 + w2 - offset, h2 + offset, z, color, this.u0, this.v1, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x2 + w2 + offset, h2 + offset, z, color, this.u1, this.v1, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x2 + w1 + offset, h1 - offset, z, color, this.u1, this.v0, light);
                ptr += GlyphVertex.STRIDE;

                writer.push(stack, buffer, 4, GlyphVertex.FORMAT);
            }
            return;
        }

        float f = x + this.left;
        float f1 = x + this.right;
        float f2 = y + this.up;
        float f3 = y + this.down;
        float f4 = italic ? 1.0F - 0.25F * this.up : 0.0F;
        float f5 = italic ? 1.0F - 0.25F * this.down : 0.0F;
        float f6 = bl2 ? 0.1F : 0.0F;
        vertexConsumer.addVertex(matrix, f + f4 - f6, f2 - f6, z).setColor(c).setUv(this.u0, this.v0).setLight(light);
        vertexConsumer.addVertex(matrix, f + f5 - f6, f3 + f6, z).setColor(c).setUv(this.u0, this.v1).setLight(light);
        vertexConsumer.addVertex(matrix, f1 + f5 + f6, f3 + f6, z).setColor(c).setUv(this.u1, this.v1).setLight(light);
        vertexConsumer.addVertex(matrix, f1 + f4 + f6, f2 - f6, z).setColor(c).setUv(this.u1, this.v0).setLight(light);
    }

    private static void write(long buffer,
                              Matrix4f matrix, float x, float y, float z, int color, float u, float v, int light) {
        float x2 = MatrixHelper.transformPositionX(matrix, x, y, z);
        float y2 = MatrixHelper.transformPositionY(matrix, x, y, z);
        float z2 = MatrixHelper.transformPositionZ(matrix, x, y, z);

        GlyphVertex.put(buffer, x2, y2, z2, color, u, v, light);
    }

    public void renderEffect(BakedGlyph.Effect p_95221_, Matrix4f p_254370_, VertexConsumer p_95223_, int p_95224_) {
        if (p_95221_.hasShadow()) {
            this.buildEffect(p_95221_, p_95221_.shadowOffset(), 0.0F, p_95221_.shadowColor(), p_95223_, p_95224_, p_254370_);
            this.buildEffect(p_95221_, 0.0F, 0.03F, p_95221_.color, p_95223_, p_95224_, p_254370_);
        } else {
            this.buildEffect(p_95221_, 0.0F, 0.0F, p_95221_.color, p_95223_, p_95224_, p_254370_);
        }
    }

    private void buildEffect(BakedGlyph.Effect effect,
                             float offset,
                             float depthOffset,
                             int c,
                             VertexConsumer vertexConsumer,
                             int light,
                             Matrix4f matrix) {

        var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer != null) {
            float x1 = effect.x0();
            float x2 = effect.x1();
            float h1 = effect.y0();
            float h2 = effect.y1();
            float z = effect.depth() + depthOffset;

            int color = ColorARGB.toABGR(c);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                long buffer = stack.nmalloc(4 * GlyphVertex.STRIDE);
                long ptr = buffer;

                write(ptr, matrix, x1 + offset, h1 + offset, z, color, this.u0, this.v0, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x2 + offset, h1 + offset, z, color, this.u0, this.v1, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x2 + offset, h2 + offset, z, color, this.u1, this.v1, light);
                ptr += GlyphVertex.STRIDE;

                write(ptr, matrix, x1 + offset, h2 + offset, z, color, this.u1, this.v0, light);
                ptr += GlyphVertex.STRIDE;

                writer.push(stack, buffer, 4, GlyphVertex.FORMAT);
            }
            return;
        }

        vertexConsumer.addVertex(matrix, effect.x0 + offset, effect.y0 + offset, effect.depth + depthOffset)
            .setColor(c)
            .setUv(this.u0, this.v0)
            .setLight(light);
        vertexConsumer.addVertex(matrix, effect.x1 + offset, effect.y0 + offset, effect.depth + depthOffset)
            .setColor(c)
            .setUv(this.u0, this.v1)
            .setLight(light);
        vertexConsumer.addVertex(matrix, effect.x1 + offset, effect.y1 + offset, effect.depth + depthOffset)
            .setColor(c)
            .setUv(this.u1, this.v1)
            .setLight(light);
        vertexConsumer.addVertex(matrix, effect.x0 + offset, effect.y1 + offset, effect.depth + depthOffset)
            .setColor(c)
            .setUv(this.u1, this.v0)
            .setLight(light);
    }

    public RenderType renderType(Font.DisplayMode p_181388_) {
        return this.renderTypes.select(p_181388_);
    }

    @OnlyIn(Dist.CLIENT)
    public static record Effect(float x0, float y0, float x1, float y1, float depth, int color, int shadowColor, float shadowOffset) {
        public Effect(float p_95247_, float p_95248_, float p_95249_, float p_95250_, float p_95251_, int p_365759_) {
            this(p_95247_, p_95248_, p_95249_, p_95250_, p_95251_, p_365759_, 0, 0.0F);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record GlyphInstance(
        float x, float y, int color, int shadowColor, BakedGlyph glyph, Style style, float boldOffset, float shadowOffset
    ) {
        boolean hasShadow() {
            return this.shadowColor() != 0;
        }
    }
}