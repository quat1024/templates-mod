package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.ann.Facet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Facet
public class Tbl extends Idable<Tbl> implements Ser<JsonObject> {
	List<TblPool> pools = new ArrayList<>(2);
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "minecraft:block");
		obj.add("pools", serList(pools));
		return obj;
	}
	
	public Tbl addPool(TblPool... pool) {
		this.pools.addAll(Arrays.asList(pool));
		return this;
	}
	
	public static Tbl survivesExplosion(TblEntry entry) {
		return new Tbl()
			.addPool(new TblPool()
				.addEntry(entry)
				.addCondition(new TblCond.SurvivesExp()));
	}
	
	public static Tbl selfdrops(String id) {
		return survivesExplosion(new TblEntry.EItem(id));
	}
	
	public static Tbl doordrops(String itemId, String blockId) {
		return survivesExplosion(new TblEntry.EItem(itemId).addCondition(new TblCond.BottomDoor(blockId)));
	}
	
	public static Tbl slabdrops(String itemId, String blockId) {
		return survivesExplosion(new TblEntry.EItem(itemId)
			.addFunction(new TblFunc.Count(2).addCondition(new TblCond.DoubleSlab(blockId)))
			.addFunction(new TblFunc.ExplDecay()));
	}
}
