package net.caffeinemc.sodium.client.render.texture;

import net.caffeinemc.sodium.api.texture.SpriteUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpriteUtilImpl implements SpriteUtil {
    @Override
    public void markSpriteActive(@NotNull TextureAtlasSprite sprite) {
        Objects.requireNonNull(sprite);
        sprite.contents().setActive(true);
    }

    @Override
    public boolean hasAnimation(@NotNull TextureAtlasSprite sprite) {
        Objects.requireNonNull(sprite);

        return sprite.contents().hasAnimation();
    }
}