package net.caffeinemc.sodium.client.world;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BiomeSeedProvider {
    @Contract(pure = true)
    static long getBiomeZoomSeed(@NotNull ClientLevel level) {
        return level.getBiomeZoomSeed();
    }

    long sodium$getBiomeZoomSeed();
}
