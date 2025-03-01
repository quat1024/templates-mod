package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.dgen.ann.MakeLootTable;
import io.github.cottonmc.templates.dgen.ann.MakeRecipe;

import java.util.Map;

public class Tmpl extends FacetHolder<Tmpl> {
	public Tmpl(String blockId) {
		this(blockId, blockId);
	}
	
	public Tmpl(String blockId, String itemId) {
		this.blockId = defPrefix(blockId);
		this.itemId = defPrefix(itemId);
	}
	
	private static String defPrefix(String in) {
		if(in.indexOf(':') == -1) return "templates:" + in;
		else return in;
	}
	
	public String blockId;
	public String itemId;
	
	//dsl
	public void selfdrops() {
		addFacet(new MakeLootTable.Inst(), Tbl.selfdrops(itemId));
	}
	
	public void doordrops() {
		addFacet(new MakeLootTable.Inst(), Tbl.doordrops(itemId, blockId));
	}
	
	public void slabdrops() {
		addFacet(new MakeLootTable.Inst(), Tbl.slabdrops(itemId, blockId));
	}
	
	///
	
	public void shaped(int count, String... rows) {
		RcpShaped recipe = new RcpShaped();
		recipe.group = "templates";
		recipe.result = itemId;
		recipe.count = count;
		recipe.pattern = rows;
		recipe.key = Map.of('~', "minecraft:string", 'I', "minecraft:bamboo");
		addFacet(new MakeRecipe.Inst(itemId), recipe);
	}
}
