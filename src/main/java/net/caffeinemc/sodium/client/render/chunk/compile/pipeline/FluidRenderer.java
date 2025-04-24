package net.caffeinemc.sodium.client.render.chunk.compile.pipeline;

import net.caffeinemc.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public abstract class FluidRenderer {
    public abstract void render(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers);
}
