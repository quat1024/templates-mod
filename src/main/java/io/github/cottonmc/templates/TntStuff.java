package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.ThemeableBlockEntity2;
import net.minecraft.block.BlockState;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TntStuff {
	//honestly, just here so i can put a breakpoint on it
	public static void onTntEsplod(World world, BlockPos pos, TntEntity tnt) {
		if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity2 be) {
			BlockState theme = be.getThemeState();
			if(theme != null && !theme.isAir()) tnt.setBlockState(theme);
		}
	}
}
