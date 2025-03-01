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
		return addFacet(new Tbl().id(blockId).selfdrops(itemId));
	}
	
	public Tbl doordrops() {
		return addFacet(new Tbl().id(blockId).doordrops(itemId, blockId));
	}
	
	public Tbl slabdrops() {
		return addFacet(new Tbl().id(itemId).slabdrops(itemId, blockId));
	}
	
	public RcpShaped shapedT() {
		return shapedT(1, itemId);
	}
	
	public RcpShaped shapedT(int count) {
		return shapedT(count, itemId);
	}
	
	public RcpShaped shapedT(int count, String itemId) {
		return addFacet(new RcpShaped()
			.group("templates")
			.result(itemId, count)
			.id(itemId));
	}
}
