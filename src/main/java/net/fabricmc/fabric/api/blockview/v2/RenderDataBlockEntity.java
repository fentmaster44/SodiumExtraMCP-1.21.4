package net.fabricmc.fabric.api.blockview.v2;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface RenderDataBlockEntity {
	/**
	 * Gets the render data provided by this block entity. The returned object must be safe to
	 * use in a multithreaded environment.
	 *
	 * <p>Note: <b>This method should not be called directly</b>; use
	 * {@link FabricBlockView#getBlockEntityRenderData(BlockPos)} instead. Only call this
	 * method when the result is used to implement
	 * {@link FabricBlockView#getBlockEntityRenderData(BlockPos)}.
	 *
	 * @return the render data
	 * @see FabricBlockView#getBlockEntityRenderData(BlockPos)
	 */
	@Nullable
	default Object getRenderData() {
		return null;
	}
}