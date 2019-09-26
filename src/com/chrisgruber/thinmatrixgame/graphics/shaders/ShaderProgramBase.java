package com.chrisgruber.thinmatrixgame.graphics.shaders;

import com.chrisgruber.thinmatrixgame.graphics.utils.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgramBase {
    private String vertexFile, fragmentFile;
    private int programId, vertexShaderId, fragmentShaderId;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgramBase(String vertexPath, String fragmentPath) {
        vertexFile = FileUtils.loadAsString(vertexPath);
        fragmentFile = FileUtils.loadAsString(fragmentPath);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programId, attribute, variableName);
    }

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programId, uniformName);
    }

    protected abstract void getAllUniformLocations();

    protected  void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        glUniform1f(location, value ? 1 : 0);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.get(matrixBuffer);
        matrixBuffer.flip();
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    public void create() {
        programId = glCreateProgram();
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexShaderId, vertexFile);
        glCompileShader(vertexShaderId);

        // Error check shader program code after trying to compile it
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            // The shader program didn't compile
            throw new RuntimeException("Vertex Shader: " + glGetShaderInfoLog(vertexShaderId));
        }

        // The shader code did successfully compile
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentShaderId, fragmentFile);
        glCompileShader(fragmentShaderId);

        // Error check fragment shader program code after attempt to compile it
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            // The fragment shader program didn't compile
            throw new RuntimeException("Fragment Shader: " + glGetShaderInfoLog(fragmentShaderId));
        }

        // The fragment shader code did successfully compile
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        bindAttributes();

        // Linking
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program Linking: " + glGetProgramInfoLog(programId));
        }

        // Validating
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program Validation: " + glGetProgramInfoLog(programId));
        }

        getAllUniformLocations();
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        glDeleteProgram(programId);
    }
}
