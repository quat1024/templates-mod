package io.github.cottonmc.templates.model;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;


public class TagPacker {
	// 0, 1, 2, 3, 4, 5, 6 -> normal AO
	// 8, 9,10,11,12,13,14 -> AO forced off
	//16,17,18,19,20,21,22 -> AO forced on
	//tags 7 and 15 are skipped so we need a mod-8 and not a mod-7
	//the first entry means "no direction" and the others follow direction.values
	
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] TAGS = new Direction[8]; //starts and ends with null
	static {
		System.arraycopy(DIRECTIONS, 0, TAGS, 1, DIRECTIONS.length);
	}
	
	public static TriState ao(int tag) {
		if(tag <= 7) return TriState.DEFAULT;
		else if(tag <= 15) return TriState.FALSE;
		else return TriState.TRUE;
	}
	
	public static @Nullable Direction dir(int tag) {
		return TAGS[tag & 0x7];
	}
	
	public static int withAo(int tag, TriState tri) {
		int dirPart = tag & 0x7;
		return switch(tri) {
			case DEFAULT -> dirPart;
			case FALSE -> dirPart + 8;
			case TRUE -> dirPart + 16;
		};
	}
	
	public static int withDir(int tag, @Nullable Direction dir) {
		int aoPart = tag & ~0x7;
		return aoPart + (dir == null ? 0 : dir.ordinal() + 1);
	}
	
	public static Builder builder() {
		return builder(0);
	}
	
	public static Builder builder(int oldTag) {
		return new Builder(oldTag);
	}
	
	public static class Builder {
		int tag;
		
		public Builder(int tag) {
			this.tag = tag;
		}
		
		public TriState ao() {
			return TagPacker.ao(tag);
		}
		
		public @Nullable Direction dir() {
			return TagPacker.dir(tag);
		}
		
		public Builder withAo(TriState tri) {
			tag = TagPacker.withAo(tag, tri);
			return this;
		}
		
		public Builder withDir(Direction dir) {
			tag = TagPacker.withDir(tag, dir);
			return this;
		}
		
		public int build() {
			return tag;
		}
	}
	
	static {
		for(Direction d : TAGS) {
			for(TriState a : TriState.values()) {
				int tag = builder().withDir(d).withAo(a).build();
				Preconditions.checkArgument(dir(tag) == d, "dir " + d + " dirTag " + dir(tag));
				Preconditions.checkArgument(ao(tag) == a, "ao " + a + " aoTag " + ao(tag));
				Preconditions.checkArgument(withDir(tag, d) == tag, "dir " + d + " tag " + tag + " withDir " + withDir(tag, d));
				Preconditions.checkArgument(withAo(tag, a) == tag, "ao " + a + " tag " + tag + " withAo " + withAo(tag, a));
			}
		}
	}
}
