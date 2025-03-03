package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Downcastable;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.gensupport.Ser;

public abstract class Ingr<D> extends Downcastable<D> implements Ser<JsonObject> {
	
	public static Ingr<?> parse(String s) {
		if(s.startsWith("#")) return new T(new Id(s.substring(1)));
		else return new I(new Id(s));
	}
	
	public static class I extends Ingr<I> {
		public I(Id itemId) {
			this.itemId = itemId;
		}
		
		public Id itemId;
		
		@Override
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("item", itemId.toString());
			return obj;
		}
	}
	
	public static class T extends Ingr<T> {
		public T(Id tagId) {
			this.tagId = tagId;
		}
		
		public Id tagId;
		
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("tag", tagId.toString());
			return obj;
		}
	}
}
