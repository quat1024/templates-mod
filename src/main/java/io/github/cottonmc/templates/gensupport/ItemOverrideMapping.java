package io.github.cottonmc.templates.gensupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Facet
public class ItemOverrideMapping implements Ser<JsonObject> {
	public Id itemId;
	public Id modelId;
	
	public ItemOverrideMapping itemId(Id itemId) {
		this.itemId = itemId;
		return this;
	}
	
	public ItemOverrideMapping model(Id modelId) {
		this.modelId = modelId;
		return this;
	}
	
	public ItemOverrideMapping model(String modelId) {
		return model(new Id(modelId));
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.addProperty("item", itemId.toString());
		obj.addProperty("model", modelId.toString());
		return obj;
	}
	
	public static ItemOverrideMapping de(JsonElement elem) {
		ItemOverrideMapping ret = new ItemOverrideMapping();
		if(elem instanceof JsonObject obj) {
			ret.itemId = new Id(obj.get("item").getAsString());
			ret.modelId = new Id(obj.get("model").getAsString());
			return ret;
		} else throw new IllegalArgumentException("expected an object, got " + elem.getClass().getSimpleName());
	}
}
