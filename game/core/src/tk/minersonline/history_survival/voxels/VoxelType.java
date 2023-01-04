package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.Color;

public class VoxelType {
	private static int IDCount = 0;

	public static VoxelType AIR = new VoxelType("air", Color.CLEAR);
	public static VoxelType GRASS = new VoxelType("grass", Color.FOREST);
	public static VoxelType STONE = new VoxelType("stone", Color.GRAY);
	public static VoxelType DIRT = new VoxelType("dirt", Color.BROWN);

	private final String name;
	private final Color color;
	private final int ID;

	public VoxelType(String name, Color color) {
		this.name = name;
		this.color = color;
		this.ID = IDCount;
		IDCount++;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return this.color;
	}

	public int getIndex() {
		return ID;
	}

	public static void init() {}
}
