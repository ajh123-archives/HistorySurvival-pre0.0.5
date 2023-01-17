package tk.minersonline.history_survival.systems;

import com.badlogic.ashley.core.PooledEngine;
import tk.minersonline.history_survival.componments.VoxelChunkComponent;
import tk.minersonline.history_survival.componments.VoxelEntity;
import tk.minersonline.history_survival.componments.VoxelTypeComponent;

public class VoxelWorld {
	public static final int CHUNK_SIZE_X = 16;
	public static final int CHUNK_SIZE_Y = 16;
	public static final int CHUNK_SIZE_Z = 16;
	public final VoxelChunkComponent[] chunks;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public PooledEngine engine;

	public VoxelWorld (int chunksX, int chunksY, int chunksZ, PooledEngine engine) {
		this.chunks = new VoxelChunkComponent[chunksX * chunksY * chunksZ];
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.chunksZ = chunksZ;
		this.voxelsX = chunksX * CHUNK_SIZE_X;
		this.voxelsY = chunksY * CHUNK_SIZE_Y;
		this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
		this.engine = engine;
	}

	public VoxelEntity set (float x, float y, float z, VoxelTypeComponent voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return getChunk(x, y, z).set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z,
				voxel);
	}

	public VoxelEntity get (float x, float y, float z) {
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

	public VoxelChunkComponent getChunk (float x, float y, float z) {
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
			VoxelEntity voxel = get(ix, y, iz);
			if (voxel != null && voxel.getType() != VoxelTypeComponent.AIR) {
				return y + 1;
			}
		}
		return 0;
	}

	public void setColumn (float x, float y, float z, VoxelTypeComponent voxel) {
		setColumn(x, 0, y, z, voxel);
	}

	public void setColumn (float x, float startY, float endY, float z, VoxelTypeComponent voxel) {
		int ix = (int)x;
		int iy = (int)endY;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return;
		if (iy < 0 || iy >= voxelsY) return;
		if (iz < 0 || iz >= voxelsZ) return;
		// FIXME optimize
		for (; iy > startY; iy--) {
			set(ix, iy, iz, voxel);
		}
	}

	public void setCube (float x, float y, float z, float width, float height, float depth, VoxelTypeComponent voxel) {
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
					set(ix, iy, iz, voxel);
				}
			}
		}
	}
}