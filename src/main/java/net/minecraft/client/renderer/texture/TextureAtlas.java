package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.val;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.caffeinemc.sodium.api.texture.SpriteUtil;
import net.caffeinemc.sodium.client.render.texture.SpriteFinderCache;
import net.fabricmc.fabric.impl.renderer.SpriteFinderImpl;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureAtlas extends AbstractTexture implements Dumpable, Tickable, SpriteFinderImpl.SpriteFinderAccess {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES = ResourceLocation.withDefaultNamespace("textures/atlas/particles.png");
    private List<SpriteContents> sprites = List.of();
    private List<TextureAtlasSprite.Ticker> animatedTextures = List.of();
    private Map<ResourceLocation, TextureAtlasSprite> texturesByName = Map.of();
    @Nullable
    private TextureAtlasSprite missingSprite;
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;
    @Getter private int width;
    @Getter private int height;
    private int mipLevel;

    // Fabric
    private @Nullable SpriteFinderImpl spriteFinder = null;

    // SodiumExtra
    private final Map<Supplier<Boolean>, List<ResourceLocation>> animatedSprites = Map.of(
            () -> SodiumExtraClientMod.options().animationSettings.water, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.lava, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/lava_still"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/lava_flow")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.portal, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/nether_portal")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.fire, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_0"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_1"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_fire_0"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_fire_1"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/campfire_fire"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/campfire_log_lit"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_campfire_fire"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_campfire_log_lit")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.blockAnimations, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/magma"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/lantern"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sea_lantern"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_lantern"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/kelp"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/kelp_plant"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/seagrass"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/tall_seagrass_top"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/tall_seagrass_bottom"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/warped_stem"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/crimson_stem"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/blast_furnace_front_on"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/smoker_front_on"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/stonecutter_saw"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/prismarine"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/respawn_anchor_top"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "entity/conduit/wind"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "entity/conduit/wind_vertical")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.sculkSensor, List.of(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_catalyst_top_bloom"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_catalyst_side_bloom"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_shrieker_inner_top"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_vein"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_shrieker_can_summon_inner_top"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_sensor_tendril_inactive"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "block/sculk_sensor_tendril_active"),
                    ResourceLocation.fromNamespaceAndPath("minecraft", "vibration")
            )
    );

    public TextureAtlas(ResourceLocation p_118269_) {
        this.location = p_118269_;
        this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    public void upload(SpriteLoader.Preparations p_250662_) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", p_250662_.width(), p_250662_.height(), p_250662_.mipLevel(), this.location);
        TextureUtil.prepareImage(this.getId(), p_250662_.mipLevel(), p_250662_.width(), p_250662_.height());
        this.width = p_250662_.width();
        this.height = p_250662_.height();
        this.mipLevel = p_250662_.mipLevel();
        this.clearTextureData();
        this.setFilter(false, this.mipLevel > 1);
        this.texturesByName = Map.copyOf(p_250662_.regions());
        this.missingSprite = this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
        if (this.missingSprite == null) {
            throw new IllegalStateException("Atlas '" + this.location + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
        } else {
            List<SpriteContents> list = new ArrayList<>();
            List<TextureAtlasSprite.Ticker> tickers = new ArrayList<>();

            for (TextureAtlasSprite textureatlassprite : p_250662_.regions().values()) {
                list.add(textureatlassprite.contents());

                try {
                    textureatlassprite.uploadFirstFrame();
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Texture being stitched together");
                    crashreportcategory.setDetail("Atlas path", this.location);
                    crashreportcategory.setDetail("Sprite", textureatlassprite);
                    throw new ReportedException(crashreport);
                }

                // SodiumExtra
                val ticker = textureatlassprite.createTicker();

                if (ticker != null
                        && SodiumExtraClientMod.options().animationSettings.animation
                        && this.shouldAnimate(textureatlassprite.contents().name())) {
                    tickers.add(ticker);
                }
            }

            this.sprites = List.copyOf(list);
            this.animatedTextures = List.copyOf(tickers);
        }

        // Sodium
        if (location.equals(LOCATION_BLOCKS)) {
            SpriteFinderCache.resetSpriteFinder();
        }

        // Fabric
        spriteFinder = null;
    }

    // SodiumExtra
    private boolean shouldAnimate(ResourceLocation identifier) {
        if (identifier != null) {
            for (Map.Entry<Supplier<Boolean>, List<ResourceLocation>> supplierListEntry : this.animatedSprites.entrySet()) {
                if (supplierListEntry.getValue().contains(identifier)) {
                    return supplierListEntry.getKey().get();
                }
            }
        }
        return true;
    }

    @Override
    public void dumpContents(ResourceLocation p_276106_, Path p_276127_) throws IOException {
        String s = p_276106_.toDebugFileName();
        TextureUtil.writeAsPNG(p_276127_, s, this.getId(), this.mipLevel, this.width, this.height);
        dumpSpriteNames(p_276127_, s, this.texturesByName);
    }

    private static void dumpSpriteNames(Path p_261769_, String p_262102_, Map<ResourceLocation, TextureAtlasSprite> p_261722_) {
        Path path = p_261769_.resolve(p_262102_ + ".txt");

        try (Writer writer = Files.newBufferedWriter(path)) {
            for (Entry<ResourceLocation, TextureAtlasSprite> entry : p_261722_.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
                TextureAtlasSprite textureatlassprite = entry.getValue();
                writer.write(
                    String.format(
                        Locale.ROOT,
                        "%s\tx=%d\ty=%d\tw=%d\th=%d%n",
                        entry.getKey(),
                        textureatlassprite.getX(),
                        textureatlassprite.getY(),
                        textureatlassprite.contents().width(),
                        textureatlassprite.contents().height()
                    )
                );
            }
        } catch (IOException ioexception) {
            LOGGER.warn("Failed to write file {}", path, ioexception);
        }
    }

    public void cycleAnimationFrames() {
        this.bind();

        for (TextureAtlasSprite.Ticker textureatlassprite$ticker : this.animatedTextures) {
            textureatlassprite$ticker.tickAndUpload();
        }
    }

    @Override
    public void tick() {
        this.cycleAnimationFrames();
    }

    public TextureAtlasSprite getSprite(ResourceLocation location) {
        val sprite = this.texturesByName.getOrDefault(location, this.missingSprite);

        if (sprite == null) {
            throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
        } else {
            // Sodium
            SpriteUtil.INSTANCE.markSpriteActive(sprite);
            return sprite;
        }
    }

    public void clearTextureData() {
        this.sprites.forEach(SpriteContents::close);
        this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
        this.sprites = List.of();
        this.animatedTextures = List.of();
        this.texturesByName = Map.of();
        this.missingSprite = null;
    }

    public ResourceLocation location() {
        return this.location;
    }

    public int maxSupportedTextureSize() {
        return this.maxSupportedTextureSize;
    }

    @Override
    public SpriteFinderImpl fabric_spriteFinder() {
        SpriteFinderImpl result = spriteFinder;

        if (result == null) {
            result = new SpriteFinderImpl(texturesByName, this);
            spriteFinder = result;
        }

        return result;
    }
}