package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import javax.annotation.Nullable;

import lombok.val;
import net.caffeinemc.sodium.client.util.sorting.VertexSorters;
import net.caffeinemc.sodium.client.util.sorting.VertexSortingExtended;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public interface MultiBufferSource {
    static MultiBufferSource.BufferSource immediate(ByteBufferBuilder p_344614_) {
        return immediateWithBuffers(Object2ObjectSortedMaps.emptyMap(), p_344614_);
    }

    static MultiBufferSource.BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> p_342750_, ByteBufferBuilder p_344601_) {
        return new MultiBufferSource.BufferSource(p_344601_, p_342750_);
    }

    VertexConsumer getBuffer(RenderType p_109903_);

    @OnlyIn(Dist.CLIENT)
    public static class BufferSource implements MultiBufferSource {
        protected final ByteBufferBuilder sharedBuffer;
        protected final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;
        protected final Map<RenderType, BufferBuilder> startedBuilders = new HashMap<>();
        @Nullable protected RenderType lastSharedType;

        // Sodium
        private static final int VERTICES_PER_QUAD = 6;

        protected BufferSource(ByteBufferBuilder p_344223_, SequencedMap<RenderType, ByteBufferBuilder> p_344104_) {
            this.sharedBuffer = p_344223_;
            this.fixedBuffers = p_344104_;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            BufferBuilder bufferbuilder = this.startedBuilders.get(renderType);
            if (bufferbuilder != null && !renderType.canConsolidateConsecutiveGeometry()) {
                this.endBatch(renderType, bufferbuilder);
                bufferbuilder = null;
            }

            if (bufferbuilder != null) {
                return bufferbuilder;
            } else {
                ByteBufferBuilder bytebufferbuilder = this.fixedBuffers.get(renderType);
                if (bytebufferbuilder != null) {
                    bufferbuilder = new BufferBuilder(bytebufferbuilder, renderType.mode(), renderType.format());
                } else {
                    if (this.lastSharedType != null) {
                        this.endBatch(this.lastSharedType);
                    }

                    bufferbuilder = new BufferBuilder(this.sharedBuffer, renderType.mode(), renderType.format());
                    this.lastSharedType = renderType;
                }

                this.startedBuilders.put(renderType, bufferbuilder);
                return bufferbuilder;
            }
        }

        public void endLastBatch() {
            if (this.lastSharedType != null) {
                this.endBatch(this.lastSharedType);
                this.lastSharedType = null;
            }
        }

        public void endBatch() {
            this.endLastBatch();

            for (RenderType rendertype : this.fixedBuffers.keySet()) {
                this.endBatch(rendertype);
            }
        }

        public void endBatch(RenderType p_109913_) {
            BufferBuilder bufferbuilder = this.startedBuilders.remove(p_109913_);
            if (bufferbuilder != null) {
                this.endBatch(p_109913_, bufferbuilder);
            }
        }

        private void endBatch(RenderType p_345497_, BufferBuilder bufferBuilder) {
            MeshData meshData = bufferBuilder.build();
            if (meshData != null) {
                if (p_345497_.sortOnUpload()) {
                    ByteBufferBuilder bytebufferbuilder = this.fixedBuffers.getOrDefault(p_345497_, this.sharedBuffer);

                    // Sodium
                    val sorting = RenderSystem.getProjectionType().vertexSorting();

                    if (sorting instanceof VertexSortingExtended sortingExtended) {
                        var sortedPrimitiveIds = VertexSorters.sort(meshData.vertexBuffer(), meshData.drawState().vertexCount(), meshData.drawState().format().getVertexSize(), sortingExtended);
                        var sortedIndexBuffer = buildSortedIndexBuffer(meshData, bytebufferbuilder, sortedPrimitiveIds);
                        meshData.setIndexBuffer(sortedIndexBuffer);
                    } else {
                        meshData.sortQuads(bytebufferbuilder, sorting);
                    }
                    // Sodium end
                }

                p_345497_.draw(meshData);
            }

            if (p_345497_.equals(this.lastSharedType)) {
                this.lastSharedType = null;
            }
        }

        // Sodium
        private static ByteBufferBuilder.Result buildSortedIndexBuffer(MeshData meshData, ByteBufferBuilder bufferBuilder, int[] primitiveIds) {
            final var indexType = meshData.drawState().indexType();
            final var ptr = bufferBuilder.reserve((primitiveIds.length * VERTICES_PER_QUAD) * indexType.bytes);

            if (indexType == VertexFormat.IndexType.SHORT) {
                writeShortIndexBuffer(ptr, primitiveIds);
            } else if (indexType == VertexFormat.IndexType.INT) {
                writeIntIndexBuffer(ptr, primitiveIds);
            }

            return bufferBuilder.build();
        }

        // Sodium
        private static void writeIntIndexBuffer(long ptr, int[] primitiveIds) {
            for (int primitiveId : primitiveIds) {
                MemoryUtil.memPutInt(ptr +  0L, (primitiveId * 4) + 0);
                MemoryUtil.memPutInt(ptr +  4L, (primitiveId * 4) + 1);
                MemoryUtil.memPutInt(ptr +  8L, (primitiveId * 4) + 2);
                MemoryUtil.memPutInt(ptr + 12L, (primitiveId * 4) + 2);
                MemoryUtil.memPutInt(ptr + 16L, (primitiveId * 4) + 3);
                MemoryUtil.memPutInt(ptr + 20L, (primitiveId * 4) + 0);
                ptr += 24L;
            }
        }

        // Sodium
        private static void writeShortIndexBuffer(long ptr, int[] primitiveIds) {
            for (int primitiveId : primitiveIds) {
                MemoryUtil.memPutShort(ptr +  0L, (short) ((primitiveId * 4) + 0));
                MemoryUtil.memPutShort(ptr +  2L, (short) ((primitiveId * 4) + 1));
                MemoryUtil.memPutShort(ptr +  4L, (short) ((primitiveId * 4) + 2));
                MemoryUtil.memPutShort(ptr +  6L, (short) ((primitiveId * 4) + 2));
                MemoryUtil.memPutShort(ptr +  8L, (short) ((primitiveId * 4) + 3));
                MemoryUtil.memPutShort(ptr + 10L, (short) ((primitiveId * 4) + 0));
                ptr += 12L;
            }
        }
    }
}