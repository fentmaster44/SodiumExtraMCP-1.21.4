package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;

import lombok.val;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.compat.IrisCompat;
import net.caffeinemc.sodium.api.math.MatrixHelper;
import net.caffeinemc.sodium.api.util.ColorARGB;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.sodium.api.vertex.format.common.EntityVertex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class BeaconRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public BeaconRenderer(BlockEntityRendererProvider.Context p_173529_) {
    }

    public void render(BeaconBlockEntity beacon,
                       float v,
                       PoseStack poseStack,
                       MultiBufferSource multiBufferSource,
                       int i1,
                       int i2) {

        // SodiumExtra
        if (!SodiumExtraClientMod.options().renderSettings.beaconBeam) {
            return;
        }

        // SodiumExtra
        val frustum = Minecraft.getInstance().levelRenderer.getCullingFrustum();
        val box = new AABB(
                beacon.getBlockPos().getX() - 1.0,
                beacon.getBlockPos().getY() - 1.0,
                beacon.getBlockPos().getZ() - 1.0,
                beacon.getBlockPos().getX() + 1.0,
                beacon.getBlockPos().getY() + (beacon.getBeamSections().isEmpty() ? 1.0 : 2048.0), // todo: probably want to limit this to max height vanilla overshoots as well
                beacon.getBlockPos().getZ() + 1.0);

        if (!frustum.isVisible(box)) {
            return;
        }
        // SodiumExtra end

        long worldTime = beacon.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = beacon.getBeamSections();

        // SodiumExtra
        var yOffset = 0;

        for (var k = 0; k < list.size(); k++) {
            val beaconblockentity$beaconbeamsection = list.get(k);
//            renderBeaconBeam(
//                poseStack,
//                multiBufferSource,
//                v,
//                i,
//                j,
//                k == list.size() - 1 ? 1024 : beaconblockentity$beaconbeamsection.getHeight(),
//                beaconblockentity$beaconbeamsection.getColor()
//            );
            var maxY = k == list.size() - 1 ? 1024 : beaconblockentity$beaconbeamsection.getHeight();

            if (maxY == 2048 && SodiumExtraClientMod.options().renderSettings.limitBeaconBeamHeight) {
                val lastSegment = beacon.getBlockPos().getY() + yOffset;
                maxY = Objects.requireNonNull(beacon.getLevel()).getMaxY() - lastSegment;
            }

            renderBeaconBeam(poseStack,
                    multiBufferSource,
                    v,
                    worldTime,
                    yOffset,
                    maxY,
                    beaconblockentity$beaconbeamsection.getColor());

            yOffset += beaconblockentity$beaconbeamsection.getHeight();
        }
    }

    private static void renderBeaconBeam(PoseStack stack,
                                         MultiBufferSource multiBufferSource,
                                         float p_112179_,
                                         long p_112180_,
                                         int p_112181_,
                                         int p_112182_,
                                         int p_344592_) {
        renderBeaconBeam(stack,
                multiBufferSource,
                BEAM_LOCATION,
                p_112179_,
                1.0F,
                p_112180_,
                p_112181_,
                p_112182_,
                p_344592_,
                0.2F,
                0.25F);
    }

    // SodiumExtra - optimize rendering beacon beam
    public static void renderBeaconBeam(PoseStack poseStack,
                                        MultiBufferSource multiBufferSource,
                                        ResourceLocation resourceLocation,
                                        float tickDelta,
                                        float heightScale,
                                        long worldTime,
                                        int yOffset,
                                        int maxY,
                                        int color,
                                        float innerRadius,
                                        float outerRadius) {
        if (IrisCompat.isRenderingShadowPass()) {
            return;
        }

        int height = yOffset + maxY;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float time = (float) Math.floorMod(worldTime, 40) + tickDelta;
        float negativeTime = maxY < 0 ? time : -time;
        float fractionalPart = Mth.frac(negativeTime * 0.2F - (float) Mth.floor(negativeTime * 0.1F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.25F - 45.0F));
        float innerX1;
        float innerZ2;
        float innerX3 = -innerRadius;
        float innerZ4 = -innerRadius;
        float innerV2 = -1.0F + fractionalPart;
        float innerV1 = (float) maxY * heightScale * (0.5F / innerRadius) + innerV2;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(2 * 16 * EntityVertex.STRIDE);
            long ptr = buffer;
            // Note: ModelVertex color takes in ABGR
            ptr = writeBeamLayerVertices(ptr, poseStack, ColorARGB.toABGR(color), yOffset, height, 0.0F, innerRadius, innerRadius, 0.0F, innerX3, 0.0F, 0.0F, innerZ4, innerV1, innerV2);
            VertexBufferWriter.of(multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false))).push(stack, buffer, 16, EntityVertex.FORMAT);

            poseStack.popPose();
            innerX1 = -outerRadius;
            float outerZ1 = -outerRadius;
            innerZ2 = -outerRadius;
            innerX3 = -outerRadius;
            innerV2 = -1.0F + fractionalPart;
            innerV1 = (float) maxY * heightScale + innerV2;

            buffer = ptr;
            ptr = writeBeamLayerVertices(ptr, poseStack, ColorARGB.toABGR(color, 32), yOffset, height, innerX1, outerZ1, outerRadius, innerZ2, innerX3, outerRadius, outerRadius, outerRadius, innerV1, innerV2);
            VertexBufferWriter.of(multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true))).push(stack, buffer, 16, EntityVertex.FORMAT);
        }
        poseStack.popPose();
    }

    // SodiumExtra
    private static long writeBeamLayerVertices(long ptr,
                                               PoseStack poseStack,
                                               int color,
                                               int yOffset,
                                               int height,
                                               float x1,
                                               float z1,
                                               float x2,
                                               float z2,
                                               float x3,
                                               float z3,
                                               float x4,
                                               float z4,
                                               float v1,
                                               float v2) {
        val pose = poseStack.last();
        val positionMatrix = pose.pose();
        val normalMatrix = pose.normal();

        var normal = MatrixHelper.transformNormal(normalMatrix, false, (float) 0.0, (float) 1.0, (float) 0.0);

        ptr = transformAndWriteVertex(ptr, positionMatrix, x1, height, z1, color, 1.0f, v1, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x1, yOffset, z1, color, 1.0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x2, yOffset, z2, color, 0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x2, height, z2, color, 0f, v1, normal);

        ptr = transformAndWriteVertex(ptr, positionMatrix, x4, height, z4, color, 1.0f, v1, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x4, yOffset, z4, color, 1.0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x3, yOffset, z3, color, 0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x3, height, z3, color, 0f, v1, normal);

        ptr = transformAndWriteVertex(ptr, positionMatrix, x2, height, z2, color, 1.0f, v1, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x2, yOffset, z2, color, 1.0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x4, yOffset, z4, color, 0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x4, height, z4, color, 0f, v1, normal);

        ptr = transformAndWriteVertex(ptr, positionMatrix, x3, height, z3, color, 1.0f, v1, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x3, yOffset, z3, color, 1.0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x1, yOffset, z1, color, 0f, v2, normal);
        ptr = transformAndWriteVertex(ptr, positionMatrix, x1, height, z1, color, 0f, v1, normal);

        return ptr;
    }

    // SodiumExtra
    private static long transformAndWriteVertex(long ptr, Matrix4f positionMatrix, float x, float y, float z, int color, float u, float v, int normal) {
        val transformedX = MatrixHelper.transformPositionX(positionMatrix, x, y, z);
        val transformedY = MatrixHelper.transformPositionY(positionMatrix, x, y, z);
        val transformedZ = MatrixHelper.transformPositionZ(positionMatrix, x, y, z);

        EntityVertex.write(ptr, transformedX, transformedY, transformedZ, color, u, v, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, normal);
        ptr += EntityVertex.STRIDE;
        return ptr;
    }

//    public static void renderBeaconBeam(PoseStack poseStack,
//                                        MultiBufferSource multiBufferSource,
//                                        ResourceLocation resourceLocation,
//                                        float tickDelta,
//                                        float heightScale,
//                                        long worldTime,
//                                        int yOffset,
//                                        int maxY,
//                                        int color,
//                                        float innerRadius,
//                                        float outerRadius) {
//        int i = yOffset + maxY;
//        poseStack.pushPose();
//        poseStack.translate(0.5, 0.0, 0.5);
//        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
//        float f1 = maxY < 0 ? f : -f;
//        float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
//        poseStack.pushPose();
//        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
//        float f3;
//        float f5;
//        float f6 = -innerRadius;
//        float f9 = -innerRadius;
//        float f12 = -1.0F + f2;
//        float f13 = (float)maxY * heightScale * (0.5F / innerRadius) + f12;
//
//        renderPart(
//            poseStack,
//            multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false)),
//            color,
//            yOffset,
//            i,
//            0.0F,
//            innerRadius,
//            innerRadius,
//            0.0F,
//            f6,
//            0.0F,
//            0.0F,
//            f9,
//            0.0F,
//            1.0F,
//            f13,
//            f12
//        );
//
//        poseStack.popPose();
//        f3 = -outerRadius;
//        float f4 = -outerRadius;
//        f5 = -outerRadius;
//        f6 = -outerRadius;
//        f12 = -1.0F + f2;
//        f13 = (float)maxY * heightScale + f12;
//
//        renderPart(
//            poseStack,
//            multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true)),
//            ARGB.color(32, color),
//            yOffset,
//            i,
//            f3,
//            f4,
//            outerRadius,
//            f5,
//            f6,
//            outerRadius,
//            outerRadius,
//            outerRadius,
//            0.0F,
//            1.0F,
//            f13,
//            f12
//        );
//
//        poseStack.popPose();
//    }

    private static void renderPart(
        PoseStack p_112156_,
        VertexConsumer p_112157_,
        int p_112162_,
        int p_112163_,
        int p_345221_,
        float p_112158_,
        float p_112159_,
        float p_112160_,
        float p_112161_,
        float p_112164_,
        float p_112165_,
        float p_112166_,
        float p_112167_,
        float p_112168_,
        float p_112169_,
        float p_112170_,
        float p_112171_
    ) {
        PoseStack.Pose posestack$pose = p_112156_.last();
        renderQuad(
            posestack$pose, p_112157_, p_112162_, p_112163_, p_345221_, p_112158_, p_112159_, p_112160_, p_112161_, p_112168_, p_112169_, p_112170_, p_112171_
        );
        renderQuad(
            posestack$pose, p_112157_, p_112162_, p_112163_, p_345221_, p_112166_, p_112167_, p_112164_, p_112165_, p_112168_, p_112169_, p_112170_, p_112171_
        );
        renderQuad(
            posestack$pose, p_112157_, p_112162_, p_112163_, p_345221_, p_112160_, p_112161_, p_112166_, p_112167_, p_112168_, p_112169_, p_112170_, p_112171_
        );
        renderQuad(
            posestack$pose, p_112157_, p_112162_, p_112163_, p_345221_, p_112164_, p_112165_, p_112158_, p_112159_, p_112168_, p_112169_, p_112170_, p_112171_
        );
    }

    private static void renderQuad(
        PoseStack.Pose p_332343_,
        VertexConsumer p_112122_,
        int p_112127_,
        int p_112128_,
        int p_345385_,
        float p_112123_,
        float p_112124_,
        float p_112125_,
        float p_112126_,
        float p_112129_,
        float p_112130_,
        float p_112131_,
        float p_112132_
    ) {
        addVertex(p_332343_, p_112122_, p_112127_, p_345385_, p_112123_, p_112124_, p_112130_, p_112131_);
        addVertex(p_332343_, p_112122_, p_112127_, p_112128_, p_112123_, p_112124_, p_112130_, p_112132_);
        addVertex(p_332343_, p_112122_, p_112127_, p_112128_, p_112125_, p_112126_, p_112129_, p_112132_);
        addVertex(p_332343_, p_112122_, p_112127_, p_345385_, p_112125_, p_112126_, p_112129_, p_112131_);
    }

    private static void addVertex(
        PoseStack.Pose p_334631_, VertexConsumer p_253894_, int p_254357_, int p_343267_, float p_253871_, float p_253841_, float p_254568_, float p_254361_
    ) {
        p_253894_.addVertex(p_334631_, p_253871_, (float)p_343267_, p_253841_)
            .setColor(p_254357_)
            .setUv(p_254568_, p_254361_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(15728880)
            .setNormal(p_334631_, 0.0F, 1.0F, 0.0F);
    }

    public boolean shouldRenderOffScreen(BeaconBlockEntity p_112138_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRender(BeaconBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(p_173532_.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
    }
}