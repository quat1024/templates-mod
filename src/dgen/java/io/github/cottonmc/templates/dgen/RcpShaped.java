package io.github.cottonmc.templates.dgen;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: better DSL for this (allow mixing pattern slots and items)
public class RcpShaped extends Rcp {
	String[] pattern;
	Map<Character, String> key = new HashMap<>();
	
	@Override
	public JsonObject ser() {
		JsonObject obj = super.ser();
		obj.addProperty("type", "minecraft:crafting_shaped");
		
		obj.add("pattern", serList(List.of(pattern)));
		
		//key
		JsonObject k = new JsonObject();
		key.forEach((charr, item) -> {
			JsonObject itemj = new JsonObject();
			itemj.addProperty("item", item);
			k.add(String.valueOf(charr), itemj);
		});
		obj.add("key", k);
		
		return obj;
	}
}
