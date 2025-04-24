package net.caffeinemc.sodium.client.render.chunk.compile.pipeline;

import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.util.ColorARGB;
import net.caffeinemc.sodium.api.util.ColorMixer;
import net.caffeinemc.sodium.client.compatibility.workarounds.Workarounds;
import net.caffeinemc.sodium.client.model.color.ColorProvider;
import net.caffeinemc.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.sodium.client.model.light.LightMode;
import net.caffeinemc.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.sodium.client.model.quad.properties.ModelQuadOrientation;
import net.caffeinemc.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.caffeinemc.sodium.client.render.chunk.terrain.material.parameters.MaterialParameters;
import net.caffeinemc.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.sodium.client.render.texture.SpriteFinderCache;
import net.caffeinemc.sodium.client.services.PlatformModelAccess;
import net.caffeinemc.sodium.client.services.SodiumModelData;
import net.caffeinemc.sodium.client.world.LevelSlice;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Iterator;

public class BlockRenderer extends AbstractBlockRenderContext {
    private final ColorProviderRegistry colorProviderRegistry;
    private final int[] vertexColors = new int[4];
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();

    private ChunkBuildBuffers buffers;

    private final Vector3f posOffset = new Vector3f();
    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
    @Nullable
    private ColorProvider<BlockState> colorProvider;
    private TranslucentGeometryCollector collector;
    private boolean allowDowngrade;

    public BlockRenderer(ColorProviderRegistry colorRegistry, LightPipelineProvider lighters) {
        this.colorProviderRegistry = colorRegistry;
        this.lighters = lighters;

        this.random = new SingleThreadedRandomSource(42L);
    }

    public void prepare(ChunkBuildBuffers buffers, LevelSlice level, TranslucentGeometryCollector collector) {
        this.buffers = buffers;
        this.level = level;
        this.collector = collector;
        this.slice = level;
    }

    public void release() {
        this.buffers = null;
        this.level = null;
        this.collector = null;
        this.slice = null;
    }

    public void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin) {
        this.state = state;
        this.pos = pos;

        this.randomSeed = state.getSeed(pos);

        this.posOffset.set(origin.getX(), origin.getY(), origin.getZ());
        if (state.hasOffsetFunction()) {
            Vec3 modelOffset = state.getOffset(pos);
            this.posOffset.add((float) modelOffset.x, (float) modelOffset.y, (float) modelOffset.z);
        }

        this.colorProvider = this.colorProviderRegistry.getColorProvider(state.getBlock());

        type = ItemBlockRenderTypes.getChunkRenderType(state);

        this.prepareCulling(true);
        this.prepareAoInfo(model.useAmbientOcclusion());

        modelData = PlatformModelAccess.getInstance().getModelData(slice, model, state, pos, slice.getPlatformModelData(pos));

        Iterable<RenderType> renderTypes = PlatformModelAccess.getInstance().getModelRenderTypes(level, model, state, pos, random, modelData);
        this.allowDowngrade = true;

        Iterator<RenderType> it = renderTypes.iterator();
        var defaultType = ItemBlockRenderTypes.getChunkRenderType(state);

        while (it.hasNext()) {
            this.type = it.next();

            // TODO: This can be removed once we have a better solution for https://github.com/CaffeineMC/sodium/issues/2868
            // If the model contains any materials that are not the default, we can't allow the block to be downgraded. This avoids a potentially incorrect render order if there are overlapping quads.
            if (it.hasNext() || this.type != defaultType) {
                this.allowDowngrade = false;
            }

            ((FabricBakedModel) model).emitBlockQuads(getEmitter(), this.level, state, pos, this.randomSupplier, this::isFaceCulled);
        }

        type = null;
        modelData = SodiumModelData.EMPTY;
    }

    /**
     * Process quad, after quad transforms and the culling check have been applied.
     */
    @Override
    protected void processQuad(MutableQuadViewImpl quad) {
        final RenderMaterial mat = quad.material();
        final TriState aoMode = mat.ambientOcclusion();
        final ShadeMode shadeMode = mat.shadeMode();
        final LightMode lightMode;
        if (aoMode == TriState.DEFAULT) {
            lightMode = this.defaultLightMode;
        } else {
            lightMode = this.useAmbientOcclusion && aoMode.get() ? LightMode.SMOOTH : LightMode.FLAT;
        }
        final boolean emissive = mat.emissive();

        Material material;

        final BlendMode blendMode = mat.blendMode();
        if (blendMode == BlendMode.DEFAULT) {
            material = DefaultMaterials.forRenderLayer(type);
        } else {
            material = DefaultMaterials.forRenderLayer(blendMode.blockRenderLayer == null ? type : blendMode.blockRenderLayer);
        }

        this.tintQuad(quad);
        this.shadeQuad(quad, lightMode, emissive, shadeMode);
        this.bufferQuad(quad, this.quadLightData.br, material);
    }

    private void tintQuad(MutableQuadViewImpl quad) {
        int tintIndex = quad.tintIndex();

        if (tintIndex != -1) {
            ColorProvider<BlockState> colorProvider = this.colorProvider;

            if (colorProvider != null) {
                int[] vertexColors = this.vertexColors;
                colorProvider.getColors(this.slice, this.pos, this.scratchPos, this.state, quad, vertexColors);

                for (int i = 0; i < 4; i++) {
                    quad.color(i, ColorMixer.mulComponentWise(vertexColors[i], quad.color(i)));
                }
            }
        }
    }

    private void bufferQuad(MutableQuadViewImpl quad, float[] brightnesses, Material material) {
        // TODO: Find a way to reimplement quad reorientation
        ModelQuadOrientation orientation = ModelQuadOrientation.NORMAL;
        ChunkVertexEncoder.Vertex[] vertices = this.vertices;
        Vector3f offset = this.posOffset;

        for (int dstIndex = 0; dstIndex < 4; dstIndex++) {
            int srcIndex = orientation.getVertexIndex(dstIndex);

            ChunkVertexEncoder.Vertex out = vertices[dstIndex];
            out.x = quad.x(srcIndex) + offset.x;
            out.y = quad.y(srcIndex) + offset.y;
            out.z = quad.z(srcIndex) + offset.z;

            // FRAPI uses ARGB color format; convert to ABGR.
            out.color = ColorARGB.toABGR(quad.color(srcIndex));
            out.ao = brightnesses[srcIndex];

            out.u = quad.u(srcIndex);
            out.v = quad.v(srcIndex);

            out.light = quad.lightmap(srcIndex);
        }

        var atlasSprite = quad.sprite(SpriteFinderCache.forBlockAtlas());
        var materialBits = material.bits();
        ModelQuadFacing normalFace = quad.normalFace();

        // attempt render pass downgrade if possible
        var pass = material.pass;

        var downgradedPass = attemptPassDowngrade(atlasSprite, pass);
        if (downgradedPass != null) {
            pass = downgradedPass;
        }

        // collect all translucent quads into the translucency sorting system if enabled
        if (pass.isTranslucent() && this.collector != null) {
            this.collector.appendQuad(quad.getFaceNormal(), vertices, normalFace);
        }

        // if there was a downgrade from translucent to cutout, the material bits' alpha cutoff needs to be updated
        if (downgradedPass != null && material == DefaultMaterials.TRANSLUCENT && pass == DefaultTerrainRenderPasses.CUTOUT) {
            // ONE_TENTH and HALF are functionally the same so it doesn't matter which one we take here
            materialBits = MaterialParameters.pack(AlphaCutoffParameter.ONE_TENTH, material.mipped);
        }

        ChunkModelBuilder builder = this.buffers.get(pass);
        ChunkMeshBufferBuilder vertexBuffer = builder.getVertexBuffer(normalFace);
        vertexBuffer.push(vertices, materialBits);

        if (atlasSprite != null) {
            builder.addSprite(atlasSprite);
        }
    }

    private boolean validateQuadUVs(TextureAtlasSprite atlasSprite) {
        // sanity check that the quad's UVs are within the sprite's bounds
        var spriteUMin = atlasSprite.getU0();
        var spriteUMax = atlasSprite.getU1();
        var spriteVMin = atlasSprite.getV0();
        var spriteVMax = atlasSprite.getV1();

        for (int i = 0; i < 4; i++) {
            var u = this.vertices[i].u;
            var v = this.vertices[i].v;
            if (u < spriteUMin || u > spriteUMax || v < spriteVMin || v > spriteVMax) {
                return false;
            }
        }

        return true;
    }

    private @Nullable TerrainRenderPass attemptPassDowngrade(TextureAtlasSprite sprite, TerrainRenderPass pass) {
        if (!allowDowngrade || Workarounds.isWorkaroundEnabled(Workarounds.Reference.INTEL_DEPTH_BUFFER_COMPARISON_UNRELIABLE)) {
            return null;
        }

        boolean attemptDowngrade = true;
        boolean hasNonOpaqueVertex = false;

        for (int i = 0; i < 4; i++) {
            hasNonOpaqueVertex |= ColorABGR.unpackAlpha(this.vertices[i].color) != 0xFF;
        }

        // don't do downgrade if some vertex is not fully opaque
        if (pass.isTranslucent() && hasNonOpaqueVertex) {
            attemptDowngrade = false;
        }

        if (attemptDowngrade) {
            attemptDowngrade = validateQuadUVs(sprite);
        }

        if (attemptDowngrade) {
            return getDowngradedPass(sprite, pass);
        }

        return null;
    }

    private static TerrainRenderPass getDowngradedPass(TextureAtlasSprite sprite, TerrainRenderPass pass) {
        if (sprite instanceof TextureAtlasSpriteExtension spriteExt) {
            // Some mods may use a custom ticker which we cannot look into. To avoid problems with these mods,
            // do not attempt to downgrade the render pass.
            if (spriteExt.sodium$hasUnknownImageContents()) {
                return pass;
            }

            if (sprite.contents() instanceof SpriteContentsExtension contentsExt) {
                if (pass == DefaultTerrainRenderPasses.TRANSLUCENT && !contentsExt.sodium$hasTranslucentPixels()) {
                    pass = DefaultTerrainRenderPasses.CUTOUT;
                }
                if (pass == DefaultTerrainRenderPasses.CUTOUT && !contentsExt.sodium$hasTransparentPixels()) {
                    pass = DefaultTerrainRenderPasses.SOLID;
                }
            }
        }

        return pass;
    }
}