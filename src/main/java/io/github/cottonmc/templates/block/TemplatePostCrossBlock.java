package io.github.cottonmc.templates.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class TemplatePostCrossBlock extends TemplatePostBlock {
	public TemplatePostCrossBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState sup = super.getPlacementState(ctx);
		if(sup != null) sup = sup.with(Properties.AXIS, ctx.getPlayerLookDirection().getAxis());
		return sup;
	}
	
	protected static final VoxelShape SHAPE_CROSS_X = VoxelShapes.union(SHAPE_Y, SHAPE_Z);
	protected static final VoxelShape SHAPE_CROSS_Y = VoxelShapes.union(SHAPE_X, SHAPE_Z);
	protected static final VoxelShape SHAPE_CROSS_Z = VoxelShapes.union(SHAPE_X, SHAPE_Y);
	
	protected VoxelShape shap(BlockState state) {
		return switch(state.get(Properties.AXIS)) {
			case X -> SHAPE_CROSS_X;
			case Y -> SHAPE_CROSS_Y;
			case Z -> SHAPE_CROSS_Z;
		};
	}
}
