package net.ddns.minersonline.HistorySurvival;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.ddns.minersonline.HistorySurvival.api.EventHandler;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.api.registries.VoxelType;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelChunkMesh;
import net.ddns.minersonline.HistorySurvival.engine.*;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import net.ddns.minersonline.HistorySurvival.engine.utils.ClassUtils;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.water.WaterFrameBuffers;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.water.WaterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.water.WaterShader;
import net.ddns.minersonline.HistorySurvival.gameplay.GamePlugin;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.Configuration;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.ddns.minersonline.HistorySurvival.network.Utils.VERSION;

public class Game extends GameHook {
	public static final Logger logger = LoggerFactory.getLogger(Game.class);
	private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();

	public static Scene currentScene = null;
	private static Scene startScene = null;
	private static final Map<DelayedTask, Integer> tasks = new HashMap<>();
	public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

	private static GuiRenderer guiRenderer;
	private static WaterFrameBuffers wfbos;
	private static WaterShader waterShader;
	private static WaterRenderer waterRenderer;
	private static MasterRenderer masterRenderer;
	public static ModelLoader modelLoader = new ModelLoader();
	public static final ExecutorService executor = Executors.newCachedThreadPool();


	/**
	 * Testing
	 */
	private void helpCommand(CommandContext<CommandSender> c) {
		CommandContext<CommandSender> context = (CommandContext<CommandSender>) (CommandContext<? extends Object>) c;
		CommandSender sender = context.getSource();
		Collection<CommandNode<CommandSender>> commands = context.getRootNode().getChildren();
		try {
			int page = getInteger(c, "page");
			sender.sendMessage(new JSONTextComponent("Help page ("+page+")\n"));
		} catch (IllegalArgumentException ignored){
			JSONTextComponent header = new JSONTextComponent(" Help page (0) ");
			header.setColor(ChatColor.DARK_GREEN.toString());

			JSONTextComponent prefix = new JSONTextComponent("=====");
			prefix.setColor(ChatColor.GOLD.toString());
			JSONTextComponent suffix = new JSONTextComponent("\n");

			sender.sendMessage(prefix);
			sender.sendMessage(header);
			sender.sendMessage(prefix);
			sender.sendMessage(suffix);

			for(CommandNode<CommandSender> command : commands){
				sender.sendMessage(new JSONTextComponent("/"+command.getName()));
			}
		}

	}

	private static void init(){
		modelLoader = new ModelLoader();
		masterRenderer = new MasterRenderer(modelLoader);
		ParticleMaster.init(modelLoader, masterRenderer.getProjectionMatrix());

		guiRenderer = new GuiRenderer(modelLoader);
		waterShader = new WaterShader();
		wfbos = new WaterFrameBuffers();
		waterRenderer = new WaterRenderer(modelLoader, waterShader, masterRenderer.getProjectionMatrix(), wfbos);
	}


	private void start() {
		setInstance(this);
		LOADER = new ResourceLoaderImpl();
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);

		List<Path> pluginDirs = new ArrayList<>();
		if(GameSettings.assetsDir!=null)
			pluginDirs.add(Paths.get(GameSettings.assetsDir));
		if(GameSettings.gameDir!=null)
			pluginDirs.add(Paths.get(GameSettings.gameDir+"/plugins"));
		if(GameSettings.dev!=null)
			pluginDirs.add(Paths.get(GameSettings.dev));

		pluginDirs.add(Paths.get(Objects.requireNonNull(ClassUtils.GetClassContainer(GamePlugin.class)).substring(10)).getParent());

		logger.info("Plugins dir: " + pluginDirs);

		// create the plugin manager
		final PluginManager pluginManager = new DefaultPluginManager(pluginDirs) {
			@Override
			protected PluginLoader createPluginLoader() {
				// load only jar plugins
				return new JarPluginLoader(this);
			}

			@Override
			protected PluginDescriptorFinder createPluginDescriptorFinder() {
				// read plugin descriptor from jar's manifest
				return new ManifestPluginDescriptorFinder();
			}
		};
		pluginManager.setSystemVersion(VERSION);

		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		this.dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("help")
		.then(
			RequiredArgumentBuilder.<CommandSender, Integer>argument("page", IntegerArgumentType.integer())
			.executes(c -> {
				helpCommand(c);
				return 1;
			})
		)
		.then(
			RequiredArgumentBuilder.<CommandSender, String>argument("comm", StringArgumentType.string())
			.executes(c -> {
				helpCommand(c);
				return 1;
			})
		)
		.executes(c -> {
			helpCommand(c);
			return 1;
		}));


		DisplayManager.createDisplay();
		DisplayManager.setShowFPSTitle(false);

		List<EventHandler> eventHandlers = pluginManager.getExtensions(EventHandler.class);

		logger.info("OpenGL: " + DisplayManager.getOpenGlVersionMessage());
		logger.info("LWJGL: " + Version.getVersion());

		ModelType.init();
		VoxelType.init();

		TextureLoader.createTextureAtlas();
		init();

		currentScene = new MenuScene(this, modelLoader, masterRenderer, guiRenderer);
		//currentScene = new MainScene(null,this, modelLoader, masterRenderer, guiRenderer);
		currentScene.init();
		currentScene.start();
		startScene = currentScene;

		for (EventHandler handler : eventHandlers) {
			if(!Objects.equals(handler.getClass().getClassLoader().getName(), "app")) {
				handler.hello();
			}
		}

		while (DisplayManager.shouldDisplayClose()) {
			float deltaTime = (float) DisplayManager.getDeltaInSeconds();
			Mouse.update();

			Iterator<Map.Entry<DelayedTask, Integer>> taskIterator = tasks.entrySet().iterator();
			while (taskIterator.hasNext()) {
				Map.Entry<DelayedTask, Integer> pair = taskIterator.next();
				DelayedTask task = pair.getKey();
				int delay = pair.getValue();
				if(delay == 0){
					Executors.newSingleThreadExecutor().execute(task::execute);
					taskIterator.remove();
				}
				pair.setValue(delay - 1);
			}
			try {
				while (!queue.isEmpty())
					queue.take().run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			DisplayManager.preUpdate(currentScene);

			GameObjectManager.update(deltaTime);
			currentScene.update(deltaTime);

			Map<TexturedModel, Collection<VoxelChunkMesh>> world = currentScene.getWorld().getVisible();
			Camera camera = currentScene.getCamera();
			List<Light> lights = currentScene.getLights();
			Light sun = currentScene.getSun();

			ParticleMaster.update(camera, deltaTime);

			if(world != null) {
				GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
				wfbos.bindReflectionFrameBuffer();
				float waterHeight = 0;
				float distance = 2 * (camera.getPosition().y - waterHeight);
				camera.getPosition().y -= distance;
				camera.invertPitch();
				masterRenderer.renderScene(world, lights, camera, new Vector4f(0, 1, 0, -waterHeight+1f), deltaTime);
				camera.getPosition().y += distance;
				camera.invertPitch();

				wfbos.bindRefractionFrameBuffer();
				masterRenderer.renderScene(world, lights, camera, new Vector4f(0, -1, 0, waterHeight+1f), deltaTime);
				wfbos.unbindCurrentFrameBuffer();

				masterRenderer.renderScene(world, lights, camera, new Vector4f(0, -1, 0, 999999999), deltaTime);
				waterRenderer.render(null, camera, sun, deltaTime);
				GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			} else {
				masterRenderer.renderScene(null, lights, camera, new Vector4f(0, -1, 0, 999999999), deltaTime);
			}

			ParticleMaster.renderParticles(camera);

			guiRenderer.render(currentScene.getGUIs());
			boolean debug = DisplayManager.getShowFPSTitle();

			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_F3)) {
				DisplayManager.setShowFPSTitle(!debug);
			}

			currentScene.renderDebug();
			DisplayManager.updateDisplay();
		}

		executor.shutdown();
		currentScene.stop();
		GameObjectManager.reset();
		logger.info("Stopping!");
		pluginManager.stopPlugins();
		logger.info("Plugins stopped");
		currentScene.stop();
		ParticleMaster.cleanUp();
		logger.info("Cleaned particles");
		//TextMaster.cleanUp();
		//logger.info("Cleaned text");
		wfbos.cleanUp();
		logger.info("Cleaned water buffers");
		waterShader.destroy();
		logger.info("Cleaned water shaders");
		guiRenderer.cleanUp();
		logger.info("Cleaned gui");
		masterRenderer.destroy();
		logger.info("Cleaned main renderer");
		modelLoader.destroy();
		logger.info("Cleaned model loader");
		ResourceType.destroy();
		logger.info("Cleaned resources");
		DisplayManager.dispose();
		logger.info("Closed display");
		System.exit(0);
	}

	private Predicate<Object> permission(String s) {
		return b -> {
			if (b instanceof String c) {
				return c.equals(s);
			}
			return false;
		};
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
		Option dv = new Option(null, "dev", true, "Dev mode dir");
		options.addOption(dv);

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
		String version = cmd.getOptionValue("version", "0.0.3");
		String gameDir = cmd.getOptionValue("gameDir");
		String uuid = cmd.getOptionValue("uuid");
		String accessToken = cmd.getOptionValue("accessToken");
		String assetsDir = cmd.getOptionValue("assetsDir");
		String demo = cmd.getOptionValue("demo");
		String devDir = cmd.getOptionValue("dev");

		GameSettings.username = username;
		GameSettings.version = version;
		GameSettings.gameDir = gameDir;
		GameSettings.uuid = uuid;
		GameSettings.accessToken = accessToken;
		GameSettings.assetsDir = assetsDir;
		GameSettings.demo = demo;
		GameSettings.dev = devDir;

		new Game().start();
	}

	@Override
	public CommandDispatcher<CommandSender> getDispatcher() {
		return dispatcher;
	}

	@Override
	public void hello() {
		logger.info("Hello!");
	}

	public static void setCurrentScene(Scene scene) {
		if(scene.equals(currentScene)){return;}
		currentScene.stop();
		currentScene.exit();
		String sceneName = currentScene.toString();
		currentScene = null;
		wfbos.cleanUp();
		waterShader.destroy();
		guiRenderer.cleanUp();
		masterRenderer.destroy();
		modelLoader.destroy();
		logger.info("Left Scene "+ sceneName);
		init();
		try {
			scene.init();
			scene.start();
		} catch (Exception e){
			e.printStackTrace();
		}
		currentScene = scene;
		logger.info("Entered Scene "+ currentScene);
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public static Scene getStartSceneScene(){
		return startScene;
	}

	public static void addTask(DelayedTask task) {
		tasks.put(task, 2);
	}

	public static void addTask(DelayedTask task, int delay) {
		tasks.put(task, delay);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
