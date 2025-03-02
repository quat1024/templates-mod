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
		this.blockId = Id.fromStrT(blockId);
		this.itemId = Id.fromStrT(itemId);
	}
	
	public Id blockId;
	public Id itemId;
	
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
	
	public Tbl candledrops() {
		return blockLoot().candledrops(itemId, blockId);
	}
	
	/// recipes ///
	
	public RcpShaped shapedT() {
		return shapedT(1);
	}
	
	public RcpShaped shapedT(int count) {
		return shapedT(count, itemId);
	}
	
	public RcpShaped shapedT(int count, Id itemId) {
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
	
	public RcpShapeless shapelessT(int count, Id itemId) {
		return addFacet(new RcpShapeless()
			.group("templates")
			.result(itemId, count)
			.id(itemId));
	}
	
	/// tags ///
	
	public AddToTag itag(Id tagId) {
		return addFacet(new AddToTag().id(tagId.prefixPath("items")).item(itemId));
	}
	
	public AddToTag btag(Id tagId) {
		return addFacet(new AddToTag().id(tagId.prefixPath("blocks")).block(blockId));
	}
	
	//add to an item and block tag that happen to have the same name
	public void ibTag(Id tagId) {
		itag(tagId);
		btag(tagId);
	}
	
	public AddToTag itag(String tagId) {
		return itag(new Id(tagId));
	}
	
	public AddToTag btag(String tagId) {
		return btag(new Id(tagId));
	}
	
	public void ibTag(String tagId) {
		ibTag(new Id(tagId));
	}
	
	public AddToTag mineableAxe() {
		return btag("minecraft:mineable/axe");
	}
	
	public AddToTag mineablePick() {
		return btag("minecraft:mineable/pickaxe");
	}
}
