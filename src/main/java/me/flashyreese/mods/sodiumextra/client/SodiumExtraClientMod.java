package me.flashyreese.mods.sodiumextra.client;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraHud;
import net.caffeinemc.sodium.client.services.PlatformRuntimeInformation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SodiumExtraClientMod {
    private static final ClientTickHandler clientTickHandler = new ClientTickHandler();

    private static SodiumExtraGameOptions CONFIG;
    private static Logger LOGGER;
    private static SodiumExtraHud hud;

    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LoggerFactory.getLogger("Sodium Extra");
        }

        return LOGGER;
    }

    public static SodiumExtraGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static ClientTickHandler getClientTickHandler() {
        return clientTickHandler;
    }

    private static SodiumExtraGameOptions loadConfig() {
        return SodiumExtraGameOptions.load(PlatformRuntimeInformation.getInstance().getConfigDirectory().resolve("sodium-extra-options.json").toFile());
    }

    public static void onTick(Minecraft client) {
        if (hud == null) {
            hud = new SodiumExtraHud();
        }

        clientTickHandler.onClientTick(client);
        hud.onStartTick(client);
    }

    public static void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (hud == null) {
            hud = new SodiumExtraHud();
        }

        hud.onHudRender(guiGraphics, deltaTracker);
    }
}
