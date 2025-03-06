package io.github.cottonmc.templates.dgen.mdl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.gensupport.Facet;
import io.github.cottonmc.templates.gensupport.Ser;

@Facet
public abstract class ItemModel<D> extends Idable<D> implements Ser<JsonObject> {
	
	public static class Forwarding extends ItemModel<Forwarding> {
		public Forwarding(String parent) {
			this.parent = parent;
		}
		
		public String parent;
		
		public Forwarding parent(String parent) {
			this.parent = parent;
			return this;
		}
		
		@Override
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("parent", parent);
			return obj;
		}
	}
	
	public static class TemplatesDummy extends Forwarding {
		public TemplatesDummy() {
			super("templates:dummy");
		}
	}
	
	//TODO more layers i guess
	public static class ItemGenerated extends ItemModel<ItemGenerated> {
		public ItemGenerated(String layer0) {
			this.layer0 = layer0;
		}
		
		public String layer0;
		
		public ItemGenerated layer0(String layer0) {
			this.layer0 = layer0;
			return this;
		}
		
		@Override
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("parent", "item/generated");
			
			JsonObject textures = new JsonObject();
			textures.addProperty("layer0", layer0);
			obj.add("textures", textures);
			
			return obj;
		}
	}
}
