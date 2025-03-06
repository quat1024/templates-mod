package io.github.cottonmc.templates;

import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class TemplatesModelProvider implements ModelLoadingPlugin {
	private final Map<Identifier, UnbakedModel> models = new HashMap<>();
	private final Map<ModelIdentifier, Identifier> itemAssignments = new HashMap<>();
	
	private volatile TemplateAppearanceManager appearanceManager;
	
	/// fabric model loading plugin
	
	@Override
	public void onInitializeModelLoader(Context ctx) {
		//Dump TAM cache while we're at it
		dumpCache();
		
		//Ensure all special models are referenced.
		//Fixes weird problems I was having with the Post. Its item model is based off of
		//`templates:models/block/fence_post_inventory` which is not referenced by anything else.
		//Without this line, the Post model was kinda working (it looked like a post in my hand)
		//but it had default transforms instead of block/block. But other models like the fencegate
		//model, which are retextured versions of vanilla inventory models, had correct transforms.
		//Very surprising, don't really understand it. Feels almost like a fabric bug
		ctx.addModels(models.keySet());
		
		//Register a model resolver for loading blockmodels; returning null if we don't load
		//a particular blockmodel is the correct course of action.
		ctx.resolveModel().register(rCtx -> models.get(rCtx.id()));
		
		//Swap out the item models. Here we have to return the original model if we're not
		//interested in swapping a particular item model.
		ctx.modifyModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
			@SuppressWarnings("SuspiciousMethodCalls") //modelidentifier is a subtype of identifier
			Identifier modelId = itemAssignments.get(context.id());
			if(modelId == null) return model;
			
			UnbakedModel base = models.get(modelId);
			if(base == null) return model;
			
			return base;
		});
	}
	
	/// template appearance manager cache
	
	public TemplateAppearanceManager getOrCreateTemplateApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		//This is kind of needlessly sketchy using the "volatile double checked locking" pattern.
		//I'd like all template models to use the same TemplateApperanceManager, despite the model
		//baking process happening concurrently on several threads, but I also don't want to
		//hold up the model baking process too long.
		
		//Volatile field read:
		TemplateAppearanceManager read = appearanceManager;
		
		if(read == null) {
			//Acquire a lock:
			synchronized(this) {
				//There's a chance another thread just initialized the object and released the lock
				//while we were waiting for it, so we do another volatile field read (the "double check"):
				read = appearanceManager;
				if(read == null) {
					//If no-one has initialized it still, I guess it falls to us
					read = appearanceManager = new TemplateAppearanceManager(spriteLookup);
				}
			}
		}
		
		return Objects.requireNonNull(read);
	}
	
	public void dumpCache() {
		appearanceManager = null; //volatile write
	}
	
	public void addTemplateModel(Identifier id, UnbakedModel unbaked) {
		models.put(id, unbaked);
	}
	
	public void assignItemModel(Identifier templateModelId, ModelIdentifier... modelIds) {
		for(ModelIdentifier modelId : modelIds) itemAssignments.put(modelId, templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, Identifier... itemIds) {
		for(Identifier itemId : itemIds) itemAssignments.put(new ModelIdentifier(itemId, "inventory"), templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, ItemConvertible... itemConvs) {
		for(ItemConvertible itemConv : itemConvs) assignItemModel(templateModelId, Registries.ITEM.getId(itemConv.asItem()));
	}
}
