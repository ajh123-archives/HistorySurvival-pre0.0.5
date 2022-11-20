package net.ddns.minersonline.HistorySurvival;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.ddns.minersonline.HistorySurvival.api.EnvironmentType;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.resources.EmptyLoader;
import net.ddns.minersonline.HistorySurvival.api.events.CommandRegisterEvent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.commands.HelpCommand;
import net.ddns.minersonline.HistorySurvival.commands.InfoCommand;
import net.ddns.minersonline.HistorySurvival.engine.utils.ClassUtils;
import net.ddns.minersonline.HistorySurvival.gameplay.GamePlugin;
import net.ddns.minersonline.HistorySurvival.network.*;
import org.apache.commons.cli.*;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.ddns.minersonline.HistorySurvival.network.Utils.VERSION;

public class ServerMain extends GameHook {
	private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
	private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
	private final int port;

	private static GenerateKeys keys;

	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	public static String verifyToken = "";
	public static String serverId = "";
	public static boolean running = true;
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static Thread logicThread = null;

	public ServerMain(int port) {
		this.port = port;
		LOADER = new EmptyLoader();
		ModelType.init();
		gson = Utils.gson;
		setInstance(this);

		List<Path> pluginDirs = new ArrayList<>();
		if(GameSettings.gameDir!=null)
			pluginDirs.add(Paths.get(GameSettings.gameDir+"/plugins"));
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

		HelpCommand.register(dispatcher);
		InfoCommand.register(dispatcher);

		List<CommandRegisterEvent> eventHandlers = pluginManager.getExtensions(CommandRegisterEvent.class);

		for (CommandRegisterEvent handler : eventHandlers) {
			if(!Objects.equals(handler.getClass().getClassLoader().getName(), "app")) {
				handler.register(getDispatcher());
			}
		}
	}

	public static void update(){
//		while (running) {
//			if (group != null) {
//				for (GameObject go: GameObjectManager.getGameObjects()){
//					group.forEach((channel -> {
//						logger.info("Update");
//						if ((Integer) (channel.attr(AttributeKey.valueOf("state")).get()) == 3) {
//							channel.writeAndFlush(new UpdateEntityPacket(go));
//						}
//					}));
//				}
//			}
//		}
	}

	public static void main(String[] args) throws Exception {
		try {
			keys = new GenerateKeys(2048);
			keys.createKeys();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		publicKey = keys.getPublicKey();
		privateKey = keys.getPrivateKey();

		Options options = new Options();

		Option pr = new Option(null, "port", true, "Server Port");
		options.addOption(pr);
		Option bn = new Option(null, "bind", true, "Bind address");
		options.addOption(bn);
		Option gd = new Option(null, "gameDir", true, "Game Directory");
		options.addOption(gd);


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

		String sPort = cmd.getOptionValue("port", "36676");
		String sBind = cmd.getOptionValue("bind");
		String gameDir = cmd.getOptionValue("gameDir");

		Files.createDirectories(Paths.get(gameDir));

		GameSettings.gameDir = gameDir;

		int port = Integer.parseInt(sPort);

		new ServerMain(port).run();
	}

	public void run() throws Exception {
		RandomString session = new RandomString();
		verifyToken = session.nextString();
		serverId = session.nextString();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			  logicThread = new Thread(() -> {
				  logger.info("Server started on port "+port);
				while (running){
					try {
						update();
					} catch (Exception e){
						logger.error("An error :(", e);
					}
				}
			});
			logicThread.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.info("Stopping");
				running = false;
				if (logicThread != null) {
					try {
						logicThread.join();
					} catch (InterruptedException ignored) {}
				}
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}));

			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new PacketDecoder(),
									new PacketEncoder(),
									new ServerHandler(group)
							);
						}
					}).option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_RCVBUF, 4096)
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024,16*1024,1024*1024))
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			running = false;
			if (logicThread != null) {
				logicThread.join();
			}
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}



	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public CommandDispatcher<CommandSender> getDispatcher() {
		return dispatcher;
	}

	@Override
	public EnvironmentType getType() {
		return EnvironmentType.SERVER;
	}
}