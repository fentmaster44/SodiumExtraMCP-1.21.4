package net.caffeinemc.sodium.client.util.collections;

import org.jetbrains.annotations.Nullable;

public interface ReadQueue<E> {
    @Nullable E dequeue();
}
