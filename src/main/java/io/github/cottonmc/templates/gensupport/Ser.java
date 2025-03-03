package io.github.cottonmc.templates.gensupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collection;

public interface Ser<E extends JsonElement> {
	E ser();
	
	//just for calling convenience (lmao?)
	default JsonArray serList(Collection<?> list) {
		return list(list);
	}
	
	static JsonArray list(Collection<?> list) {
		JsonArray arr = new JsonArray();
		for(Object item : list) {
			if(item instanceof Ser<?> s) arr.add(s.ser());
			else if(item instanceof JsonElement j) arr.add(j);
			else if(item instanceof String s) arr.add(s);
			else if(item instanceof Number n) arr.add(n);
			else if(item instanceof Boolean b) arr.add(b);
			else if(item instanceof Character c) arr.add(c);
			else throw new IllegalArgumentException(item.getClass().getName());
		}
		return arr;
	}
}
