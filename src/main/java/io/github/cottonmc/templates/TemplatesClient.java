package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.model.SlopeBaseMesh;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.ApiStatus;


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
		
		//put all template blocks on the cutout layer
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Templates.INTERNAL_TEMPLATES.toArray(new Block[0]));
		
		//register the model loading plugin
		PreparableModelLoadingPlugin.register(provider, provider);
		
		//register special models.
		//they were formerly all done in-code here, but the information is now datagenned and loaded at resource-load time.
		//see TemplatesModelProvider. there are still a few custom mesh models left over which can't be created from json.
		TemplatesClientApi api = TemplatesClientApi.getInstance();
		api.addTemplateModel(Templates.id("slope_special")                , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeUpright));
		api.addTemplateModel(Templates.id("slope_side_special")           , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeSide));
		api.addTemplateModel(Templates.id("tiny_slope_special")           , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinyUpright));
		api.addTemplateModel(Templates.id("tiny_slope_side_special")      , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinySide));
	}
}
