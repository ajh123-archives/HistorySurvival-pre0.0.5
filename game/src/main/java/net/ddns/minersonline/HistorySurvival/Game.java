package net.ddns.minersonline.HistorySurvival;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.ddns.minersonline.HistorySurvival.api.EventHandler;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.api.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.text.fontRendering.TextMaster;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.utils.ClassUtils;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterFrameBuffers;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterShader;
import net.ddns.minersonline.HistorySurvival.gameplay.GamePlugin;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import org.joml.Vector2f;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class Game extends GameHook {
	private static final Logger logger = LoggerFactory.getLogger(Game.class);
	private final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();

	public static String GAME = "History Survival";
	public static String VERSION = "0.0.2";

	private Scene currentScene = null;
	private final List<JSONTextComponent> debugString = new ArrayList<>();
	private final Map<DelayedTask, Integer> tasks = new HashMap<>();
	public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();


	private void helpCommand(CommandContext<Object> c) {
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


	private void start() {
		GameHook.instance = this;

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


		this.dispatcher.register(literal("help")
		.then(
			argument("page", integer())
			.executes(c -> {
				helpCommand(c);
				return 1;
			})
		)
		.then(
			argument("comm", string())
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

		ModelLoader modelLoader = new ModelLoader();
		TextMaster.init(modelLoader);
		MasterRenderer masterRenderer = new MasterRenderer();
		ParticleMaster.init(modelLoader, masterRenderer.getProjectionMatrix());

		FontType font = new FontType(modelLoader.loadTexture("font/consolas.png"), "font/consolas.fnt");
		FontType font_bold = new FontType(modelLoader.loadTexture("font/consolas_bold.png"), "font/consolas_bold.fnt");
		FontType font_bold_italic = new FontType(modelLoader.loadTexture("font/consolas_bold_italic.png"), "font/consolas_bold_italic.fnt");
		FontType font_italic = new FontType(modelLoader.loadTexture("font/consolas_italic.png"), "font/consolas_italic.fnt");
		FontGroup consolas = new FontGroup(font, font_bold, font_bold_italic, font, font, font_italic, font, font);

		GuiRenderer guiRenderer = new GuiRenderer(modelLoader);
		WaterShader waterShader = new WaterShader();
		WaterFrameBuffers wfbos = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(modelLoader, waterShader, masterRenderer.getProjectionMatrix(), wfbos);

		GUIText debugText = null;
		GUIText debugParent = new GUIText("", 1.3f, consolas, new Vector2f(0, 0), -1, false);

		currentScene = new MenuScene(this, modelLoader, masterRenderer, guiRenderer);
		currentScene.init();

		for (EventHandler handler : eventHandlers) {
			if(!Objects.equals(handler.getClass().getClassLoader().getName(), "app")) {
				handler.hello();
			}
		}

		while (DisplayManager.shouldDisplayClose()) {
			KeyEvent keyEvent = Keyboard.getKeyEvent();
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

			try {
				currentScene.update(keyEvent);
			} catch (Exception ignored){}

			World world = currentScene.getWorld();
			Camera camera = currentScene.getCamera();
			Player player = currentScene.getPlayer();
			List<Entity> entityList = currentScene.getEntities();
			List<Light> lights = currentScene.getLights();
			Light sun = currentScene.getSun();


			if(world != null) {
				GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
				wfbos.bindReflectionFrameBuffer();
				float waterHeight = world.getHeightOfWater(player.getPosition().x, player.getPosition().z);
				float distance = 2 * (camera.getPosition().y - waterHeight);
				camera.getPosition().y -= distance;
				camera.invertPitch();
				masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, 1, 0, -waterHeight+1f));
				camera.getPosition().y += distance;
				camera.invertPitch();

				wfbos.bindRefractionFrameBuffer();
				masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, -1, 0, waterHeight+1f));
				wfbos.unbindCurrentFrameBuffer();

				masterRenderer.renderScene(entityList, world, lights, camera, new Vector4f(0, -1, 0, 999999999));
				waterRenderer.render(world.getWaterTiles(), camera, sun);
				GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			} else {
				masterRenderer.renderScene(entityList, null, lights, camera, new Vector4f(0, -1, 0, 999999999));
			}

			ParticleMaster.renderParticles(camera);

			guiRenderer.render(currentScene.getGUIs());
			Terrain region = null;
			if(world != null && player != null) {
				region = world.getTerrain(player.getPosition().x, player.getPosition().z);
			}

			debugString.clear();
			debugString.add(new JSONTextComponent(GAME+" "+VERSION+"\n"));
			debugString.add(new JSONTextComponent("FPS: "+DisplayManager.getFPS()+"\n"));
			debugString.add(new JSONTextComponent("P: "+ParticleMaster.getCount()+"\n"));
			if(player!=null) {
				debugString.add(new JSONTextComponent("PlayerPosition\n"));
				debugString.add(new JSONTextComponent("X: "+player.getPosition().x+"\n"));
				debugString.add(new JSONTextComponent("Y: "+player.getPosition().y+"\n"));
				debugString.add(new JSONTextComponent("Z: "+player.getPosition().z+"\n"));
			}
			debugString.add(new JSONTextComponent("CameraPosition\n"));
			debugString.add(new JSONTextComponent("X: "+camera.getPosition().x+"\n"));
			debugString.add(new JSONTextComponent("Y: "+camera.getPosition().y+"\n"));
			debugString.add(new JSONTextComponent("Z: "+camera.getPosition().z+"\n"));
			if(region == null) {
				debugString.add(new JSONTextComponent("Region\n"));
				JSONTextComponent x = new JSONTextComponent("X: null\n");
				x.setColor(ChatColor.RED.toString());
				debugString.add(x);
				JSONTextComponent z = new JSONTextComponent("Z: null\n");
				z.setColor(ChatColor.RED.toString());
				debugString.add(z);
			}
			if(region != null) {
				debugString.add(new JSONTextComponent("Region\n"));
				JSONTextComponent x = new JSONTextComponent("X: "+region.getX() / region.getSize()+"\n");
				x.setColor(ChatColor.RED.toString());
				debugString.add(x);
				JSONTextComponent z = new JSONTextComponent("Z: "+region.getZ() / region.getSize()+"\n");
				z.setColor(ChatColor.RED.toString());
				debugString.add(z);
			}

			if (debugText != null) {
				debugText.remove();
			}
			debugText = JSONTextBuilder.build_string_array(debugString, debugParent);
			debugText.setVisible(DisplayManager.getShowFPSTitle());

			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_F3)) {
				boolean debug = DisplayManager.getShowFPSTitle();
				DisplayManager.setShowFPSTitle(!debug);
				debugText.setVisible(!debug);
			}
			TextMaster.render();

			DisplayManager.updateDisplay();
		}

		currentScene.stop();
		logger.info("Stopping!");
		DisplayManager.closeDisplay();
		logger.info("Closed display");
		pluginManager.stopPlugins();
		logger.info("Plugins stopped");
		currentScene.stop();
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
		String version = cmd.getOptionValue("version");
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
	public CommandDispatcher<Object> getDispatcher() {
		return dispatcher;
	}

	@Override
	public void hello() {
		logger.info("Hello!");
	}

	public void setCurrentScene(Scene currentScene) {
		logger.debug("Left Scene "+ this.currentScene);
		this.currentScene.stop();
		this.currentScene = currentScene;
		try {
			currentScene.init();
		} catch (Exception e){
			e.printStackTrace();
		}
		logger.debug("Entered Scene "+ currentScene);
	}

	public void addTask(DelayedTask task) {
		tasks.put(task, 2);
	}
}
