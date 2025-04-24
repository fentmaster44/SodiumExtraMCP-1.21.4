package me.flashyreese.mods.sodiumextra.client;

import com.google.common.collect.EvictingQueue;
import lombok.val;
import net.minecraft.client.Minecraft;

import java.util.Comparator;
import java.util.Queue;

public class ClientTickHandler {
    private final Queue<Integer> fpsQueue = EvictingQueue.create(200);
    private int averageFps, lowestFps, highestFps;

    public void onClientTick(Minecraft client) {
        val currentFPS = Minecraft.getInstance().getFps();
        this.fpsQueue.add(currentFPS);
        this.averageFps = (int) this.fpsQueue.stream().mapToInt(Integer::intValue).average().orElse(0);
        this.lowestFps = this.fpsQueue.stream().min(Comparator.comparingInt(e -> e)).orElse(0);
        this.highestFps = this.fpsQueue.stream().max(Comparator.comparingInt(e -> e)).orElse(0);
    }

    public int getAverageFps() {
        return this.averageFps;
    }

    public int getLowestFps() {
        return this.lowestFps;
    }

    public int getHighestFps() {
        return this.highestFps;
    }
}
