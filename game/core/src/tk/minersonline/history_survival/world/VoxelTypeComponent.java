package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import tk.minersonline.history_survival.componments.SolidModelComponent;

public class VoxelTypeComponent implements Component {
	public static VoxelTypeComponent AIR = new VoxelTypeComponent("air", new SolidModelComponent(
			Color.CLEAR
	));
	public static VoxelTypeComponent GRASS = new VoxelTypeComponent("grass", new SolidModelComponent(
			Color.FOREST,
			Gdx.audio.newSound(Gdx.files.internal("data/voxels/grass/step_l.mp3"))
	));
	public static VoxelTypeComponent STONE = new VoxelTypeComponent("stone", new SolidModelComponent(
			Color.GRAY
	));
	public static VoxelTypeComponent DIRT = new VoxelTypeComponent("dirt", new SolidModelComponent(
			Color.BROWN
	));
	public static VoxelTypeComponent SAND = new VoxelTypeComponent("sand", new SolidModelComponent(
			Color.TAN
	));
	public static VoxelTypeComponent WATER = new VoxelTypeComponent("water", new SolidModelComponent(
			new Color(0, 0, 1, 0.5f), true
	));

	private final String name;

	private final SolidModelComponent properties;

	public VoxelTypeComponent(String name, SolidModelComponent properties) {
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
