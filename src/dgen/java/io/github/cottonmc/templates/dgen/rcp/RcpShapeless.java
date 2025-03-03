package io.github.cottonmc.templates.dgen.rcp;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.gensupport.Id;
import io.github.cottonmc.templates.dgen.Tmpl;

import java.util.ArrayList;
import java.util.List;

public class RcpShapeless extends Rcp<RcpShapeless> {
	public List<Ingr<?>> ingrs = new ArrayList<>(9);
	
	public RcpShapeless add(Object... in) {
		for(Object i : in) {
			if(i instanceof Ingr<?> ing) ingrs.add(ing);
			else if(i instanceof Tmpl tmpl) ingrs.add(new Ingr.I(tmpl.itemId));
			else if(i instanceof Id idd) ingrs.add(new Ingr.I(idd));
			else if(i instanceof String s) ingrs.add(Ingr.parse(s));
			else throw new IllegalArgumentException(i.getClass().toString());
		}
		return this;
	}
	
	public RcpShapeless rep(int count, Object... ingrs) {
		for(int i = 0; i < count; i++) add(ingrs);
		return this;
	}
	
	@Override
	public JsonObject ser() {
		JsonObject obj = super.ser();
		obj.addProperty("type", "minecraft:crafting_shapeless");
		obj.add("ingredients", serList(ingrs));
		return obj;
	}
}
