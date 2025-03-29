package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplateAppearance;
import io.github.cottonmc.templates.block.TemplateEntity;
import io.github.cottonmc.templates.mixin.MinecraftAccessor;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RetexturingBakedModel extends ForwardingBakedModel {
	public RetexturingBakedModel(BakedModel baseModel, Mesh baseMesh, TemplateAppearanceManager tam, ModelBakeSettings settings, boolean ao) {
		this.wrapped = baseModel; //field from the superclass; vanilla getQuads etc will delegate through to this
		this.baseMesh = baseMesh;
		this.tam = tam;
		this.uvlock = settings.isUvLocked();
		this.ao = ao;
	}
	
	protected final TemplateAppearanceManager tam;
	
	//the "retexturable mesh", quads with tag 0 emit as-is, quads with tags 1-6 get retextured
	protected final Mesh baseMesh;
	
	//pass MutableQuadView.BAKE_LOCK_UV as a bakeflag
	protected final boolean uvlock;
	
	//use an ambient-occlusion enabled material
	protected final boolean ao;
	
	//cache from template appearances ("blocks inside templates") to Mesh objects.
	//never invalidated until you F3+T
	protected final ConcurrentMap<TemplateAppearance, Mesh> retexturedMeshes = new ConcurrentHashMap<>();
	
	protected static final Direction[] DIRECTIONS = Direction.values();
	protected static final Direction[] DIRECTIONS_AND_NULL = new Direction[DIRECTIONS.length + 1];
	static { System.arraycopy(DIRECTIONS, 0, DIRECTIONS_AND_NULL, 0, DIRECTIONS.length); }
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		//We don't have access to the BlockRenderView at the moment, so return some junk.
		//Mixins will override this for common particle types (breaking, sprinting, etc)
		return tam.getDefaultAppearance().getSprite(Direction.UP);
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		//FabricBlockView itf-inject
		BlockState theme = blockView.getBlockEntityRenderData(pos) instanceof BlockState s ? s : null;
		if(theme == null || theme.isAir()) {
			getUntintedRetexturedMesh(tam.getDefaultAppearance()).outputTo(context.getEmitter());
			return;
		} else if(theme.getBlock() == Blocks.BARRIER) {
			return;
		}
		
		//getAppearance is an itf-inject from FabricBlock. Calling this allows my block to connect to others with connected textures.
		//See https://github.com/PepperCode1/Continuity/blob/c2b1d3cd085368ca360ecda991c1618545a11fc6/src/main/java/me/pepperbell/continuity/client/model/CtmBakedModel.java#L51-L66
		theme = theme.getAppearance(blockView, pos, Direction.DOWN, theme, pos);
		
		TemplateAppearance ta = tam.getAppearance(theme);
		Mesh untintedMesh = getUntintedRetexturedMesh(ta);
		
		//The specific tint might vary a lot (imagine grass color changing smoothly) so caching pre-tinted meshes is a waste of time.
		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, blockView, pos, 0);
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(context.getEmitter());
		} else {
			context.pushTransform(new TintingTransformer(ta, tint));
			untintedMesh.outputTo(context.getEmitter());
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it & look up the item color provider
		//none of this is accessible unless you're in creative mode doing ctrl-pick btw
		TemplateAppearance nbtAppearance;
		int tint;
		BlockState theme = TemplateEntity.readStateFromItem(stack);
		if(!theme.isAir()) {
			nbtAppearance = tam.getAppearance(theme);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).templates$getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
		} else {
			nbtAppearance = tam.getDefaultAppearance();
			tint = 0xFFFFFFFF;
		}
		
		Mesh untintedMesh = getUntintedRetexturedMesh(nbtAppearance);
		
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(context.getEmitter());
		} else {
			context.pushTransform(new TintingTransformer(nbtAppearance, tint));
			untintedMesh.outputTo(context.getEmitter());
			context.popTransform();
		}
	}
	
	protected Mesh getUntintedRetexturedMesh(TemplateAppearance ta) {
		return retexturedMeshes.computeIfAbsent(ta, this::createUntintedRetexturedMesh);
	}
	
	protected Mesh createUntintedRetexturedMesh(TemplateAppearance ta) {
		return MeshTransformUtil.pretransformMesh(baseMesh, new RetexturingTransformer(ta));
	}
	
	protected class RetexturingTransformer implements RenderContext.QuadTransform {
		protected RetexturingTransformer(TemplateAppearance ta) {
			this.ta = ta;
		}
		
		protected final TemplateAppearance ta;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			int tag = quad.tag();
			
			boolean useAo = switch(TagPacker.ao(tag)) {
				case TRUE -> true;
				case FALSE -> false;
				case DEFAULT -> ao; //from the model settings... TODO maybe deprecate that now that it can be per-quad
			};
			quad.material(ta.getRenderMaterial(useAo));
			
			@Nullable Direction dir = TagPacker.dir(tag);
			if(dir == null) return true; //Pass the quad through unmodified.
			
			quad.spriteBake(ta.getSprite(dir), MutableQuadView.BAKE_NORMALIZED | ta.getBakeFlags(dir) | (uvlock ? MutableQuadView.BAKE_LOCK_UV : 0));
			
			return true;
		}
	}
	
	@SuppressWarnings("ClassCanBeRecord")
	protected static class TintingTransformer implements RenderContext.QuadTransform {
		protected TintingTransformer(TemplateAppearance ta, int tint) {
			this.ta = ta;
			this.tint = tint;
		}
		
		protected final TemplateAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			int tag = quad.tag();
			@Nullable Direction dir = TagPacker.dir(tag);
			if(dir != null && ta.hasColor(dir)) quad.color(tint, tint, tint, tint);
			return true;
		}
	}
}
