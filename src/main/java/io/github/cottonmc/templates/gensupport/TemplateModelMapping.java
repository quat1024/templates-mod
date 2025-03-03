package io.github.cottonmc.templates.gensupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

@Facet
public class TemplateModelMapping implements Ser<JsonObject> {
	public Kind kind;
	public Id id;
	public Id base;
	
	public TemplateModelMapping kind(Kind kind) {
		this.kind = kind;
		return this;
	}
	
	public TemplateModelMapping id(Id id) {
		this.id = id;
		return this;
	}
	
	public TemplateModelMapping id(String id) {
		return id(new Id(id));
	}
	
	public TemplateModelMapping base(Id base) {
		this.base = base;
		return this;
	}
	
	public TemplateModelMapping base(String base) {
		return base(new Id(base));
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new JsonObject();
		obj.add("kind", kind.ser());
		obj.addProperty("id", id.toString());
		obj.addProperty("base", base.toString());
		return obj;
	}
	
	public static TemplateModelMapping de(JsonElement elem) {
		TemplateModelMapping ret = new TemplateModelMapping();
		if(elem instanceof JsonObject obj) {
			ret.kind = Kind.de(obj.get("kind"));
			ret.id = new Id(obj.get("id").getAsString());
			ret.base = new Id(obj.get("base").getAsString());
			return ret;
		} else throw new IllegalArgumentException("expected an object, got " + elem.getClass().getSimpleName());
	}
	
	public enum Kind implements Ser<JsonPrimitive> {
		AUTO, JSON;
		
		@Override
		public JsonPrimitive ser() {
			return new JsonPrimitive(name().toLowerCase(Locale.ROOT));
		}
		
		public static Kind de(JsonElement elem) {
			if(elem instanceof JsonPrimitive prim) return switch(prim.getAsString()) {
				case "auto" -> AUTO;
				case "json" -> JSON;
				default -> throw new IllegalArgumentException("expected 'auto' or 'json', got " + prim.getAsString());
			};
			else throw new IllegalArgumentException("expected a prim, got " + elem.getClass().getSimpleName());
		}
	}
}
