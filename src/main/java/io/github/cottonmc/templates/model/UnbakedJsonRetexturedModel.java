package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class UnbakedJsonRetexturedModel extends TemplateUnbakedModel {
	public UnbakedJsonRetexturedModel(Identifier parent) {
		super(parent);
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, BakedModel wrappedModel, TemplateAppearanceManager tam) {
		//lookup special sprites
		Sprite[] specialSprites = new Sprite[6];
		for(int i = 0; i < 6; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Templates.id("templates_special/" + Direction.values()[i].getName()));
			specialSprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}
		
		return new RetexturingBakedModel(wrappedModel, convertModel(specialSprites, wrappedModel, modelBakeSettings), tam, modelBakeSettings, ao);
	}
	
	protected Mesh convertModel(Sprite[] specialSprites, BakedModel wrapped, ModelBakeSettings modelBakeSettings) {
		Renderer r = TemplatesClientApi.getInstance().getFabricRenderer();
		MeshBuilder builder = r.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		RenderMaterial mat = r.materialFinder().find();
		
		Random rand = Random.create(42);
		
		//TODO: is this the right place for facePermutation? Permuting faces here fixes textures on like, fence_side and wall_side
		// However the reason we need to permute those is because they're used as part of a multipart model, not really
		// because they're an unbakedjsonretexturedmodel.
		// Arguably you are using an ujrm because you care about the face permutation in the first place
		Map<Direction, Direction> facePermutation = MeshTransformUtil.facePermutation(modelBakeSettings);
		
		for(Direction cullFace : RetexturingBakedModel.DIRECTIONS_AND_NULL) {
			for(BakedQuad quad : wrapped.getQuads(null, cullFace, rand)) {
				emitter.fromVanilla(quad, mat, cullFace);
				
				QuadUvBounds bounds = QuadUvBounds.read(emitter);
				for(int i = 0; i < specialSprites.length; i++) {
					if(bounds.displaysSprite(specialSprites[i])) {
						bounds.normalizeUv(emitter, specialSprites[i]);
						
						Direction dir = RetexturingBakedModel.DIRECTIONS[i];
						Direction permutedDir = facePermutation.get(dir);
						emitter.tag(TagPacker.withDir(0, permutedDir));
						break;
					}
				}
				
				emitter.emit();
			}
		}
		
		return builder.build();
	}
}
