package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;

public class ResourceType {
	public static final TextureResource TEXTURE = new TextureResource(TextureResource.TextureType.GENERIC);
	public static final TextureResource VOXEL_TEXTURE = new TextureResource(TextureResource.TextureType.VOXEL);
	public static final ResourceType MODEL = new ResourceType("assets/models");
	public static final ResourceType FRAGMENT_SHADER = new ResourceType("assets/shaders/fragment");
	public static final ResourceType VERTEX_SHADER = new ResourceType("assets/shaders/vertex");
	public static final ResourceType COMPUTE_SHADER = new ResourceType("assets/shaders/compute");

	private final String root;

	public ResourceType(String root) {
		this.root = root;
	}

	public String getRoot() {
		return root;
	}

	public static void init(){}


}
