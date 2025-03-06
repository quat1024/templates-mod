package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.gensupport.ItemOverrideMapping;
import io.github.cottonmc.templates.gensupport.MagicPaths;
import io.github.cottonmc.templates.gensupport.TemplateModelMapping;
import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * TODO ABI: rename to TemplatesModelLoadingPlugin or something
 *   Can probably use a redesign to better leverage the capabilities of ModelLoadingPlugin, too.
 *   For example there's no need to keep a singleton around since i get everything i need through
 *   ModelLoadingPlugin
 */
public class TemplatesModelProvider implements PreparableModelLoadingPlugin<TemplatesModelProvider.DataModels>, PreparableModelLoadingPlugin.DataLoader<TemplatesModelProvider.DataModels> {
	
	//Stuff registered thru the code api, ends up underneath all resourcepacks
	private final Map<Identifier, UnbakedModel> permanentModels = new HashMap<>();
	private final Map<ModelIdentifier, Identifier> permanentItemAssignments = new HashMap<>();
	
	//Cache of all template models, dumped on resource-reload
	private volatile TemplateAppearanceManager appearanceManager;
	
	/// fabric model loading plugin
	
	public static class DataModels {
		public final Map<Identifier, UnbakedModel> models = new HashMap<>();
		public final Map<ModelIdentifier, Identifier> itemAssignments = new HashMap<>();
	}
	
	@Override
	public CompletableFuture<DataModels> load(ResourceManager res, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			DataModels dms = new DataModels();
			//throw in the permanently-registered code stuff first, so data can override it
			dms.models.putAll(permanentModels);
			dms.itemAssignments.putAll(permanentItemAssignments);
			
			TemplatesClientApi api = TemplatesClientApi.getInstance();
			
			for(Resource modelMappingRes : res.getAllResources(new Identifier("templates", "template_model_mappings.json"))) {
				try(Reader reader = modelMappingRes.getReader()) {
					List<TemplateModelMapping> modelMappings = MagicPaths.parseJsonArray(
						reader, TemplateModelMapping.class, TemplateModelMapping::de).toList();
					
					Templates.LOG.info("Found {} model mappings from pack '{}'", modelMappings.size(), modelMappingRes.getResourcePackName());
					
					modelMappings.forEach(modelMapping ->
						dms.models.put(modelMapping.id.toMinecraft(), switch(modelMapping.kind) {
							case AUTO -> api.auto(modelMapping.base.toMinecraft());
							case JSON -> api.json(modelMapping.base.toMinecraft());
						}));
				} catch (Exception e) {
					Templates.LOG.error("Failed to load model mappings from pack '{}'", modelMappingRes.getResourcePackName(), e);
				}
			}
			
			for(Resource itemOverrideRes : res.getAllResources(new Identifier("templates", "template_item_overrides.json"))) {
				try(Reader reader = itemOverrideRes.getReader()) {
					List<ItemOverrideMapping> itemOverrideMappings = MagicPaths.parseJsonArray(
						reader, ItemOverrideMapping.class, ItemOverrideMapping::de).toList();
					
					Templates.LOG.info("Found {} item model overrides from pack '{}'.", itemOverrideMappings.size(), itemOverrideRes.getResourcePackName());
					
					itemOverrideMappings.forEach(iom -> dms.itemAssignments.put(
						new ModelIdentifier(iom.itemId.toMinecraft(), "inventory"),
						iom.modelId.toMinecraft()
					));
				} catch (Exception e) {
					Templates.LOG.error("Failed to load item overrides from pack '{}'", itemOverrideRes.getResourcePackName(), e);
				}
			}
			
			return dms;
		}, executor);
	}
	
	@Override
	public void onInitializeModelLoader(DataModels data, ModelLoadingPlugin.Context ctx) {
		//Dump TAM cache while we're at it
		dumpCache();
		
		//Ensure all special models are referenced.
		//Fixes weird problems I was having with the Post. Its item model is based off of
		//`templates:models/block/fence_post_inventory` which is not referenced by anything else.
		//Without this line, the Post model was kinda working (it looked like a post in my hand)
		//but it had default transforms instead of block/block. But other models like the fencegate
		//model, which are retextured versions of vanilla inventory models, had correct transforms.
		//Very surprising, don't really understand it. Feels almost like a fabric bug
		ctx.addModels(data.models.keySet());
		
		//Register a model resolver for loading blockmodels; returning null if we don't load
		//a particular blockmodel is the correct course of action.
		ctx.resolveModel().register(rCtx -> data.models.get(rCtx.id()));
		
		//Swap out the item models. Here we have to return the original model if we're not
		//interested in swapping a particular item model.
		ctx.modifyModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
			@SuppressWarnings("SuspiciousMethodCalls") //modelidentifier is a subtype of identifier
			Identifier modelId = data.itemAssignments.get(context.id());
			if(modelId == null) return model;
			
			UnbakedModel base = data.models.get(modelId);
			if(base == null) return model;
			
			return base;
		});
	}
	
	/// template appearance manager cache
	//TODO: push this up into the model loading process, the new fabric apis are a lot nicer about this
	
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
		permanentModels.put(id, unbaked);
	}
	
	public void assignItemModel(Identifier templateModelId, ModelIdentifier... modelIds) {
		for(ModelIdentifier modelId : modelIds) permanentItemAssignments.put(modelId, templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, Identifier... itemIds) {
		for(Identifier itemId : itemIds) permanentItemAssignments.put(new ModelIdentifier(itemId, "inventory"), templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, ItemConvertible... itemConvs) {
		for(ItemConvertible itemConv : itemConvs) assignItemModel(templateModelId, Registries.ITEM.getId(itemConv.asItem()));
	}
}
