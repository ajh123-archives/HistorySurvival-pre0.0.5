package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import tk.minersonline.history_survival.componments.SolidModelComponent;

public class VoxelType implements Component {
	public static VoxelType AIR = new VoxelType("air", new SolidModelComponent(
			Color.CLEAR
	));
	public static VoxelType GRASS = new VoxelType("grass", new SolidModelComponent(
			Color.FOREST,
			Gdx.audio.newSound(Gdx.files.internal("data/voxels/grass/step_l.mp3"))
	));
	public static VoxelType STONE = new VoxelType("stone", new SolidModelComponent(
			Color.GRAY
	));
	public static VoxelType DIRT = new VoxelType("dirt", new SolidModelComponent(
			Color.BROWN
	));
	public static VoxelType SAND = new VoxelType("sand", new SolidModelComponent(
			Color.TAN
	));
	public static VoxelType WATER = new VoxelType("water", new SolidModelComponent(
			new Color(0, 0, 1, 0.5f), true
	));

	private final String name;

	private final SolidModelComponent properties;

	public VoxelType(String name, SolidModelComponent properties) {
		this.properties = properties;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public SolidModelComponent getProperties() {
		return this.properties;
	}

	public static void init() {}
}
