package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Downcastable;
import io.github.cottonmc.templates.dgen.Ser;

public abstract class Ingr<D> extends Downcastable<D> implements Ser<JsonObject> {
	
	public static Ingr<?> fromString(String s) {
		if(s.startsWith("#")) return new T(s.substring(1));
		else return new I(s);
	}
	
	public static class I extends Ingr<I> {
		public I(String itemId) {
			this.itemId = itemId;
		}
		
		public String itemId;
		
		@Override
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("item", itemId);
			return obj;
		}
	}
	
	public static class T extends Ingr<T> {
		public T(String tagId) {
			this.tagId = tagId;
		}
		
		public String tagId;
		
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("tag", tagId);
			return obj;
		}
	}
}
