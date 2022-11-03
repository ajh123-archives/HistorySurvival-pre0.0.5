package net.ddns.minersonline.HistorySurvival.api.data.resources.types;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;

public class TextureResource extends ResourceType {
	private final TextureType type;

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
		return new ModelTexture(GameHook.getLoader().loadTexture(
				path.getPath()+"."+format.format,
				addToAtlas,
				type.path),
				this
		);
	}

	public enum TextureType {
		GENERIC("assets/textures"),
		VOXEL("assets/voxels/textures")
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
