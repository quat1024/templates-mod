package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.gensupport.Ser;
import io.github.cottonmc.templates.gensupport.Facet;

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
	
	public Tbl selfdrops(Id itemId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId))
			.cond(new TblCond.SurvivesExp()));
	}
	
	public Tbl doordrops(Id itemId, Id blockId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId).cond(new TblCond.BottomDoor(blockId)))
			.cond(new TblCond.SurvivesExp()));
	}
	
	public Tbl slabdrops(Id itemId, Id blockId) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId)
				.countIf(2, new TblCond.DoubleSlab(blockId))
				.func(new TblFunc.ExplDecay())));
	}
	
	public Tbl candledrops(Id itemId, Id blockid) {
		return pool(new TblPool()
			.entry(new TblEntry.EItem(itemId)
				.countIf(2, new TblCond.CandleCount(blockid, 2))
				.countIf(3, new TblCond.CandleCount(blockid, 3))
				.countIf(4, new TblCond.CandleCount(blockid, 4))
				.func(new TblFunc.ExplDecay())));
	}
}
