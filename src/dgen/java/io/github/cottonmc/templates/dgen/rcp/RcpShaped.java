package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: better DSL for this (allow mixing pattern slots and items)
public class RcpShaped extends Rcp<RcpShaped> {
	public String[] pattern;
	public Map<Character, Ingr<?>> key = new HashMap<>();
	
	@Override
	public JsonObject ser() {
		JsonObject obj = super.ser();
		obj.addProperty("type", "minecraft:crafting_shaped");
		
		obj.add("pattern", serList(List.of(pattern)));
		
		//key
		JsonObject k = new JsonObject();
		key.forEach((charr, item) -> k.add(String.valueOf(charr), item.ser()));
		obj.add("key", k);
		
		return obj;
	}
}
