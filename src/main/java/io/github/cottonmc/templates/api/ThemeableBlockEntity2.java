package io.github.cottonmc.templates.api;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.block.BlockState;

public interface ThemeableBlockEntity2 extends RenderDataBlockEntity {
	default BlockState getThemeState() {
		return (BlockState) getRenderData();
	}
}
