package net.ddns.minersonline.HistorySurvival.engine.voxel;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class VoxelRenderer {
	private VoxelShader shader;
	public static Map<TexturedModel, List<Voxel>> voxels = new ConcurrentHashMap<>();

	public VoxelRenderer(VoxelShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.unbind();
	}

	public void render(Map<Vector3f, Voxel> voxels, Camera camera, float deltaTime) {
		for (Voxel tile : voxels.values()) {
			prepareTexturedModel(tile.getModel());
			shader.loadViewMatrix(camera);
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(tile.getPosition(), 0, 0, 0, 1);
			shader.loadTransformationMatrix(transformationMatrix);
			GL11.glDrawElements(GL11.GL_TRIANGLES, tile.getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbind();
		}
	}

	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	private void prepareTexturedModel(TexturedModel texturedModel) {
		RawModel rawModel = texturedModel.getRawModel();

		glBindVertexArray(rawModel.getVaoId());
		glEnableVertexAttribArray(0);   // VAO 0 = vertex spacial coordinates
		glEnableVertexAttribArray(1);   // VAO 1 = texture coordinates
		//TODO: glEnableVertexAttribArray(2);   // VAO 2 = normals

		ModelTexture texture = texturedModel.getModelTexture();

		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}

		glActiveTexture(GL_TEXTURE0);
		int textureId = texturedModel.getModelTexture().getTextureId();
		glBindTexture(GL_TEXTURE_2D, textureId);    // sampler2D in fragment shader  uses texture bank 0 by default
	}
}
