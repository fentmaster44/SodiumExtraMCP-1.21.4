package net.caffeinemc.sodium.client.gl.functions;

import net.caffeinemc.sodium.client.gl.device.RenderDevice;

public class DeviceFunctions {
    private final BufferStorageFunctions bufferStorageFunctions;

    public DeviceFunctions(RenderDevice device) {
        this.bufferStorageFunctions = BufferStorageFunctions.pickBest(device);
    }

    public BufferStorageFunctions getBufferStorageFunctions() {
        return this.bufferStorageFunctions;
    }
}
