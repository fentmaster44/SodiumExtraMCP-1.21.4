package net.caffeinemc.sodium.fabric;

import net.caffeinemc.sodium.client.services.PlatformRuntimeInformation;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public class FabricRuntimeInformation implements PlatformRuntimeInformation {

    @Override
    public boolean isDevelopmentEnvironment() {
        // todo
        return true;
    }

    @Override
    public Path getGameDirectory() {
        return Minecraft.getInstance().gameDirectory.toPath();
    }

    @Override
    public Path getConfigDirectory() {
        return getGameDirectory().resolve("sodium");
    }

    @Override
    public boolean platformHasEarlyLoadingScreen() {
        return false;
    }

    @Override
    public boolean platformUsesRefmap() {
        return true;
    }

    @Override
    public boolean isModInLoadingList(String modId) {
        /*todo return FabricLoader.getInstance().isModLoaded(modId);*/
        return false;
    }

    @Override
    public boolean usesAlphaMultiplication() {
        return false;
    }

}
