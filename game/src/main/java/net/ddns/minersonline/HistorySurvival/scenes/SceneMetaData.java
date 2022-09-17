package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.engine.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.World;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SceneMetaData {
	public String name = "";
	public String version = GameSettings.version;
	public volatile Map<Vector3f, Voxel> voxels = new ConcurrentHashMap<>();

	public List<GameObject> gameObjects = new ArrayList<>();

	public SceneMetaData() {}
}
