package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.ann.Facet;
import org.jetbrains.annotations.Nullable;

@Facet
public abstract class Rcp<D> extends Idable<D> implements Ser<JsonObject> {
	@Nullable String group;
	String result;
	int count = 1;
	
	public D group(String group) {
		this.group = group;
		return downcast();
	}
	
	public D result(String r, int count) {
		this.result = r;
		this.count = count;
		return downcast();
	}
	
	public D result(String r) {
		return result(r, 1);
	}
	
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
