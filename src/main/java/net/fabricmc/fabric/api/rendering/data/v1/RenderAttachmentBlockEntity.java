package net.fabricmc.fabric.api.rendering.data.v1;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import org.jetbrains.annotations.Nullable;

@Deprecated
@FunctionalInterface
public interface RenderAttachmentBlockEntity {
	/**
	 * This method will be automatically called if {@link RenderDataBlockEntity#getRenderData()} is not overridden.
	 *
	 * @deprecated Use {@link RenderDataBlockEntity#getRenderData()} instead.
	 */
	@Deprecated
	@Nullable
	Object getRenderAttachmentData();
}
