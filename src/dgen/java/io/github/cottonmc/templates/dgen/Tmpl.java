package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.dgen.rcp.Ingr;
import io.github.cottonmc.templates.dgen.rcp.RcpShaped;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

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
	
	public Tbl selfdrops() {
		Tbl selfdrops = new Tbl().id(blockId).selfdrops(itemId);
		addFacet(selfdrops);
		return selfdrops;
	}
	
	public Tbl doordrops() {
		Tbl doordrops = new Tbl().id(blockId).doordrops(itemId, blockId);
		addFacet(doordrops);
		return doordrops;
	}
	
	public Tbl slabdrops() {
		Tbl slabdrops = new Tbl().id(itemId).slabdrops(itemId, blockId);
		addFacet(slabdrops);
		return slabdrops;
	}
	
	public void shaped(int count, String... rows) {
		RcpShaped recipe = new RcpShaped()
			.group("templates")
			.result(itemId, count)
			.id(itemId);
		recipe.pattern = rows;
		recipe.key = new TreeMap<>(Map.of('~', Ingr.fromString("minecraft:string"), 'I', Ingr.fromString("minecraft:bamboo")));
		addFacet(recipe);
	}
}
