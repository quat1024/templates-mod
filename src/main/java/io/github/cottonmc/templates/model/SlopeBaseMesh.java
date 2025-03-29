package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SlopeBaseMesh {
	//TODO: figure out how to enable AO without it looking like shit.
	public static final int TAG_SLOPE = TagPacker.builder().withDir(Direction.UP).withAo(TriState.FALSE).build();
	public static final int TAG_LEFT = TagPacker.builder().withDir(Direction.EAST).withAo(TriState.FALSE).build();
	public static final int TAG_RIGHT = TagPacker.builder().withDir(Direction.WEST).withAo(TriState.FALSE).build();
	public static final int TAG_BACK = TagPacker.builder().withDir(Direction.SOUTH).withAo(TriState.FALSE).build();
	public static final int TAG_BOTTOM = TagPacker.builder().withDir(Direction.DOWN).withAo(TriState.FALSE).build();
	
	public static Mesh makeUpright() {
		Renderer renderer = TemplatesClientApi.getInstance().getFabricRenderer();
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter qu = builder.getEmitter();
		qu.tag(TAG_SLOPE)
//			.square(Direction.UP, 0, 0, 1, 1, 0.5f).uvUnitSquare()
			.pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 0f)
			.uv(0, 0f, 0f).uv(1, 0f, 1f).uv(2, 1f, 1f).uv(3, 1f, 0f)
			.color(-1, -1, -1, -1)
			.cullFace(null)
			.nominalFace(Direction.UP)
			.emit()
			.tag(TAG_LEFT)
			.square(Direction.WEST, 0, 0, 1, 1, 0).uvUnitSquare() //use square() to set most of the positions
			.pos(0, 0f, 0.5f, 0.5f).uv(0, 0.5f, 0.5f) //fix the last one manually (it's vertex index 0, found with trial and error)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_RIGHT)
			.square(Direction.EAST, 0, 0, 1, 1, 0).uvUnitSquare() //use square() to set most of the positions
			.pos(3, 1f, 0.5f, 0.5f).uv(3, 0.5f, 0.5f) //fix
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BACK)
			.square(Direction.SOUTH, 0, 0, 1, 1, 0).uvUnitSquare()
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BOTTOM)
			.square(Direction.DOWN, 0, 0, 1, 1, 0).uvUnitSquare()
			.color(-1, -1, -1, -1)
			.emit();
		return builder.build();
	}
	
	//My mfw (my face when) mfw face when you can't rotate blockmodels on the z axis from a blockstate file
	//Fine i will do it myself !!!
	public static Mesh makeSide() {
		Matrix4f mat = new Matrix4f();
		RotationAxis.POSITIVE_Z.rotationDegrees(90).get(mat);
		return MeshTransformUtil.pretransformMesh(makeUpright(), MeshTransformUtil.applyAffine(mat));
	}
	
	//looks weird since i wrote a janky script to massage a .bbmodel, some manual fixups applied
	public static Mesh makeTinyUpright() {
		Renderer renderer = TemplatesClientApi.getInstance().getFabricRenderer();
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter qu = builder.getEmitter();
		qu.tag(TAG_LEFT)
			.pos(0, 1f, 0.25f, 0.75f).uv(0, 0.25f, 0.75f)
			.pos(1, 1f, 0.5f, 1f).uv(1, 0f, 0.5f)
			.pos(2, 1f, 0f, 1f).uv(2, 0f, 1f)
			.pos(3, 1f, 0f, 0.5f).uv(3, 0.5f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_RIGHT)
			.pos(0, 0f, 0f, 1f).uv(0, 1f, 1f)
			.pos(1, 0f, 0.5f, 1f).uv(1, 1f, 0.5f)
			.pos(2, 0f, 0.25f, 0.75f).uv(2, 0.75f, 0.75f)
			.pos(3, 0f, 0f, 0.5f).uv(3, 0.5f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BOTTOM)
			.pos(0, 1f, 0f, 0.5f).uv(0, 1f, 0.5f)
			.pos(1, 1f, 0f, 1f).uv(1, 1f, 0f)
			.pos(2, 0f, 0f, 1f).uv(2, 0f, 0f)
			.pos(3, 0f, 0f, 0.5f).uv(3, 0f, 0.5f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BACK)
			.pos(0, 1f, 0f, 1f).uv(0, 1f, 1f)
			.pos(1, 1f, 0.5f, 1f).uv(1, 1f, 0.5f)
			.pos(2, 0f, 0.5f, 1f).uv(2, 0f, 0.5f)
			.pos(3, 0f, 0f, 1f).uv(3, 0f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_SLOPE)
			.pos(0, 1f, 0.5f, 1f).uv(2, 0f, 0.5f) //manually permuted uvs
			.pos(1, 1f, 0f, 0.5f).uv(3, 0f, 1f)
			.pos(2, 0f, 0f, 0.5f).uv(0, 1f, 1f)
			.pos(3, 0f, 0.5f, 1f).uv(1, 1f, 0.5f)
			.color(-1, -1, -1, -1)
			.emit()
		;
		return builder.build();
	}
	
	public static Mesh makeTinySide() {
		Matrix4f mat = new Matrix4f();
		RotationAxis.POSITIVE_Z.rotationDegrees(90).get(mat);
		return MeshTransformUtil.pretransformMesh(makeTinyUpright(), MeshTransformUtil.applyAffine(mat));
	}
}
