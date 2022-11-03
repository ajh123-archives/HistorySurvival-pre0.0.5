package net.ddns.minersonline.HistorySurvival.engine.raytracing;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.engine.shaders.ComputeShaderBase;

public class RayTracerShader extends ComputeShaderBase {
	private static final ResourceLocation COMPUTE_FILE = new ResourceLocation("ray_tracing");

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
