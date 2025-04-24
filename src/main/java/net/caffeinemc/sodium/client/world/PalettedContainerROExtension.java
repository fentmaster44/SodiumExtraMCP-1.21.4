package net.caffeinemc.sodium.client.world;

import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface PalettedContainerROExtension<T> {
    @SuppressWarnings("unchecked")
    static <T> PalettedContainerROExtension<T> of(PalettedContainerRO<T> container) {
        return (PalettedContainerROExtension<T>) container;
    }

    static <T> PalettedContainerRO<T> clone(PalettedContainerRO<T> container) {
        if (container == null) {
            return null;
        }

        return of(container).sodium$copy();
    }

    void unpack(T[] values);
    void unpack(T[] values, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    PalettedContainerRO<T> sodium$copy();
}
