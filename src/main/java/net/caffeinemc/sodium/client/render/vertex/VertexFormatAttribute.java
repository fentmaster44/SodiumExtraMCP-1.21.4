package net.caffeinemc.sodium.client.render.vertex;

import net.caffeinemc.sodium.client.gl.attribute.GlVertexAttributeFormat;

public record VertexFormatAttribute(String name, GlVertexAttributeFormat format, int count, boolean normalized, boolean intType) {

}