package net.caffeinemc.sodium.client.gl.tessellation;

import net.caffeinemc.sodium.client.gl.device.CommandList;

public interface GlTessellation {
    void delete(CommandList commandList);

    void bind(CommandList commandList);

    void unbind(CommandList commandList);

    GlPrimitiveType getPrimitiveType();
}
