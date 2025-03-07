package io.github.cottonmc.templates.api;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public interface TemplateAppearance {
	@NotNull RenderMaterial getRenderMaterial(boolean ao);
	@NotNull Sprite getSprite(Direction dir);
	int getBakeFlags(Direction dir);
	boolean hasColor(Direction dir);
}
