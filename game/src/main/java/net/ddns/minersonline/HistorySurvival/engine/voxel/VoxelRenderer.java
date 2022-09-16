package net.ddns.minersonline.HistorySurvival.engine.voxel;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;


public class VoxelRenderer {
	private VoxelShader shader;
	private RawModel quad;

	public VoxelRenderer(ModelLoader loader, VoxelShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.unbind();
		setUpVAO(loader);
	}

	public void render(List<Voxel> voxels, Camera camera,float deltaTime) {
		prepareRender(camera, deltaTime);
		for (Voxel tile : voxels) {
			System.out.println(tile.getPosition());
			shader.loadViewMatrix(camera);
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(tile.getPosition(), 0, 0, 0, 1);
			shader.loadTransformationMatrix(transformationMatrix);
			GL11.glDrawElements(GL11.GL_TRIANGLES, quad.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		unbind();
	}
	
	private void prepareRender(Camera camera, float deltaTime){
		shader.bind();
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(quad.getVaoId());
		GL20.glEnableVertexAttribArray(0);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.unbind();
	}

	private void setUpVAO(ModelLoader loader) {
		float[] vertices = {
				-0.5f, 0.5f, 0,
				-0.5f, -0.5f, 0,
				0.5f, -0.5f, 0,
				0.5f, 0.5f, 0,
		};
		int[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		quad = loader.loadToVao(vertices, indices);
	}

}
