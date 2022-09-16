package net.ddns.minersonline.HistorySurvival.engine.worldOld.water;

public class WaterTile {

	private float height;
	private float x,z;
	private float size;

	public WaterTile(float x, float z, float height, float size) {
		this.x = x;
		this.z = z;
		this.height = height;
		this.size = size;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public float getSize() {
		return size;
	}
}
