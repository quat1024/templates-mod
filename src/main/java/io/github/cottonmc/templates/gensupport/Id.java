package io.github.cottonmc.templates.gensupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * basically, when working with mods, sometimes you want an unprefixed id to mean "minecraft"
 * and sometimes you mean "your mod". this forces you to disambiguate which one you mean
 */
public class Id implements Ser<JsonPrimitive> {
	public Id(@NotNull String str) {
		String[] split = str.split(":");
		if(split.length == 1) {
			throw new IllegalArgumentException("not clear what namespace you mean: " + str);
		} else if(split.length == 2) {
			this.ns = split[0];
			this.path = split[1];
		} else throw new IllegalArgumentException("weird id: " + str);
	}
	
	public Id(@NotNull String ns, @NotNull String path) {
		this.ns = ns.toLowerCase(Locale.ROOT);
		this.path = path.toLowerCase(Locale.ROOT);
	}
	
	public static Id mc(String str) {
		return fromStrFallback(str, "minecraft");
	}
	
	public static Id t(String str) {
		return fromStrFallback(str, "templates");
	}
	
	public static Id fromStrFallback(String str, String fallbackNs) {
		String[] split = str.split(":");
		if(split.length == 1) return new Id(fallbackNs, split[0]);
		else if(split.length == 2) return new Id(split[0], split[1]);
		else throw new IllegalArgumentException("weird id " + str);
	}
	
	@NotNull public String ns;
	@NotNull public String path;
	
	public Id mapPath(UnaryOperator<String> op) {
		return new Id(ns, op.apply(path));
	}
	
	public Id prefixPath(String prefix) {
		if(prefix.endsWith("/")) prefix = prefix.substring(0, prefix.length() - 1);
		return new Id(ns, prefix + "/" + path);
	}
	
	public String toString() {
		return ns + ":" + path;
	}
	
	//domain = "block", "item", etc
	public String toTranslationKey(String domain) {
		return domain + "." + ns + "." + path.replace('/', '.');
	}
	
	public Identifier toMinecraft() {
		return new Identifier(ns, path);
	}
	
	@Override
	public JsonPrimitive ser() {
		return new JsonPrimitive(toString());
	}
	
	public static Id de(JsonElement elem) {
		if(elem instanceof JsonPrimitive prim) return new Id(prim.getAsString());
		else throw new IllegalArgumentException("expected a prim, got " + elem.getClass().getSimpleName());
	}
	
	//generated
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		Id id = (Id) o;
		return ns.equals(id.ns) && path.equals(id.path);
	}
	
	@Override
	public int hashCode() {
		int result = ns.hashCode();
		result = 31 * result + path.hashCode();
		return result;
	}
}
