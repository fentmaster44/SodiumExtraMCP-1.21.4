package net.caffeinemc.sodium.client.render.chunk.compile.executor;

import net.caffeinemc.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.sodium.client.util.task.CancellationToken;

public interface ChunkJob extends CancellationToken {
    void execute(ChunkBuildContext context);

    boolean isStarted();

    int getEffort();
}
