package io.github.cottonmc.templates.dgen.tbl;

import com.google.gson.JsonObject;
import io.github.cottonmc.templates.dgen.Ser;

import java.util.HashMap;
import java.util.Map;

public abstract class TblCond implements Ser<JsonObject> {
	
	public static class Bsp extends TblCond {
		public Bsp(String blockId) {
			this.blockId = blockId;
		}
		
		public Bsp(String blockId, String k, String v) {
			this.blockId = blockId;
			this.props.put(k, v);
		}
		
		public Bsp(String blockId, Map<String, String> props) {
			this.blockId = blockId;
			this.props.putAll(props);
		}
		
		public String blockId;
		public Map<String, String> props = new HashMap<>();
		
		public Bsp blockId(String blockId) {
			this.blockId = blockId;
			return this;
		}
		
		public Bsp prop(String k, String v) {
			this.props.put(k, v);
			return this;
		}

		public Bsp props(Map<String, String> props) {
			this.props.putAll(props);
			return this;
		}
		
		@Override
		public JsonObject ser() {
			JsonObject properties = new JsonObject();
			props.forEach(properties::addProperty);
			
			JsonObject obj = new JsonObject();
			obj.addProperty("condition", "minecraft:block_state_property");
			obj.addProperty("block", blockId);
			obj.add("properties", properties);
			return obj;
		}
	}
	
	public static class BottomDoor extends Bsp {
		public BottomDoor(String blockId) {
			super(blockId, "half", "lower");
		}
	}
	
	public static class DoubleSlab extends Bsp {
		public DoubleSlab(String blockId) {
			super(blockId, "type", "double");
		}
	}
	
	public static class CandleCount extends Bsp {
		public CandleCount(String blockId, int count) {
			super(blockId, "candles", String.valueOf(count));
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
