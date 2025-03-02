package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.dgen.rcp.RcpShaped;
import io.github.cottonmc.templates.dgen.rcp.RcpShapeless;
import io.github.cottonmc.templates.dgen.tag.AddToTag;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

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
	
	//dsl!
	
	/// drops ///
	
	public Tbl blockLoot() {
		return addFacet(new Tbl().id(blockId));
	}
	
	public Tbl selfdrops() {
		return blockLoot().selfdrops(itemId);
	}
	
	public Tbl doordrops() {
		return blockLoot().doordrops(itemId, blockId);
	}
	
	public Tbl slabdrops() {
		return blockLoot().slabdrops(itemId, blockId);
	}
	
	/// recipes ///
	
	public RcpShaped shapedT() {
		return shapedT(1);
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
	
	public RcpShapeless shapelessT() {
		return shapelessT(1);
	}
	
	public RcpShapeless shapelessT(int count) {
		return shapelessT(count, itemId);
	}
	
	public RcpShapeless shapelessT(int count, String itemId) {
		return addFacet(new RcpShapeless()
			.group("templates")
			.result(itemId, count)
			.id(itemId));
	}
	
	/// tags ///
	
	public AddToTag itag(String tagId) {
		return addFacet(new AddToTag().id(tagId).item(itemId));
	}
	
	public AddToTag btag(String tagId) {
		return addFacet(new AddToTag().id(tagId).block(blockId));
	}
	
	public void ibTag(String tagId) {
		itag(tagId);
		btag(tagId.replace(":blocks/", ":items/")); //boy this is janky
	}
	
	public AddToTag mineableAxe() {
		return btag("minecraft:blocks/mineable/axe");
	}
	
	public AddToTag mineablePick() {
		return btag("minecraft:blocks/mineable/pickaxe");
	}
}
