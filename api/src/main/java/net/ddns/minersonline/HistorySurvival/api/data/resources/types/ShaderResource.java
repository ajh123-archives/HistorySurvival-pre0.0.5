package net.ddns.minersonline.HistorySurvival.api.data.resources.types;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.util.FileUtils;

public class ShaderResource extends ResourceType {
	private final ShaderType type;

	public ShaderResource(ShaderType type) {
		super(type.path);
		this.type = type;
	}

	public ShaderType getType() {
		return type;
	}

	public String load(ResourceLocation path) {
		return FileUtils.loadAsString(type.toString() + "/" + path.getPath() + ".glsl");
	}

	public enum ShaderType {
		VERTEX("assets/shaders/vertex"),
		FRAGMENT("assets/shaders/fragment"),
		COMPUTE("assets/shaders/compute")
		;

		private final String path;

		/**
		 * @param path The resource path
		 */
		ShaderType(final String path) {
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
}
