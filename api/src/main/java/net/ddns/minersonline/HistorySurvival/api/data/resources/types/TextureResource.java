package net.ddns.minersonline.HistorySurvival.api.data.resources.types;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;

import java.util.HashMap;

public class TextureResource extends ResourceType {
	private final TextureType type;
	private static final HashMap<HashMap<ResourceLocation, TextureType>, ModelTexture> cache = new HashMap<>();

	public TextureResource(TextureType type) {
		super(type.path);
		this.type = type;
	}

	public TextureType getType() {
		return type;
	}

	public ModelTexture load(ResourceLocation path, TextureFormat format) {
		return load(path, format, false);
	}

	public ModelTexture load(ResourceLocation path, TextureFormat format, boolean addToAtlas) {
		HashMap<ResourceLocation, TextureType> key = new HashMap<>();
		key.put(path, type);
		if (cache.containsKey(key)) {
			return cache.get(key);
		} else {
			ModelTexture texture = new ModelTexture(GameHook.getLoader().loadTexture(
					path.getPath() + "." + format.format,
					addToAtlas,
					"assets/" + path.getNamespace() + "/" + type.path),
					this
			);
			cache.put(key, texture);
			return texture;
		}
	}

	public static void destroy() {
		cache.clear();
	}

	public enum TextureType {
		GENERIC("textures"),
		VOXEL("voxels/textures")
		;

		private final String path;

		/**
		 * @param path The resource path
		 */
		TextureType(final String path) {
			this.path = path;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return path;
		}
	}

	public enum TextureFormat {
		PNG("png"),
		JPG("jpg"),
		JPEG("jepg")
		;

		private final String format;

		/**
		 * @param format The resource format
		 */
		TextureFormat(final String format) {
			this.format = format;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return format;
		}
	}
}
