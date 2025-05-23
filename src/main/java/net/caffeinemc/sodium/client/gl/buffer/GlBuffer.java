package net.caffeinemc.sodium.client.gl.buffer;

import net.caffeinemc.sodium.client.gl.GlObject;
import org.lwjgl.opengl.GL20C;

public abstract class GlBuffer extends GlObject {
    private GlBufferMapping activeMapping;

    protected GlBuffer() {
        this.setHandle(GL20C.glGenBuffers());
    }

    public GlBufferMapping getActiveMapping() {
        return this.activeMapping;
    }

    public void setActiveMapping(GlBufferMapping mapping) {
        this.activeMapping = mapping;
    }
}
