package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.data.ChunkMesh;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.voxels.Voxel;
import tk.minersonline.history_survival.world.voxels.VoxelType;

import static tk.minersonline.history_survival.world.World.*;

public class Chunk implements Component {
	public final Voxel[] voxels;
	public final int width;
	public final int height;
	public final int depth;
	public final Vector3 offset = new Vector3();
	private final int widthTimesHeight;
	private final World world;
	public boolean dirty = false;
	public final ChunkMesh chunkMesh;


	public Chunk(int width, int height, int depth, World world) {
		this.world = world;
		this.voxels = new Voxel[width * height * depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.chunkMesh = new ChunkMesh(width, depth);

		this.widthTimesHeight = width * height;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++) {
					set(x, y, z, VoxelType.AIR);
				}
			}
		}
		Entity me = world.engine.createEntity();
		me.add(this);
		world.engine.addEntity(me);
	}

	public Voxel get (int x, int y, int z) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		if (z < 0 || z >= depth) return null;
		return getFast(x, y, z);
	}

	public Voxel getFast (int x, int y, int z) {
		return voxels[x + z * width + y * widthTimesHeight];
	}

	public void set (int x, int y, int z, VoxelType type) {
		if (x < 0 || x >= width) return;
		if (y < 0 || y >= height) return;
		if (z < 0 || z >= depth) return;
		setFast(x, y, z, type);
	}

	public void setFast (int x, int y, int z, VoxelType type) {
		Voxel voxelEntity = new Voxel(type, new Vector3(x, y, z), world);
		voxels[x + z * width + y * widthTimesHeight] = voxelEntity;
	}

	public void initMesh() {
		int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += (short) 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}

		VertexAttribute colorAttribute = ChunkMesh.USE_PACKED_COLOR ? VertexAttribute.ColorPacked() : VertexAttribute.ColorUnpacked();
		VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), colorAttribute);

		chunkMesh.indices = indices;
		chunkMesh.mesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
				CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
		chunkMesh.mesh.setIndices(indices);
		chunkMesh.transparentMesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
				CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
		chunkMesh.transparentMesh.setIndices(indices);

		chunkMesh.numVertices = 0;
		chunkMesh.numTransparentVertices = 0;
		chunkMesh.material = new Material();
	}
}
