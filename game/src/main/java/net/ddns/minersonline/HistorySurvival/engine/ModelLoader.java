package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.text.MeshData;
import net.ddns.minersonline.HistorySurvival.engine.utils.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class ModelLoader {
	private final List<Integer> vaos = new ArrayList<>();
	private final List<MeshData> vbos = new LinkedList<>();
	private final List<Integer> textureList = new ArrayList<>();;

	public ModelLoader() {

	}

	private int createVao() {
		int vaoId = glGenVertexArrays();            // initialize an empty VAO
		glBindVertexArray(vaoId);                   // select this vao
		vaos.add(vaoId);
		return vaoId;
	}

	private int storeDataInAttributeList(int attributeNumber, int vertexLength, float[] data) {
		int vboId = glGenBuffers();                                 // initialize an empty VBO
		glBindBuffer(GL_ARRAY_BUFFER, vboId);                       // select this VBO into the VAO Id specified
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data);   // make VBO from data
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);      // store data into VBO & Not going to edit this data
		glVertexAttribPointer(attributeNumber, vertexLength, GL_FLOAT, false, 0, 0);    // place VBO into VAO
		glBindBuffer(GL_ARRAY_BUFFER, 0);                   // unbind the VBO
		vbos.add(new MeshData(attributeNumber, vboId, -Integer.MAX_VALUE));
		return vboId;
	}

	private void unbindVao() {
		glBindVertexArray(0);
	}

	private void bindIndicesBuffer(int vao, int[] indices) {
		int vboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = BufferUtils.createIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		vbos.add(new MeshData(vao, vboId, -Integer.MAX_VALUE));
	}

	public MeshData loadToVao(float[] positions, float[] textureCoOrds) {
		int vaoId = createVao();
		int vbo1 = storeDataInAttributeList(0, 2, positions);     // using VAO attribute 0. Could be any 0 thru 15
		int vbo2 = storeDataInAttributeList(1, 2, textureCoOrds);
		unbindVao();
		return new MeshData(vaoId, vbo1, vbo2);
	}

	public RawModel loadToVao(float[] positions, float[] textureCoOrds, float[] normals, int[] indices) {
		int vaoId = createVao();
		bindIndicesBuffer(vaoId, indices);
		storeDataInAttributeList(0, 3, positions);     // using VAO attribute 0. Could be any 0 thru 15
		storeDataInAttributeList(1, 2, textureCoOrds);
		storeDataInAttributeList(2, 3, normals);
		unbindVao();

		return new RawModel(vaoId, indices.length);
	}
	public RawModel loadToVaoRaw(float[] positions, float[] textureCoOrds) {
		int vaoId = createVao();
		storeDataInAttributeList(0, 3, positions);     // using VAO attribute 0. Could be any 0 thru 15
		storeDataInAttributeList(1, 2, textureCoOrds);
		unbindVao();

		return new RawModel(vaoId, positions.length);
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

	private void destroy(int vao) {
		for (Iterator<Integer> it = vaos.iterator(); it.hasNext(); ) {
			int vao_loop = it.next();
			if (vao_loop == vao) {
				GL30.glDeleteVertexArrays(vao);
				destroyVBO(vao);
				it.remove();
				break;
			}
		}
		destroyVBOS();
	}

	public void destroy(MeshData data){
		destroy(data.getVao());
	}

	private void destroyVBOS() {
		for (Iterator<MeshData> it = vbos.iterator(); it.hasNext(); ) {
			MeshData mesh = it.next();
			GL15.glDeleteBuffers(mesh.getVbo1());
			GL15.glDeleteBuffers(mesh.getVbo2());
			it.remove();
		}

	}

	private void destroyVBO(int vao) {
		for (Iterator<MeshData> it = vbos.iterator(); it.hasNext(); ) {
			MeshData mesh = it.next();
			if(mesh.getVao() == vao) {
				GL15.glDeleteBuffers(mesh.getVbo1());
				GL15.glDeleteBuffers(mesh.getVbo2());
				it.remove();
			}
		}
	}

	public void destroy() {
		for (Iterator<Integer> it = vaos.iterator(); it.hasNext(); ) {
			int vao = it.next();
			GL30.glDeleteVertexArrays(vao);
			it.remove();
		}
		destroyVBOS();

		for (int texture : textureList) {
			GL11.glDeleteTextures(texture);
		}
		vaos.clear();
		vbos.clear();
		textureList.clear();
	}

	public MeshData createEmptyVbo(int floatCount) {
		int vbo = GL15.glGenBuffers();
		MeshData data = new MeshData(-Integer.MAX_VALUE, vbo, -Integer.MAX_VALUE);
		vbos.add(data);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4L, GL15.GL_STREAM_DRAW);
		// unbind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return data;
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
