package net.caffeinemc.sodium.client.render.chunk.vertex.format;

import net.caffeinemc.sodium.client.gl.attribute.GlVertexFormat;

public interface ChunkVertexType {
    GlVertexFormat getVertexFormat();

    ChunkVertexEncoder getEncoder();
}
