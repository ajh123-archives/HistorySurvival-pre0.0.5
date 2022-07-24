package net.ddns.minersonline.HistorySurvival.api.registries;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ModelType<T extends TexturedModel> {
	protected ResourceLocation registryName;
	private static Map<String, TexturedModel> MODElS = new HashMap<>();

	static {
		TexturedModel tree = new TexturedModel(
				GameHook.getLoader().loadObjModel("tree.obj"),
				new ModelTexture(GameHook.getLoader().loadTexture("tree.png")));
		MODElS.put("tree", tree);


		TexturedModel low_poly_tree = new TexturedModel(
				GameHook.getLoader().loadObjModel("lowPolyTree.obj"),
				new ModelTexture(GameHook.getLoader().loadTexture("lowPolyTree.png")));
		MODElS.put("low_poly_tree", low_poly_tree);


		TexturedModel grassModel = new TexturedModel(
				GameHook.getLoader().loadObjModel("grassModel.obj"),
				new ModelTexture(GameHook.getLoader().loadTexture("grassTexture.png"))
		);
		grassModel.getModelTexture().setHasTransparency(true);
		grassModel.getModelTexture().setUseFakeLighting(true);
		MODElS.put("grass", grassModel);


		ModelTexture fernTextureAtlas = new ModelTexture(GameHook.getLoader().loadTexture("fern.png"));
		fernTextureAtlas.setNumberOfRowsInTextureAtlas(2);
		TexturedModel fernModel = new TexturedModel(
				GameHook.getLoader().loadObjModel("fern.obj"),
				fernTextureAtlas
		);
		fernModel.getModelTexture().setHasTransparency(true);
		MODElS.put("fern", fernModel);


		TexturedModel player = new TexturedModel(
				GameHook.getLoader().loadObjModel("person.obj"),
				new ModelTexture(GameHook.getLoader().loadTexture("playerTexture.png"))
		);
		MODElS.put("player", player);
	}

	public static ModelType<TexturedModel> TREE_MODEL = register(
			"tree",
			ModelType.Builder.of(() -> MODElS.get("tree"))
	);

	public static ModelType<TexturedModel> LOW_POLY_TREE_MODEL = register(
			"low_poly_tree",
			ModelType.Builder.of(() -> MODElS.get("low_poly_tree"))
	);

	public static ModelType<TexturedModel> GRASS_MODEL = register(
			"grass",
			ModelType.Builder.of(() -> MODElS.get("grass"))
	);

	public static ModelType<TexturedModel> FERN_MODEL = register(
			"fern",
			ModelType.Builder.of(() -> MODElS.get("fern"))
	);

	public static ModelType<TexturedModel> PLAYER_MODEL = register(
			"player",
			ModelType.Builder.of(() -> MODElS.get("player"))
	);

	public static void init(){}

	@SuppressWarnings("unchecked")
	private static <T extends TexturedModel> ModelType<T> register(String id, ModelType.Builder<T> builder) {
		ResourceLocation registryName = new ResourceLocation(id);
		ModelType<T> type = (ModelType<T>) Registry.register(Registries.MODEL_REGISTRY, registryName, builder.build());
		type.registryName = registryName;
		return type;
	}

	@Nullable
	public final ResourceLocation getRegistryName()
	{
		return registryName;
	}



	private final ModelFactory<T> factory;

	public ModelType(ModelFactory<T> factory) {
		this.factory = factory;
	}

	public static class Builder<T extends TexturedModel> {
		private final ModelFactory<T> factory;

		private Builder(ModelFactory<T> factory) {
			this.factory = factory;
		}

		public static <T extends TexturedModel> ModelType.Builder<T> of(ModelFactory<T> factory) {
			return new ModelType.Builder<>(factory);
		}

		public ModelType<T> build() {
			return new ModelType<>(this.factory);
		}
	}

	@FunctionalInterface
	public interface ModelFactory<T extends TexturedModel> {
		T create();
	}

	@Nullable
	public T create() {
		T type = this.factory.create();
		type.setModelType(registryName);
		return type;
	}
}
