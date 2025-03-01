package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TblPool implements Ser<JsonObject> {
	int rolls = 1;
	List<TblEntry> entries = new ArrayList<>(2);
	List<TblCond> conditions = new ArrayList<>(2);
	
	public TblPool addCondition(TblCond... cond) {
		this.conditions.addAll(Arrays.asList(cond));
		return this;
	}
	
	public TblPool addEntry(TblEntry... entry) {
		this.entries.addAll(Arrays.asList(entry));
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.addProperty("rolls", rolls);
		obj.add("entries", serList(entries));
		obj.add("conditions", serList(conditions));
		return obj;
	}
}
