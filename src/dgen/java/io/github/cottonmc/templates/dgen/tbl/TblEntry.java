package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.gensupport.Ser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TblEntry implements Ser<JsonObject> {
	List<TblCond> conditions = new ArrayList<>(2);
	List<TblFunc> functions = new ArrayList<>(2);
	
	public TblEntry cond(TblCond... cond) {
		this.conditions.addAll(Arrays.asList(cond));
		return this;
	}
	
	public TblEntry func(TblFunc... func) {
		this.functions.addAll(Arrays.asList(func));
		return this;
	}
	
	//common idiom
	public TblEntry countIf(int count, TblCond... conds) {
		return func(new TblFunc.Count(count).cond(conds));
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		if(!conditions.isEmpty()) obj.add("conditions", serList(conditions));
		if(!functions.isEmpty()) obj.add("functions", serList(functions));
		return obj;
	}
	
	public static class EItem extends TblEntry {
		public EItem(Id itemId) {
			this.itemId = itemId;
		}
		
		Id itemId;
		
		@Override
		public JsonObject ser() {
			JsonObject obj = super.ser();
			obj.addProperty("type", "minecraft:item");
			obj.addProperty("name", itemId.toString());
			return obj;
		}
	}
}
