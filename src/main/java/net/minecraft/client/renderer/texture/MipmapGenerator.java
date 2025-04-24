package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.client.util.color.ColorSRGB;
import net.minecraft.Util;
import net.minecraft.util.ARGB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MipmapGenerator {
    private static final int ALPHA_CUTOUT_CUTOFF = 96;
    private static final float[] POW22 = Util.make(new float[256], p_118058_ -> {
        for (int i = 0; i < p_118058_.length; i++) {
            p_118058_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
        }
    });

    private MipmapGenerator() {
    }

    public static NativeImage[] generateMipLevels(NativeImage[] p_251300_, int p_252326_) {
        if (p_252326_ + 1 <= p_251300_.length) {
            return p_251300_;
        } else {
            NativeImage[] anativeimage = new NativeImage[p_252326_ + 1];
            anativeimage[0] = p_251300_[0];
            boolean flag = hasTransparentPixel(anativeimage[0]);

            for (int i = 1; i <= p_252326_; i++) {
                if (i < p_251300_.length) {
                    anativeimage[i] = p_251300_[i];
                } else {
                    NativeImage nativeimage = anativeimage[i - 1];
                    NativeImage nativeimage1 = new NativeImage(nativeimage.getWidth() >> 1, nativeimage.getHeight() >> 1, false);
                    int j = nativeimage1.getWidth();
                    int k = nativeimage1.getHeight();

                    for (int l = 0; l < j; l++) {
                        for (int i1 = 0; i1 < k; i1++) {
                            nativeimage1.setPixel(
                                l,
                                i1,
                                alphaBlend(
                                    nativeimage.getPixel(l * 2 + 0, i1 * 2 + 0),
                                    nativeimage.getPixel(l * 2 + 1, i1 * 2 + 0),
                                    nativeimage.getPixel(l * 2 + 0, i1 * 2 + 1),
                                    nativeimage.getPixel(l * 2 + 1, i1 * 2 + 1),
                                    flag
                                )
                            );
                        }
                    }

                    anativeimage[i] = nativeimage1;
                }
            }

            return anativeimage;
        }
    }

    private static boolean hasTransparentPixel(NativeImage p_252279_) {
        for (int i = 0; i < p_252279_.getWidth(); i++) {
            for (int j = 0; j < p_252279_.getHeight(); j++) {
                if (ARGB.alpha(p_252279_.getPixel(i, j)) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    // Sodium
    private static int alphaBlend(int one, int two, int three, int four, boolean checkAlpha) {
        // First blend horizontally, then blend vertically.
        //
        // This works well for the case where our change is the most impactful (grass side overlays)
        return weightedAverageColor(weightedAverageColor(one, two), weightedAverageColor(three, four));
    }

    // Sodium
    private static int weightedAverageColor(int one, int two) {
        int alphaOne = ColorABGR.unpackAlpha(one);
        int alphaTwo = ColorABGR.unpackAlpha(two);

        // In the case where the alpha values of the same, we can get by with an unweighted average.
        if (alphaOne == alphaTwo) {
            return averageRgb(one, two, alphaOne);
        }

        // If one of our pixels is fully transparent, ignore it.
        // We just take the value of the other pixel as-is. To compensate for not changing the color value, we
        // divide the alpha value by 4 instead of 2.
        if (alphaOne == 0) {
            return (two & 0x00FFFFFF) | ((alphaTwo >> 2) << 24);
        }

        if (alphaTwo == 0) {
            return (one & 0x00FFFFFF) | ((alphaOne >> 2) << 24);
        }

        // Use the alpha values to compute relative weights of each color.
        float scale = 1.0f / (alphaOne + alphaTwo);

        float relativeWeightOne = alphaOne * scale;
        float relativeWeightTwo = alphaTwo * scale;

        // Convert the color components into linear space, then multiply the corresponding weight.
        float oneR = ColorSRGB.srgbToLinear(ColorABGR.unpackRed(one)) * relativeWeightOne;
        float oneG = ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(one)) * relativeWeightOne;
        float oneB = ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(one)) * relativeWeightOne;

        float twoR = ColorSRGB.srgbToLinear(ColorABGR.unpackRed(two)) * relativeWeightTwo;
        float twoG = ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(two)) * relativeWeightTwo;
        float twoB = ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(two)) * relativeWeightTwo;

        // Combine the color components of each color
        float linearR = oneR + twoR;
        float linearG = oneG + twoG;
        float linearB = oneB + twoB;

        // Take the average alpha of both alpha values
        int averageAlpha = (alphaOne + alphaTwo) >> 1;

        // Convert to sRGB and pack the colors back into an integer.
        return ColorSRGB.linearToSrgb(linearR, linearG, linearB, averageAlpha);
    }

    // Sodium
    // Computes a non-weighted average of the two sRGB colors in linear space, avoiding brightness losses.
    private static int averageRgb(int a, int b, int alpha) {
        float ar = ColorSRGB.srgbToLinear(ColorABGR.unpackRed(a));
        float ag = ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(a));
        float ab = ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(a));

        float br = ColorSRGB.srgbToLinear(ColorABGR.unpackRed(b));
        float bg = ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(b));
        float bb = ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(b));

        return ColorSRGB.linearToSrgb((ar + br) * 0.5f, (ag + bg) * 0.5f, (ab + bb) * 0.5f, alpha);
    }

    private static int gammaBlend(int p_118043_, int p_118044_, int p_118045_, int p_118046_, int p_118047_) {
        float f = getPow22(p_118043_ >> p_118047_);
        float f1 = getPow22(p_118044_ >> p_118047_);
        float f2 = getPow22(p_118045_ >> p_118047_);
        float f3 = getPow22(p_118046_ >> p_118047_);
        float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25, 0.45454545454545453)));
        return (int)((double)f4 * 255.0);
    }

    private static float getPow22(int p_118041_) {
        return POW22[p_118041_ & 0xFF];
    }
}