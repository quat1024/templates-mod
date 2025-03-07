package io.github.cottonmc.templates.dgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.cottonmc.templates.dgen.rcp.Ingr;
import io.github.cottonmc.templates.dgen.rcp.Rcp;
import io.github.cottonmc.templates.dgen.tag.AddToTag;
import io.github.cottonmc.templates.dgen.tbl.Tbl;
import io.github.cottonmc.templates.gensupport.AddTooltip;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.gensupport.ItemOverrideMapping;
import io.github.cottonmc.templates.gensupport.MagicPaths;
import io.github.cottonmc.templates.gensupport.TemplateModelMapping;
import net.minecraft.util.Formatting;

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
		
		//build the final facet holder containing all facets
		FacetHolder allFacets = new FacetHolder();
		
		allFacets.addFacet(new AddToLang(EN_US, "itemGroup.templates.tab", "Templates"));
		String sneakSideways = allFacets.addFacet(new AddToLang(EN_US, "templates.tooltip.sneakSideways", "Sneak to place sideways against walls"))
			.key;
		
		List<FacetHolder> templates = new ArrayList<>();
		
		Tmpl button = add(templates, new Tmpl("button") {{
			enUS("Button Template");
			selfdrops();
			shapedT(1).key(key).rows("~", "I");
			ibTag("minecraft:wooden_buttons");
			mineableAxe();
			autoRetexture().id("templates:button_special").base("minecraft:block/button");
			autoRetexture().id("templates:button_pressed_special").base("minecraft:block/button_pressed");
			itemOverride(autoRetexture().id("templates:button_inventory_special").base("minecraft:block/button_inventory"));
		}});
		
		Tmpl candle = add(templates, new Tmpl("candle") {{
			enUS("Candle Template");
			candledrops();
			shapedT(1).key(key).rows("~", "I", "C");
			ibTag("minecraft:candles");
			mineableAxe();
			autoRetexture().id("templates:one_candle_special").base("minecraft:block/template_candle");
			autoRetexture().id("templates:two_candles_special").base("minecraft:block/template_two_candles");
			autoRetexture().id("templates:three_candles_special").base("minecraft:block/template_three_candles");
			autoRetexture().id("templates:four_candles_special").base("minecraft:block/template_four_candles");
			itemGenerated("templates:item/candle");
		}});
		
		Tmpl carpet = add(templates, new Tmpl("carpet") {{
			enUS("Carpet Template");
			selfdrops();
			shapedT(12).key(key).rows("~~", "II");
			ibTag("minecraft:wool_carpets");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:carpet_special").base("minecraft:block/carpet"));
		}});
		
		//TODO this isn't actually a template, lol
		Tmpl cool_rivulet = add(templates, new Tmpl("cool_rivulet") {{
			enUS("cool rivulet");
			selfdrops();
			mineablePick();
			itemForwardingToBlock();
			
			tooltip(blockId.toTranslationKey("block")); //put the block name again on the tooltip. the funny!!!!!
		}});
		
		Tmpl cube = add(templates, new Tmpl("cube") {{
			enUS("Cube Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "I~I", "III");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:cube_special").base("minecraft:block/cube"));
		}});
		
		Tmpl door = add(templates, new Tmpl("door") {{
			enUS("Door Template");
			doordrops();
			shapedT(2).key(key).rows("II ", "II~", "II ");
			ibTag("minecraft:wooden_doors");
			mineableAxe();
			autoRetexture().id("templates:door_bottom_left_special").base("minecraft:block/door_bottom_left");
			autoRetexture().id("templates:door_bottom_right_special").base("minecraft:block/door_bottom_right");
			autoRetexture().id("templates:door_top_left_special").base("minecraft:block/door_top_left");
			autoRetexture().id("templates:door_top_right_special").base("minecraft:block/door_top_right");
			autoRetexture().id("templates:door_bottom_left_open_special").base("minecraft:block/door_bottom_left_open");
			autoRetexture().id("templates:door_bottom_right_open_special").base("minecraft:block/door_bottom_right_open");
			autoRetexture().id("templates:door_top_left_open_special").base("minecraft:block/door_top_left_open");
			autoRetexture().id("templates:door_top_right_open_special").base("minecraft:block/door_top_right_open");
			itemGenerated("templates:item/door");
		}});
		
		Tmpl fence = add(templates, new Tmpl("fence") {{
			enUS("Fence Template");
			selfdrops();
			shapedT(8).key(key).rows("I~I", "I~I");
			ibTag("minecraft:wooden_fences");
			mineableAxe();
			autoRetexture().id("templates:fence_post_special").base("minecraft:block/fence_post");
			jsonRetexture().id("templates:fence_side_special").base("templates:block/fence_side");
			itemOverride(autoRetexture().id("templates:fence_inventory_special").base("minecraft:block/fence_inventory"));
		}});
		
		Tmpl fence_gate = add(templates, new Tmpl("fence_gate") {{
			enUS("Fence Gate Template");
			selfdrops();
			shapedT(2).key(key).rows("~I~", "~I~");
			ibTag("minecraft:fence_gates");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:fence_gate_special").base("minecraft:block/template_fence_gate"));
			autoRetexture().id("templates:fence_gate_open_special").base("minecraft:block/template_fence_gate_open");
			autoRetexture().id("templates:fence_gate_wall_special").base("minecraft:block/template_fence_gate_wall");
			autoRetexture().id("templates:fence_gate_wall_open_special").base("minecraft:block/template_fence_gate_wall_open");
		}});
		
		Tmpl iron_door = add(templates, new Tmpl("iron_door") {{
			enUS("Iron Door Template");
			doordrops();
			shapedT(2).key(key).rows("II ", "II~", "IIX");
			ibTag("minecraft:doors");
			mineablePick();
			//uses door models
			itemGenerated("templates:item/door");
		}});
		
		Tmpl iron_trapdoor = add(templates, new Tmpl("iron_trapdoor") {{
			enUS("Iron Trapdoor Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "III", "~X~");
			ibTag("minecraft:trapdoors");
			mineablePick();
			//uses trapdoor models
			itemOverride("templates:trapdoor_bottom_special");
		}});
		
		Tmpl lever = add(templates, new Tmpl("lever") {{
			enUS("Lever Template");
			selfdrops();
			shapedT(1).key(key).rows("~", "I", "C");
			mineableAxe();
			jsonRetexture().id("templates:lever_special").base("templates:block/lever");
			jsonRetexture().id("templates:lever_on_special").base("templates:block/lever_on");
			itemGenerated("templates:item/lever");
		}});
		
		Tmpl pane = add(templates, new Tmpl("pane") {{
			enUS("Pane Template");
			selfdrops();
			shapedT(16).key(key).rows("~ ~", "III", "III");
			mineableAxe();
			autoRetexture().id("templates:glass_pane_post_special").base("minecraft:block/glass_pane_post");
			autoRetexture().id("templates:glass_pane_noside_special").base("minecraft:block/glass_pane_noside");
			autoRetexture().id("templates:glass_pane_noside_alt_special").base("minecraft:block/glass_pane_noside_alt");
			jsonRetexture().id("templates:glass_pane_side_special").base("templates:block/glass_pane_side");
			jsonRetexture().id("templates:glass_pane_side_alt_special").base("templates:block/glass_pane_side_alt");
			itemGenerated("minecraft:block/scaffolding_top");
		}});
		
		Tmpl post = add(templates, new Tmpl("post") {{
			enUS("Post Template");
			selfdrops();
			shapedT(8).key(key).rows("I~", "I ", "I~");
			mineableAxe();
			//uses fence model for blocks
			itemOverride(autoRetexture().id("templates:fence_post_inventory_special").base("templates:block/fence_post_inventory"));
		}});
		
		Tmpl post_cross = add(templates, new Tmpl("post_cross") {{
			enUS("Post Cross Template");
			selfdrops();
			shapelessT().add(post, post);
			mineableAxe();
			itemOverride(autoRetexture().id("templates:post_cross_special").base("templates:block/post_cross"));
		}});
		
		Tmpl pressure_plate = add(templates, new Tmpl("pressure_plate") {{
			enUS("Pressure Plate Template");
			selfdrops();
			shapedT(1).key(key).rows("~ ", "II");
			ibTag("minecraft:wooden_pressure_plates");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:pressure_plate_up_special").base("minecraft:block/pressure_plate_up"));
			autoRetexture().id("templates:pressure_plate_down_special").base("minecraft:block/pressure_plate_down");
		}});
		
		Tmpl slab = add(templates, new Tmpl("slab") {{
			enUS("Slab Template");
			slabdrops();
			shapedT(6).key(key).rows(" ~ ", "III");
			ibTag("minecraft:wooden_slabs");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:slab_bottom_special").base("minecraft:block/slab"));
			autoRetexture().id("templates:slab_top_special").base("minecraft:block/slab_top");
			
			shapelessT().id(Id.t("slab_from_vert_slab")).add("templates:vertical_slab");
		}});
		
		Tmpl slope = add(templates, new Tmpl("slope") {{
			enUS("Slope Template");
			selfdrops();
			shapedT(4).key(key).rows("I  ", "I~ ", "III");
			mineableAxe();
			//mesh model defined in-code with this id
			itemOverride("templates:slope_special");
			
			tooltip(sneakSideways);
		}});
		
		Tmpl stairs = add(templates, new Tmpl("stairs") {{
			enUS("Stairs Template");
			selfdrops();
			shapedT(10).key(key).rows("I~ ", "II~", "III");
			ibTag("minecraft:wooden_stairs");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:stairs_special").base("minecraft:block/stairs"));
			autoRetexture().id("templates:inner_stairs_special").base("minecraft:block/inner_stairs");
			autoRetexture().id("templates:outer_stairs_special").base("minecraft:block/outer_stairs");
		}});
		
		Tmpl tiny_slope = add(templates, new Tmpl("tiny_slope") {{
			enUS("Tiny Slope Template");
			selfdrops();
			shapedT(8).key(key).rows("I~", "II");
			mineableAxe();
			//mesh model defined in-code with this id
			itemOverride("templates:tiny_slope_special");
			
			tooltip(sneakSideways);
		}});
		
		Tmpl tnt = add(templates, new Tmpl("tnt") {{
			enUS("TNTemplate");
			selfdrops();
			shapelessT().add(cube, "minecraft:tnt");
			//no mineable tag, instabreak
			//reuses cube model
			itemOverride("templates:cube_special");
		}});
		
		Tmpl trapdoor = add(templates, new Tmpl("trapdoor") {{
			enUS("Trapdoor Template");
			selfdrops();
			shapedT(4).key(key).rows("III", "III", "~ ~");
			ibTag("minecraft:wooden_trapdoors");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:trapdoor_bottom_special").base("minecraft:block/template_trapdoor_bottom"));
			autoRetexture().id("templates:trapdoor_top_special").base("minecraft:block/template_trapdoor_top");
			jsonRetexture().id("templates:trapdoor_open_special").base("templates:block/trapdoor_open");
		}});
		
		Tmpl vertical_slab = add(templates, new Tmpl("vertical_slab") {{
			enUS("Vertical Slab Template");
			slabdrops();
			shapedT(6).key(key).rows("I ", "I~", "I ");
			ibTag("minecraft:wooden_slabs");
			mineableAxe();
			itemOverride(autoRetexture().id("templates:vertical_slab_special").base("templates:block/vertical_slab"));
			
			shapelessT().id(Id.t("vert_slab_from_slab")).add(slab);
		}});
		
		Tmpl wall = add(templates, new Tmpl("wall") {{
			enUS("Wall Template");
			selfdrops();
			shapedT(8).key(key).rows(" ~ ", "III", "III");
			ibTag("minecraft:walls");
			mineableAxe();
			autoRetexture().id("templates:wall_post_special").base("minecraft:block/template_wall_post");
			jsonRetexture().id("templates:wall_side_special").base("templates:block/wall_side");
			jsonRetexture().id("templates:wall_side_tall_special").base("templates:block/wall_side_tall");
			itemOverride(autoRetexture().id("templates:wall_inventory_special").base("minecraft:block/wall_inventory"));
		}});
		
		allFacets.addAll(templates);
		
		/// WRITING ! ///
		
		//loot tables
		allFacets.forEachFacet(Tbl.class, tbl -> writeJson("data/" + tbl.ns() + "/loot_table/blocks/" + tbl.path() + ".json", tbl.ser()));
		
		//recipes
		AdvancementSketch recipeAdv = new AdvancementSketch().id("templates:recipes/decorations/templates");
		allFacets.forEachFacet(Rcp.class, rcp -> {
			writeJson("data/" + rcp.ns() + "/recipe/" + rcp.path() + ".json", rcp.ser());
			
			if("templates".equals(rcp.group)) recipeAdv.recipeReward(rcp);
		});
		//recipe advancement
		writeJson("data/" + recipeAdv.ns() + "/advancement/" + recipeAdv.path() + ".json", recipeAdv.ser());
		
		//tags
		Map<Id, Set<AddToTag>> tags = new HashMap<>();
		allFacets.forEachFacet(AddToTag.class, att -> tags.computeIfAbsent(att.id, __ -> new LinkedHashSet<>()).add(att));
		tags.forEach((id, atts) -> writeJson("data/" + id.ns + "/tags/" + id.path + ".json", AddToTag.makeTag(atts)));
		
		//lang
		Map<Id, Set<AddToLang>> langs = new HashMap<>();
		allFacets.forEachFacet(AddToLang.class, lang -> langs.computeIfAbsent(lang.id, __ -> new LinkedHashSet<>()).add(lang));
		langs.forEach((id, atls) -> writeJson("assets/" + id.ns + "/lang/" + id.path + ".json", AddToLang.makeLang(atls)));
		
		//models (TODO datagen more models)
		allFacets.forEachFacet(ItemModel.class, dim ->
			writeJson("assets/" + dim.id.ns + "/models/item/" + dim.id.path + ".json", dim.ser()));
		
		//CUSTOM STUFF!
		//template model mappings
		JsonArray everyModelMapping = new JsonArray();
		allFacets.forEachFacet(TemplateModelMapping.class, tmm -> everyModelMapping.add(tmm.ser()));
		writeJson("assets/templates/template_model_mappings.json", everyModelMapping);
		
		//item model overrides
		JsonArray everyItemOverride = new JsonArray();
		allFacets.forEachFacet(ItemOverrideMapping.class, iom -> everyItemOverride.add(iom.ser()));
		writeJson("assets/templates/template_item_overrides.json", everyItemOverride);
		
		//tooltips
		JsonArray everyTooltip = new JsonArray();
		allFacets.forEachFacet(AddTooltip.class, tt -> everyTooltip.add(tt.ser()));
		writeJson(MagicPaths.TOOLTIPS, everyTooltip);
		
		writeFile("templates-static/README.md", """
			# Note
			
			These files are used during the loading process of Templates, and therefore they
			aren't modifiable with a resource pack, sorry. They are automatically generated.""");
	}
	
	private void writeJson(String subpath, JsonElement jsonElem) {
		writeFile(subpath, gson.toJson(jsonElem));
	}
	
	private void writeFile(String subpath, String contents) {
		//paths intended to be loaded with getResourceAsStream need a leading slash
		//we don't want the leading slash right now
		if(subpath.startsWith("/")) subpath = subpath.substring(1);
		
		try {
			Path dst = genRoot.resolve(subpath);
			if(Files.notExists(dst)) {
//					if(true) throw new IllegalStateException("no new files! " + dst);
				System.out.println("writing new " + dst);
				Files.createDirectories(dst.getParent());
				Files.writeString(dst, contents);
			} else {
				// we have ExistingFileHelper at home
				String curr = Files.readString(dst);
				if(!curr.equals(contents)) {
//						if(true) throw new IllegalStateException("no changed files! $$$$$ old $$$$\n " + curr + "\n$$$$ new $$$$\n" + json);
					System.out.println("writing chg " + dst);
					Files.writeString(dst, contents);
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
