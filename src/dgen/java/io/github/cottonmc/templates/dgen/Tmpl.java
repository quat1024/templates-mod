package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.dgen.lang.AddToLang;
import io.github.cottonmc.templates.dgen.rcp.RcpShaped;
import io.github.cottonmc.templates.dgen.rcp.RcpShapeless;
import io.github.cottonmc.templates.dgen.tag.AddToTag;
import io.github.cottonmc.templates.dgen.tbl.Tbl;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.gensupport.ItemOverrideMapping;
import io.github.cottonmc.templates.gensupport.TemplateModelMapping;

public class Tmpl extends FacetHolder {
	public Tmpl(String blockId) {
		this(blockId, blockId);
	}
	
	public Tmpl(String blockId, String itemId) {
		this.blockId = Id.t(blockId);
		this.itemId = Id.t(itemId);
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
	
	public AddToTag tag(Id tagId) {
		return addFacet(new AddToTag().id(tagId).item(itemId));
	}
	
	public AddToTag itag(Id tagId) {
		return tag(tagId.prefixPath("items"));
	}
	
	public AddToTag btag(Id tagId) {
		return tag(tagId.prefixPath("blocks"));
	}
	
	//add to an item and block tag that happen to have the same name
	public void ibTag(Id tagId) {
		itag(tagId);
		btag(tagId);
	}
	
	public AddToTag tag(String tagId) {
		return tag(new Id(tagId));
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
	
	/// lang ///
	
	public AddToLang enUS(String name) {
		return addFacet(new AddToLang(Dgen.EN_US, itemId.toTranslationKey("block"), name));
	}
	
	/// model handling ///
	
	public TemplateModelMapping autoRetexture() {
		return addFacet(new TemplateModelMapping()).kind(TemplateModelMapping.Kind.AUTO);
	}
	
	public TemplateModelMapping jsonRetexture() {
		return addFacet(new TemplateModelMapping()).kind(TemplateModelMapping.Kind.JSON);
	}
	
	public ItemOverrideMapping itemOverride(Id modelId) {
		return addFacet(new ItemOverrideMapping()).itemId(itemId).model(modelId);
	}
	
	public ItemOverrideMapping itemOverride(String modelId) {
		return itemOverride(new Id(modelId));
	}
	
	public ItemOverrideMapping itemOverride(TemplateModelMapping asThis) {
		return itemOverride(asThis.id);
	}
}
