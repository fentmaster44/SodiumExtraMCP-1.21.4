package net.caffeinemc.sodium.client.gl.sync;

import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class GlFence {
    private final long id;
    private boolean disposed;

    public GlFence(long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        this.checkDisposed();

        int result;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer count = stack.callocInt(1);
            result = GL32C.glGetSynci(this.id, GL32C.GL_SYNC_STATUS, count);

            if (count.get(0) != 1) {
                throw new RuntimeException("glGetSync returned more than one value");
            }
        }

        return result == GL32C.GL_SIGNALED;
    }

    public void sync() {
        this.checkDisposed();
        this.sync(Long.MAX_VALUE);
    }

    public void sync(long timeout) {
        this.checkDisposed();
        GL32C.glWaitSync(this.id, GL32C.GL_SYNC_FLUSH_COMMANDS_BIT, timeout);
    }

    public void delete() {
        GL32C.glDeleteSync(this.id);
        this.disposed = true;
    }

    private void checkDisposed() {
        if (this.disposed) {
            throw new IllegalStateException("Fence object has been disposed");
        }
    }
}
