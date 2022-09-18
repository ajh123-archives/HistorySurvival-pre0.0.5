package net.ddns.minersonline.HistorySurvival.engine.voxel;

import org.joml.Vector3f;

import java.util.Map;

public class VoxelChunk {
	private Map<Vector3f, Voxel> voxels;
	private Vector3f origin;

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
}
