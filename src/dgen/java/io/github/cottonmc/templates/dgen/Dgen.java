package io.github.cottonmc.templates.dgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.cottonmc.templates.dgen.rcp.Ingr;
import io.github.cottonmc.templates.dgen.rcp.Rcp;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Dgen {
	public static void main(String[] args) {
		System.out.println("hi");
		for(int i = 0; i < args.length; i++) {
			System.out.println("arg " + i + ":\t" + args[i]);
		}
		
		Path genPath = Path.of(args[0]);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
		Map<Character, Ingr<?>> key = Map.of(
			'~', Ingr.fromString("minecraft:string"),
			'I', Ingr.fromString("minecraft:bamboo"),
			
			'C', Ingr.fromString("#minecraft:candles"),
			'X', Ingr.fromString("minecraft:iron_ingot"),
			'S', Ingr.fromString("minecraft:cobblestone")
		);
		
		List<Tmpl> templates = List.of(
			new Tmpl("button") {{
				selfdrops();
				shapedT(1).key(key).rows("~", "I");
			}},
			new Tmpl("candle") {{
				//todo candle loot table (needs a weird loot function)
				shapedT(1).key(key).rows("~", "I", "C");
			}},
			new Tmpl("carpet") {{
				selfdrops();
				shapedT(12).key(key).rows("~~", "II");
			}},
			new Tmpl("cool_rivulet") {{
				selfdrops();
				//no recipe
			}},
			new Tmpl("cube") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "I~I", "III");
			}},
			new Tmpl("door") {{
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "II ");
			}},
			new Tmpl("fence") {{
				selfdrops();
				shapedT(8).key(key).rows("I~I", "I~I");
			}},
			new Tmpl("fence_gate") {{
				selfdrops();
				shapedT(2).key(key).rows("~I~", "~I~");
			}},
			new Tmpl("iron_door") {{
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "IIX");
			}},
			new Tmpl("iron_trapdoor") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~X~");
			}},
			new Tmpl("lever") {{
				selfdrops();
				shapedT(1).key(key).rows("~", "I", "C");
			}},
			new Tmpl("pane") {{
				selfdrops();
				shapedT(16).key(key).rows("~ ~", "III", "III");
			}},
			new Tmpl("post") {{
				selfdrops();
				shapedT(8).key(key).rows("I~", "I ", "I~");
			}},
			new Tmpl("post_cross") {{
				selfdrops();
				//TODO shapeless recipes
			}},
			new Tmpl("pressure_plate") {{
				selfdrops();
				shapedT(1).key(key).rows("~ ", "II");
			}},
			new Tmpl("slab") {{
				slabdrops();
				shapedT(6).key(key).rows(" ~ ", "III");
			}},
			new Tmpl("slope") {{
				selfdrops();
				shapedT(4).key(key).rows("I  ", "I~ ", "III");
			}},
			new Tmpl("stairs") {{
				selfdrops();
				shapedT(10).key(key).rows("I~ ", "II~", "III");
			}},
			new Tmpl("tiny_slope") {{
				selfdrops();
				shapedT(8).key(key).rows("I~", "II");
			}},
			new Tmpl("tnt") {{
				selfdrops();
				//TODO shapeless recipes
			}},
			new Tmpl("trapdoor") {{
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~ ~");
			}},
			new Tmpl("vertical_slab") {{
				slabdrops();
				shapedT(6).key(key).rows("I ", "I~", "I ");
			}},
			new Tmpl("wall") {{
				selfdrops();
				shapedT(8).key(key).rows(" ~ ", "III", "III");
			}}
		);
		
		for(Tmpl t : templates) go(t, (p, elem) -> {
			Path dst = genPath.resolve(p);
			
			try {
				if(Files.notExists(dst)) {
					System.out.println("writing new " + dst);
					Files.createDirectories(dst.getParent());
					Files.writeString(dst, gson.toJson(elem));
				} else {
					// we have ExistingFileHelper at home
					String curr = Files.readString(dst);
					String next = gson.toJson(elem);
					if(!curr.equals(next)) {
						System.out.println("writing chg " + dst);
						Files.writeString(dst, next);
					} else {
						System.out.println("    no chgs " + dst);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static void go(Tmpl tmpl, BiConsumer<String, JsonElement> toWrite) {
		try {
			tmpl.gatherFacets();
			
			//loot tables
			for(Tbl tbl : tmpl.<Tbl>getFacets(Tbl.class)) {
				toWrite.accept("data/" + tbl.namespace() + "/loot_tables/blocks/" + tbl.path() + ".json", tbl.ser());
			}
			
			//recipes
			for(Rcp<?> rcp : tmpl.<Rcp<?>>getFacets(Rcp.class)) {
				toWrite.accept("data/" + rcp.namespace() + "/recipes/" + rcp.path() + ".json", rcp.ser());
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
