package net.ddns.minersonline.HistorySurvival.api.voxel;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.models.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VoxelChunkMesh {
	public VoxelChunk chunk;
	public TexturedModel model;
	public int index;

	private final List<Vertex> vertices;

	private final List<Float> positionsList;
	private final List<Float> uvsList;
	private final List<Float> normalsList;

	public float[] positions;
	public float[] uvs;
	public float[] normals;

	public VoxelChunkMesh(VoxelChunk chunk) {
		this.chunk = chunk;
		vertices = new ArrayList<>();
		positionsList = new ArrayList<>();
		uvsList = new ArrayList<>();
		normalsList = new ArrayList<>();

		buildMesh();
		populateLists();
	}

	public void update(VoxelChunk chunk){
		this.chunk = chunk;
		buildMesh();
		populateLists();
	}

	private void buildMesh(){
		for (Voxel voxel : chunk.getVoxels().values()) {
			Vector3f pos1 = voxel.getPosition();
			boolean px = false;
			boolean nx = false;
			boolean py = false;
			boolean ny = false;
			boolean pz = false;
			boolean nz = false;

			for (Voxel voxel2 : chunk.getVoxels().values()) {
				Vector3f pos2 = voxel2.getPosition();
				if (((pos1.x + 1) == (pos2.x)) &&
						((pos1.y) == (pos2.y)) &&
						((pos1.z) == (pos2.z))) {
					px = true;
				}
				if (((pos1.x - 1) == (pos2.x)) &&
						((pos1.y) == (pos2.y)) &&
						((pos1.z) == (pos2.z))) {
					nx = true;
				}
				if (((pos1.x) == (pos2.x)) &&
						((pos1.y + 1) == (pos2.y)) &&
						((pos1.z) == (pos2.z))) {
					py = true;
				}
				if (((pos1.x) == (pos2.x)) &&
						((pos1.y - 1) == (pos2.y)) &&
						((pos1.z) == (pos2.z))) {
					ny = true;
				}
				if (((pos1.x) == (pos2.x)) &&
						((pos1.y) == (pos2.y)) &&
						((pos1.z + 1) == (pos2.z))) {
					pz = true;
				}
				if (((pos1.x) == (pos2.x)) &&
						((pos1.y) == (pos2.y)) &&
						((pos1.z - 1) == (pos2.z))) {
					nz = true;
				}
			}
			if (!px){
				buildVertex(voxel, VoxelModel.PX_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
			if (!nx){
				buildVertex(voxel, VoxelModel.NX_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
			if (!py){
				buildVertex(voxel, VoxelModel.PY_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
			if (!ny){
				buildVertex(voxel, VoxelModel.NY_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
			if (!pz){
				buildVertex(voxel, VoxelModel.PZ_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
			if (!nz){
				buildVertex(voxel, VoxelModel.NZ_POS, VoxelModel.NORMALS, VoxelModel.UV, pos1);
			}
		}
	}

	private void buildVertex(Voxel voxel, Vector3f[] poses, Vector3f[] normals, Vector2f[] uv, Vector3f pos1) {
		Vector2f atlasSize = GameHook.getLoader().getTextureAtlasSize();
		for (int k = 0; k < 6; k++) {
			Vector2f tc = (uv[k].mul(new Vector2f(32,32)).div(new Vector2f(atlasSize.x,atlasSize.y)));
			vertices.add(new Vertex(
				new Vector3f(
					poses[k].x + pos1.x,
					poses[k].y + pos1.y,
					poses[k].z + pos1.z
				),
				normals[k],
				new Vector2f(
//					uv[k].x,//* voxel.getModel().getModelTexture().getAtlasOffset() * 6,// * voxel.getModel().getModelTexture().getAtlasOffset(),
//					uv[k].y + (((voxel.getModel().getModelTexture().getAtlasOffset())*6)*8192)*8192
					tc.x + (((voxel.getModel().getModelTexture().getAtlasOffset() % GameHook.getLoader().getAtlasCount()) * 32)/ atlasSize.x),
					tc.y + (((voxel.getModel().getModelTexture().getAtlasOffset()) * 32)/ atlasSize.y)
				)
			));
		}
	}

	private void populateLists() {
		for (Vertex vertex : vertices) {
			positionsList.add(vertex.positions.x);
			positionsList.add(vertex.positions.y);
			positionsList.add(vertex.positions.z);
			uvsList.add(vertex.uvs.x);
			uvsList.add(vertex.uvs.y);
			normalsList.add(vertex.normals.x);
			normalsList.add(vertex.normals.y);
			normalsList.add(vertex.normals.z);
		}
		positions = new float[positionsList.size()];
		uvs = new float[uvsList.size()];
		normals = new float[normalsList.size()];

		for (int i = 0; i<positionsList.size(); i++){
			positions[i] = positionsList.get(i);
		}

		for (int i = 0; i<uvsList.size(); i++){
			uvs[i] = uvsList.get(i);
		}

		for (int i = 0; i<normalsList.size(); i++){
			normals[i] = normalsList.get(i);
		}

		positionsList.clear();
		uvsList.clear();
		normalsList.clear();
	}
}
