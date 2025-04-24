package net.caffeinemc.sodium.client.compatibility.checks;

import net.caffeinemc.sodium.client.compatibility.environment.GlContextInfo;
import net.caffeinemc.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import net.caffeinemc.sodium.client.platform.NativeWindowHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs OpenGL driver validation after the game creates an OpenGL context. This runs immediately after OpenGL
 * context creation, and uses the implementation details of the OpenGL context to perform validation.
 */
public class PostLaunchChecks {
    private static final Logger LOGGER = LoggerFactory.getLogger("Sodium-PostlaunchChecks");

    public static void onContextInitialized(NativeWindowHandle window, GlContextInfo context) {
        GraphicsDriverChecks.postContextInit(window, context);
        NvidiaWorkarounds.applyContextChanges(context);

        // FIXME: This can be determined earlier, but we can't access the GUI classes in pre-launch
        if (isUsingPojavLauncher()) {
            throw new RuntimeException("It appears that you are using PojavLauncher, which is not supported when " +
                    "using Sodium. Please check your mods list.");
        }
    }

    // https://github.com/CaffeineMC/sodium/issues/1916
    private static boolean isUsingPojavLauncher() {
        if (System.getenv("POJAV_RENDERER") != null) {
            LOGGER.warn("Detected presence of environment variable POJAV_LAUNCHER, which seems to indicate we are running on Android");

            return true;
        }

        var librarySearchPaths = System.getProperty("java.library.path", null);

        if (librarySearchPaths != null) {
            for (var path : librarySearchPaths.split(":")) {
                if (isKnownAndroidPathFragment(path)) {
                    LOGGER.warn("Found a library search path which seems to be hosted in an Android filesystem: {}", path);

                    return true;
                }
            }
        }

        var workingDirectory = System.getProperty("user.home", null);

        if (workingDirectory != null) {
            if (isKnownAndroidPathFragment(workingDirectory)) {
                LOGGER.warn("Working directory seems to be hosted in an Android filesystem: {}", workingDirectory);
            }
        }

        return false;
    }

    private static boolean isKnownAndroidPathFragment(String path) {
        return path.matches("/data/user/[0-9]+/net\\.kdt\\.pojavlaunch");
    }
}
