package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.Color;

public class VoxelType {
	private static int IDCount = 0;

	public static VoxelType AIR = new VoxelType("air", Color.CLEAR, true);
	public static VoxelType GRASS = new VoxelType("grass", Color.FOREST);
	public static VoxelType STONE = new VoxelType("stone", Color.GRAY);
	public static VoxelType DIRT = new VoxelType("dirt", Color.BROWN);
	public static VoxelType SAND = new VoxelType("sand", Color.TAN);
	public static VoxelType WATER = new VoxelType("water", new Color(0, 0, 1, 0.5f), true);

	private final String name;
	private final Color color;
	private final int ID;
	private final boolean transparent;

	public VoxelType(String name, Color color) {
		this.name = name;
		this.color = color;
		this.ID = IDCount;
		this.transparent = false;
		IDCount++;
	}

	public VoxelType(String name, Color color, boolean transparent) {
		this.name = name;
		this.color = color;
		this.ID = IDCount;
		this.transparent = transparent;
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

	public boolean isTransparent() {
		return transparent;
	}

	public static void init() {}
}
