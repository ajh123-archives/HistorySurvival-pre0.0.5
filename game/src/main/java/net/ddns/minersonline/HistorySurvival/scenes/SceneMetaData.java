package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.engine.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.World;

import java.util.ArrayList;
import java.util.List;

public class SceneMetaData {
	public String name = "";
	public String version = GameSettings.version;
	public List<Voxel> voxels = new ArrayList<>();

	public List<GameObject> gameObjects = new ArrayList<>();

	public SceneMetaData() {}
}
