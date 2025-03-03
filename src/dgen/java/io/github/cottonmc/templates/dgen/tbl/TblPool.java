package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Ser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TblPool implements Ser<JsonObject> {
	int rolls = 1;
	List<TblEntry> entries = new ArrayList<>(2);
	List<TblCond> conditions = new ArrayList<>(2);
	
	public TblPool cond(TblCond... cond) {
		this.conditions.addAll(Arrays.asList(cond));
		return this;
	}
	
	public TblPool entry(TblEntry... entry) {
		this.entries.addAll(Arrays.asList(entry));
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.addProperty("rolls", rolls);
		obj.add("entries", serList(entries));
		if(!conditions.isEmpty()) obj.add("conditions", serList(conditions));
		return obj;
	}
}
