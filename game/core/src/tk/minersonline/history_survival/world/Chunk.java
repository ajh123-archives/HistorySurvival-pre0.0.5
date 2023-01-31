package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.data.ChunkMesh;
import tk.minersonline.history_survival.world.voxels.Voxel;
import tk.minersonline.history_survival.world.voxels.VoxelType;

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
		Voxel voxel = new Voxel(type, new Vector3(x, y, z), world);
		voxels[x + z * width + y * widthTimesHeight] = voxel;
	}
}
