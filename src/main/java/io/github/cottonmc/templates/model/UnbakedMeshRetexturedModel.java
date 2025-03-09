package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class UnbakedMeshRetexturedModel extends TemplateUnbakedModel {
	public UnbakedMeshRetexturedModel(Identifier parent, Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory) {
		super(parent);
		this.baseMeshFactory = baseMeshFactory;
	}
	
	protected final Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory;
	protected Mesh baseMesh;
	
	@Override
	public void reinit(ReinitContext ctx) {
		super.reinit(ctx);
		baseMesh = null;
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, BakedModel wrappedModel, TemplateAppearanceManager tam) {
		if(baseMesh == null) {
			baseMesh = baseMeshFactory.apply(spriteLookup);
		}
		Mesh transformedBaseMesh = MeshTransformUtil.pretransformMesh(baseMesh, MeshTransformUtil.applyAffine(modelBakeSettings));
		return new RetexturingBakedModel(wrappedModel, transformedBaseMesh, tam, modelBakeSettings, ao);
	}
}
