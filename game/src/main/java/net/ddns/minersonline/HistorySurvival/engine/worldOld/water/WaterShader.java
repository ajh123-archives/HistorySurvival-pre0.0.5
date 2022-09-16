package net.ddns.minersonline.HistorySurvival.engine.worldOld.water;


import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.joml.Matrix4f;

public class WaterShader extends ShaderProgramBase {

	private final static String VERTEX_FILE = "shaders/waterVertex.glsl";
	private final static String FRAGMENT_FILE = "shaders/waterFragment.glsl";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflection;
	private int location_refraction;
	private int location_dudv;
	private int location_normal;
	private int location_depth;
	private int location_rippleFactor;
	private int location_camPos;
	private int location_lightColour;
	private int location_lightPos;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflection = getUniformLocation("reflectionTexture");
		location_refraction = getUniformLocation("refractionTexture");
		location_dudv = getUniformLocation("dudvMap");
		location_normal = getUniformLocation("normalMap");
		location_depth = getUniformLocation("depthMap");
		location_rippleFactor = getUniformLocation("rippleFactor");
		location_camPos = getUniformLocation("camPos");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPos = getUniformLocation("lightPos");
	}

	public void connectTextureUnits(){
		super.loadInt(location_reflection, 0);
		super.loadInt(location_refraction, 1);
		super.loadInt(location_dudv, 2);
		super.loadInt(location_normal, 3);
		super.loadInt(location_depth, 4);
	}

	public void loadLight(Light light){
		super.loadVector(location_lightColour, light.getColor());
		super.loadVector(location_lightPos, light.getPosition());
	}

	public void loadRippleFactor(float rippleFactor){
		super.loadFloat(location_rippleFactor, rippleFactor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_camPos, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}
}
