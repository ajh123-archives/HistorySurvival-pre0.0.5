package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.textures.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.*;

public class Game {
    private void start() {
        DisplayManager.createDisplay();
        DisplayManager.setShowFPSTitle(true);   // TODO: Debug only

        System.out.println("OpenGL: " + DisplayManager.getOpenGlVersionMessage());

        ModelLoader modelLoader = new ModelLoader();

        // Tree entity
        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("tree.png")));

        // Low poly tree entity
        TexturedModel lowPolyTreeModel = new TexturedModel(ObjLoader.loadObjModel("lowPolyTree.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("lowPolyTree.png")));

        // Grass entity
        TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grassModel.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("grassTexture.png")));
        grassModel.getModelTexture().setHasTransparency(true);
        grassModel.getModelTexture().setUseFakeLighting(true);

        // Fern entity
        ModelTexture fernTextureAtlas = new ModelTexture(modelLoader.loadTexture("fern.png"));
        fernTextureAtlas.setNumberOfRowsInTextureAtlas(2);
        TexturedModel fernModel = new TexturedModel(ObjLoader.loadObjModel("fern.obj", modelLoader), fernTextureAtlas);
        fernModel.getModelTexture().setHasTransparency(true);

        // Multi-textured Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(modelLoader.loadTexture("grassy2.png"));
        TerrainTexture rTexture = new TerrainTexture(modelLoader.loadTexture("mud.png"));
        TerrainTexture gTexture = new TerrainTexture(modelLoader.loadTexture("grassFlowers.png"));
        TerrainTexture bTexture = new TerrainTexture(modelLoader.loadTexture("path.png"));
        TerrainTexture blendMap = new TerrainTexture(modelLoader.loadTexture("blendMap.png"));

        TerrainTexturePack terrainTexturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        // Terrain entityList
        int size = 3;
        Map<Integer, Map<Integer, Terrain>> world = new HashMap<>();

        for(int i = -size; i < size; i++)  {
            world.put(i, new HashMap<>());
        }

        Terrain terrain = new Terrain(0, -1, modelLoader, terrainTexturePack, blendMap, "heightmap.png");
        world.get(0).put(-1, terrain);
        Terrain terrain2 = new Terrain(-1, -1, modelLoader, terrainTexturePack, blendMap, "heightmap.png");
        world.get(-1).put(-1, terrain2);

        List<Entity> entityList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 400; i++) {
            float x = random.nextFloat() * 800 - 400;
            float z = random.nextFloat() * -600;
            float y = terrain.getHeightOfTerrain(x, z);

            if (i % 20 == 0) {
                entityList.add(new Entity(lowPolyTreeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
            }

            x = random.nextFloat() * 800 - 400;
            z = random.nextFloat() * -600;
            y = terrain.getHeightOfTerrain(x, z);

            if (i % 20 == 0) {
                entityList.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 5));
            }

            x = random.nextFloat() * 800 - 400;
            z = random.nextFloat() * -600;
            y = terrain.getHeightOfTerrain(x, z);

            if (i % 10 == 0) {
                // assigns a random texture for each fern from its texture atlas
                entityList.add(new Entity(fernModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
            }

            if (i % 5 == 0) {
                entityList.add(new Entity(grassModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
            }
        }

        List<Light> lights = new ArrayList<>();
        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.4f, 0.4f,0.4f));
        lights.add(light);

        TexturedModel lamp = new TexturedModel(ObjLoader.loadObjModel("lamp.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("lamp.png")));

        Light moveLight = new Light(
                new Vector3f(185, 10, -293),
                new Vector3f(2, 0,0),
                new Vector3f(1, 0.01f,0.002f));
        lights.add(moveLight);
        Entity moveEntity = new Entity(lamp,
                new Vector3f(185, -4.7f, -293),
                0,
                0,
                0,
                1);
        entityList.add(moveEntity);


        lights.add(new Light(
                new Vector3f(370, 17, -293),
                new Vector3f(0, 2,0),
                new Vector3f(1, 0.01f,0.002f)));
        entityList.add(new Entity(lamp,
                new Vector3f(370, 4.2f, -293),
                0,
                0,
                0,
                1));

        MasterRenderer masterRenderer = new MasterRenderer();

        TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));
        Player player = new Player(world, playerOBJ, new Vector3f(100, 0, -100), 0,0,0,0.6f);
        Camera camera = new Camera(player);

        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(modelLoader);

        MousePicker picker = new MousePicker(world, masterRenderer.getProjectionMatrix(), camera);

        while (DisplayManager.shouldDisplayClose()) {
            player.move();
            camera.move();
            picker.update();

            masterRenderer.processEntity(player);
            masterRenderer.processWorld(world);

            for (Entity entity : entityList) {
                masterRenderer.processEntity(entity);
            }

            masterRenderer.render(lights, camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }

        guiRenderer.cleanUp();
        masterRenderer.destory();
        modelLoader.destroy();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
