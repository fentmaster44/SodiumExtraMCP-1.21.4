package net.caffeinemc.sodium.api.vertex.format.common;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.sodium.api.vertex.attributes.common.ColorAttribute;
import net.caffeinemc.sodium.api.vertex.attributes.common.LightAttribute;
import net.caffeinemc.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.sodium.api.vertex.attributes.common.TextureAttribute;

public final class ParticleVertex {
    public static final VertexFormat FORMAT = DefaultVertexFormat.PARTICLE;

    public static final int STRIDE = 28;

    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_TEXTURE = 12;
    private static final int OFFSET_COLOR = 20;
    private static final int OFFSET_LIGHT = 24;

    public static void put(long ptr,
                           float x, float y, float z, float u, float v, int color, int light) {
        PositionAttribute.put(ptr + OFFSET_POSITION, x, y, z);
        TextureAttribute.put(ptr + OFFSET_TEXTURE, u, v);
        ColorAttribute.set(ptr + OFFSET_COLOR, color);
        LightAttribute.set(ptr + OFFSET_LIGHT, light);
    }
}
