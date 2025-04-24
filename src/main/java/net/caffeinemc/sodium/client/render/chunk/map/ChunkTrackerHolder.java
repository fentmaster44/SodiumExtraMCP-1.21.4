package net.caffeinemc.sodium.client.render.chunk.map;

import net.minecraft.client.multiplayer.ClientLevel;

public interface ChunkTrackerHolder {
    static ChunkTracker get(ClientLevel level) {
        return level.getChunkTracker();
    }
}
