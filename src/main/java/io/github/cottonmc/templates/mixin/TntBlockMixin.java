package io.github.cottonmc.templates.mixin;

import io.github.cottonmc.templates.TntStuff;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TntBlock.class)
public class TntBlockMixin {
	//This mixin by LemmaEOF !
	@Inject(
		method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
		locals = LocalCapture.CAPTURE_FAILSOFT //not a big deal
	)
	private static void templates$primeTnt(World world, BlockPos pos, @Nullable LivingEntity living, CallbackInfo ci, TntEntity tnt) {
		TntStuff.onTntEsplod(world, pos, tnt);
	}
}
