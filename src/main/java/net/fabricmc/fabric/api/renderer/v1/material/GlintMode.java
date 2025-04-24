/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.renderer.v1.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.item.ItemStackRenderState;

/**
 * Controls how glint should be applied.
 */
public enum GlintMode {
	/**
	 * Use the glint {@linkplain ItemStackRenderState.LayerRenderState#setFoilType(ItemStackRenderState.FoilType)} set in the layer.
	 */
	DEFAULT(null),
	NONE(ItemStackRenderState.FoilType.NONE),
	STANDARD(ItemStackRenderState.FoilType.STANDARD),
	SPECIAL(ItemStackRenderState.FoilType.SPECIAL);

	@Nullable
	public final ItemStackRenderState.FoilType glint;

	GlintMode(@Nullable ItemStackRenderState.FoilType glint) {
		this.glint = glint;
	}

	public static GlintMode fromGlint(@Nullable ItemStackRenderState.FoilType glint) {
		return switch (glint) {
		case null -> DEFAULT;
		case NONE -> NONE;
		case STANDARD -> STANDARD;
		case SPECIAL -> SPECIAL;
		};
	}
}
