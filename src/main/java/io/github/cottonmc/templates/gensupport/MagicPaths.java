package io.github.cottonmc.templates.gensupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.NoSuchFileException;
import java.util.function.Function;
import java.util.stream.Stream;

public class MagicPaths {
	public static final String TOOLTIPS = "/templates-static/tooltips.json";
	
	public static InputStreamReader get(String path) throws IOException {
		if(!path.startsWith("/")) path = "/" + path;
		InputStream in = MagicPaths.class.getResourceAsStream(path);
		if(in == null) throw new NoSuchFileException("Can't read " + path + " from the jar");
		return new InputStreamReader(in);
	}
	
	public static <T> Stream<T> parseJsonArray(Reader reader, Class<T> type, Function<JsonElement, T> de) {
		return new Gson()
			.fromJson(reader, JsonArray.class)
			.asList()
			.stream()
			.map(de);
	}
}
