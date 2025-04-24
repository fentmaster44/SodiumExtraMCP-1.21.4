package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.val;
import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.sodium.api.vertex.format.common.ParticleVertex;
import net.caffeinemc.sodium.client.render.vertex.VertexConsumerUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public abstract class SingleQuadParticle extends Particle {
    protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

    // Sodium
    private static final Vector3f TEMP_LEFT = new Vector3f();
    private static final Vector3f TEMP_UP = new Vector3f();

    protected SingleQuadParticle(ClientLevel p_107665_, double p_107666_, double p_107667_, double p_107668_) {
        super(p_107665_, p_107666_, p_107667_, p_107668_);
    }

    protected SingleQuadParticle(
        ClientLevel p_107670_, double p_107671_, double p_107672_, double p_107673_, double p_107674_, double p_107675_, double p_107676_
    ) {
        super(p_107670_, p_107671_, p_107672_, p_107673_, p_107674_, p_107675_, p_107676_);
    }

    public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
        return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
    }

    @Override
    public void render(VertexConsumer vertexConsumer,
                       Camera camera,
                       float tickDelta) {
        final var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        // Sodium
        if (writer != null) {
            val size = this.getQuadSize(tickDelta);

            Vector3f left = TEMP_LEFT;
            left.set(camera.getLeftVector())
                    .mul(size);

            Vector3f up = TEMP_UP;
            up.set(camera.getUpVector())
                    .mul(size);

            if (!Mth.equal(this.roll, 0.0f)) {
                val roll = Mth.lerp(tickDelta, this.oRoll, this.roll);

                val sinRoll = org.joml.Math.sin(roll);
                val cosRoll = org.joml.Math.cosFromSin(sinRoll, roll);

                final float rv1x = org.joml.Math.fma(cosRoll, left.x, sinRoll * up.x),
                        rv1y = org.joml.Math.fma(cosRoll, left.y, sinRoll * up.y),
                        rv1z = org.joml.Math.fma(cosRoll, left.z, sinRoll * up.z);

                final float rv2x = org.joml.Math.fma(-sinRoll, left.x, cosRoll * up.x),
                        rv2y = org.joml.Math.fma(-sinRoll, left.y, cosRoll * up.y),
                        rv2z = Math.fma(-sinRoll, left.z, cosRoll * up.z);

                left.set(rv1x, rv1y, rv1z);
                up.set(rv2x, rv2y, rv2z);
            }

            sodium$emitVertices(writer, camera.getPosition(), left, up, tickDelta);
            return;
        }

        val quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, camera, tickDelta);
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(tickDelta, this.oRoll, this.roll));
        }

        renderRotatedQuad(vertexConsumer, camera, quaternionf, tickDelta);
    }

    // Sodium
    private void sodium$emitVertices(VertexBufferWriter writer,
                                     Vec3 camera,
                                     Vector3f left,
                                     Vector3f up,
                                     float tickDelta) {
        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();

        int light = this.getLightColor(tickDelta);
        int color = ColorABGR.pack(this.rCol, this.gCol, this.bCol, this.alpha);

        float x = (float) (Mth.lerp(tickDelta, this.xo, this.x) - camera.x());
        float y = (float) (Mth.lerp(tickDelta, this.yo, this.y) - camera.y());
        float z = (float) (Mth.lerp(tickDelta, this.zo, this.z) - camera.z());

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(4 * ParticleVertex.STRIDE);
            long ptr = buffer;

            ParticleVertex.put(ptr, -left.x - up.x + x, -left.y - up.y + y, -left.z - up.z + z, maxU, maxV, color, light);
            ptr += ParticleVertex.STRIDE;

            ParticleVertex.put(ptr, -left.x + up.x + x, -left.y + up.y + y, -left.z + up.z + z, maxU, minV, color, light);
            ptr += ParticleVertex.STRIDE;

            ParticleVertex.put(ptr, left.x + up.x + x, left.y + up.y + y, left.z + up.z + z, minU, minV, color, light);
            ptr += ParticleVertex.STRIDE;

            ParticleVertex.put(ptr, left.x - up.x + x, left.y - up.y + y, left.z - up.z + z, minU, maxV, color, light);
            ptr += ParticleVertex.STRIDE;

            writer.push(stack, buffer, 4, ParticleVertex.FORMAT);
        }
    }

    protected void renderRotatedQuad(VertexConsumer vertexConsumer,
                                     Camera camera,
                                     Quaternionf quaternion,
                                     float tickDelta) {
        // Sodium
        final var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer != null) {
            val size = this.getQuadSize(tickDelta);

            // Some particle class implementations may call this function directly, in which case we cannot assume anything
            // about the transform being used. However, we can still extract the left/up vectors from the quaternion,
            // it's just slightly slower than using the camera's left/up vectors directly.
            val left = TEMP_LEFT;
            left.set(-size, 0.0f, 0.0f)
                    .rotate(quaternion);

            val up = TEMP_UP;
            up.set(0.0f, size, 0.0f)
                    .rotate(quaternion);

            sodium$emitVertices(writer, camera.getPosition(), left, up, tickDelta);
            return;
        }

        val vec3 = camera.getPosition();
        val f = (float)(Mth.lerp(tickDelta, this.xo, this.x) - vec3.x());
        val f1 = (float)(Mth.lerp(tickDelta, this.yo, this.y) - vec3.y());
        val f2 = (float)(Mth.lerp(tickDelta, this.zo, this.z) - vec3.z());

        renderRotatedQuad(vertexConsumer, quaternion, f, f1, f2, tickDelta);
    }

    protected void renderRotatedQuad(VertexConsumer p_345131_, Quaternionf p_343948_, float p_344896_, float p_343625_, float p_342312_, float p_342822_) {
        float f = this.getQuadSize(p_342822_);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(p_342822_);
        this.renderVertex(p_345131_, p_343948_, p_344896_, p_343625_, p_342312_, 1.0F, -1.0F, f, f2, f4, i);
        this.renderVertex(p_345131_, p_343948_, p_344896_, p_343625_, p_342312_, 1.0F, 1.0F, f, f2, f3, i);
        this.renderVertex(p_345131_, p_343948_, p_344896_, p_343625_, p_342312_, -1.0F, 1.0F, f, f1, f3, i);
        this.renderVertex(p_345131_, p_343948_, p_344896_, p_343625_, p_342312_, -1.0F, -1.0F, f, f1, f4, i);
    }

    private void renderVertex(
        VertexConsumer p_343555_,
        Quaternionf p_344882_,
        float p_343363_,
        float p_344803_,
        float p_345370_,
        float p_343670_,
        float p_345101_,
        float p_342842_,
        float p_342598_,
        float p_344326_,
        int p_345275_
    ) {
        Vector3f vector3f = new Vector3f(p_343670_, p_345101_, 0.0F).rotate(p_344882_).mul(p_342842_).add(p_343363_, p_344803_, p_345370_);
        p_343555_.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
            .setUv(p_342598_, p_344326_)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setLight(p_345275_);
    }

    public float getQuadSize(float p_107681_) {
        return this.quadSize;
    }

    @Override
    public Particle scale(float p_107683_) {
        this.quadSize *= p_107683_;
        return super.scale(p_107683_);
    }

    protected abstract float getU0();

    protected abstract float getU1();

    protected abstract float getV0();

    protected abstract float getV1();

    @OnlyIn(Dist.CLIENT)
    public interface FacingCameraMode {
        SingleQuadParticle.FacingCameraMode LOOKAT_XYZ = (p_312026_, p_311956_, p_310043_) -> p_312026_.set(p_311956_.rotation());
        SingleQuadParticle.FacingCameraMode LOOKAT_Y = (p_310770_, p_309904_, p_311153_) -> p_310770_.set(
                0.0F, p_309904_.rotation().y, 0.0F, p_309904_.rotation().w
            );

        void setRotation(Quaternionf p_309893_, Camera p_309691_, float p_312801_);
    }
}