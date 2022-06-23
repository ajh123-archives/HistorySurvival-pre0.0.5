package net.ddns.minersonline.HistorySurvival.api.entities;

import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import org.joml.Vector3f;

public class ClientEntity<T extends Entity> {
	private TexturedModel texturedModel;
	private int textureAtlasIndex;
	private final T entity;

	public ClientEntity(T entity, TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		this.texturedModel = texturedModel;
		this.entity = entity;
		this.entity.position = position;
		this.entity.rotationX = rotationX;
		this.entity.rotationY = rotationY;
		this.entity.rotationZ = rotationZ;
		this.entity.scale = scale;
	}

	public ClientEntity(T entity, TexturedModel texturedModel, int textureAtlasIndex, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		this.entity = entity;
		this.texturedModel = texturedModel;
		this.entity.position = position;
		this.entity.rotationX = rotationX;
		this.entity.rotationY = rotationY;
		this.entity.rotationZ = rotationZ;
		this.entity.scale = scale;
		this.textureAtlasIndex = textureAtlasIndex;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.entity.position.x += dx;
		this.entity.position.y += dy;
		this.entity.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.entity.rotationX += dx;
		this.entity.rotationY += dy;
		this.entity.rotationZ += dz;
	}

	public float getTextureAtlasXOffset() {
		int column = textureAtlasIndex % texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) column / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}

	public float getTextureAtlasYOffset() {
		int row = textureAtlasIndex / texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) row / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public void setTexturedModel(TexturedModel texturedModel) {
		this.texturedModel = texturedModel;
	}

	public Vector3f getPosition() {
		return entity.position;
	}

	public void setPosition(Vector3f position) {
		this.entity.position = position;
	}

	public float getRotationX() {
		return entity.rotationX;
	}

	public void setRotationX(float rotationX) {
		this.entity.rotationX = rotationX;
	}

	public float getRotationY() {
		return entity.rotationY;
	}

	public void setRotationY(float rotationY) {
		this.entity.rotationY = rotationY;
	}

	public float getRotationZ() {
		return entity.rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.entity.rotationZ = rotationZ;
	}

	public float getScale() {
		return entity.scale;
	}

	public void setScale(float scale) {
		this.entity.scale = scale;
	}

	public T getEntity() {
		return entity;
	}
}
