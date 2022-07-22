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
		this.entity.setPosition(new Vector3f(position));
		this.entity.setRotationX(rotationX);
		this.entity.setRotationY(rotationY);
		this.entity.setRotationZ(rotationZ);
		this.entity.setScale(scale);
	}

	public ClientEntity(T entity, TexturedModel texturedModel, int textureAtlasIndex, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		this.entity = entity;
		this.texturedModel = texturedModel;
		this.entity.setPosition(new Vector3f(position));
		this.entity.setRotationX(rotationX);
		this.entity.setRotationY(rotationY);
		this.entity.setRotationZ(rotationZ);
		this.entity.setScale(scale);
		this.textureAtlasIndex = textureAtlasIndex;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.entity.getPosition().x += dx;
		this.entity.getPosition().y += dy;
		this.entity.getPosition().z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.entity.setRotationX(this.entity.getRotationX() + dx);
		this.entity.setRotationY(this.entity.getRotationY() + dy);
		this.entity.setRotationZ(this.entity.getRotationZ() + dz);
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
		return entity.getPosition();
	}

	public void setPosition(Vector3f position) {
		this.entity.setPosition(position);
	}

	public float getRotationX() {
		return entity.getRotationX();
	}

	public void setRotationX(float rotationX) {
		this.entity.setRotationX(rotationX);
	}

	public float getRotationY() {
		return entity.getRotationY();
	}

	public void setRotationY(float rotationY) {
		this.entity.setRotationY(rotationY);
	}

	public float getRotationZ() {
		return entity.getRotationZ();
	}

	public void setRotationZ(float rotationZ) {
		this.entity.setRotationZ(rotationZ);
	}

	public float getScale() {
		return entity.getScale();
	}

	public void setScale(float scale) {
		this.entity.setScale(scale);
	}

	public T getEntity() {
		return entity;
	}

	public void initDebug(){}

	public final void renderDebug(){

	}
}
