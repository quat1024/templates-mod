package io.github.cottonmc.templates.block;

import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TemplateButtonBlock extends ButtonBlock implements BlockEntityProvider {
	public TemplateButtonBlock(Settings settings) {
		this(BlockSetType.OAK, 30, settings);
	}
	
	public TemplateButtonBlock(BlockSetType blockSetType, int i, Settings settings) {
		super(blockSetType, i, settings);
		setDefaultState(TemplateInteractionUtil.setDefaultStates(getDefaultState()));
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return Templates.TEMPLATE_BLOCK_ENTITY.instantiate(pos, state);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(TemplateInteractionUtil.appendProperties(builder));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return TemplateInteractionUtil.modifyPlacementState(super.getPlacementState(ctx), ctx);
	}
	
	@Override
	protected ItemActionResult onUseWithItem(ItemStack held, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemActionResult r = TemplateInteractionUtil.onUseWithItem(held, state, world, pos, player, hand, hit);
		if(!r.isAccepted()) r = super.onUseWithItem(held, state, world, pos, player, hand, hit);
		return r;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		TemplateInteractionUtil.onStateReplaced(state, world, pos, newState, moved);
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TemplateInteractionUtil.onPlaced(world, pos, state, placer, stack);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(TemplateInteractionUtil.getCollisionShape(state, view, pos, ctx), super.getCollisionShape(state, view, pos, ctx));
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		//return TemplateInteractionUtil.emitsRedstonePower(state);
		return super.emitsRedstonePower(state);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		boolean a = 0 != super.getWeakRedstonePower(state, view, pos, dir);
		boolean b = 0 != TemplateInteractionUtil.getWeakRedstonePower(state, view, pos, dir);
		return (a ^ b) ? 15 : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		if(getDirection(state) != dir) return 0;
		else return getWeakRedstonePower(state, view, pos, dir);
	}
	
	//from FabricBlock
	@Override
	public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
		return TemplateInteractionUtil.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
	}
}
