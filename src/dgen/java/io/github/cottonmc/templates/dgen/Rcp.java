package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public abstract class Rcp implements Ser<JsonObject> {
	@Nullable String group;
	String result;
	int count = 1;
	
	@Override
	public JsonObject ser() {
		if(result == null) throw new IllegalStateException("no result");
		
		JsonObject obj = new JsonObject();
		if(group != null) obj.addProperty("group", group);
		
		JsonObject result = new JsonObject();
		result.addProperty("item", this.result);
		result.addProperty("count", count); //TODO is it needed if it's 1
		obj.add("result", result);
		
		return obj;
	}
}
