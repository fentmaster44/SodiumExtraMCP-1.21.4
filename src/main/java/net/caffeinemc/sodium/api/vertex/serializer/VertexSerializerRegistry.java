package net.caffeinemc.sodium.api.vertex.serializer;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.sodium.client.render.vertex.serializers.VertexSerializerRegistryImpl;

public interface VertexSerializerRegistry {
    VertexSerializerRegistry INSTANCE = new VertexSerializerRegistryImpl();

    static VertexSerializerRegistry instance() {
        return INSTANCE;
    }

    VertexSerializer get(VertexFormat srcFormat, VertexFormat dstFormat);

    void registerSerializer(VertexFormat srcFormat, VertexFormat dstFormat, VertexSerializer serializer);
}
