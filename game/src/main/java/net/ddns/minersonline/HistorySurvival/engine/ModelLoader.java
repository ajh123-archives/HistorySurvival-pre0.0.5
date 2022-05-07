package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.engine.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.utils.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class ModelLoader {
    private ConcurrentHashMap<Integer, List<Integer>> vao_vbos;
    private List<Integer> instance_vbos = new ArrayList<>();
    private List<Integer> textureList;

    public ModelLoader() {
        textureList = new ArrayList<>();
        vao_vbos = new ConcurrentHashMap<>();
    }

    private int createVao() {
        int vaoId = glGenVertexArrays();            // initialize an empty VAO
        glBindVertexArray(vaoId);                   // select this vao
        return vaoId;
    }

    private int storeDataInAttributeList(int attributeNumber, int vertexLength, float[] data) {
        int vboId = glGenBuffers();                                 // initialize an empty VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboId);                       // select this VBO into the VAO Id specified
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data);   // make VBO from data
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);      // store data into VBO & Not going to edit this data
        glVertexAttribPointer(attributeNumber, vertexLength, GL_FLOAT, false, 0, 0);    // place VBO into VAO
        glBindBuffer(GL_ARRAY_BUFFER, 0);                   // unbind the VBO
        return vboId;
    }

    private void unbindVao() {
        glBindVertexArray(0);
    }

    private int bindIndicesBuffer(int[] indices) {
        int vboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = BufferUtils.createIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        return vboId;
    }

    public int loadToVao(float[] positions, float[] textureCoords) {
        int vaoId = createVao();
        int vbo1 = storeDataInAttributeList(0, 2, positions);     // using VAO attribute 0. Could be any 0 thru 15
        int vbo2 = storeDataInAttributeList(1, 2, textureCoords);
        unbindVao();

        List<Integer> VBOs = new ArrayList<>();
        VBOs.add(vbo1);
        VBOs.add(vbo2);
        vao_vbos.put(vaoId, VBOs);
        return vaoId;
    }

    public RawModel loadToVao(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoId = createVao();
        int vbo1 = bindIndicesBuffer(indices);
        int vbo2 = storeDataInAttributeList(0, 3, positions);     // using VAO attribute 0. Could be any 0 thru 15
        int vbo3 = storeDataInAttributeList(1, 2, textureCoords);
        int vbo4 = storeDataInAttributeList(2, 3, normals);
        unbindVao();

        List<Integer> VBOs = new ArrayList<>();
        VBOs.add(vbo1);
        VBOs.add(vbo2);
        VBOs.add(vbo3);
        VBOs.add(vbo4);
        vao_vbos.put(vaoId, VBOs);
        return new RawModel(vaoId, indices.length);
    }

    public RawModel loadToVao(float[] positions, int length) {
        int vaoId = createVao();
        int vbo1 = storeDataInAttributeList(0, length, positions);
        unbindVao();

        List<Integer> VBOs = new ArrayList<>();
        VBOs.add(vbo1);
        vao_vbos.put(vaoId, VBOs);
        return new RawModel(vaoId, positions.length/length);
    }

    public RawModel loadToVao(float[] positions) {
        int vaoId = createVao();
        int vbo1 = storeDataInAttributeList(0, 2, positions);
        unbindVao();

        List<Integer> VBOs = new ArrayList<>();
        VBOs.add(vbo1);
        vao_vbos.put(vaoId, VBOs);
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
        for (int vboId: vao_vbos.get(vao)) {
            glDeleteBuffers(vboId);
        }
        glDeleteVertexArrays(vao);
        vao_vbos.remove(vao);
    }

    public void destroy() {
        for (int vao : vao_vbos.keySet()) {
            destroy(vao);
        }

        for (int textureId : textureList) {
            glDeleteTextures(textureId);
        }

        for (int vboIdx = 0; vboIdx<instance_vbos.size(); vboIdx++ ) {
            int vboId = instance_vbos.get(vboIdx);
            glDeleteBuffers(vboId);
            instance_vbos.remove(vboIdx);
            vboIdx++;
        }
    }

    public int createEmptyVbo(int floatCount) {
        int vbo = GL15.glGenBuffers();
        instance_vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
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
