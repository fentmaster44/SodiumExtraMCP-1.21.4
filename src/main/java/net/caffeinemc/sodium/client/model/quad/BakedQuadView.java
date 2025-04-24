package net.caffeinemc.sodium.client.model.quad;

import net.caffeinemc.sodium.client.model.quad.properties.ModelQuadFacing;

public interface BakedQuadView extends ModelQuadView {
    ModelQuadFacing getNormalFace();

    int getFaceNormal();

    boolean hasShade();

    boolean hasAO();
}
