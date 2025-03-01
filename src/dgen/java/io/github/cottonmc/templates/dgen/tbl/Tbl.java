package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.dgen.Ser;
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
	
	public Tbl pool(TblPool... pool) {
		this.pools.addAll(Arrays.asList(pool));
		return this;
	}
	
	public Tbl selfdrops(String itemId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId))
			.cond(new TblCond.SurvivesExp()));
	}
	
	public Tbl doordrops(String itemId, String blockId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId).cond(new TblCond.BottomDoor(blockId)))
			.cond(new TblCond.SurvivesExp()));
	}
	
	public Tbl slabdrops(String itemId, String blockId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId)
				.func(new TblFunc.Count(2).cond(new TblCond.DoubleSlab(blockId)))
				.func(new TblFunc.ExplDecay())));
	}
}
