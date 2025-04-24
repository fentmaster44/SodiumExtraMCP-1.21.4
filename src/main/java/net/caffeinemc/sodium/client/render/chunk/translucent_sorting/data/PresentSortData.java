package net.caffeinemc.sodium.client.render.chunk.translucent_sorting.data;

import net.caffeinemc.sodium.client.util.NativeBuffer;

import java.nio.IntBuffer;

public interface PresentSortData {
    NativeBuffer getIndexBuffer();

    default IntBuffer getIntBuffer() {
        return this.getIndexBuffer().getDirectBuffer().asIntBuffer();
    }
}
