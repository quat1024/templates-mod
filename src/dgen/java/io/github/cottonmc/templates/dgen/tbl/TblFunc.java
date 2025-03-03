package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Ser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TblFunc implements Ser<JsonObject> {
	List<TblCond> conditions = new ArrayList<>(2);
	
	public TblFunc cond(TblCond... cond) {
		this.conditions.addAll(Arrays.asList(cond));
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		if(!conditions.isEmpty()) obj.add("conditions", serList(conditions));
		return obj;
	}
	
	public static class Count extends TblFunc {
		public Count(double count) {
			this.count = count;
		}
		
		double count;
		
		@Override
		public JsonObject ser() {
			JsonObject obj = super.ser();
			obj.addProperty("function", "minecraft:set_count");
			obj.addProperty("add", false);
			obj.addProperty("count", count);
			return obj;
		}
	}
	
	public static class ExplDecay extends TblFunc {
		@Override
		public JsonObject ser() {
			JsonObject obj = super.ser();
			obj.addProperty("function", "minecraft:explosion_decay");
			return obj;
		}
	}
}
