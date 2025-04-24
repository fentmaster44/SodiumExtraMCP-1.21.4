package net.caffeinemc.sodium.client.render.vertex.buffer;

import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;

public interface BufferBuilderExtension extends VertexBufferWriter {
    void duplicateVertex();
}
