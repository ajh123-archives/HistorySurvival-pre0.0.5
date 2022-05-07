package net.ddns.minersonline.HistorySurvival.engine.particles;

import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class ParticleShader extends ShaderProgramBase {

	private static final String VERTEX_FILE = "shaders/particleVShader.glsl";
	private static final String FRAGMENT_FILE = "shaders/particleFShader.glsl";

	private int location_numberOfRows;
	private int location_projectionMatrix;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	protected void loadNumberOfRows(float numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}
}
