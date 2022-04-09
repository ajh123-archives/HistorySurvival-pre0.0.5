package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.engine.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class ModelLoader {
    private ConcurrentHashMap<Integer, List<Integer>> vao_vbos;
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
            for (int vboId: vao_vbos.get(vao)) {
                glDeleteBuffers(vboId);
            }
            glDeleteVertexArrays(vao);
            vao_vbos.remove(vao);
        }

        for (int textureId : textureList) {
            glDeleteTextures(textureId);
        }
    }
}
