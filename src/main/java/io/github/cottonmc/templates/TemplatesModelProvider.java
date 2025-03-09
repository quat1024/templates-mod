package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.gensupport.ItemOverrideMapping;
import io.github.cottonmc.templates.gensupport.MagicPaths;
import io.github.cottonmc.templates.gensupport.TemplateModelMapping;
import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import io.github.cottonmc.templates.model.TemplateUnbakedModel;
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
			
			for(Resource modelMappingRes : res.getAllResources(Identifier.of("templates", "template_model_mappings.json"))) {
				try(Reader reader = modelMappingRes.getReader()) {
					List<TemplateModelMapping> modelMappings = MagicPaths.parseJsonArray(
						reader, TemplateModelMapping.class, TemplateModelMapping::de).toList();
					
					Templates.LOG.info("Found {} model mappings from pack '{}'", modelMappings.size(), modelMappingRes.getPackId());
					
					modelMappings.forEach(modelMapping ->
						dms.models.put(modelMapping.id.toMinecraft(), switch(modelMapping.kind) {
							case AUTO -> api.auto(modelMapping.base.toMinecraft());
							case JSON -> api.json(modelMapping.base.toMinecraft());
						}));
				} catch (Exception e) {
					Templates.LOG.error("Failed to load model mappings from pack '{}'", modelMappingRes.getPackId(), e);
				}
			}
			
			for(Resource itemOverrideRes : res.getAllResources(Identifier.of("templates", "template_item_overrides.json"))) {
				try(Reader reader = itemOverrideRes.getReader()) {
					List<ItemOverrideMapping> itemOverrideMappings = MagicPaths.parseJsonArray(
						reader, ItemOverrideMapping.class, ItemOverrideMapping::de).toList();
					
					Templates.LOG.info("Found {} item model overrides from pack '{}'.", itemOverrideMappings.size(), itemOverrideRes.getPackId());
					
					itemOverrideMappings.forEach(iom -> dms.itemAssignments.put(
						new ModelIdentifier(iom.itemId.toMinecraft(), "inventory"),
						iom.modelId.toMinecraft()
					));
				} catch (Exception e) {
					Templates.LOG.error("Failed to load item overrides from pack '{}'", itemOverrideRes.getPackId(), e);
				}
			}
			
			return dms;
		}, executor);
	}
	
	@Override
	public void onInitializeModelLoader(DataModels data, ModelLoadingPlugin.Context ctx) {
		//make a new tam
		TemplateAppearanceManager tam = new TemplateAppearanceManager();
		
		//tell unbaked models to use it (TODO: construct new unbakedmodels instead, have some kind of baked-model-factory api)
		@SuppressWarnings("Convert2Lambda")
		TemplateUnbakedModel.ReinitContext reinitContext = new TemplateUnbakedModel.ReinitContext() {
			@Override
			public TemplateAppearanceManager getTAM(Function<SpriteIdentifier, Sprite> spriteLookup) {
				tam.ready(spriteLookup);
				return tam;
			}
		};
		for(UnbakedModel ub : data.models.values()) {
			if(ub instanceof TemplateUnbakedModel reinitable) {
				reinitable.reinit(reinitContext);
			}
		}
		
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
			Identifier modelId = data.itemAssignments.get(context.topLevelId());
			if(modelId == null) return model;
			
			UnbakedModel base = data.models.get(modelId);
			if(base == null) return model;
			
			return base;
		});
	}
	
	//code models
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
