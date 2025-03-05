package io.github.cottonmc.templates.api;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;

/**
 * This class is not loaded by Templates. It exists only to avoid an ABI break when I switched from
 * RenderAttachmentBlockEntity (fabric-rendering-data-attachment-v1) to RenderDataBlockEntity (fabric-block-view-api-v2).
 * Basically Templates addons formerly implemented this class from their block entities and overrode either
 * getRenderAttachmentData. When these classes are loaded against the latest version of Templates they will now
 * automatically implement RenderDataBlockEntity instead and things should be groovy
 *
 * @deprecated Implement ThemeableBlockEntity2 instead
 */
@Deprecated(forRemoval = true)
public interface ThemeableBlockEntity extends RenderAttachmentBlockEntity, RenderDataBlockEntity, ThemeableBlockEntity2 {
	@Override
	default BlockState getThemeState() { //from ThemeableBlockEntity2
		return (BlockState) getRenderAttachmentData(); //RenderAttachmentBlockEntity, old system
	}
	
	@Override
	default Object getRenderData() { //RenderDataBlockEntity, new system
		return getThemeState();
	}
}
