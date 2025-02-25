package io.github.cottonmc.templates.util;

import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

public interface TntEntityExt {
	void templates$setRenderBlockState(BlockState state);
	@Nullable BlockState templates$getRenderBlockState();
}
