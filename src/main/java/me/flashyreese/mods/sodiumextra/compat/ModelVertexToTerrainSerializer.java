package me.flashyreese.mods.sodiumextra.compat;

import net.caffeinemc.sodium.api.vertex.format.common.EntityVertex;
import net.caffeinemc.sodium.api.vertex.serializer.VertexSerializer;
import org.lwjgl.system.MemoryUtil;

public class ModelVertexToTerrainSerializer implements VertexSerializer {
    @Override
    public void serialize(long src, long dst, int vertexCount) {
        for (int i = 0; i < vertexCount; ++i) {
            MemoryUtil.memCopy(src, dst, 24); // Copies position, color and texture
            // todo: overlay src + 24L
            MemoryUtil.memCopy(src + 28L, dst + 24L, 8); // Copies light and normal

            src += EntityVertex.STRIDE;
            dst += IrisCompat.getTerrainFormat().getVertexSize();
        }
    }
}
