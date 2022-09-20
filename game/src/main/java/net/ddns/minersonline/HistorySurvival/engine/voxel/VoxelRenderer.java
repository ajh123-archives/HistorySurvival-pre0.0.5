package net.ddns.minersonline.HistorySurvival.engine.voxel;

import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelChunkMesh;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class VoxelRenderer {
	private final VoxelShader shader;

	public VoxelRenderer(VoxelShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.unbind();
	}

	public void render(Map<TexturedModel, Collection<VoxelChunkMesh>> voxels, Camera camera, float deltaTime) {
		for (TexturedModel model : voxels.keySet()) {
			prepareTexturedModel(model);
			for (VoxelChunkMesh tile : voxels.get(model)) {
				shader.loadViewMatrix(camera);
				Matrix4f transformationMatrix = Maths.createTransformationMatrix(tile.chunk.getOrigin(), 0, 0, 0, 1);
				shader.loadTransformationMatrix(transformationMatrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getRawModel().getVertexCount());
			}
			unbind();
		}
	}

	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		glBindVertexArray(0);
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
