package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PistonHeadRenderer implements BlockEntityRenderer<PistonMovingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public PistonHeadRenderer(BlockEntityRendererProvider.Context p_173623_) {
        this.blockRenderer = p_173623_.getBlockRenderDispatcher();
    }

    public void render(PistonMovingBlockEntity piston,
                       float v,
                       PoseStack poseStack,
                       MultiBufferSource multiBufferSource,
                       int i,
                       int j) {
        // SodiumExtra
        if (!SodiumExtraClientMod.options().renderSettings.piston) {
            return;
        }

        Level level = piston.getLevel();
        if (level != null) {
            BlockPos blockpos = piston.getBlockPos().relative(piston.getMovementDirection().getOpposite());
            BlockState blockstate = piston.getMovedState();
            if (!blockstate.isAir()) {
                ModelBlockRenderer.enableCaching();
                poseStack.pushPose();
                poseStack.translate(piston.getXOff(v), piston.getYOff(v), piston.getZOff(v));
                if (blockstate.is(Blocks.PISTON_HEAD) && piston.getProgress(v) <= 4.0F) {
                    blockstate = blockstate.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(piston.getProgress(v) <= 0.5F));
                    this.renderBlock(blockpos, blockstate, poseStack, multiBufferSource, level, false, j);
                } else if (piston.isSourcePiston() && !piston.isExtending()) {
                    PistonType pistontype = blockstate.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
                    BlockState blockstate1 = Blocks.PISTON_HEAD
                        .defaultBlockState()
                        .setValue(PistonHeadBlock.TYPE, pistontype)
                        .setValue(PistonHeadBlock.FACING, blockstate.getValue(PistonBaseBlock.FACING));
                    blockstate1 = blockstate1.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(piston.getProgress(v) >= 0.5F));
                    this.renderBlock(blockpos, blockstate1, poseStack, multiBufferSource, level, false, j);
                    BlockPos blockpos1 = blockpos.relative(piston.getMovementDirection());
                    poseStack.popPose();
                    poseStack.pushPose();
                    blockstate = blockstate.setValue(PistonBaseBlock.EXTENDED, Boolean.valueOf(true));
                    this.renderBlock(blockpos1, blockstate, poseStack, multiBufferSource, level, true, j);
                } else {
                    this.renderBlock(blockpos, blockstate, poseStack, multiBufferSource, level, false, j);
                }

                poseStack.popPose();
                ModelBlockRenderer.clearCache();
            }
        }
    }

    private void renderBlock(
        BlockPos p_112459_, BlockState p_112460_, PoseStack p_112461_, MultiBufferSource p_112462_, Level p_112463_, boolean p_112464_, int p_112465_
    ) {
        RenderType rendertype = ItemBlockRenderTypes.getMovingBlockRenderType(p_112460_);
        VertexConsumer vertexconsumer = p_112462_.getBuffer(rendertype);
        this.blockRenderer
            .getModelRenderer()
            .tesselateBlock(
                p_112463_,
                this.blockRenderer.getBlockModel(p_112460_),
                p_112460_,
                p_112459_,
                p_112461_,
                vertexconsumer,
                p_112464_,
                RandomSource.create(),
                p_112460_.getSeed(p_112459_),
                p_112465_
            );
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}