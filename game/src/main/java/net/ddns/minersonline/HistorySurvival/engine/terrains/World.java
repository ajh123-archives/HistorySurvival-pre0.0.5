package net.ddns.minersonline.HistorySurvival.engine.terrains;

import com.google.gson.*;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterTile;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public abstract class World {
	protected List<Terrain> terrains = new ArrayList<>();
	protected TerrainTexturePack texturePack;
	protected TerrainTexture blendMap;

	public abstract float getHeightOfTerrain(float worldX, float worldZ);
	public abstract Vector3f getTerrainPoint(float worldX, float worldZ, float yOffset);
	public abstract float getHeightOfWater(float worldX, float worldZs);
	public final List<Terrain> getTerrains(){
		return terrains;
	}

	public abstract List<WaterTile> getWaterTiles();

	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}

	public final void updateWorld(){
		for (Terrain terrain : terrains){
			terrain.setWorld(this);
			terrain.setBlendMap(blendMap);
			terrain.setTexturePack(texturePack);
			terrain.setModel(terrain.updateTerrain(Game.modelLoader, null));
		}
	}

	public final Terrain getTerrain(float worldX, float worldZ) {
		// this could be optimized with a hash table
		for (Terrain terrain : terrains) {
			if (terrain.containsPosition(worldX, worldZ)) {
				return terrain;
			}
		}
		return null;
	}

	// size of each terrain tile
	public abstract float getTerrainSize();
	public abstract float getXSize();
	public abstract float getZSize();

	public static class JSON implements JsonDeserializer<World>, JsonSerializer<World> {
		@Override
		public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String type = jsonObject.get("type").getAsString();
			JsonElement element = jsonObject.get("properties");

			try {
				return context.deserialize(element, Class.forName(type));
			} catch (ClassNotFoundException e){
				throw new JsonParseException("Unknown type `"+type+"` to parse a `World` from.", e);
			}
		}

		@Override
		public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
			result.add("properties", context.serialize(src, src.getClass()));
			return result;
		}
	}
}