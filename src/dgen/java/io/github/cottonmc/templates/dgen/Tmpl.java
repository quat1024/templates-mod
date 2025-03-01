package io.github.cottonmc.templates.dgen;

import java.util.Map;
import java.util.TreeMap;

public class Tmpl extends FacetHolder {
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
		addFacet(Tbl.selfdrops(itemId).id(blockId));
	}
	
	public void doordrops() {
		addFacet(Tbl.doordrops(itemId, blockId).id(blockId));
	}
	
	public void slabdrops() {
		addFacet(Tbl.slabdrops(itemId, blockId).id(blockId));
	}
	
	///
	
	public void shaped(int count, String... rows) {
		RcpShaped recipe = new RcpShaped()
			.group("templates")
			.result(itemId, count)
			.id(itemId);
		recipe.pattern = rows;
		recipe.key = new TreeMap<>(Map.of('~', "minecraft:string", 'I', "minecraft:bamboo"));
		addFacet(recipe);
	}
}
