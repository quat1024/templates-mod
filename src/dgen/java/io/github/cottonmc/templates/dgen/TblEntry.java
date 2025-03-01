package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TblEntry implements Ser<JsonObject> {
	List<TblCond> conditions = new ArrayList<>(2);
	List<TblFunc> functions = new ArrayList<>(2);
	
	public TblEntry addCondition(TblCond... cond) {
		this.conditions.addAll(Arrays.asList(cond));
		return this;
	}
	
	public TblEntry addFunction(TblFunc... func) {
		this.functions.addAll(Arrays.asList(func));
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		if(!conditions.isEmpty()) obj.add("conditions", serList(conditions));
		if(!functions.isEmpty()) obj.add("functions", serList(functions));
		return obj;
	}
	
	public static class EItem extends TblEntry {
		public EItem(String itemId) {
			this.itemId = itemId;
		}
		
		String itemId;
		
		@Override
		public JsonObject ser() {
			JsonObject obj = super.ser();
			obj.addProperty("type", "minecraft:item");
			obj.addProperty("name", itemId);
			return obj;
		}
	}
}
