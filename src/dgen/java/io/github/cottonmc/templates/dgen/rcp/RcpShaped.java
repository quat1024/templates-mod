package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//TODO: better DSL for this (allow mixing pattern slots and items)
public class RcpShaped extends Rcp<RcpShaped> {
	public String[] pattern;
	public Map<Character, Ingr<?>> key = new HashMap<>();
	
	public RcpShaped rows(String... rows) {
		pattern = rows;
		return this;
	}
	
	public RcpShaped key(Map<Character, Ingr<?>> key) {
		this.key.putAll(key);
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = super.ser();
		obj.addProperty("type", "minecraft:crafting_shaped");
		
		obj.add("pattern", serList(List.of(pattern)));
		
		//sort the key and filter to only items actually used
		Set<Character> usedChars = new HashSet<>();
		for(String row : pattern) for(char c : row.toCharArray()) usedChars.add(c);
		Map<Character, Ingr<?>> key2 = new TreeMap<>(key);
		key2.keySet().removeIf(c -> !usedChars.contains(c));
		
		//make json key
		JsonObject k = new JsonObject();
		key2.forEach((charr, item) -> k.add(String.valueOf(charr), item.ser()));
		obj.add("key", k);
		
		return obj;
	}
}
