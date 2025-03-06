package io.github.cottonmc.templates.dgen.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.gensupport.Idable;
import io.github.cottonmc.templates.gensupport.Ser;
import io.github.cottonmc.templates.gensupport.Facet;

import java.util.Collection;
import java.util.Objects;

@Facet
public class AddToTag extends Idable<AddToTag> implements Ser<JsonElement> { //id is the id of the tag
	String thing; //TODO should maybe be an Id
	boolean optional = false;
	
	public AddToTag opt() {
		optional = true;
		return downcast();
	}
	
	public AddToTag thing(String thingId) {
		this.thing = thingId;
		return this;
	}
	
	public AddToTag item(Id itemId) {
		return thing(itemId.toString());
	}
	
	public AddToTag block(Id blockId) {
		return thing(blockId.toString());
	}
	
	public AddToTag tag(String tagId) {
		if(!tagId.startsWith("#")) tagId = "#" + tagId;
		return thing(tagId);
	}
	
	@Override
	public JsonElement ser() {
		if(optional) {
			JsonObject obj = new JsonObject();
			obj.addProperty("required", false);
			obj.addProperty("id", thing);
			return obj;
		} else return new JsonPrimitive(thing);
	}
	
	//generated
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		AddToTag addToTag = (AddToTag) o;
		return optional == addToTag.optional && Objects.equals(thing, addToTag.thing);
	}
	
	@Override
	public int hashCode() {
		int result = Objects.hashCode(thing);
		result = 31 * result + Boolean.hashCode(optional);
		return result;
	}
	
	public static JsonObject makeTag(Collection<AddToTag> coll) {
		JsonObject obj = new JsonObject();
		obj.addProperty("replace", false);
		obj.add("values", Ser.list(coll));
		return obj;
	}
}
