package net.ddns.minersonline.HistorySurvival.api.registries;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ModelType<T extends TexturedModel> {
	protected ResourceLocation registryName;
	private static Map<String, TexturedModel> MODElS = new HashMap<>();

	static {
		TexturedModel grassModel = new TexturedModel(
				GameHook.getLoader().loadCube(),
				ResourceType.VOXEL_TEXTURE.load(new ResourceLocation("grass"), TextureResource.TextureFormat.PNG, true)
		);
		grassModel.getModelTexture().setHasTransparency(true);
		grassModel.getModelTexture().setUseFakeLighting(true);
		MODElS.put("grass", grassModel);

		TexturedModel mudModel = new TexturedModel(
				GameHook.getLoader().loadCube(),
				ResourceType.VOXEL_TEXTURE.load(new ResourceLocation("mud"), TextureResource.TextureFormat.PNG, true)
		);
		mudModel.getModelTexture().setHasTransparency(true);
		mudModel.getModelTexture().setUseFakeLighting(true);
		MODElS.put("mud", mudModel);

		TexturedModel player = new TexturedModel(
				GameHook.getLoader().loadObjModel("person.obj"),
				ResourceType.TEXTURE.load(new ResourceLocation("player"), TextureResource.TextureFormat.PNG)
		);
		MODElS.put("player", player);
	}

	public static ModelType<TexturedModel> MUD_MODEL = register(
			"mud",
			ModelType.Builder.of(() -> MODElS.get("mud"))
	);

	public static ModelType<TexturedModel> GRASS_MODEL = register(
			"grass",
			ModelType.Builder.of(() -> MODElS.get("grass"))
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
