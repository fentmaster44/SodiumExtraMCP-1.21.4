package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import java.util.List;
import javax.annotation.Nullable;

import lombok.val;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.caffeinemc.sodium.client.util.color.FastCubicSampler;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
    private static final int WATER_FOG_DISTANCE = 96;
    private static final List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(
        new FogRenderer.BlindnessFogFunction(), new FogRenderer.DarknessFogFunction()
    );
    public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
    private static int targetBiomeFog = -1;
    private static int previousBiomeFog = -1;
    private static long biomeChangedTime = -1L;
    private static boolean fogEnabled = true;

    // idfk what some of the arg names are - even in the sodium src theyre random letters
    public static Vector4f computeFogColor(Camera camera,
                                           float tickDelta,
                                           ClientLevel level,
                                           int nigg,
                                           float jew) {
        FogType fogtype = camera.getFluidInCamera();
        Entity entity = camera.getEntity();
        float f;
        float f1;
        float f2;
        if (fogtype == FogType.WATER) {
            long i = Util.getMillis();
            int k = level.getBiome(BlockPos.containing(camera.getPosition())).value().getWaterFogColor();
            if (biomeChangedTime < 0L) {
                targetBiomeFog = k;
                previousBiomeFog = k;
                biomeChangedTime = i;
            }

            int l = targetBiomeFog >> 16 & 0xFF;
            int i1 = targetBiomeFog >> 8 & 0xFF;
            int j1 = targetBiomeFog & 0xFF;
            int k1 = previousBiomeFog >> 16 & 0xFF;
            int l1 = previousBiomeFog >> 8 & 0xFF;
            int i2 = previousBiomeFog & 0xFF;
            float f3 = Mth.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
            float f4 = Mth.lerp(f3, (float)k1, (float)l);
            float f5 = Mth.lerp(f3, (float)l1, (float)i1);
            float f6 = Mth.lerp(f3, (float)i2, (float)j1);
            f = f4 / 255.0F;
            f1 = f5 / 255.0F;
            f2 = f6 / 255.0F;
            if (targetBiomeFog != k) {
                targetBiomeFog = k;
                previousBiomeFog = Mth.floor(f4) << 16 | Mth.floor(f5) << 8 | Mth.floor(f6);
                biomeChangedTime = i;
            }
        } else if (fogtype == FogType.LAVA) {
            f = 0.6F;
            f1 = 0.1F;
            f2 = 0.0F;
            biomeChangedTime = -1L;
        } else if (fogtype == FogType.POWDER_SNOW) {
            f = 0.623F;
            f1 = 0.734F;
            f2 = 0.785F;
            biomeChangedTime = -1L;
        } else {
            float f7 = 0.25F + 0.75F * (float)nigg / 32.0F;
            f7 = 1.0F - (float)Math.pow((double)f7, 0.25);
            int j = level.getSkyColor(camera.getPosition(), tickDelta);
            float f9 = ARGB.redFloat(j);
            float f11 = ARGB.greenFloat(j);
            float f13 = ARGB.blueFloat(j);
            float f14 = Mth.clamp(Mth.cos(level.getTimeOfDay(tickDelta) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeManager biomemanager = level.getBiomeManager();

            // Sodium
            val pos = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
//            Vec3 vec31 = CubicSampler.gaussianSampleVec3(
//                vec3,
//                (p_109033_, p_109034_, p_109035_) -> p_361507_.effects()
//                        .getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_109033_, p_109034_, p_109035_).value().getFogColor()), f14)
//            );
            val u = Mth.clamp(Mth.cos(level.getTimeOfDay(tickDelta) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
            val vec31 = FastCubicSampler.sampleColor(pos,
                    (x, y, z) -> level.getBiomeManager().getNoiseBiomeAtQuart(x, y, z).value().getFogColor(),
                    (v) -> level.effects().getBrightnessDependentFogColor(v, u));

            f = (float)vec31.x();
            f1 = (float)vec31.y();
            f2 = (float)vec31.z();
            if (nigg >= 4) {
                float f15 = Mth.sin(level.getSunAngle(tickDelta)) > 0.0F ? -1.0F : 1.0F;
                Vector3f vector3f = new Vector3f(f15, 0.0F, 0.0F);
                float f19 = camera.getLookVector().dot(vector3f);
                if (f19 < 0.0F) {
                    f19 = 0.0F;
                }

                if (f19 > 0.0F && level.effects().isSunriseOrSunset(level.getTimeOfDay(tickDelta))) {
                    int j2 = level.effects().getSunriseOrSunsetColor(level.getTimeOfDay(tickDelta));
                    f19 *= ARGB.alphaFloat(j2);
                    f = f * (1.0F - f19) + ARGB.redFloat(j2) * f19;
                    f1 = f1 * (1.0F - f19) + ARGB.greenFloat(j2) * f19;
                    f2 = f2 * (1.0F - f19) + ARGB.blueFloat(j2) * f19;
                }
            }

            f += (f9 - f) * f7;
            f1 += (f11 - f1) * f7;
            f2 += (f13 - f2) * f7;
            float f16 = level.getRainLevel(tickDelta);
            if (f16 > 0.0F) {
                float f17 = 1.0F - f16 * 0.5F;
                float f20 = 1.0F - f16 * 0.4F;
                f *= f17;
                f1 *= f17;
                f2 *= f20;
            }

            float f18 = level.getThunderLevel(tickDelta);
            if (f18 > 0.0F) {
                float f21 = 1.0F - f18 * 0.5F;
                f *= f21;
                f1 *= f21;
                f2 *= f21;
            }

            biomeChangedTime = -1L;
        }

        float f8 = ((float)camera.getPosition().y - (float)level.getMinY()) * level.getLevelData().getClearColorScale();
        FogRenderer.MobEffectFogFunction fogrenderer$mobeffectfogfunction = getPriorityFogFunction(entity, tickDelta);
        if (fogrenderer$mobeffectfogfunction != null) {
            LivingEntity livingentity = (LivingEntity)entity;
            f8 = fogrenderer$mobeffectfogfunction.getModifiedVoidDarkness(livingentity, livingentity.getEffect(fogrenderer$mobeffectfogfunction.getMobEffect()), f8, tickDelta);
        }

        if (f8 < 1.0F && fogtype != FogType.LAVA && fogtype != FogType.POWDER_SNOW) {
            if (f8 < 0.0F) {
                f8 = 0.0F;
            }

            f8 *= f8;
            f *= f8;
            f1 *= f8;
            f2 *= f8;
        }

        if (jew > 0.0F) {
            f = f * (1.0F - jew) + f * 0.7F * jew;
            f1 = f1 * (1.0F - jew) + f1 * 0.6F * jew;
            f2 = f2 * (1.0F - jew) + f2 * 0.6F * jew;
        }

        float f10;
        if (fogtype == FogType.WATER) {
            if (entity instanceof LocalPlayer) {
                f10 = ((LocalPlayer)entity).getWaterVision();
            } else {
                f10 = 1.0F;
            }
        } else {
            label86: {
                if (entity instanceof LivingEntity livingentity1
                    && livingentity1.hasEffect(MobEffects.NIGHT_VISION)
                    && !livingentity1.hasEffect(MobEffects.DARKNESS)) {
                    f10 = GameRenderer.getNightVisionScale(livingentity1, tickDelta);
                    break label86;
                }

                f10 = 0.0F;
            }
        }

        if (f != 0.0F && f1 != 0.0F && f2 != 0.0F) {
            float f12 = Math.min(1.0F / f, Math.min(1.0F / f1, 1.0F / f2));
            f = f * (1.0F - f10) + f * f12 * f10;
            f1 = f1 * (1.0F - f10) + f1 * f12 * f10;
            f2 = f2 * (1.0F - f10) + f2 * f12 * f10;
        }

        return new Vector4f(f, f1, f2, 1.0F);
    }

    public static boolean toggleFog() {
        return fogEnabled = !fogEnabled;
    }

    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity p_234166_, float p_234167_) {
        return p_234166_ instanceof LivingEntity livingentity
            ? MOB_EFFECT_FOG.stream().filter(p_234171_ -> p_234171_.isEnabled(livingentity, p_234167_)).findFirst().orElse(null)
            : null;
    }

    // SodiumExtra
    public static FogParameters setupFog(Camera camera,
                                         FogRenderer.FogMode fogMode,
                                         Vector4f vector4f,
                                         float v,
                                         boolean thickFog,
                                         float tickDelta) {
        if (!fogEnabled) {
            return FogParameters.NO_FOG;
        }

        val fogtype = camera.getFluidInCamera();
        val entity = camera.getEntity();
        val fogData = new FogRenderer.FogData(fogMode);
        val mobEffectFogFunction = getPriorityFogFunction(entity, tickDelta);

        if (fogtype == FogType.LAVA) {
            if (entity.isSpectator()) {
                fogData.start = -8.0F;
                fogData.end = v * 0.5F;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                fogData.start = 0.0F;
                fogData.end = 5.0F;
            } else {
                fogData.start = 0.25F;
                fogData.end = 1.0F;
            }
        } else if (fogtype == FogType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                fogData.start = -8.0F;
                fogData.end = v * 0.5F;
            } else {
                fogData.start = 0.0F;
                fogData.end = 2.0F;
            }
        } else if (mobEffectFogFunction != null) {
            LivingEntity livingentity = (LivingEntity)entity;
            MobEffectInstance mobeffectinstance = livingentity.getEffect(mobEffectFogFunction.getMobEffect());
            if (mobeffectinstance != null) {
                mobEffectFogFunction.setupFog(fogData, livingentity, mobeffectinstance, v, tickDelta);
            }
        } else if (fogtype == FogType.WATER) {
            fogData.start = -8.0F;
            fogData.end = 96.0F;
            if (entity instanceof LocalPlayer localplayer) {
                fogData.end = fogData.end * Math.max(0.25F, localplayer.getWaterVision());
                Holder<Biome> holder = localplayer.level().getBiome(localplayer.blockPosition());
                if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    fogData.end *= 0.85F;
                }
            }

            if (fogData.end > v) {
                fogData.end = v;
                fogData.shape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            fogData.start = v * 0.05F;
            fogData.end = Math.min(v, 192.0F) * 0.5F;
        } else if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            fogData.start = 0.0F;
            fogData.end = v;
            fogData.shape = FogShape.CYLINDER;
        } else if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
            float f = Mth.clamp(v / 10.0F, 4.0F, 64.0F);
            fogData.start = v - f;
            fogData.end = v;
            fogData.shape = FogShape.CYLINDER;
        }

        val fogParams = new FogParameters(
                fogData.start * ((float) SodiumExtraClientMod.options().renderSettings.fogStart / 100),
                fogData.end,
                fogData.shape,
                vector4f.x,
                vector4f.y,
                vector4f.z,
                vector4f.w
        );

        SodiumExtraClientMod.options().renderSettings.dimensionFogDistanceMap.putIfAbsent(entity.level().dimensionType().effectsLocation(), 0);

        val fogDistance = SodiumExtraClientMod.options().renderSettings.multiDimensionFogControl
                ?
                SodiumExtraClientMod.options().renderSettings.dimensionFogDistanceMap.get(entity.level().dimensionType().effectsLocation())
                :
                SodiumExtraClientMod.options().renderSettings.fogDistance;

        if (fogDistance == 0 || mobEffectFogFunction != null) {
            return fogParams;
        }

        if (camera.getFluidInCamera() == FogType.NONE && (thickFog || fogMode == FogRenderer.FogMode.FOG_TERRAIN)) {
            val fogStart = (float) SodiumExtraClientMod.options().renderSettings.fogStart / 100;
            if (fogDistance == 33) {
                return FogParameters.NO_FOG;
            } else {
                return new FogParameters(
                        fogDistance * 16 * fogStart,
                        (fogDistance + 1) * 16,
                        fogParams.shape(),
                        fogParams.red(),
                        fogParams.green(),
                        fogParams.blue(),
                        fogParams.alpha());
            }
        }

        return fogParams;
    }

    @OnlyIn(Dist.CLIENT)
    static class BlindnessFogFunction implements FogRenderer.MobEffectFogFunction {
        @Override
        public Holder<MobEffect> getMobEffect() {
            return MobEffects.BLINDNESS;
        }

        @Override
        public void setupFog(FogRenderer.FogData p_234181_, LivingEntity p_234182_, MobEffectInstance p_234183_, float p_234184_, float p_234185_) {
            float f = p_234183_.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)p_234183_.getDuration() / 20.0F), p_234184_, 5.0F);
            if (p_234181_.mode == FogRenderer.FogMode.FOG_SKY) {
                p_234181_.start = 0.0F;
                p_234181_.end = f * 0.8F;
            } else if (p_234181_.mode == FogRenderer.FogMode.FOG_TERRAIN) {
                p_234181_.start = f * 0.25F;
                p_234181_.end = f;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class DarknessFogFunction implements FogRenderer.MobEffectFogFunction {
        @Override
        public Holder<MobEffect> getMobEffect() {
            return MobEffects.DARKNESS;
        }

        @Override
        public void setupFog(FogRenderer.FogData p_234194_, LivingEntity p_234195_, MobEffectInstance p_234196_, float p_234197_, float p_234198_) {
            float f = Mth.lerp(p_234196_.getBlendFactor(p_234195_, p_234198_), p_234197_, 15.0F);

            p_234194_.start = switch (p_234194_.mode) {
                case FOG_SKY -> 0.0F;
                case FOG_TERRAIN -> f * 0.75F;
            };
            p_234194_.end = f;
        }

        @Override
        public float getModifiedVoidDarkness(LivingEntity p_234189_, MobEffectInstance p_234190_, float p_234191_, float p_234192_) {
            return 1.0F - p_234190_.getBlendFactor(p_234189_, p_234192_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FogData {
        public final FogRenderer.FogMode mode;
        public float start;
        public float end;
        public FogShape shape = FogShape.SPHERE;

        public FogData(FogRenderer.FogMode p_234204_) {
            this.mode = p_234204_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum FogMode {
        FOG_SKY,
        FOG_TERRAIN;
    }

    @OnlyIn(Dist.CLIENT)
    interface MobEffectFogFunction {
        Holder<MobEffect> getMobEffect();

        void setupFog(FogRenderer.FogData p_234212_, LivingEntity p_234213_, MobEffectInstance p_234214_, float p_234215_, float p_234216_);

        default boolean isEnabled(LivingEntity p_234206_, float p_234207_) {
            return p_234206_.hasEffect(this.getMobEffect());
        }

        default float getModifiedVoidDarkness(LivingEntity p_234208_, MobEffectInstance p_234209_, float p_234210_, float p_234211_) {
            MobEffectInstance mobeffectinstance = p_234208_.getEffect(this.getMobEffect());
            if (mobeffectinstance != null) {
                if (mobeffectinstance.endsWithin(19)) {
                    p_234210_ = 1.0F - (float)mobeffectinstance.getDuration() / 20.0F;
                } else {
                    p_234210_ = 0.0F;
                }
            }

            return p_234210_;
        }
    }
}