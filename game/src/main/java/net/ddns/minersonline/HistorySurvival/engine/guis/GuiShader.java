package net.ddns.minersonline.HistorySurvival.engine.guis;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import org.joml.Matrix4f;

public class GuiShader extends ShaderProgramBase {
	
	private static final ResourceLocation VERTEX_FILE = new ResourceLocation("gui");
	private static final ResourceLocation FRAGMENT_FILE = new ResourceLocation("gui");
	
	private int location_transformationMatrix;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}
