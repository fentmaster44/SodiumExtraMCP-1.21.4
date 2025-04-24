package net.caffeinemc.sodium.fabric.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.sodium.client.services.PlatformLevelRenderHooks;
import net.caffeinemc.sodium.client.world.LevelSlice;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class FabricLevelRenderHooks implements PlatformLevelRenderHooks {
    @Override
    public void runChunkLayerEvents(RenderType renderLayer, Level level, LevelRenderer levelRenderer, Matrix4f modelMatrix, Matrix4f projectionMatrix, int ticks, Camera mainCamera, Frustum cullingFrustum) {

    }

    @Override
    public List<?> retrieveChunkMeshAppenders(Level level, BlockPos origin) {
        return List.of();
    }

    @Override
    public void runChunkMeshAppenders(List<?> renderers, Function<RenderType, VertexConsumer> typeToConsumer, LevelSlice slice) {

    }
}