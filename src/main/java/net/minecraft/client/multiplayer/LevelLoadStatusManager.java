package net.minecraft.client.multiplayer;

import lombok.val;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LevelLoadStatusManager {
    private final LocalPlayer player;
    private final ClientLevel level;
    private final LevelRenderer levelRenderer;
    private LevelLoadStatusManager.Status status = LevelLoadStatusManager.Status.WAITING_FOR_SERVER;

    public LevelLoadStatusManager(LocalPlayer p_312813_, ClientLevel p_310113_, LevelRenderer p_311686_) {
        this.player = p_312813_;
        this.level = p_310113_;
        this.levelRenderer = p_311686_;
    }

    public void tick() {
        switch (this.status) {
            case WAITING_FOR_PLAYER_CHUNK:
                // Sodium
                // Ensure the "eye" position (which the chunk rendering code is actually concerned about) is used instead of
                // the "feet" position. This solves a problem where the loading screen can become stuck waiting for the chunk
                // at the player's feet to load, when it is determined to not be visible due to the true location of the
                // player's eyes.
                val blockpos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());

                boolean flag = this.level.isOutsideBuildHeight(blockpos.getY());
                if (flag || this.levelRenderer.isSectionCompiled(blockpos) || this.player.isSpectator() || !this.player.isAlive()) {
                    this.status = LevelLoadStatusManager.Status.LEVEL_READY;
                }
            case WAITING_FOR_SERVER:
            case LEVEL_READY:
        }
    }

    public boolean levelReady() {
        return this.status == LevelLoadStatusManager.Status.LEVEL_READY;
    }

    public void loadingPacketsReceived() {
        if (this.status == LevelLoadStatusManager.Status.WAITING_FOR_SERVER) {
            this.status = LevelLoadStatusManager.Status.WAITING_FOR_PLAYER_CHUNK;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum Status {
        WAITING_FOR_SERVER,
        WAITING_FOR_PLAYER_CHUNK,
        LEVEL_READY;
    }
}