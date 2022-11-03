package net.ddns.minersonline.HistorySurvival.api.data.models;

import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;

public class ModelTexture {
	private int atlasOffset = 0;
	private int textureId;
	private float shineDamper;
	private float reflectivity;
	private boolean hasTransparency;
	private int numberOfRowsInTextureAtlas;
	private final TextureResource resource;

	// some models, like the grass model, needs fake lighting to look better as the model is a quad with normals facing in many different directions
	private boolean useFakeLighting;

	public ModelTexture(int textureId, TextureResource resource) {
		this.textureId = textureId;
		this.resource = resource;
		shineDamper = 1;
		numberOfRowsInTextureAtlas = 1;
	}

	public int getTextureId() {
		return textureId;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public int getNumberOfRowsInTextureAtlas() {
		return numberOfRowsInTextureAtlas;
	}

	public void setNumberOfRowsInTextureAtlas(int numberOfRowsInTextureAtlas) {
		this.numberOfRowsInTextureAtlas = numberOfRowsInTextureAtlas;
	}

	public int getAtlasOffset() {
		return atlasOffset;
	}

	public void setAtlasOffset(int atlasOffset) {
		this.atlasOffset = atlasOffset;
	}

	public TextureResource getResource() {
		return resource;
	}
}
