package io.github.cottonmc.templates.gensupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

@Facet
public class AddTooltip extends Idable<AddTooltip> implements Ser<JsonObject> {
	public List<String> tipKeys = new ArrayList<>(1);
	
	public AddTooltip tipKeys(String... keys) {
		tipKeys.addAll(List.of(keys));
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.add("item", id.ser());
		obj.add("tip", serList(tipKeys));
		return obj;
	}
	
	public static AddTooltip de(JsonElement elem) {
		if(elem instanceof JsonObject obj) {
			AddTooltip res = new AddTooltip();
			res.id = Id.de(obj.get("item"));
			res.tipKeys = new ArrayList<>(obj.get("tip").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList());
			return res;
		} else throw new IllegalArgumentException("Expected an object, got " + elem.getClass().getSimpleName());
	}
}
