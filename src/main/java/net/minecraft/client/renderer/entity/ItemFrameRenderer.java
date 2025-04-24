package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemFrameRenderer<T extends ItemFrame> extends EntityRenderer<T, ItemFrameRenderState> {
    public static final int GLOW_FRAME_BRIGHTNESS = 5;
    public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
    private final ItemModelResolver itemModelResolver;
    private final MapRenderer mapRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ItemFrameRenderer(EntityRendererProvider.Context p_174204_) {
        super(p_174204_);
        this.itemModelResolver = p_174204_.getItemModelResolver();
        this.mapRenderer = p_174204_.getMapRenderer();
        this.blockRenderer = p_174204_.getBlockRenderDispatcher();
    }

    protected int getBlockLightLevel(T p_174216_, BlockPos p_174217_) {
        return p_174216_.getType() == EntityType.GLOW_ITEM_FRAME ? Math.max(5, super.getBlockLightLevel(p_174216_, p_174217_)) : super.getBlockLightLevel(p_174216_, p_174217_);
    }

    public void render(ItemFrameRenderState itemFrameRenderState,
                       PoseStack poseStack,
                       MultiBufferSource multiBufferSource,
                       int i1) {
        super.render(itemFrameRenderState, poseStack, multiBufferSource, i1);

        // SodiumExtra
        if (!SodiumExtraClientMod.options().renderSettings.itemFrame) {
            return;
        }
        
        poseStack.pushPose();
        Direction direction = itemFrameRenderState.direction;
        Vec3 vec3 = this.getRenderOffset(itemFrameRenderState);
        poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
        double d0 = 0.46875;
        poseStack.translate((double)direction.getStepX() * 0.46875, (double)direction.getStepY() * 0.46875, (double)direction.getStepZ() * 0.46875);
        float f;
        float f1;
        if (direction.getAxis().isHorizontal()) {
            f = 0.0F;
            f1 = 180.0F - direction.toYRot();
        } else {
            f = (float)(-90 * direction.getAxisDirection().getStep());
            f1 = 180.0F;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(f));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        if (!itemFrameRenderState.isInvisible) {
            ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
            ModelResourceLocation modelresourcelocation = getFrameModelResourceLocation(itemFrameRenderState);
            poseStack.pushPose();
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            this.blockRenderer
                .getModelRenderer()
                .renderModel(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS)),
                    null,
                    modelmanager.getModel(modelresourcelocation),
                    1.0F,
                    1.0F,
                    1.0F,
                    i1,
                    OverlayTexture.NO_OVERLAY
                );
            poseStack.popPose();
        }

        if (itemFrameRenderState.isInvisible) {
            poseStack.translate(0.0F, 0.0F, 0.5F);
        } else {
            poseStack.translate(0.0F, 0.0F, 0.4375F);
        }

        if (itemFrameRenderState.mapId != null) {
            int j = itemFrameRenderState.rotation % 4 * 2;
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)j * 360.0F / 8.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float f2 = 0.0078125F;
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-64.0F, -64.0F, 0.0F);
            poseStack.translate(0.0F, 0.0F, -1.0F);
            int i = this.getLightCoords(itemFrameRenderState.isGlowFrame, 15728850, i1);
            this.mapRenderer.render(itemFrameRenderState.mapRenderState, poseStack, multiBufferSource, true, i);
        } else if (!itemFrameRenderState.item.isEmpty()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)itemFrameRenderState.rotation * 360.0F / 8.0F));
            int k = this.getLightCoords(itemFrameRenderState.isGlowFrame, 15728880, i1);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            itemFrameRenderState.item.render(poseStack, multiBufferSource, k, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    private int getLightCoords(boolean p_368253_, int p_174210_, int p_174211_) {
        return p_368253_ ? p_174210_ : p_174211_;
    }

    private static ModelResourceLocation getFrameModelResourceLocation(ItemFrameRenderState p_375535_) {
        if (p_375535_.mapId != null) {
            return p_375535_.isGlowFrame ? BlockStateModelLoader.GLOW_MAP_FRAME_LOCATION : BlockStateModelLoader.MAP_FRAME_LOCATION;
        } else {
            return p_375535_.isGlowFrame ? BlockStateModelLoader.GLOW_FRAME_LOCATION : BlockStateModelLoader.FRAME_LOCATION;
        }
    }

    public Vec3 getRenderOffset(ItemFrameRenderState p_368370_) {
        return new Vec3((double)((float)p_368370_.direction.getStepX() * 0.3F), -0.25, (double)((float)p_368370_.direction.getStepZ() * 0.3F));
    }

    protected boolean shouldShowName(T p_115091_, double p_366137_) {
        return Minecraft.renderNames() && this.entityRenderDispatcher.crosshairPickEntity == p_115091_ && p_115091_.getItem().getCustomName() != null;
    }

    protected Component getNameTag(T p_364863_) {
        return p_364863_.getItem().getHoverName();
    }

    public ItemFrameRenderState createRenderState() {
        return new ItemFrameRenderState();
    }

    public void extractRenderState(T p_369136_, ItemFrameRenderState p_364469_, float p_366511_) {
        super.extractRenderState(p_369136_, p_364469_, p_366511_);
        p_364469_.direction = p_369136_.getDirection();
        ItemStack itemstack = p_369136_.getItem();
        this.itemModelResolver.updateForNonLiving(p_364469_.item, itemstack, ItemDisplayContext.FIXED, p_369136_);
        p_364469_.rotation = p_369136_.getRotation();
        p_364469_.isGlowFrame = p_369136_.getType() == EntityType.GLOW_ITEM_FRAME;
        p_364469_.mapId = null;
        if (!itemstack.isEmpty()) {
            MapId mapid = p_369136_.getFramedMapId(itemstack);
            if (mapid != null) {
                MapItemSavedData mapitemsaveddata = p_369136_.level().getMapData(mapid);
                if (mapitemsaveddata != null) {
                    this.mapRenderer.extractRenderState(mapid, mapitemsaveddata, p_364469_.mapRenderState);
                    p_364469_.mapId = mapid;
                }
            }
        }
    }
}