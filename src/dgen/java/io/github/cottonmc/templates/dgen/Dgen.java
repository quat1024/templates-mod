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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dgen {
	public Dgen(Path genRoot) {
		this.genRoot = genRoot;
	}
	
	public static final Id EN_US = new Id("templates-lang-gen:en_us");
	
	private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final Path genRoot;
	
	public static void main(String[] args) throws Exception {
		System.out.println("hi");
		for(int i = 0; i < args.length; i++) {
			System.out.println("arg " + i + ":\t" + args[i]);
		}
		new Dgen(Path.of(args[0])).go();
	}
	
	private void go() throws Exception {
		Map<Character, Ingr<?>> key = Map.of(
			'~', Ingr.parse("minecraft:string"),
			'I', Ingr.parse("minecraft:bamboo"),
			
			'C', Ingr.parse("#minecraft:candles"),
			'X', Ingr.parse("minecraft:iron_ingot"),
			'S', Ingr.parse("minecraft:cobblestone")
		);
		
		List<FacetHolder> templates = new ArrayList<>();
		
		Tmpl button = add(templates, new Tmpl("button") {{
			enUS("Button Template");
			selfdrops();
			shapedT(1).key(key).rows("~", "I");
			ibTag("minecraft:buttons");
			mineableAxe();
		}});
		
		Tmpl candle = add(templates, new Tmpl("candle") {{
			enUS("Candle Template");
			candledrops();
			shapedT(1).key(key).rows("~", "I", "C");
			ibTag("minecraft:candles");
			mineableAxe();
		}});
		
		Tmpl carpet = add(templates, new Tmpl("carpet") {{
			enUS("Carpet Template");
			selfdrops();
			shapedT(12).key(key).rows("~~", "II");
			ibTag("minecraft:wool_carpets");
			mineableAxe();
		}});
		
		Tmpl cool_rivulet = add(templates, new Tmpl("cool_rivulet") {{
			enUS("cool rivulet");
			selfdrops();
			//no recipe
			mineablePick();
		}});
		
		Tmpl cube = add(templates, new Tmpl("cube") {{
			enUS("Cube Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "I~I", "III");
			mineableAxe();
		}});
		
		Tmpl door = add(templates, new Tmpl("door") {{
			enUS("Door Template");
			doordrops();
			shapedT(2).key(key).rows("II ", "II~", "II ");
			ibTag("minecraft:wooden_doors");
			mineableAxe();
		}});
		
		Tmpl fence = add(templates, new Tmpl("fence") {{
			enUS("Fence Template");
			selfdrops();
			shapedT(8).key(key).rows("I~I", "I~I");
			ibTag("minecraft:wooden_fences");
			mineableAxe();
		}});
		
		Tmpl fence_gate = add(templates, new Tmpl("fence_gate") {{
			enUS("Fence Gate Template");
			selfdrops();
			shapedT(2).key(key).rows("~I~", "~I~");
			ibTag("minecraft:fence_gates");
			mineableAxe();
		}});
		
		Tmpl iron_door = add(templates, new Tmpl("iron_door") {{
			enUS("Iron Door Template");
			doordrops();
			shapedT(2).key(key).rows("II ", "II~", "IIX");
			ibTag("minecraft:doors");
			mineablePick();
		}});
		
		Tmpl iron_trapdoor = add(templates, new Tmpl("iron_trapdoor") {{
			enUS("Iron Trapdoor Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "III", "~X~");
			ibTag("minecraft:trapdoors");
			mineablePick();
		}});
		
		Tmpl lever = add(templates, new Tmpl("lever") {{
			enUS("Lever Template");
			selfdrops();
			shapedT(1).key(key).rows("~", "I", "C");
			mineableAxe();
		}});
		
		Tmpl pane = add(templates, new Tmpl("pane") {{
			enUS("Pane Template");
			selfdrops();
			shapedT(16).key(key).rows("~ ~", "III", "III");
			mineableAxe();
		}});
		
		Tmpl post = add(templates, new Tmpl("post") {{
			enUS("Post Template");
			selfdrops();
			shapedT(8).key(key).rows("I~", "I ", "I~");
			mineableAxe();
		}});
		
		Tmpl post_cross = add(templates, new Tmpl("post_cross") {{
			enUS("Post Cross Template");
			selfdrops();
			shapelessT().add(post, post);
			mineableAxe();
		}});
		
		Tmpl pressure_plate = add(templates, new Tmpl("pressure_plate") {{
			enUS("Pressure Plate Template");
			selfdrops();
			shapedT(1).key(key).rows("~ ", "II");
			ibTag("minecraft:wooden_pressure_plates");
			mineableAxe();
		}});
		
		Tmpl slab = add(templates, new Tmpl("slab") {{
			enUS("Slab Template");
			slabdrops();
			shapedT(6).key(key).rows(" ~ ", "III");
			ibTag("minecraft:wooden_slabs");
			mineableAxe();
		}});
		
		Tmpl slope = add(templates, new Tmpl("slope") {{
			enUS("Slope Template");
			selfdrops();
			shapedT(4).key(key).rows("I  ", "I~ ", "III");
			mineableAxe();
		}});
		
		Tmpl stairs = add(templates, new Tmpl("stairs") {{
			enUS("Stairs Template");
			selfdrops();
			shapedT(10).key(key).rows("I~ ", "II~", "III");
			ibTag("minecraft:wooden_stairs");
			mineableAxe();
		}});
		
		Tmpl tiny_slope = add(templates, new Tmpl("tiny_slope") {{
			enUS("Tiny Slope Template");
			selfdrops();
			shapedT(8).key(key).rows("I~", "II");
			mineableAxe();
		}});
		
		Tmpl tnt = add(templates, new Tmpl("tnt") {{
			enUS("TNTemplate");
			selfdrops();
			shapelessT().add(cube, "minecraft:tnt");
			//no mineable tag, instabreak
		}});
		
		Tmpl trapdoor = add(templates, new Tmpl("trapdoor") {{
			enUS("Trapdoor Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "III", "~ ~");
			ibTag("minecraft:wooden_trapdoors");
			mineableAxe();
		}});
		
		Tmpl vertical_slab = add(templates, new Tmpl("vertical_slab") {{
			enUS("Vertical Slab Template");
			slabdrops();
			shapedT(6).key(key).rows("I ", "I~", "I ");
			ibTag("minecraft:wooden_slabs");
			mineableAxe();
		}});
		
		Tmpl wall = add(templates, new Tmpl("wall") {{
			enUS("Wall Template");
			selfdrops();
			shapedT(8).key(key).rows(" ~ ", "III", "III");
			ibTag("minecraft:walls");
			mineableAxe();
		}});
		
		//build the final facet holder containing all facets
		FacetHolder allFacets = new FacetHolder().addAll(templates);
		//creative tab name
		allFacets.addFacet(new AddToLang(EN_US, "itemGroup.templates.tab", "Templates"));
		
		/// WRITING ! ///
		
		//loot tables
		allFacets.forEachFacet(Tbl.class, tbl -> writeJson("data/" + tbl.ns() + "/loot_tables/blocks/" + tbl.path() + ".json", tbl.ser()));
		
		//recipes
		AdvancementSketch recipeAdv = new AdvancementSketch().id("templates:recipes/decorations/templates");
		allFacets.forEachFacet(Rcp.class, rcp -> {
			writeJson("data/" + rcp.ns() + "/recipes/" + rcp.path() + ".json", rcp.ser());
			
			if("templates".equals(rcp.group)) recipeAdv.recipeReward(rcp);
		});
		//recipe advancement
		writeJson("data/" + recipeAdv.ns() + "/advancements/" + recipeAdv.path() + ".json", recipeAdv.ser());
		
		//tags
		Map<Id, Set<AddToTag>> tags = new HashMap<>();
		allFacets.forEachFacet(AddToTag.class, att -> tags.computeIfAbsent(att.id, __ -> new LinkedHashSet<>()).add(att));
		tags.forEach((id, atts) -> writeJson("data/" + id.ns + "/tags/" + id.path + ".json", AddToTag.makeTag(atts)));
		
		//lang
		Map<Id, Set<AddToLang>> langs = new HashMap<>();
		allFacets.forEachFacet(AddToLang.class, lang -> langs.computeIfAbsent(lang.id, __ -> new LinkedHashSet<>()).add(lang));
		langs.forEach((id, atls) -> writeJson("assets/" + id.ns + "/lang/" + id.path + ".json", AddToLang.makeLang(atls)));
	}
	
	private void writeJson(String subpath, JsonElement jsonElem) {
		Path dst = genRoot.resolve(subpath);
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
	}
	
	private static <T> T add(List<? super T> list, T t) {
		list.add(t);
		return t;
	}
}
