package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.api.EventHandler;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleRenderer;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
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
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    public static String GAME = "History Survival";
    public static String VERSION = "0.0.1";


    private void start() {
        List<Path> pluginDirs = new ArrayList<>();
        if(GameSettings.assetsDir!=null)
            pluginDirs.add(Paths.get(GameSettings.assetsDir));
        if(GameSettings.gameDir!=null)
            pluginDirs.add(Paths.get(GameSettings.gameDir+"/plugins"));

        logger.info("Plugins dir: " + pluginDirs);

        // create the plugin manager
        final PluginManager pluginManager = new DefaultPluginManager(pluginDirs) {
            @Override
            protected CompoundPluginDescriptorFinder createPluginDescriptorFinder() {
                return new CompoundPluginDescriptorFinder()
                        .add(new PropertiesPluginDescriptorFinder())
                        .add(new ManifestPluginDescriptorFinder());
            }
        };

        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        DisplayManager.createDisplay();
        DisplayManager.setShowFPSTitle(false);

        List<EventHandler> eventHandlers = pluginManager.getExtensions(EventHandler.class);

        logger.info("OpenGL: " + DisplayManager.getOpenGlVersionMessage());
        logger.info("LWJGL: " + Version.getVersion());

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

        ParticleTexture particleTexture = new ParticleTexture(modelLoader.loadTexture("grass.png"),  1, false);
        ParticleSystem particleSystem = new ParticleSystem(particleTexture, 50, 0, 0.3f, 4, 2);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        for (EventHandler handler : eventHandlers) {
            handler.hello();
        }

        while (DisplayManager.shouldDisplayClose()) {
            Mouse.update();
            player.move();
            camera.move();
            picker.update();

            Vector3f pos = new Vector3f(player.getPosition());
            pos.y += 10;
            particleSystem.generateParticles(pos);
            ParticleMaster.update(camera);


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
        logger.info("Stopping!");
        pluginManager.stopPlugins();
        logger.info("Plugins stopped");
        ParticleMaster.cleanUp();
        logger.info("Cleaned particles");
        TextMaster.cleanUp();
        logger.info("Cleaned text");
        wfbos.cleanUp();
        logger.info("Cleaned water buffers");
        waterShader.destroy();
        logger.info("Cleaned water shaders");
        guiRenderer.cleanUp();
        logger.info("Cleaned gui");
        masterRenderer.destory();
        logger.info("Cleaned main renderer");
        modelLoader.destroy();
        logger.info("Cleaned model loader");
        DisplayManager.closeDisplay();
        logger.info("Closed display");
    }


    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option un = new Option(null, "username", true, "Username");
        options.addOption(un);
        Option ve = new Option(null, "version", true, "Game Version");
        options.addOption(ve);
        Option gd = new Option(null, "gameDir", true, "Game Directory");
        options.addOption(gd);
        Option id = new Option(null, "uuid", true, "User ID");
        options.addOption(id);
        Option at = new Option(null, "accessToken", true, "Auth access token");
        options.addOption(at);
        Option ad = new Option(null, "assetsDir", true, "Assets directory");
        options.addOption(ad);
        Option de = new Option(null, "demo", false, "Demo Mode");
        options.addOption(de);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("game", options);

            System.exit(1);
        }

        String username = cmd.getOptionValue("username");
        String version = cmd.getOptionValue("version");
        String gameDir = cmd.getOptionValue("gameDir");
        String uuid = cmd.getOptionValue("uuid");
        String accessToken = cmd.getOptionValue("accessToken");
        String assetsDir = cmd.getOptionValue("assetsDir");
        String demo = cmd.getOptionValue("demo");

        GameSettings.username = username;
        GameSettings.version = version;
        GameSettings.gameDir = gameDir;
        GameSettings.uuid = uuid;
        GameSettings.accessToken = accessToken;
        GameSettings.assetsDir = assetsDir;
        GameSettings.demo = demo;

        new Game().start();
    }
}
