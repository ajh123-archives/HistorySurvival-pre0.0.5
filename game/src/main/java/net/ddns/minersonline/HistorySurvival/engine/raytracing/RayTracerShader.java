package net.ddns.minersonline.HistorySurvival.engine.raytracing;

import net.ddns.minersonline.HistorySurvival.engine.shaders.ComputeShaderBase;

public class RayTracerShader extends ComputeShaderBase {
	private static final String COMPUTE_FILE = "shaders/rayTracerShader.glsl";

	public RayTracerShader() {
		super(COMPUTE_FILE);
	}

	@Override
	protected void bindAttributes() {

	}

	@Override
	protected void getAllUniformLocations() {

	}
}
