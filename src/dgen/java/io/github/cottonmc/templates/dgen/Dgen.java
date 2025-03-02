package io.github.cottonmc.templates.dgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.cottonmc.templates.dgen.ann.Facet;
import io.github.cottonmc.templates.dgen.rcp.Ingr;
import io.github.cottonmc.templates.dgen.rcp.Rcp;
import io.github.cottonmc.templates.dgen.tag.AddToTag;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Dgen {
	public static void main(String[] args) throws Exception {
		System.out.println("hi");
		for(int i = 0; i < args.length; i++) {
			System.out.println("arg " + i + ":\t" + args[i]);
		}
		
		Path genPath = Path.of(args[0]);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		BiConsumer<String, JsonElement> jsonWriter = (subpath, jsonElem) -> {
			Path dst = genPath.resolve(subpath);
			String json = gson.toJson(jsonElem);
			
			try {
				if(Files.notExists(dst)) {
					System.out.println("writing new " + dst);
					Files.createDirectories(dst.getParent());
					Files.writeString(dst, json);
				} else {
					// we have ExistingFileHelper at home
					String curr = Files.readString(dst);
					if(!curr.equals(json)) {
						System.out.println("writing chg " + dst);
						Files.writeString(dst, json);
					} else {
						System.out.println("    no chgs " + dst);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		
		
		Map<Character, Ingr<?>> key = Map.of(
			'~', Ingr.fromString("minecraft:string"),
			'I', Ingr.fromString("minecraft:bamboo"),
			
			'C', Ingr.fromString("#minecraft:candles"),
			'X', Ingr.fromString("minecraft:iron_ingot"),
			'S', Ingr.fromString("minecraft:cobblestone")
		);
		
		List<FacetHolder> templates = List.of(
			new Tmpl("button") {{
				selfdrops();
				shapedT(1).key(key).rows("~", "I");
				ibTag("minecraft:blocks/buttons");
				mineableAxe();
			}},
			new Tmpl("candle") {{
				//todo candle loot table (needs a weird loot function)
				shapedT(1).key(key).rows("~", "I", "C");
				ibTag("minecraft:blocks/candles");
				mineableAxe();
			}},
			new Tmpl("carpet") {{
				selfdrops();
				shapedT(12).key(key).rows("~~", "II");
				ibTag("minecraft:blocks/wool_carpets");
				mineableAxe();
			}},
			new Tmpl("cool_rivulet") {{
				selfdrops();
				//no recipe
				mineablePick();
			}},
			new Tmpl("cube") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "I~I", "III");
				mineableAxe();
			}},
			new Tmpl("door") {{
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "II ");
				ibTag("minecraft:blocks/wooden_doors");
				mineableAxe();
			}},
			new Tmpl("fence") {{
				selfdrops();
				shapedT(8).key(key).rows("I~I", "I~I");
				ibTag("minecraft:blocks/wooden_fences");
				mineableAxe();
			}},
			new Tmpl("fence_gate") {{
				selfdrops();
				shapedT(2).key(key).rows("~I~", "~I~");
				ibTag("minecraft:blocks/fence_gates");
				mineableAxe();
			}},
			new Tmpl("iron_door") {{
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "IIX");
				ibTag("minecraft:blocks/doors");
				mineablePick();
			}},
			new Tmpl("iron_trapdoor") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~X~");
				ibTag("minecraft:blocks/trapdoors");
				mineablePick();
			}},
			new Tmpl("lever") {{
				selfdrops();
				shapedT(1).key(key).rows("~", "I", "C");
				mineableAxe();
			}},
			new Tmpl("pane") {{
				selfdrops();
				shapedT(16).key(key).rows("~ ~", "III", "III");
				mineableAxe();
			}},
			new Tmpl("post") {{
				selfdrops();
				shapedT(8).key(key).rows("I~", "I ", "I~");
				mineableAxe();
			}},
			new Tmpl("post_cross") {{
				selfdrops();
				//TODO shapeless recipes
				mineableAxe();
			}},
			new Tmpl("pressure_plate") {{
				selfdrops();
				shapedT(1).key(key).rows("~ ", "II");
				ibTag("minecraft:blocks/wooden_pressure_plates");
				mineableAxe();
			}},
			new Tmpl("slab") {{
				slabdrops();
				shapedT(6).key(key).rows(" ~ ", "III");
				ibTag("minecraft:blocks/wooden_slabs");
				mineableAxe();
			}},
			new Tmpl("slope") {{
				selfdrops();
				shapedT(4).key(key).rows("I  ", "I~ ", "III");
				mineableAxe();
			}},
			new Tmpl("stairs") {{
				selfdrops();
				shapedT(10).key(key).rows("I~ ", "II~", "III");
				ibTag("minecraft:blocks/wooden_stairs");
				mineableAxe();
			}},
			new Tmpl("tiny_slope") {{
				selfdrops();
				shapedT(8).key(key).rows("I~", "II");
				mineableAxe();
			}},
			new Tmpl("tnt") {{
				selfdrops();
				//TODO shapeless recipes
				//no minable tag, instabreak
			}},
			new Tmpl("trapdoor") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~ ~");
				ibTag("minecraft:blocks/wooden_trapdoors");
				mineableAxe();
			}},
			new Tmpl("vertical_slab") {{
				slabdrops();
				shapedT(6).key(key).rows("I ", "I~", "I ");
				ibTag("minecraft:blocks/wooden_slabs");
				mineableAxe();
			}},
			new Tmpl("wall") {{
				selfdrops();
				shapedT(8).key(key).rows(" ~ ", "III", "III");
				ibTag("minecraft:blocks/walls");
				mineableAxe();
			}}
		);
		
		for(FacetHolder h : templates) h.gatherFacets(); //reflective stuff
		
		Map<String, Set<AddToTag>> tags = new HashMap<>();
		
		for(FacetHolder h : templates) {
			//loot tables
			for(Tbl tbl : h.<Tbl>getFacets(Tbl.class)) {
				jsonWriter.accept("data/" + tbl.namespace() + "/loot_tables/blocks/" + tbl.path() + ".json", tbl.ser());
			}
			
			//recipes
			for(Rcp<?> rcp : h.<Rcp<?>>getFacets(Rcp.class)) {
				jsonWriter.accept("data/" + rcp.namespace() + "/recipes/" + rcp.path() + ".json", rcp.ser());
			}
			
			for(AddToTag att : h.<AddToTag>getFacets(AddToTag.class)) {
				tags.computeIfAbsent(att.id, __ -> new LinkedHashSet<>()).add(att);
			}
		}
		
		//tags
		tags.forEach((id, atts) -> {
			//TODO i should really use *actual* ID objects for the keys of this map
			String[] split = id.split(":");
			String namespace, path;
			if(split.length == 2) {
				namespace = split[0]; path = split[1];
			} else {
				namespace = "minecraft"; path = split[0];
			}
			
			jsonWriter.accept("data/" + namespace + "/tags/" + path + ".json", AddToTag.makeTag(atts));
		});
	}
}
