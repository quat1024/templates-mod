package io.github.cottonmc.templates.dgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.cottonmc.templates.dgen.rcp.Rcp;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

public class Dgen {
	public static void main(String[] args) {
		System.out.println("hi");
		for(int i = 0; i < args.length; i++) {
			System.out.println("arg " + i + ":\t" + args[i]);
		}
		
		Path genPath = Path.of(args[0]);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
		List<Tmpl> templates = List.of(
			new Tmpl("button") {{
				selfdrops();
				shaped(1, "~", "I");
			}},
			new Tmpl("candle") {{
				//todo candle loot table (needs a weird loot function)
				//TODO recipe
			}},
			new Tmpl("carpet") {{
				selfdrops();
				shaped(12, "~~", "II");
			}},
			new Tmpl("cool_rivulet") {{
				selfdrops();
				//no recipe
			}},
			new Tmpl("cube") {{
				selfdrops();
				shaped(4, "III", "I~I", "III");
			}},
			new Tmpl("door") {{
				doordrops();
				shaped(2, "II ", "II~", "II ");
			}},
			new Tmpl("fence") {{
				selfdrops();
				shaped(8, "I~I", "I~I");
			}},
			new Tmpl("fence_gate") {{
				selfdrops();
				shaped(2, "~I~", "~I~");
			}},
			new Tmpl("iron_door") {{
				doordrops();
				//TODO needs custom recipe key
			}},
			new Tmpl("iron_trapdoor") {{
				selfdrops();
				//TODO needs custom recipe key
			}},
			new Tmpl("lever") {{
				selfdrops();
				//TODO needs custom recipe key
			}},
			new Tmpl("pane") {{
				selfdrops();
				shaped(16, "~ ~", "III", "III");
			}},
			new Tmpl("post") {{
				selfdrops();
				shaped(8, "I~", "I ", "I~");
			}},
			new Tmpl("post_cross") {{
				selfdrops();
				//TODO shapeless recipes
			}},
			new Tmpl("pressure_plate") {{
				selfdrops();
				shaped(1, "~ ", "II");
			}},
			new Tmpl("slab") {{
				slabdrops();
				shaped(6, " ~ ", "III");
			}},
			new Tmpl("slope") {{
				selfdrops();
				shaped(4, "I  ", "I~ ", "III");
			}},
			new Tmpl("stairs") {{
				selfdrops();
				shaped(10, "I~ ", "II~", "III");
			}},
			new Tmpl("tiny_slope") {{
				selfdrops();
				shaped(8, "I~", "II");
			}},
			new Tmpl("tnt") {{
				selfdrops();
				//TODO shapeless recipes
			}},
			new Tmpl("trapdoor") {{
				selfdrops();
				shaped(4, "III", "III", "~ ~");
			}},
			new Tmpl("vertical_slab") {{
				slabdrops();
				shaped(6, "I ", "I~", "I ");
			}},
			new Tmpl("wall") {{
				selfdrops();
				shaped(8, " ~ ", "III", "III");
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
