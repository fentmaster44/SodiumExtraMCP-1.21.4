package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.SnifferAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SnifferRenderState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnifferModel extends EntityModel<SnifferRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
    private static final float WALK_ANIMATION_SPEED_MAX = 9.0F;
    private static final float WALK_ANIMATION_SCALE_FACTOR = 100.0F;
    private final ModelPart head;

    public SnifferModel(ModelPart p_272867_) {
        super(p_272867_);
        this.head = p_272867_.getChild("bone").getChild("body").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .texOffs(62, 68)
                .addBox(-12.5F, -14.0F, -20.0F, 25.0F, 29.0F, 40.0F, new CubeDeformation(0.0F))
                .texOffs(62, 0)
                .addBox(-12.5F, -14.0F, -20.0F, 25.0F, 24.0F, 40.0F, new CubeDeformation(0.5F))
                .texOffs(87, 68)
                .addBox(-12.5F, 12.0F, -20.0F, 25.0F, 0.0F, 40.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        partdefinition1.addOrReplaceChild(
            "right_front_leg",
            CubeListBuilder.create().texOffs(32, 87).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-7.5F, 10.0F, -15.0F)
        );
        partdefinition1.addOrReplaceChild(
            "right_mid_leg",
            CubeListBuilder.create().texOffs(32, 105).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-7.5F, 10.0F, 0.0F)
        );
        partdefinition1.addOrReplaceChild(
            "right_hind_leg",
            CubeListBuilder.create().texOffs(32, 123).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-7.5F, 10.0F, 15.0F)
        );
        partdefinition1.addOrReplaceChild(
            "left_front_leg",
            CubeListBuilder.create().texOffs(0, 87).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(7.5F, 10.0F, -15.0F)
        );
        partdefinition1.addOrReplaceChild(
            "left_mid_leg",
            CubeListBuilder.create().texOffs(0, 105).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(7.5F, 10.0F, 0.0F)
        );
        partdefinition1.addOrReplaceChild(
            "left_hind_leg",
            CubeListBuilder.create().texOffs(0, 123).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(7.5F, 10.0F, 15.0F)
        );
        PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(8, 15)
                .addBox(-6.5F, -7.5F, -11.5F, 13.0F, 18.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(8, 4)
                .addBox(-6.5F, 7.5F, -11.5F, 13.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 6.5F, -19.48F)
        );
        partdefinition3.addOrReplaceChild(
            "left_ear",
            CubeListBuilder.create().texOffs(2, 0).addBox(0.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(6.51F, -7.5F, -4.51F)
        );
        partdefinition3.addOrReplaceChild(
            "right_ear",
            CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-6.51F, -7.5F, -4.51F)
        );
        partdefinition3.addOrReplaceChild(
            "nose",
            CubeListBuilder.create().texOffs(10, 45).addBox(-6.5F, -2.0F, -9.0F, 13.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -4.5F, -11.5F)
        );
        partdefinition3.addOrReplaceChild(
            "lower_beak",
            CubeListBuilder.create().texOffs(10, 57).addBox(-6.5F, -7.0F, -8.0F, 13.0F, 12.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 2.5F, -12.5F)
        );
        return LayerDefinition.create(meshdefinition, 192, 192);
    }

    public void setupAnim(SnifferRenderState p_367347_) {
        super.setupAnim(p_367347_);
        this.head.xRot = p_367347_.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = p_367347_.yRot * (float) (Math.PI / 180.0);
        if (p_367347_.isSearching) {
            this.animateWalk(SnifferAnimation.SNIFFER_SNIFF_SEARCH, p_367347_.walkAnimationPos, p_367347_.walkAnimationSpeed, 9.0F, 100.0F);
        } else {
            this.animateWalk(SnifferAnimation.SNIFFER_WALK, p_367347_.walkAnimationPos, p_367347_.walkAnimationSpeed, 9.0F, 100.0F);
        }

        this.animate(p_367347_.diggingAnimationState, SnifferAnimation.SNIFFER_DIG, p_367347_.ageInTicks);
        this.animate(p_367347_.sniffingAnimationState, SnifferAnimation.SNIFFER_LONGSNIFF, p_367347_.ageInTicks);
        this.animate(p_367347_.risingAnimationState, SnifferAnimation.SNIFFER_STAND_UP, p_367347_.ageInTicks);
        this.animate(p_367347_.feelingHappyAnimationState, SnifferAnimation.SNIFFER_HAPPY, p_367347_.ageInTicks);
        this.animate(p_367347_.scentingAnimationState, SnifferAnimation.SNIFFER_SNIFFSNIFF, p_367347_.ageInTicks);
        if (p_367347_.isBaby) {
            this.applyStatic(SnifferAnimation.BABY_TRANSFORM);
        }
    }
}