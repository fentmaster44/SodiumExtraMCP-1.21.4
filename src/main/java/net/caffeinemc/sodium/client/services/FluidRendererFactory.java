package net.caffeinemc.sodium.client.services;

import net.caffeinemc.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.sodium.client.model.quad.blender.BlendedColorProvider;
import net.caffeinemc.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.sodium.fabric.render.FluidRendererImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface FluidRendererFactory {
    FluidRendererFactory INSTANCE = new FluidRendererImpl.FabricFactory();

    static FluidRendererFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new platform dependent fluid renderer.
     * @param colorRegistry The current color registry.
     * @param lightPipelineProvider The current {@code LightPipelineProvider}.
     * @return A new fluid renderer.
     */
    FluidRenderer createPlatformFluidRenderer(ColorProviderRegistry colorRegistry, LightPipelineProvider lightPipelineProvider);

    BlendedColorProvider<FluidState> getWaterColorProvider();

    BlendedColorProvider<BlockState> getWaterBlockColorProvider();
}
