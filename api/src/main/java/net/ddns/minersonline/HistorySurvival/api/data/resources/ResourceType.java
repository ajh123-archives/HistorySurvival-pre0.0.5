package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.resources.types.ShaderResource;
import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;

public class ResourceType {
	public static final TextureResource TEXTURE = new TextureResource(TextureResource.TextureType.GENERIC);
	public static final TextureResource VOXEL_TEXTURE = new TextureResource(TextureResource.TextureType.VOXEL);
	public static final ResourceType MODEL = new ResourceType("assets/models");
	public static final ShaderResource VERTEX_SHADER = new ShaderResource(ShaderResource.ShaderType.VERTEX);
	public static final ShaderResource FRAGMENT_SHADER = new ShaderResource(ShaderResource.ShaderType.FRAGMENT);
	public static final ShaderResource COMPUTE_SHADER = new ShaderResource(ShaderResource.ShaderType.COMPUTE);

	private final String root;

	public ResourceType(String root) {
		this.root = root;
	}

	public String getRoot() {
		return root;
	}

	public static void init(){}


}
