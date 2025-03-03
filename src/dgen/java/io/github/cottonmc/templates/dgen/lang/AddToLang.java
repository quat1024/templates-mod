package io.github.cottonmc.templates.dgen.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.gensupport.Facet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Facet
public class AddToLang extends Idable<AddToLang> implements Comparable<AddToLang> {
	public AddToLang(Id langFileId, String key, String value) {
		this.id = langFileId;
		this.key = key;
		this.value = value;
	}
	
	String key, value;
	
	@Override
	public int compareTo(@NotNull AddToLang o) {
		return key.compareTo(o.key);
	}
	
	public static JsonElement makeLang(Collection<AddToLang> entries) {
		JsonObject obj = new JsonObject();
		entries.stream().sorted().forEach(lang -> obj.addProperty(lang.key, lang.value));
		return obj;
	}
}
