package io.github.cottonmc.templates.mixin;

import io.github.cottonmc.templates.util.TntEntityExt;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntity.class)
public class TntEntityMixin implements TntEntityExt {
	//lets hope and pray i dont explode the data tracker
	@Unique private static final TrackedData<BlockState> APPEARANCE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
	
	@SuppressWarnings("DataFlowIssue")
	@Unique private TntEntity This() {
		return (TntEntity) (Object) this;
	}
	
	@Inject(method = "initDataTracker", at = @At("TAIL"))
	protected void templates$initDataTracker(CallbackInfo ci) {
		This().getDataTracker().startTracking(APPEARANCE, Blocks.AIR.getDefaultState());
	}
	
	@Override
	public void templates$setRenderBlockState(BlockState state) {
		This().getDataTracker().set(APPEARANCE, state);
	}
	
	@Override
	public @Nullable BlockState templates$getRenderBlockState() {
		return This().getDataTracker().get(APPEARANCE);
	}
}
