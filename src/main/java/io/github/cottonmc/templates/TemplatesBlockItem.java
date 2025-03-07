package io.github.cottonmc.templates;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TemplatesBlockItem extends BlockItem {
	public TemplatesBlockItem(Block block, Settings settings) {
		super(block, settings);
	}
	
	private @Nullable List<? extends Text> tooltip;
	
	public TemplatesBlockItem tooltip(@Nullable List<? extends Text> tooltip) {
		this.tooltip = tooltip;
		return this;
	}
	
	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Text> list, TooltipType tooltipType) {
		if(tooltip != null) list.addAll(tooltip);
	}
}
