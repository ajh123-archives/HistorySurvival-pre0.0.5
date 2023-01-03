package tk.minersonline.history_survival;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import io.github.classgraph.ClassGraph;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.ObjectShare;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.minecraft.Slf4jLogHandler;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.LoaderUtil;
import net.fabricmc.loader.impl.util.log.Log;
import org.spongepowered.asm.util.JavaVersion;
import tk.minersonline.history_survival.main.Client;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HistorySurvivalGameProvider implements GameProvider {
	private EnvType envType;
	private Arguments arguments;
	private final List<Path> gameJars = new ArrayList<>();
	private FabricLauncher launcher;

	private static final GameTransformer TRANSFORMER = new GameTransformer();

	@Override
	public String getGameId() {
		return "history_survival";
	}

	@Override
	public String getGameName() {
		return "History Survival";
	}

	@Override
	public String getRawGameVersion() {
		return "0.0.4";
	}

	@Override
	public String getNormalizedGameVersion() {
		return "0.0.4";
	}

	@Override
	public Collection<BuiltinMod> getBuiltinMods() {
		BuiltinModMetadata.Builder metadata = new BuiltinModMetadata.Builder(
				getGameId(),
				getNormalizedGameVersion())
				.setName(getGameName())
				.setDescription("Hello World program made to demonstrate the Fabric Loader")
				.setEnvironment(ModEnvironment.UNIVERSAL);

		String version = Runtime.version().toString();

		try {
			metadata.addDependency(new ModDependencyImpl(ModDependency.Kind.DEPENDS, "java", Collections.singletonList(String.format(Locale.ENGLISH, ">=%s", version))));
		} catch (VersionParsingException e) {
			throw new RuntimeException(e);
		}

		return Collections.singletonList(new BuiltinMod(gameJars, metadata.build()));
	}

	@Override
	public String getEntrypoint() {
		return "";
	}

	@Override
	public Path getLaunchDirectory() {
		if (arguments == null) {
			return Paths.get(".");
		}
		return getLaunchDirectory(arguments);
	}

	@Override
	public boolean isObfuscated() {
		return false; // generally no...
	}

	@Override
	public boolean requiresUrlClassLoader() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return System.getProperty("historySurvival.skipProvider") == null;
	}

	@Override
	public boolean locateGame(FabricLauncher launcher, String[] args) {
		this.envType = launcher.getEnvironmentType();
		this.arguments = new Arguments();
		arguments.parse(args);

		List<URI> classpath = new ClassGraph().getClasspathURIs();
		for (URI url : classpath) {
			gameJars.add(Path.of(url));
		}
		ObjectShare share = FabricLoaderImpl.INSTANCE.getObjectShare();
		share.put("fabric-loader:inputGameJars", gameJars);
		return true;
	}

	private static Path getLaunchDirectory(Arguments argMap) {
		return Paths.get(argMap.getOrDefault("gameDir", "."));
	}

	@Override
	public void initialize(FabricLauncher launcher) {
		Log.init(new Slf4jLogHandler());
		TRANSFORMER.locateEntrypoints(launcher, gameJars);
		this.launcher = launcher;
	}

	@Override
	public Arguments getArguments() {
		return arguments;
	}

	@Override
	public String[] getLaunchArguments(boolean sanitize) {
		if (arguments == null) return new String[0];

		String[] ret = arguments.toArray();
		if (!sanitize) return ret;

		int writeIdx = 0;

		for (int i = 0; i < ret.length; i++) {
			String arg = ret[i];

			if (i + 1 < ret.length && arg.startsWith("--")) {
				i++; // skip value
			} else {
				ret[writeIdx++] = arg;
			}
		}

		if (writeIdx < ret.length) ret = Arrays.copyOf(ret, writeIdx);

		return ret;
	}

	@Override
	public GameTransformer getEntrypointTransformer() {
		return TRANSFORMER;
	}

	@Override
	public boolean canOpenErrorGui() {
		if (arguments == null || envType == EnvType.CLIENT) {
			return true;
		}

		List<String> extras = arguments.getExtraArgs();
		return !extras.contains("nogui") && !extras.contains("--nogui");
	}

	@Override
	public boolean hasAwtSupport() {
		return LoaderUtil.hasAwtSupport();
	}

	@Override
	public void unlockClassPath(FabricLauncher launcher) {
		for (Path gameJar : gameJars) {
			launcher.addToClassPath(gameJar);
		}
//		try {
//			launcher.loadIntoTarget("com.badlogic.gdx.Gdx");
//			launcher.loadIntoTarget("tk.minersonline.history_survival.HistorySurvival");
//
//			Class<?> hs = Class.forName("tk.minersonline.history_survival.HistorySurvival", true, launcher.getTargetClassLoader());
//
//			Method initMethod = hs.getDeclaredMethod("setINSTANCE", HistorySurvival.class);
//			initMethod.setAccessible(true);
//			initMethod.invoke(null, HistorySurvival.INSTANCE);
//
//		} catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
	}

	@Override
	public void launch(ClassLoader loader) {
		launcher.setValidParentClassPath(gameJars);
		String targetClass = "tk.minersonline.history_survival.main.ServerLauncher";

		if (envType == EnvType.CLIENT) {
			targetClass = "tk.minersonline.history_survival.main.Client";
		}

		try {
			Class<?> c = loader.loadClass(targetClass);
			Object client = c.getConstructor().newInstance();
			Method m = c.getMethod("create");
			m.invoke(client);
		} catch (InvocationTargetException e) {
			throw new FormattedException("History Survival has crashed!", e.getCause());
		} catch (ReflectiveOperationException e) {
			throw new FormattedException("Failed to start History Survival", e);
		}
	}
}
