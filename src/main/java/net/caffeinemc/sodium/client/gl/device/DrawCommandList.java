package net.caffeinemc.sodium.client.gl.device;

import net.caffeinemc.sodium.client.gl.tessellation.GlIndexType;

public interface DrawCommandList extends AutoCloseable {
    void multiDrawElementsBaseVertex(MultiDrawBatch batch, GlIndexType indexType);

    void endTessellating();

    void flush();

    @Override
    default void close() {
        this.flush();
    }
}
