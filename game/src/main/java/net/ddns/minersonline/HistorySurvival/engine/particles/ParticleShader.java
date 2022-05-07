package net.ddns.minersonline.HistorySurvival.engine.particles;

import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class ParticleShader extends ShaderProgramBase {

	private static final String VERTEX_FILE = "shaders/particleVShader.glsl";
	private static final String FRAGMENT_FILE = "shaders/particleFShader.glsl";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int location_texOffset1;
	private int location_texOffset2;
	private int location_texCoordInfo;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_texOffset1 = super.getUniformLocation("texOffset1");
		location_texOffset2 = super.getUniformLocation("texOffset2");
		location_texCoordInfo = super.getUniformLocation("texCoOrdInfo");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	protected void loadTextureCoordInfo(Vector2f offset1, Vector2f offset2, float numRows, float blend) {
		super.loadVector(location_texOffset1, offset1);
		super.loadVector(location_texOffset2, offset2);
		super.loadVector(location_texCoordInfo, new Vector2f(numRows, blend));
	}


	protected void loadModelViewMatrix(Matrix4f modelViewMatrix) {
		super.loadMatrix(location_modelViewMatrix, modelViewMatrix);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
