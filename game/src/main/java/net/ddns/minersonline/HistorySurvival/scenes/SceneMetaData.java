package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelWorld;

import java.util.*;

public class SceneMetaData {
	public String name = "";
	public String version = GameSettings.version;
	public VoxelWorld world = new VoxelWorld();

	public List<GameObject> gameObjects = new ArrayList<>();

	public SceneMetaData() {}
}
