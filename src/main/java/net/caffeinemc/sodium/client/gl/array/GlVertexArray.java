package net.caffeinemc.sodium.client.gl.array;

import net.caffeinemc.sodium.client.gl.GlObject;
import org.lwjgl.opengl.GL30C;

/**
 * Provides Vertex Array functionality on supported platforms.
 */
public class GlVertexArray extends GlObject {
    public static final int NULL_ARRAY_ID = 0;

    public GlVertexArray() {
        this.setHandle(GL30C.glGenVertexArrays());
    }
}
