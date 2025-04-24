package net.caffeinemc.sodium.client.services;

import net.caffeinemc.sodium.fabric.FabricMixinOverrides;

import java.util.List;

public interface PlatformMixinOverrides {
    PlatformMixinOverrides INSTANCE = new FabricMixinOverrides();

    static PlatformMixinOverrides getInstance() {
        return INSTANCE;
    }

    List<MixinOverride> applyModOverrides();

    record MixinOverride(String modId, String option, boolean enabled) {

    }
}
