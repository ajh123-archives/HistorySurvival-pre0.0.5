package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.utils.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class ModelLoader {
	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();
	private List<Integer> textureList = new ArrayList<>();;

	public ModelLoader() {

	}

	private int createVao() {
		int vaoId = glGenVertexArrays();            // initialize an empty VAO
		glBindVertexArray(vaoId);                   // select this vao
		vaos.add(vaoId);
		return vaoId;
	}

	private void storeDataInAttributeList(int attributeNumber, int vertexLength, float[] data) {
		int vboId = glGenBuffers();                                 // initialize an empty VBO
		glBindBuffer(GL_ARRAY_BUFFER, vboId);                       // select this VBO into the VAO Id specified
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data);   // make VBO from data
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);      // store data into VBO & Not going to edit this data
		glVertexAttribPointer(attributeNumber, vertexLength, GL_FLOAT, false, 0, 0);    // place VBO into VAO
		glBindBuffer(GL_ARRAY_BUFFER, 0);                   // unbind the VBO
		vbos.add(vboId);
	}

	private void unbindVao() {
		glBindVertexArray(0);
	}

	private void bindIndicesBuffer(int[] indices) {
		int vboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = BufferUtils.createIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		vbos.add(vboId);
	}

	public int loadToVao(float[] positions, float[] textureCoords) {
		int vaoId = createVao();
		storeDataInAttributeList(0, 2, positions);     // using VAO attribute 0. Could be any 0 thru 15
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVao();

		return vaoId;
	}

	public RawModel loadToVao(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoId = createVao();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);     // using VAO attribute 0. Could be any 0 thru 15
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVao();

		return new RawModel(vaoId, indices.length);
	}

	public RawModel loadToVao(float[] positions, int length) {
		int vaoId = createVao();
		storeDataInAttributeList(0, length, positions);
		unbindVao();

		return new RawModel(vaoId, positions.length/length);
	}

	public RawModel loadToVao(float[] positions) {
		int vaoId = createVao();
		storeDataInAttributeList(0, 2, positions);
		unbindVao();

		return new RawModel(vaoId, positions.length/2);
	}

	public int loadTexture(String filename) {
		TextureLoader textureLoader = new TextureLoader(filename);
		int textureId = textureLoader.getTextureId();
		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);
		textureList.add(textureId);
		return textureId;
	}

	public void destroy(int vao) {
		//vao_vbos.get(vao).forEach(GL15::glDeleteBuffers);
		glDeleteVertexArrays(vao);
		//vaos.remove(vao-2);
		vaos.remove(vaos.indexOf(vao)-1);
	}

	public void destroy() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textureList) {
			GL11.glDeleteTextures(texture);
		}
		vaos.clear();
		vbos.clear();
		textureList.clear();
	}

	public int createEmptyVbo(int floatCount) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4L, GL15.GL_STREAM_DRAW);
		// unbind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize,
									  int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false,
				instancedDataLength * 4, offset * 4L);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// unbind vao
		GL30.glBindVertexArray(0);
	}

	public void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4L, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
}
