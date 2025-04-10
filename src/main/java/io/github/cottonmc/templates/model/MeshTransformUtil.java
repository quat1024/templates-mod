package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

public class MeshTransformUtil {
	public static Mesh pretransformMesh(Mesh mesh, RenderContext.QuadTransform transform) {
		MeshBuilder builder = TemplatesClientApi.getInstance().getFabricRenderer().meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		
		mesh.forEach(quad -> {
			emitter.copyFrom(quad);
			if(transform.transform(emitter)) emitter.emit();
		});
		
		return builder.build();
	}
	
	//Hard to explain what this is for...
	//Basically, Templates 1.x manually emitted the north/south/east/west faces all individually.
	//This means it was easy to get the orientation of the block correct - to populate the north face of the slope, look at
	//the north texture of the theme block. In this version, there is only *one* slope model that is dynamically rotated
	//to form the other possible orientations. If I populate the north face of the model using the north face of the theme,
	//then rotate the model, it's no longer facing the right way. So I need to "un-rotate" the face assignments first.
	//
	//This seems to work, but I'm kinda surprised I don't need to invert the transformation here, which is a clue that
	//I don't really understand all the math, loool
	public static Map<Direction, Direction> facePermutation(ModelBakeSettings aff) {
		return facePermutation(aff.getRotation().getMatrix());
	}
	
	public static Map<Direction, Direction> facePermutation(Matrix4f mat) {
		Map<Direction, Direction> facePermutation = new EnumMap<>(Direction.class);
		for(Direction input : Direction.values()) {
			Direction output = Direction.transform(mat, input);
			facePermutation.put(input, output);
		}
		return facePermutation;
	}
	
	public static RenderContext.QuadTransform applyAffine(ModelBakeSettings settings) {
		return applyAffine(settings.getRotation().getMatrix());
	}
	
	public static RenderContext.QuadTransform applyAffine(Matrix4f mat) {
		Vector3f pos3 = new Vector3f();
		Vector4f pos4 = new Vector4f();
		
		return quad -> {
			//For each vertex:
			for(int i = 0; i < 4; i++) {
				//Copy pos into a vec3, then a vec4.
				//the w component is set to 0 since this is a point, not a normal.
				//Also, prepare to do a rotation about the origin.
				quad.copyPos(i, pos3);
				pos3.add(-0.5f, -0.5f, -0.5f);
				pos4.set(pos3, 0);
				
				//Compute the matrix-vector product. This function mutates the vec4 in-place.
				mat.transformAffine(pos4);
				
				//Copy back onto the vertex, transforming back to (0, 1)
				quad.pos(i, pos4.x + 0.5f, pos4.y + 0.5f, pos4.z + 0.5f);
				
				//frapi always overrides the normal anyway >.>
				//so maybe this is useless
				//Copy normal into a vec3
				quad.copyNormal(i, pos3);
				pos4.set(pos3, 1);
				//transform the normal and copy it back to the quad
				mat.transformAffine(pos4);
				quad.normal(i, pos4.x, pos4.y, pos4.z);
			}
			
			//permute tags
			TagPacker.Builder tb = new TagPacker.Builder(quad.tag());
			Direction tagDir = tb.dir();
			if(tagDir != null) tb.withDir(Direction.transform(mat, tagDir));
			quad.tag(tb.build());
			
			//permute cullface
			Direction cull = quad.cullFace();
			if(cull != null) quad.cullFace(Direction.transform(mat, cull));
			
			//uhh
			quad.nominalFace(null);
			
//			quad.color(0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
//			switch(quad.nominalFace()) {
//				case null  -> {}
//				case EAST  -> quad.color(0xFF0000, 0xFF0000, 0xFF0000, 0xFF0000);
//				case NORTH -> quad.color(0xFFFF00, 0xFFFF00, 0xFFFF00, 0xFFFF00);
//				case UP    -> quad.color(0x00FF00, 0x00FF00, 0x00FF00, 0x00FF00);
//				case WEST  -> quad.color(0x00FFFF, 0x00FFFF, 0x00FFFF, 0x00FFFF);
//				case SOUTH -> quad.color(0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF);
//				case DOWN  -> quad.color(0xFF00FF, 0xFF00FF, 0xFF00FF, 0xFF00FF);
//			}
			
//			fixWinding(quad);
			
			//Output the quad
			return true;
		};
	}
	
//	private static void fixWinding(MutableQuadView e) {
//		//todo JANK JANK JANK JANK
//		if(!e.lightFace().getAxis().isVertical()) return;
//
//		//find the vert with the lowest position
//		Vector3f best = new Vector3f(999, 999, 999);
//		Vector3f scratch = new Vector3f();
//		int bestId = -1;
//		for(int i = 0; i < 4; i++) {
//			e.copyPos(i, scratch);
//			if(scratch.x <= best.x && scratch.y <= best.y && scratch.z <= best.z) {
//				bestId = i;
//				best = new Vector3f(scratch);
//			}
//		}
//
//		if(e.y(bestId) > 0.5) {
//			if(bestId == 1) {
//				cycleVerts(e, scratch, 3, 2, 1, 0);
//			} else if(bestId == 2) {
//				for(int i = 0; i < 2; i++)
//					cycleVerts(e, scratch, 0, 1, 2, 3);
//			} else if(bestId == 3) {
//				cycleVerts(e, scratch, 0, 1, 2, 3);
//			}
//		} else if(bestId == 0) {
//			cycleVerts(e, scratch, 0, 1, 2, 3);
//		} else if(bestId == 1) {
//			//leave it
//		} else if(bestId == 2) {
//			cycleVerts(e, scratch, 3, 2, 1, 0);
//		} else {
//			for(int i = 0; i < 2; i++)
//				cycleVerts(e, scratch, 0, 1, 2, 3);
//		}
//	}
//
//	private static void cycleVerts(MutableQuadView e, Vector3f scratch, int... order) {
//		//pass 0 1 2 3
//		//means 0 should go to 1, 1 should go to 2, 2 should to go 3, 3 should go to 0
//		//a. back up the contents of 3
//		//b. 2 to 3, then 1 to 2, then 0 to 1
//		//c. load the backup onto 0
//
//		int first = order[0];
//		int last = order[order.length - 1];
//		Vector2f oldUv = e.copyUv(last, null);
//		Vector3f oldPos = e.copyPos(last, null);
//		Vector3f oldNormal = e.copyNormal(last, null);
//		int oldColor = e.color(last);
//
//		for(int i = order.length - 2; i >= 0; i--) {
//			int from = order[i];
//			int to = order[i + 1];
//
//			e.uv(to, e.u(from), e.v(from))
//				.pos(to, e.copyPos(from, scratch))
//				.normal(to, e.copyNormal(from, scratch))
//				.color(to, e.color(from));
//		}
//		e.uv(first, oldUv).pos(first, oldPos).normal(first, oldNormal).color(first, oldColor);
//	}
}
