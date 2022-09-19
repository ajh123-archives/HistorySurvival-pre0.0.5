package net.ddns.minersonline.HistorySurvival.api.voxel;

import org.joml.Vector3f;

import java.util.Map;

public class VoxelChunk {
	private Map<Vector3f, Voxel> voxels;
	private Vector3f origin;

	public static int CHUNK_SIZE = 35;

	public VoxelChunk(Map<Vector3f, Voxel>  voxels, Vector3f origin) {
		this.voxels = voxels;
		this.origin = origin;
	}

	public Map<Vector3f, Voxel> getVoxels() {
		return voxels;
	}

	public void setVoxels(Map<Vector3f, Voxel>  voxels) {
		this.voxels = voxels;
	}

	public Vector3f getOrigin() {
		return origin;
	}

	public Vector3f getCenter() {
		Vector3f center = new Vector3f(origin);
		center.x = (int) (center.x+1) * CHUNK_SIZE;
		center.y = (int) (center.y+1) * CHUNK_SIZE;
		center.z = (int) (center.z+1) * CHUNK_SIZE;
		return center;
	}
}
