package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.dgen.Ser;
import io.github.cottonmc.templates.dgen.ann.Facet;
import org.jetbrains.annotations.Nullable;

@Facet
public abstract class Rcp<D> extends Idable<D> implements Ser<JsonObject> {
	public @Nullable String group;
	public String result;
	public int count = 1;
	
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
		if(count != 1) result.addProperty("count", count);
		obj.add("result", result);
		
		return obj;
	}
}
