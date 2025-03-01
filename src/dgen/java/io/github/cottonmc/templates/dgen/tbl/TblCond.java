package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Ser;

public abstract class TblCond implements Ser<JsonObject> {
	
	public static class BottomDoor extends TblCond {
		public BottomDoor(String blockId) {
			this.blockId = blockId;
		}
		
		public String blockId;
		
		@Override
		public JsonObject ser() {
			JsonObject properties = new JsonObject();
			properties.addProperty("half", "lower");
			
			JsonObject obj = new JsonObject();
			obj.addProperty("condition", "minecraft:block_state_property");
			obj.addProperty("block", blockId);
			obj.add("properties", properties);
			return obj;
		}
	}
	
	public static class DoubleSlab extends TblCond {
		public DoubleSlab(String blockId) {
			this.blockId = blockId;
		}
		
		public String blockId;
		
		@Override
		public JsonObject ser() {
			JsonObject properties = new JsonObject();
			properties.addProperty("type", "double");
			
			JsonObject obj = new JsonObject();
			obj.addProperty("condition", "minecraft:block_state_property");
			obj.addProperty("block", blockId);
			obj.add("properties", properties);
			return obj;
		}
	}
	
	public static class SurvivesExp extends TblCond {
		@Override
		public JsonObject ser() {
			JsonObject obj = new JsonObject();
			obj.addProperty("condition", "minecraft:survives_explosion");
			return obj;
		}
	}
}
