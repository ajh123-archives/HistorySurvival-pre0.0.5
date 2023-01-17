package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import tk.minersonline.history_survival.voxels.data.VoxelProperties;

public class VoxelType {
	public static VoxelType AIR = new VoxelType("air", new VoxelProperties(
			Color.CLEAR
	));
	public static VoxelType GRASS = new VoxelType("grass", new VoxelProperties(
			Color.FOREST,
			Gdx.audio.newSound(Gdx.files.internal("data/grass/step_l.mp3"))
	));
	public static VoxelType STONE = new VoxelType("stone", new VoxelProperties(
			Color.GRAY
	));
	public static VoxelType DIRT = new VoxelType("dirt", new VoxelProperties(
			Color.BROWN
	));
	public static VoxelType SAND = new VoxelType("sand", new VoxelProperties(
			Color.TAN
	));
	public static VoxelType WATER = new VoxelType("water", new VoxelProperties(
			new Color(0, 0, 1, 0.5f), true
	));

	private final String name;

	private final VoxelProperties properties;


	public VoxelType(String name, VoxelProperties properties) {
		this.properties = properties;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public VoxelProperties getProperties() {
		return this.properties;
	}

	public static void init() {}
}
