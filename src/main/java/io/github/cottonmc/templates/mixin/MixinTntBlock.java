package io.github.cottonmc.templates.mixin;

import io.github.cottonmc.templates.api.ThemeableBlockEntity;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TntBlock.class)
public class MixinTntBlock {
	@Inject(
			method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V",
			at = @At(value = "INVOKE", target = "net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION
	)
	private static void handleTemplateTnt(World world, BlockPos pos, @Nullable LivingEntity living, CallbackInfo info, TntEntity tnt) {
		if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity be) {
			tnt.method_54455(be.getThemeState());
		}
	}
}
