package io.github.cottonmc.templates.dgen.adv;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Idable;
import io.github.cottonmc.templates.dgen.Ser;
import io.github.cottonmc.templates.dgen.rcp.Rcp;

import java.util.ArrayList;
import java.util.List;

//This is 100% a sketch atm
public class AdvancementSketch extends Idable<AdvancementSketch> implements Ser<JsonObject> {
	List<String> recipeRewards = new ArrayList<>();
	
	public AdvancementSketch recipeReward(Object... things) {
		for(Object thing : things) {
			if(thing instanceof Rcp<?> r) recipeRewards.add(r.id());
			else if(thing instanceof String s) recipeRewards.add(s);
			else throw new IllegalArgumentException(thing.getClass().toString());
		}
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = new Gson().fromJson("""
{
	"parent": "minecraft:recipes/root",
	"criteria": {
		"has_bamboo": {
			"trigger": "minecraft:inventory_changed",
			"conditions": {"items":[{"items":["minecraft:bamboo"]}]}
		},
		"has_the_recipe": {
			"trigger": "minecraft:recipe_unlocked",
			"conditions": {
				"recipe": "templates:slope"
			}
		}
	},
	"requirements": [
		[
			"has_bamboo",
			"has_the_recipe"
		]
	]
}""", JsonObject.class);
		
		JsonObject rewards = new JsonObject();
		rewards.add("recipes", serList(recipeRewards));
		obj.add("rewards", rewards);
		
		return obj;
	}
}
