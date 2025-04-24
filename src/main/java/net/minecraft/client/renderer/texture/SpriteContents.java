package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.util.ColorMixer;
import net.caffeinemc.sodium.client.SodiumClientMod;
import net.caffeinemc.sodium.client.util.NativeImageHelper;
import net.caffeinemc.sodium.client.util.color.ColorSRGB;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SpriteContents implements Stitcher.Entry, AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation name;
    final int width;
    final int height;
    private final NativeImage originalImage;
    @Getter NativeImage[] images;
    @Nullable private final SpriteContents.AnimatedTexture animatedTexture;
    private final ResourceMetadata metadata;

    // Sodium
    @Getter @Setter boolean active;

    // Sodium
    public boolean hasTransparentPixels = false;
    public boolean hasTranslucentPixels = false;

    public SpriteContents(ResourceLocation p_249787_,
                          FrameSize p_251031_,
                          NativeImage nativeImage,
                          ResourceMetadata p_299427_) {
        this.name = p_249787_;
        this.width = p_251031_.width();
        this.height = p_251031_.height();
        this.metadata = p_299427_;
        this.animatedTexture = p_299427_.getSection(AnimationMetadataSection.TYPE)
            .map(p_374666_ -> this.createAnimatedTexture(p_251031_, nativeImage.getWidth(), nativeImage.getHeight(), p_374666_))
            .orElse(null);

        // Sodium
        scanSpriteContents(nativeImage);
        fillInTransparentPixelColors(nativeImage);

        this.originalImage = nativeImage;
        this.images = new NativeImage[]{this.originalImage};
    }

    // Sodium
    public boolean hasAnimation() {
        return animatedTexture != null;
    }

    // Sodium
    private void scanSpriteContents(NativeImage nativeImage) {
        val ppPixel = NativeImageHelper.getPointerRGBA(nativeImage);
        val pixelCount = nativeImage.getHeight() * nativeImage.getWidth();

        for (var pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
            val color = MemoryUtil.memGetInt(ppPixel + (pixelIndex * 4L));
            val alpha = ColorABGR.unpackAlpha(color);

            // 25 is used as the threshold since the alpha cutoff is 0.1
            if (alpha <= 25) { // 0.1 * 255
                this.hasTransparentPixels = true;
            } else if (alpha < 255) {
                this.hasTranslucentPixels = true;
            }
        }

        // the image contains transparency also if there are translucent pixels,
        // since translucent pixels prevent a downgrade to the opaque render pass just as transparent pixels do
        this.hasTransparentPixels |= this.hasTranslucentPixels;
    }

    // Sodium
    /**
     * Fixes a common issue in image editing programs where fully transparent pixels are saved with fully black colors.
     *
     * This causes issues with mipmapped texture filtering, since the black color is used to calculate the final color
     * even though the alpha value is zero. While ideally it would be disregarded, we do not control that. Instead,
     * this code tries to calculate a decent average color to assign to these fully-transparent pixels so that their
     * black color does not leak over into sampling.
     */
    private static void fillInTransparentPixelColors(NativeImage nativeImage) {
        final long ppPixel = NativeImageHelper.getPointerRGBA(nativeImage);
        final int pixelCount = nativeImage.getHeight() * nativeImage.getWidth();

        // Calculate an average color from all pixels that are not completely transparent.
        // This average is weighted based on the (non-zero) alpha value of the pixel.
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;

        float totalWeight = 0.0f;

        for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
            long pPixel = ppPixel + (pixelIndex * 4L);

            int color = MemoryUtil.memGetInt(pPixel);
            int alpha = ColorABGR.unpackAlpha(color);

            // Ignore all fully-transparent pixels for the purposes of computing an average color.
            if (alpha != 0) {
                float weight = (float) alpha;

                // Make sure to convert to linear space so that we don't lose brightness.
                r += ColorSRGB.srgbToLinear(ColorABGR.unpackRed(color)) * weight;
                g += ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(color)) * weight;
                b += ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(color)) * weight;

                totalWeight += weight;
            }
        }

        // Bail if none of the pixels are semi-transparent.
        if (totalWeight == 0.0f) {
            return;
        }

        r /= totalWeight;
        g /= totalWeight;
        b /= totalWeight;

        // Convert that color in linear space back to sRGB.
        // Use an alpha value of zero - this works since we only replace pixels with an alpha value of 0.
        int averageColor = ColorSRGB.linearToSrgb(r, g, b, 0);

        for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
            long pPixel = ppPixel + (pixelIndex * 4);

            int color = MemoryUtil.memGetInt(pPixel);
            int alpha = ColorABGR.unpackAlpha(color);

            // Replace the color values of pixels which are fully transparent, since they have no color data.
            if (alpha == 0) {
                MemoryUtil.memPutInt(pPixel, averageColor);
            }
        }
    }

    public void increaseMipLevel(int p_248864_) {
        try {
            this.images = MipmapGenerator.generateMipLevels(this.images, p_248864_);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Generating mipmaps for frame");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Sprite being mipmapped");
            crashreportcategory.setDetail("First frame", () -> {
                StringBuilder stringbuilder = new StringBuilder();
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
                return stringbuilder.toString();
            });
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Frame being iterated");
            crashreportcategory1.setDetail("Sprite name", this.name);
            crashreportcategory1.setDetail("Sprite size", () -> this.width + " x " + this.height);
            crashreportcategory1.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
            crashreportcategory1.setDetail("Mipmap levels", p_248864_);
            throw new ReportedException(crashreport);
        }
    }

    private int getFrameCount() {
        return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
    }

    @Nullable
    private SpriteContents.AnimatedTexture createAnimatedTexture(FrameSize p_250817_, int p_249792_, int p_252353_, AnimationMetadataSection p_250947_) {
        int i = p_249792_ / p_250817_.width();
        int j = p_252353_ / p_250817_.height();
        int k = i * j;
        int l = p_250947_.defaultFrameTime();
        List<SpriteContents.FrameInfo> list;
        if (p_250947_.frames().isEmpty()) {
            list = new ArrayList<>(k);

            for (int i1 = 0; i1 < k; i1++) {
                list.add(new SpriteContents.FrameInfo(i1, l));
            }
        } else {
            List<AnimationFrame> list1 = p_250947_.frames().get();
            list = new ArrayList<>(list1.size());

            for (AnimationFrame animationframe : list1) {
                list.add(new SpriteContents.FrameInfo(animationframe.index(), animationframe.timeOr(l)));
            }

            int j1 = 0;
            IntSet intset = new IntOpenHashSet();

            for (Iterator<SpriteContents.FrameInfo> iterator = list.iterator(); iterator.hasNext(); j1++) {
                SpriteContents.FrameInfo spritecontents$frameinfo = iterator.next();
                boolean flag = true;
                if (spritecontents$frameinfo.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.name, j1, spritecontents$frameinfo.time);
                    flag = false;
                }

                if (spritecontents$frameinfo.index < 0 || spritecontents$frameinfo.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.name, j1, spritecontents$frameinfo.index);
                    flag = false;
                }

                if (flag) {
                    intset.add(spritecontents$frameinfo.index);
                } else {
                    iterator.remove();
                }
            }

            int[] aint = IntStream.range(0, k).filter(p_251185_ -> !intset.contains(p_251185_)).toArray();
            if (aint.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(aint));
            }
        }

        return list.size() <= 1 ? null : new SpriteContents.AnimatedTexture(List.copyOf(list), i, p_250947_.interpolatedFrames());
    }

    void upload(int x, int y, int unpackSkipPixels, int unpackSkipRows, NativeImage[] images) {
        for (var i = 0; i < this.images.length; i++) {
            images[i].upload(i, x >> i, y >> i, unpackSkipPixels >> i, unpackSkipRows >> i, this.width >> i, this.height >> i, false);
        }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public ResourceLocation name() {
        return this.name;
    }

    public IntStream getUniqueFrames() {
        return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
    }

    @Nullable
    public SpriteTicker createTicker() {
        return this.animatedTexture != null ? this.animatedTexture.createTicker() : null;
    }

    public ResourceMetadata metadata() {
        return this.metadata;
    }

    @Override
    public void close() {
        for (NativeImage nativeimage : this.images) {
            nativeimage.close();
        }
    }

    @Override
    public String toString() {
        return "SpriteContents{name=" + this.name + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isTransparent(int p_250374_, int p_250934_, int p_249573_) {
        int i = p_250934_;
        int j = p_249573_;
        if (this.animatedTexture != null) {
            i = p_250934_ + this.animatedTexture.getFrameX(p_250374_) * this.width;
            j = p_249573_ + this.animatedTexture.getFrameY(p_250374_) * this.height;
        }

        return ARGB.alpha(this.originalImage.getPixel(i, j)) == 0;
    }

    public void uploadFirstFrame(int p_252315_, int p_248634_) {
        if (this.animatedTexture != null) {
            this.animatedTexture.uploadFirstFrame(p_252315_, p_248634_);
        } else {
            this.upload(p_252315_, p_248634_, 0, 0, this.images);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class AnimatedTexture {
        @Getter final List<SpriteContents.FrameInfo> frames;
        @Getter private final int frameRowSize;
        private final boolean interpolateFrames;

        AnimatedTexture(final List<SpriteContents.FrameInfo> p_250968_, final int p_251686_, final boolean p_251832_) {
            this.frames = p_250968_;
            this.frameRowSize = p_251686_;
            this.interpolateFrames = p_251832_;
        }

        int getFrameX(int p_249475_) {
            return p_249475_ % this.frameRowSize;
        }

        int getFrameY(int p_251327_) {
            return p_251327_ / this.frameRowSize;
        }

        void uploadFrame(int p_250449_, int p_248877_, int p_249060_) {
            int i = this.getFrameX(p_249060_) * SpriteContents.this.width;
            int j = this.getFrameY(p_249060_) * SpriteContents.this.height;
            SpriteContents.this.upload(p_250449_, p_248877_, i, j, SpriteContents.this.images);
        }

        public SpriteTicker createTicker() {
            return SpriteContents.this.new Ticker(this, this.interpolateFrames ? SpriteContents.this.new InterpolationData() : null);
        }

        public void uploadFirstFrame(int p_251807_, int p_248676_) {
            this.uploadFrame(p_251807_, p_248676_, this.frames.get(0).index);
        }

        public IntStream getUniqueFrames() {
            return this.frames.stream().mapToInt(p_249981_ -> p_249981_.index).distinct();
        }

    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Data
    @OnlyIn(Dist.CLIENT)
    static class FrameInfo {
        int index;
        int time;
    }

    @OnlyIn(Dist.CLIENT)
    final class InterpolationData implements AutoCloseable {

        private final NativeImage[] activeFrame = new NativeImage[SpriteContents.this.images.length];

        // Sodium
        private SpriteContents parent;
        private static final int STRIDE = 4;

        InterpolationData() {
            for (int i = 0; i < this.activeFrame.length; i++) {
                int j = SpriteContents.this.width >> i;
                int k = SpriteContents.this.height >> i;
                this.activeFrame[i] = new NativeImage(j, k, false);
            }

            parent = SpriteContents.this;
        }

        // Sodium
        void uploadInterpolatedFrame(int x, int y, SpriteContents.Ticker arg) {
            val animation = arg.getAnimationInfo();
            val animation2 = arg.getAnimationInfo();
            val frames = animation.getFrames();
            val animationFrame = frames.get(arg.getFrame());

            int curIndex = animationFrame.getIndex();
            int nextIndex = animation2.getFrames().get((arg.getFrame() + 1) % frames.size()).getIndex();

            if (curIndex == nextIndex) {
                return;
            }

            // The mix factor between the current and next frame
            float mix = 1.0F - (float) arg.getSubFrame() / (float) animationFrame.getTime();

            for (int layer = 0; layer < this.activeFrame.length; layer++) {
                int width = this.parent.width() >> layer;
                int height = this.parent.height() >> layer;

                int curX = ((curIndex % animation2.getFrameRowSize()) * width);
                int curY = ((curIndex / animation2.getFrameRowSize()) * height);

                int nextX = ((nextIndex % animation2.getFrameRowSize()) * width);
                int nextY = ((nextIndex / animation2.getFrameRowSize()) * height);

                NativeImage src = this.parent.getImages()[layer];
                NativeImage dst = this.activeFrame[layer];

                long ppSrcPixel = NativeImageHelper.getPointerRGBA(src);
                long ppDstPixel = NativeImageHelper.getPointerRGBA(dst);

                for (int layerY = 0; layerY < height; layerY++) {
                    // Pointers to the pixel array for the current and next frame
                    long pRgba1 = ppSrcPixel + (curX + (long) (curY + layerY) * src.getWidth()) * STRIDE;
                    long pRgba2 = ppSrcPixel + (nextX + (long) (nextY + layerY) * src.getWidth()) * STRIDE;

                    for (int layerX = 0; layerX < width; layerX++) {
                        int rgba1 = MemoryUtil.memGetInt(pRgba1);
                        int rgba2 = MemoryUtil.memGetInt(pRgba2);

                        // Mix the RGB components and truncate the A component
                        int mixedRgb = ColorMixer.mix(rgba1, rgba2, mix) & 0x00FFFFFF;

                        // Take the A component from the source pixel
                        int alpha = rgba1 & 0xFF000000;

                        // Update the pixel within the interpolated frame using the combined RGB and A components
                        MemoryUtil.memPutInt(ppDstPixel, mixedRgb | alpha);

                        pRgba1 += STRIDE;
                        pRgba2 += STRIDE;

                        ppDstPixel += STRIDE;
                    }
                }
            }

            parent.upload(x, y, 0, 0, activeFrame);
        }

        private int getPixel(SpriteContents.AnimatedTexture p_251976_, int p_250761_, int p_250049_, int p_250004_, int p_251489_) {
            return SpriteContents.this.images[p_250049_]
                .getPixel(
                    p_250004_ + (p_251976_.getFrameX(p_250761_) * SpriteContents.this.width >> p_250049_),
                    p_251489_ + (p_251976_.getFrameY(p_250761_) * SpriteContents.this.height >> p_250049_)
                );
        }

        @Override
        public void close() {
            for (NativeImage nativeimage : this.activeFrame) {
                nativeimage.close();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Ticker implements SpriteTicker {
        @Getter int frame;
        @Getter int subFrame;
        @Getter final SpriteContents.AnimatedTexture animationInfo;
        @Nullable
        private final SpriteContents.InterpolationData interpolationData;

        // Sodium
        private SpriteContents parent;

        Ticker(final SpriteContents.AnimatedTexture animation,
               @Nullable final SpriteContents.InterpolationData interpolation) {
            this.animationInfo = animation;
            this.interpolationData = interpolation;
            parent = SpriteContents.this;
        }

        @Override
        public void tickAndUpload(int p_249105_, int p_249676_) {
            val onDemand = SodiumClientMod.options().performance.animateOnlyVisibleTextures;

            if (onDemand && !parent.isActive()) {
                this.subFrame++;
                val frames = this.animationInfo.getFrames();
                if (this.subFrame >= frames.get(this.frame).getTime()) {
                    this.frame = (this.frame + 1) % frames.size();
                    this.subFrame = 0;
                }

                return;
            }

            this.subFrame++;
            SpriteContents.FrameInfo spritecontents$frameinfo = this.animationInfo.frames.get(this.frame);
            if (this.subFrame >= spritecontents$frameinfo.time) {
                int i = spritecontents$frameinfo.index;
                this.frame = (this.frame + 1) % this.animationInfo.frames.size();
                this.subFrame = 0;
                int j = this.animationInfo.frames.get(this.frame).index;
                if (i != j) {
                    this.animationInfo.uploadFrame(p_249105_, p_249676_, j);
                }
            } else if (this.interpolationData != null) {
                this.interpolationData.uploadInterpolatedFrame(p_249105_, p_249676_, this);
            }

            parent.setActive(false);
        }

        @Override
        public void close() {
            if (this.interpolationData != null) {
                this.interpolationData.close();
            }
        }
    }
}