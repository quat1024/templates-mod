package io.github.cottonmc.slopetest.model;

import java.util.HashMap;
import java.util.function.Function;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.registry.Registry;

public class SlopeModelVariantProvider implements ModelVariantProvider {

    private final HashMap<ModelIdentifier, UnbakedModel> variants = new HashMap<>();
    
    public SlopeModelVariantProvider() { }
    
    @Override
    public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        return variants.get(modelId);
    }

    public void registerTemplateBlock(Block block, BlockState itemState, Function<BlockState, SimpleModel> model) {
        for (BlockState state : block.getStateFactory().getStates()) {
            variants.put(BlockModels.getModelId(state), (SimpleUnbakedModel)() -> model.apply(state));
        }
        variants.put(new ModelIdentifier(Registry.ITEM.getId(block.asItem()), "inventory"), (SimpleUnbakedModel)() -> model.apply(itemState));
    }
}
