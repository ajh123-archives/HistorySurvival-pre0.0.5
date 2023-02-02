package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Disposable;
import tk.minersonline.history_survival.world.voxels.VoxelType;

public class World implements Disposable {
	public static final int CHUNK_SIZE_X = 16;
	public static final int CHUNK_SIZE_Y = 16;
	public static final int CHUNK_SIZE_Z = 16;
	public final Chunk[] chunks;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public PooledEngine engine;

	public World(int chunksX, int chunksY, int chunksZ, PooledEngine engine) {
		this.chunks = new Chunk[chunksX * chunksY * chunksZ];
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.chunksZ = chunksZ;
		this.voxelsX = chunksX * CHUNK_SIZE_X;
		this.voxelsY = chunksY * CHUNK_SIZE_Y;
		this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
		this.engine = engine;
	}

	public void set (float x, float y, float z, VoxelType type) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return;
		getChunk(x, y, z).set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, type);
	}

	public VoxelType get (float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return getChunk(x, y, z).get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y,
				iz % CHUNK_SIZE_Z);
	}

	public Chunk getChunk (float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ];
	}

	public float getHighest (float x, float z) {
		int ix = (int)x;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return 0;
		if (iz < 0 || iz >= voxelsZ) return 0;
		// FIXME optimize
		for (int y = voxelsY - 1; y > 0; y--) {
			VoxelType voxel = get(ix, y, iz);
			if (voxel != null && voxel != VoxelType.AIR) {
				return y + 1;
			}
		}
		return 0;
	}

	public void setColumn (float x, float y, float z, VoxelType type) {
		setColumn(x, 0, y, z, type);
	}

	public void setColumn (float x, float startY, float endY, float z, VoxelType type) {
		int ix = (int)x;
		int iy = (int)endY;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return;
		if (iy < 0 || iy >= voxelsY) return;
		if (iz < 0 || iz >= voxelsZ) return;
		// FIXME optimize
		for (; iy > startY; iy--) {
			set(ix, iy, iz, type);
		}
	}

	public void setCube (float x, float y, float z, float width, float height, float depth, VoxelType type) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int iwidth = (int)width;
		int iheight = (int)height;
		int idepth = (int)depth;
		int startX = Math.max(ix, 0);
		int endX = Math.min(voxelsX, ix + iwidth);
		int startY = Math.max(iy, 0);
		int endY = Math.min(voxelsY, iy + iheight);
		int startZ = Math.max(iz, 0);
		int endZ = Math.min(voxelsZ, iz + idepth);
		// FIXME optimize
		for (iy = startY; iy < endY; iy++) {
			for (iz = startZ; iz < endZ; iz++) {
				for (ix = startX; ix < endX; ix++) {
					set(ix, iy, iz, type);
				}
			}
		}
	}

	@Override
	public void dispose() {
		for (Chunk chunk : chunks) {
			chunk.dispose();
		}
	}
}