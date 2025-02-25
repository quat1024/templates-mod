package io.github.cottonmc.templates.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.cottonmc.templates.util.TntEntityExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TntEntityRenderer.class)
public class TntEntityRendererMixin {
	@WrapOperation(
		method = "render",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;")
	)
	public BlockState templates$modifyBlockState(Block b, Operation<BlockState> original, TntEntity ent) {
		BlockState masked = ((TntEntityExt) ent).templates$getRenderBlockState();
		if(masked != null && !masked.isAir()) return masked;
		else return original.call(b);
	}
}
