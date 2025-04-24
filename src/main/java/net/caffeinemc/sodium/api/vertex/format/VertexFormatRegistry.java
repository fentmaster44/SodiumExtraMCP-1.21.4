package net.caffeinemc.sodium.api.vertex.format;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.sodium.client.render.vertex.VertexFormatRegistryImpl;

public interface VertexFormatRegistry {
    VertexFormatRegistry INSTANCE = new VertexFormatRegistryImpl();

    static VertexFormatRegistry instance() {
        return INSTANCE;
    }

    int allocateGlobalId(VertexFormat format);
}