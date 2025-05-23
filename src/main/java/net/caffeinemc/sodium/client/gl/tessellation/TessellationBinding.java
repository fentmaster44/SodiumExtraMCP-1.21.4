package net.caffeinemc.sodium.client.gl.tessellation;

import net.caffeinemc.sodium.client.gl.attribute.GlVertexAttributeBinding;
import net.caffeinemc.sodium.client.gl.buffer.GlBuffer;
import net.caffeinemc.sodium.client.gl.buffer.GlBufferTarget;

public record TessellationBinding(GlBufferTarget target,
                                  GlBuffer buffer,
                                  GlVertexAttributeBinding[] attributeBindings) {
    public static TessellationBinding forVertexBuffer(GlBuffer buffer, GlVertexAttributeBinding[] attributes) {
        return new TessellationBinding(GlBufferTarget.ARRAY_BUFFER, buffer, attributes);
    }

    public static TessellationBinding forElementBuffer(GlBuffer buffer) {
        return new TessellationBinding(GlBufferTarget.ELEMENT_BUFFER, buffer, new GlVertexAttributeBinding[0]);
    }
}
