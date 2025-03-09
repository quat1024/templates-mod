package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class TemplateUnbakedModel implements UnbakedModel, TemplatesClientApi.TweakableUnbakedModel {
	public TemplateUnbakedModel(Identifier parent) {
		this.parent = parent;
	}
	
	protected final Identifier parent;
	protected boolean ao = true;
	
	@Override
	public TemplateUnbakedModel disableAo() {
		ao = false;
		return this;
	}
	
	//Reinitializing the unbakedmodel.
	//TODO TODO TODO take this stuff as a constructor argument and use a "context -> unbakedmodel" factory style api,
	// instead of having you register unbakedmodel objects which stick around forever.
	
	protected Function<Function<SpriteIdentifier, Sprite>, TemplateAppearanceManager> tamGetter = null;
	
	public void reinit(ReinitContext ctx) {
		this.tamGetter = ctx::getTAM;
	}
	
	public interface ReinitContext {
		TemplateAppearanceManager getTAM(Function<SpriteIdentifier, Sprite> spriteLookup);
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings) {
		BakedModel wrapped = baker.bake(parent, modelBakeSettings);
		TemplateAppearanceManager tam = tamGetter.apply(spriteLookup);
		
		return bake(baker, spriteLookup, modelBakeSettings, wrapped, tam);
	}
	
	public abstract BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, BakedModel wrappedModel, TemplateAppearanceManager tam);
	
	@Override
	public Collection<Identifier> getModelDependencies() {
		return List.of(parent);
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		function.apply(parent).setParents(function);
	}
}
