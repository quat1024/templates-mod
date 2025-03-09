package io.github.cottonmc.templates.model;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.function.Function;

public class UnbakedAutoRetexturedModel extends TemplateUnbakedModel {
	public UnbakedAutoRetexturedModel(Identifier parent) {
		super(parent);
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, BakedModel wrappedModel, TemplateAppearanceManager tam) {
		return new RetexturingBakedModel(wrappedModel, convertModel(wrappedModel), tam, modelBakeSettings, ao);
	}
	
	protected Mesh convertModel(BakedModel wrapped) {
		Renderer r = TemplatesClientApi.getInstance().getFabricRenderer();
		MeshBuilder builder = r.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		RenderMaterial mat = r.materialFinder().find();
		
		Random rand = Random.create(42);
		
		for(Direction cullFace : RetexturingBakedModel.DIRECTIONS_AND_NULL) {
			for(BakedQuad quad : wrapped.getQuads(null, cullFace, rand)) {
				emitter.fromVanilla(quad, mat, cullFace);
				QuadUvBounds.read(emitter).normalizeUv(emitter, quad.getSprite());
				emitter.tag(emitter.lightFace().ordinal() + 1);
				emitter.emit();
			}
		}
		
		return builder.build();
	}
}
