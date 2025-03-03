package io.github.cottonmc.templates;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.gensupport.ItemOverrideMapping;
import io.github.cottonmc.templates.gensupport.TemplateModelMapping;
import io.github.cottonmc.templates.model.SlopeBaseMesh;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class TemplatesClient implements ClientModInitializer {
	@ApiStatus.Internal //2.2 - Please use the new TemplatesClientApi.getInstance() method.
	public static final TemplatesModelProvider provider = new TemplatesModelProvider();
	
	@ApiStatus.Internal //Please use TemplatesClientApi.getInstance() instead.
	public static final TemplatesClientApiImpl API_IMPL = new TemplatesClientApiImpl(provider);
	
	@Override
	public void onInitializeClient() {
		//set up some magic to force chunk rerenders when you change a template (see TemplateEntity)
		Templates.chunkRerenderProxy = (world, pos) -> {
			if(world == MinecraftClient.getInstance().world) {
				MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
				);
			}
		};
		
		//supporting code for the TemplatesModelProvider
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> provider); //block models
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> provider); //item models
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override public Identifier getFabricId() { return Templates.id("dump-caches"); }
			@Override public void reload(ResourceManager blah) { provider.dumpCache(); }
		});
		
		//put all template blocks on the cutout layer
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Templates.INTERNAL_TEMPLATES.toArray(new Block[0]));
		
		//register special models
		//they were formerly done in-code here, but the information is now datagenned
		TemplatesClientApi api = TemplatesClientApi.getInstance();
		
		//however there are still a few custom mesh models left over;
		//the mesh can't be created from json
		api.addTemplateModel(Templates.id("slope_special")                , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeUpright).disableAo());
		api.addTemplateModel(Templates.id("slope_side_special")           , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeSide).disableAo());
		api.addTemplateModel(Templates.id("tiny_slope_special")           , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinyUpright).disableAo());
		api.addTemplateModel(Templates.id("tiny_slope_side_special")      , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinySide).disableAo());
		
		try(
			Reader templateModels = new InputStreamReader(
				Objects.requireNonNull(
					TemplatesClient.class.getResourceAsStream("/templates-static/template_model_mappings.json")));
			
			Reader itemOverrides = new InputStreamReader(
				Objects.requireNonNull(
					TemplatesClient.class.getResourceAsStream("/templates-static/template_item_overrides.json")))
		) {
			Gson gson = new Gson();
			
			List<TemplateModelMapping> modelMappings = gson.fromJson(templateModels, JsonArray.class).asList().stream().map(TemplateModelMapping::de).toList();
			LogManager.getLogger("Templates").info("Found {} model mappings.", modelMappings.size());
			modelMappings.forEach(modelMapping ->
				api.addTemplateModel(modelMapping.id.toMinecraft(), switch(modelMapping.kind) {
					case AUTO -> api.auto(modelMapping.base.toMinecraft());
					case JSON -> api.json(modelMapping.base.toMinecraft());
				}));
			
			List<ItemOverrideMapping> itemMappings = gson.fromJson(itemOverrides, JsonArray.class).asList().stream().map(ItemOverrideMapping::de).toList();
			LogManager.getLogger("Templates").info("Found {} item model overrides.", modelMappings.size());
			itemMappings.forEach(itemMapping -> api.assignItemModel(itemMapping.modelId.toMinecraft(), itemMapping.itemId.toMinecraft()));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
