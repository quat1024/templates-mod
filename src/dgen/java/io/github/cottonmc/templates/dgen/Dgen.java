package io.github.cottonmc.templates.dgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.cottonmc.templates.dgen.adv.AdvancementSketch;
import io.github.cottonmc.templates.dgen.lang.AddToLang;
import io.github.cottonmc.templates.dgen.rcp.Ingr;
import io.github.cottonmc.templates.dgen.rcp.Rcp;
import io.github.cottonmc.templates.dgen.tag.AddToTag;
import io.github.cottonmc.templates.dgen.tbl.Tbl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Dgen {
	
	public static final Id EN_US = new Id("templates-lang-gen:en_us");
	
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
					
//					if(true) throw new IllegalStateException("no new files! " + dst);
					
					System.out.println("writing new " + dst);
					Files.createDirectories(dst.getParent());
					Files.writeString(dst, json);
				} else {
					// we have ExistingFileHelper at home
					String curr = Files.readString(dst);
					if(!curr.equals(json)) {
						
//						if(true) throw new IllegalStateException("no changed files! $$$$$ old $$$$\n " + curr + "\n$$$$ new $$$$\n" + json);
						
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
			'~', Ingr.parse("minecraft:string"),
			'I', Ingr.parse("minecraft:bamboo"),
			
			'C', Ingr.parse("#minecraft:candles"),
			'X', Ingr.parse("minecraft:iron_ingot"),
			'S', Ingr.parse("minecraft:cobblestone")
		);
		
		List<FacetHolder> templates = List.of(
			new Tmpl("button") {{
				enUS("Button Template");
				selfdrops();
				shapedT(1).key(key).rows("~", "I");
				ibTag("minecraft:buttons");
				mineableAxe();
			}},
			new Tmpl("candle") {{
				enUS("Candle Template");
				candledrops();
				shapedT(1).key(key).rows("~", "I", "C");
				ibTag("minecraft:candles");
				mineableAxe();
			}},
			new Tmpl("carpet") {{
				enUS("Carpet Template");
				selfdrops();
				shapedT(12).key(key).rows("~~", "II");
				ibTag("minecraft:wool_carpets");
				mineableAxe();
			}},
			new Tmpl("cool_rivulet") {{
				enUS("cool rivulet");
				selfdrops();
				//no recipe
				mineablePick();
			}},
			new Tmpl("cube") {{
				enUS("Cube Template");
				selfdrops();
				shapedT(4).key(key).rows("III", "I~I", "III");
				mineableAxe();
			}},
			new Tmpl("door") {{
				enUS("Door Template");
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "II ");
				ibTag("minecraft:wooden_doors");
				mineableAxe();
			}},
			new Tmpl("fence") {{
				enUS("Fence Template");
				selfdrops();
				shapedT(8).key(key).rows("I~I", "I~I");
				ibTag("minecraft:wooden_fences");
				mineableAxe();
			}},
			new Tmpl("fence_gate") {{
				enUS("Fence Gate Template");
				selfdrops();
				shapedT(2).key(key).rows("~I~", "~I~");
				ibTag("minecraft:fence_gates");
				mineableAxe();
			}},
			new Tmpl("iron_door") {{
				enUS("Iron Door Template");
				doordrops();
				shapedT(2).key(key).rows("II ", "II~", "IIX");
				ibTag("minecraft:doors");
				mineablePick();
			}},
			new Tmpl("iron_trapdoor") {{
				enUS("Iron Trapdoor Template");
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~X~");
				ibTag("minecraft:trapdoors");
				mineablePick();
			}},
			new Tmpl("lever") {{
				enUS("Lever Template");
				selfdrops();
				shapedT(1).key(key).rows("~", "I", "C");
				mineableAxe();
			}},
			new Tmpl("pane") {{
				enUS("Pane Template");
				selfdrops();
				shapedT(16).key(key).rows("~ ~", "III", "III");
				mineableAxe();
			}},
			new Tmpl("post") {{
				enUS("Post Template");
				selfdrops();
				shapedT(8).key(key).rows("I~", "I ", "I~");
				mineableAxe();
			}},
			new Tmpl("post_cross") {{
				enUS("Post Cross Template");
				selfdrops();
				shapelessT().rep(2, "templates:post");
				mineableAxe();
			}},
			new Tmpl("pressure_plate") {{
				enUS("Pressure Plate Template");
				selfdrops();
				shapedT(1).key(key).rows("~ ", "II");
				ibTag("minecraft:wooden_pressure_plates");
				mineableAxe();
			}},
			new Tmpl("slab") {{
				enUS("Slab Template");
				slabdrops();
				shapedT(6).key(key).rows(" ~ ", "III");
				ibTag("minecraft:wooden_slabs");
				mineableAxe();
			}},
			new Tmpl("slope") {{
				enUS("Slope Template");
				selfdrops();
				shapedT(4).key(key).rows("I  ", "I~ ", "III");
				mineableAxe();
			}},
			new Tmpl("stairs") {{
				enUS("Stairs Template");
				selfdrops();
				shapedT(10).key(key).rows("I~ ", "II~", "III");
				ibTag("minecraft:wooden_stairs");
				mineableAxe();
			}},
			new Tmpl("tiny_slope") {{
				enUS("Tiny Slope Template");
				selfdrops();
				shapedT(8).key(key).rows("I~", "II");
				mineableAxe();
			}},
			new Tmpl("tnt") {{
				enUS("TNTemplate");
				selfdrops();
				shapelessT().add("templates:cube", "minecraft:tnt");
				//no minable tag, instabreak
			}},
			new Tmpl("trapdoor") {{
				enUS("Trapdoor Template");
				selfdrops();
				shapedT(4).key(key).rows("III", "III", "~ ~");
				ibTag("minecraft:wooden_trapdoors");
				mineableAxe();
			}},
			new Tmpl("vertical_slab") {{
				enUS("Vertical Slab Template");
				slabdrops();
				shapedT(6).key(key).rows("I ", "I~", "I ");
				ibTag("minecraft:wooden_slabs");
				mineableAxe();
			}},
			new Tmpl("wall") {{
				enUS("Wall Template");
				selfdrops();
				shapedT(8).key(key).rows(" ~ ", "III", "III");
				ibTag("minecraft:walls");
				mineableAxe();
			}}
		);
		
		for(FacetHolder h : templates) h.gatherReflectiveFacets(); //(TODO i dont use reflective stuff atm)
		
		FacetHolder allFacets = new FacetHolder().addAll(templates);
		allFacets.addFacet(new AddToLang(EN_US, "itemGroup.templates.tab", "Templates")); //CREATIVE tab name
		
		//loot tables
		for(Tbl tbl : allFacets.getFacets(Tbl.class)) {
			jsonWriter.accept("data/" + tbl.ns() + "/loot_tables/blocks/" + tbl.path() + ".json", tbl.ser());
		}
		
		//recipes
		AdvancementSketch recipeAdv = new AdvancementSketch().id("templates:recipes/decorations/templates");
		for(Rcp<?> rcp : allFacets.getFacets(Rcp.class)) {
			jsonWriter.accept("data/" + rcp.ns() + "/recipes/" + rcp.path() + ".json", rcp.ser());
			
			if("templates".equals(rcp.group)) recipeAdv.recipeReward(rcp);
		}
		//recipe advancement
		jsonWriter.accept("data/" + recipeAdv.ns() + "/advancements/" + recipeAdv.path() + ".json", recipeAdv.ser());
		
		//tags
		Map<Id, Set<AddToTag>> tags = new HashMap<>();
		for(AddToTag att : allFacets.getFacets(AddToTag.class)) {
			tags.computeIfAbsent(att.id, __ -> new LinkedHashSet<>()).add(att);
		}
		tags.forEach((id, atts) -> jsonWriter.accept("data/" + id.ns + "/tags/" + id.path + ".json", AddToTag.makeTag(atts)));
		
		//lang
		Map<Id, Set<AddToLang>> langs = new HashMap<>();
		for(AddToLang lang : allFacets.getFacets(AddToLang.class)) {
			langs.computeIfAbsent(lang.id, __ -> new LinkedHashSet<>()).add(lang);
		}
		langs.forEach((id, atls) -> jsonWriter.accept("assets/" + id.ns + "/lang/" + id.path + ".json", AddToLang.makeLang(atls)));
	}
}
