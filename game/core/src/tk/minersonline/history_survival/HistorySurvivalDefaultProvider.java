package tk.minersonline.history_survival;

import com.google.gwt.thirdparty.guava.common.annotations.GwtIncompatible;
import io.github.classgraph.ClassGraph;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@GwtIncompatible("")
public class HistorySurvivalDefaultProvider implements GameProvider {
	private static final String[] ALLOWED_EARLY_CLASS_PREFIXES = {"net.fabricmc.loader.api.", "net.fabricmc.loader.impl."};
	private static final String[] ALLOWED_EARLY_CLASSES = {"net.fabricmc.loader.api.FabricLoader", "net.fabricmc.loader.impl.FabricLoaderImpl"};
	private final List<Path> validParentClassPath = new ArrayList<>();
	private EnvType envType;
	private Arguments arguments;
	private final List<Path> gameJars = new ArrayList<>();

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
				.setDescription("Default data for History Survival")
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
			Path path = Path.of(url);
			gameJars.add(path);
			validParentClassPath.add(path);
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
		launcher.setValidParentClassPath(validParentClassPath);

		for (Path gameJar : gameJars) {
			launcher.addToClassPath(gameJar, ALLOWED_EARLY_CLASS_PREFIXES);
		}
//		ClassLoader loader = launcher.getTargetClassLoader();
//		ClassLoader prev = Thread.currentThread().getContextClassLoader();
//		System.out.println(loader);
//		Thread.currentThread().setContextClassLoader(loader);
//
//		System.out.println(FabricLoader.getInstance().getAllMods());
//
//		Thread.currentThread().setContextClassLoader(prev);

		Log.init(new Slf4jLogHandler());
		TRANSFORMER.locateEntrypoints(launcher, gameJars);
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
//		for (Path gameJar : gameJars) {
//			launcher.setAllowedPrefixes(gameJar);
//		}
	}



	@Override
	public void launch(ClassLoader loader) {
		String targetClass = getMainEntryPoint();

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

	public String getMainEntryPoint() {
		return null;
	}
}
