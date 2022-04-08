package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.particles.Particle;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.text.fontRendering.TextMaster;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import net.ddns.minersonline.HistorySurvival.engine.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.textures.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterFrameBuffers;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterShader;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterTile;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;
import java.util.*;

public class Game {
    public static String GAME = "History Survival";
    public static String VERSION = "0.0.1";


    public static HashMap<Integer, Player> playerList = new HashMap<Integer, Player>();

    private void start() throws IOException, ClassNotFoundException {
        //MixinBootstrap.init(); //mixins do not work for now
        //Mixins.addConfiguration("mixins.json");

        DisplayManager.createDisplay();
        DisplayManager.setShowFPSTitle(false);

        System.out.println("OpenGL: " + DisplayManager.getOpenGlVersionMessage());
        System.out.println("LWJGL: " + Version.getVersion());

        ModelLoader modelLoader = new ModelLoader();
        TextMaster.init(modelLoader);
        MasterRenderer masterRenderer = new MasterRenderer();
        ParticleMaster.init(modelLoader, masterRenderer.getProjectionMatrix());

        FontType font = new FontType(modelLoader.loadTexture("font/consolas.png"), "font/consolas.fnt");

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
        Light sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
        lights.add(sun);

        TexturedModel lamp = new TexturedModel(ObjLoader.loadObjModel("lamp.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("lamp.png")));

        Light moveLight = new Light(
                new Vector3f(400, 22, -293),
                new Vector3f(2, 0,0),
                new Vector3f(1, 0.01f,0.002f));
        lights.add(moveLight);
        Entity moveEntity = new Entity(lamp,
                new Vector3f(400, 9, -293),
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


        TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));
        Player player = new Player(world, playerOBJ, new Vector3f(380, 8, -290), 0,0,0,0.6f);
        entityList.add(player);
        Camera camera = new Camera(player);

        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(modelLoader);

        MousePicker picker = new MousePicker(world, masterRenderer.getProjectionMatrix(), camera);

        WaterShader waterShader = new WaterShader();
        WaterFrameBuffers wfbos = new WaterFrameBuffers();
        WaterRenderer waterRenderer = new WaterRenderer(modelLoader, waterShader, masterRenderer.getProjectionMatrix(), wfbos);

        List<WaterTile> waterTiles = new ArrayList<>();
        WaterTile water = new WaterTile(370, -293, 4.2f);
        waterTiles.add(water);

        String my_text = "[{\"text\":\"Should not be shown!\"}]";
        GUIText debugText = JSONTextBuilder.build_string(font, my_text);
        debugText.setVisible(DisplayManager.getShowFPSTitle());

        ParticleSystem particleSystem = new ParticleSystem(50, 0, 0.3f, 4, 2);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        while (DisplayManager.shouldDisplayClose()) {
            Mouse.update();
            player.move();
            camera.move();
            picker.update();

            Vector3f pos = new Vector3f(player.getPosition());
            pos.y += 2;
            particleSystem.generateParticles(pos);
            ParticleMaster.update();

//            HashMap<Integer, Integer[]> player_pos_hmap = processPlayers(player);
//            Integer[] player_pos = player_pos_hmap.get(clientId);
//
//            for (Integer key : player_pos_hmap.keySet()) {
//                if (!key.equals(clientId)) {
//                    if (!playerList.containsKey(key)) {
//                        Player player2 = new Player(world, playerOBJ, new Vector3f(400,0,-400), 0, 180, 0, 1);
//                        playerList.put(key, player2);
//                        entityList.add(player2);
//                    } else {
//                        Player player2 = playerList.get(key);
//                        Integer[] player2_pos = player_pos_hmap.get(key);
//                        player2.setCurrentSpeed(player2_pos[0]);
//                        player2.setCurrentTurnSpeed(player2_pos[1]);
//                        player2.setJump(player2_pos[2]);
//                    }
//                }
//            }

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            wfbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            wfbos.bindRefractionFrameBuffer();
            masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, -1, 0, water.getHeight()+1f));
            wfbos.unbindCurrentFrameBuffer();

            masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, -1, 0, 999999999));
            waterRenderer.render(waterTiles, camera, sun);
            ParticleMaster.renderParticles(camera);

            guiRenderer.render(guis);
            Terrain region = Terrain.getTerrain(world, player.getPosition().x, player.getPosition().z);
            String debugString = "[{\"text\":\""+GAME+" \"},{\"text\":\""+VERSION+"\"},";
            debugString+="{\"text\":\"\nFPS: "+DisplayManager.getFPS()+"\"},";
            debugString+="{\"text\":\"\nP: "+ParticleMaster.getCount()+"\"},";
            debugString+="{\"text\":\"\nPlayerPosition:\"},";
            debugString+="{\"text\":\" X:"+player.getPosition().x+"\"},";
            debugString+="{\"text\":\" Y:"+player.getPosition().y+"\"},";
            debugString+="{\"text\":\" Z:"+player.getPosition().z+"\"},";
            debugString+="{\"text\":\"\nCameraPosition:\"},";
            debugString+="{\"text\":\" X:"+camera.getPosition().x+"\"},";
            debugString+="{\"text\":\" Y:"+camera.getPosition().y+"\"},";
            debugString+="{\"text\":\" Z:"+camera.getPosition().z+"\"}";
            if(region == null) {
                debugString+=",{\"text\":\"\nRegion:\"},";
                debugString+="{\"text\":\" X:null\", \"color\":\""+ChatColor.RED+"\"},";
                debugString+="{\"text\":\" Z:null\", \"color\":\""+ChatColor.RED+"\"}";
            }
            if(region != null) {
                debugString+=",{\"text\":\"\nRegion:\"},";
                debugString+="{\"text\":\" X:"+region.getX() / Terrain.SIZE+"\", \"color\":\""+ChatColor.GREEN+"\"},";
                debugString+="{\"text\":\" Z:"+region.getZ() / Terrain.SIZE+"\", \"color\":\""+ChatColor.GREEN+"\"}";
            }
            debugString += "]";

            debugText.remove();
            debugText = JSONTextBuilder.build_string(font, debugString);
            debugText.setVisible(DisplayManager.getShowFPSTitle());

            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_F3)) {
                boolean debug = DisplayManager.getShowFPSTitle();
                DisplayManager.setShowFPSTitle(!debug);
                debugText.setVisible(!debug);
            }
            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        wfbos.cleanUp();
        waterShader.destroy();
        guiRenderer.cleanUp();
        masterRenderer.destory();
        modelLoader.destroy();
        DisplayManager.closeDisplay();
    }

//    private HashMap<Integer, Integer[]> processPlayers(Player myPlayer) throws ClassNotFoundException, IOException {
//        int jumping = myPlayer.isJump() ? 1 : 0;
//        Integer[] controlsUpdate = {Math.round(myPlayer.getCurrentSpeed()), Math.round(myPlayer.getCurrentTurnSpeed()), jumping};
//        return client.sendMessage(clientId, controlsUpdate);
//    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Game().start();
    }
}
