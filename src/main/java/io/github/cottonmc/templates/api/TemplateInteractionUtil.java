package io.github.cottonmc.templates.api;

import io.github.cottonmc.templates.block.TemplateEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//For an example of how to use this class, have a look at TemplateBlock.
//Basically there are several methods that would like to modify the return value of something.
public class TemplateInteractionUtil {
	public static final BooleanProperty LIGHT = BooleanProperty.of("templates_light");
	
	public static StateManager.Builder<Block, BlockState> appendProperties(StateManager.Builder<Block, BlockState> builder) {
		return builder.add(LIGHT);
	}
	
	//Use this to obtain a Block.Settings that'll make your Template act like the ones in the mod.
	//(To complete the look, don't forget to tag your blocks with mineable/axe.)
	private static final AbstractBlock.ContextPredicate NOPE = (blah, blahdey, blahh) -> false;
	public static AbstractBlock.Settings configureSettings(AbstractBlock.Settings s) {
		return s.luminance(TemplateInteractionUtil::luminance).nonOpaque().sounds(BlockSoundGroup.WOOD).hardness(0.2f).suffocates(NOPE).blockVision(NOPE);
	}
	
	//And if you don't have a Block.Settings to copy off of.
	public static AbstractBlock.Settings makeSettings() {
		return configureSettings(AbstractBlock.Settings.create());
	}
	
	public static BlockState setDefaultStates(BlockState in) {
		if(in.contains(LIGHT)) in = in.with(LIGHT, false);
		return in;
	}
	
	public static @Nullable BlockState modifyPlacementState(@Nullable BlockState in, ItemPlacementContext ctx) {
		return TemplateEntity.weirdNbtLightLevelStuff(in, ctx.getStack());
	}
	
	public static ItemActionResult onUseWithItem(ItemStack held, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!(world.getBlockEntity(pos) instanceof TemplateEntity be)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if(!player.canModifyBlocks() || !world.canPlayerModifyAt(player, pos)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		
		TemplateInteractionUtilExt ext = state.getBlock() instanceof TemplateInteractionUtilExt e ? e : TemplateInteractionUtilExt.Default.INSTANCE;
		
		//Glowstone
		if(state.contains(LIGHT) && held.getItem() == Items.GLOWSTONE_DUST && !state.get(LIGHT) && !be.hasSpentGlowstoneDust()) {
			world.setBlockState(pos, state.with(LIGHT, true));
			be.spentGlowstoneDust();
			
			if(!player.isCreative()) held.decrement(1);
			world.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1f, 1f);
			return ItemActionResult.success(world.isClient);
		}
		
		//Redstone
		if(held.getItem() == Blocks.REDSTONE_TORCH.asItem() &&
			!be.emitsRedstone() &&
			!be.hasSpentRedstoneTorch() &&
			ext.templatesPlayerCanAddRedstoneEmission(state, world, pos)
		) {
			be.setEmitsRedstone(true);
			be.spentRedstoneTorch();
			
			if(!player.isCreative()) held.decrement(1);
			world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
			return ItemActionResult.success(world.isClient);
		}
		
		//Popped chorus fruit
		if(held.getItem() == Items.POPPED_CHORUS_FRUIT &&
			be.isSolid() &&
			!be.hasSpentPoppedChorus() &&
			ext.templatesPlayerCanRemoveCollision(state, world, pos)
		) {
			be.setSolidity(false);
			be.spentPoppedChorus();
			
			if(!player.isCreative()) held.decrement(1);
			world.playSound(player, pos, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
			return ItemActionResult.success(world.isClient);
		}
		
		//Changing the theme
		if(held.getItem() instanceof BlockItem bi && be.getThemeState().getBlock() == Blocks.AIR) {
			Block block = bi.getBlock();
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placementState = block.getPlacementState(ctx);
			if(placementState != null && Block.isShapeFullCube(placementState.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
				if(!world.isClient) be.setRenderedState(placementState);
				
				world.setBlockState(pos, state.with(LIGHT, be.hasSpentGlowstoneDust() || (placementState.getLuminance() != 0)));
				be.setEmitsRedstone(be.hasSpentRedstoneTorch() || placementState.getWeakRedstonePower(world, pos, Direction.NORTH) != 0);
				
				if(!player.isCreative()) held.decrement(1);
				world.playSound(player, pos, placementState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1.1f);
				return ItemActionResult.success(world.isClient);
			}
		}
		
		return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	@Deprecated(forRemoval = true)
	public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack held = player.getStackInHand(hand);
		return onUseWithItem(held, state, world, pos, player, hand, hit).toActionResult();
	}
	
	//Maybe an odd spot to put this logic but it's consistent w/ vanilla chests, barrels, etc
	public static void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(!state.isOf(newState.getBlock()) &&
			world.getBlockEntity(pos) instanceof TemplateEntity template &&
			world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
		) {
			DefaultedList<ItemStack> drops = DefaultedList.of();
			
			//TODO: remember the specific ItemStack
			Block theme = template.getThemeState().getBlock();
			if(theme != Blocks.AIR) drops.add(new ItemStack(theme));
			
			if(template.hasSpentRedstoneTorch()) drops.add(new ItemStack(Items.REDSTONE_TORCH));
			if(template.hasSpentGlowstoneDust()) drops.add(new ItemStack(Items.GLOWSTONE_DUST));
			if(template.hasSpentPoppedChorus()) drops.add(new ItemStack(Items.POPPED_CHORUS_FRUIT));
			
			ItemScatterer.spawn(world, pos, drops);
		}
	}
	
	public static void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		//Load the BlockEntityTag clientside, which fixes the template briefly showing its default state when placing it.
		//I'm surprised this doesn't happen by default; the BlockEntityTag stuff is only done serverside.
		if(
			world.isClient &&
			world.getBlockEntity(pos) instanceof TemplateEntity te &&
			stack.getItem() instanceof BlockItem &&
			placer instanceof PlayerEntity
		) {
			//BlockItem.writeNbtToBlockEntity(world, player, pos, stack); //checks MinecraftServer != null, rah rah singleplayer
			NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
			if(!nbtComponent.isEmpty()) nbtComponent.applyToBlockEntity(te, world.getRegistryManager());
		}
	}
	
	//Returns "null" to signal "no opinion". Imagine it like an InteractionResult.PASS.
	public static @Nullable VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return view.getBlockEntity(pos) instanceof TemplateEntity be && !be.isSolid() ? VoxelShapes.empty() : null;
	}
	
	public static boolean emitsRedstonePower(BlockState state) {
		//return state.contains(REDSTONE) ? state.get(REDSTONE) : false;
		return false; //TODO, not available after punting this to BlockEntity. Yarn makes this method sound more important than it is, it's just for dust redirection.
	}
	
	public static int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof TemplateEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	public static int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof TemplateEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	public static int luminance(BlockState state) {
		return state.contains(LIGHT) && state.get(LIGHT) ? 15 : 0;
	}
	
	/** FabricBlock hook used by CTM mods */
	public static BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
		BlockState renderState = null;
		//See docs for FabricBlock
		if(renderView instanceof ServerWorld sworld && sworld.getBlockEntity(pos) instanceof ThemeableBlockEntity2 te) {
			renderState = te.getThemeState();
		} else if(renderView.getBlockEntityRenderData(pos) instanceof BlockState theme) { //FabricBlockView itf-inject
			renderState = theme;
		}
		
		if(renderState == null || renderState.isAir() || renderState.getBlock() instanceof BlockEntityProvider) {
			return state; //no change.
			//We ignore BlockEntityProviders just in case someone somehow stuffs a template in a template.
		}
		
		//delegate
		return renderState.getAppearance(renderView, pos, side, sourceState, sourcePos);
	}
}
