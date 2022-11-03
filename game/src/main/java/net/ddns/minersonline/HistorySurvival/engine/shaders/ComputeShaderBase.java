package net.ddns.minersonline.HistorySurvival.engine.shaders;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.util.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public abstract class ComputeShaderBase {
	private String computeFile;
	private int programId, computeShaderId;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);    // 16 is a 4 x 4 matrix

	public ComputeShaderBase(ResourceLocation compute) {
		computeFile = ResourceType.COMPUTE_SHADER.load(compute);

		create();
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programId, attribute, variableName);
	}

	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programId, uniformName);
	}

	protected abstract void getAllUniformLocations();

	protected void loadFloat(int location, float value) {
		glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector3f vector) {
		glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector(int location, Vector4f vector) {
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadVector(int location, Vector2f vector) {
		glUniform2f(location, vector.x, vector.y );
	}

	protected void loadBoolean(int location, boolean value) {
		glUniform1f(location, value ? 1 : 0);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		glUniformMatrix4fv(location, false, matrix.get(matrixBuffer));
	}

	private void create() {
		programId = glCreateProgram();
		computeShaderId = glCreateShader(GL43.GL_COMPUTE_SHADER);

		glShaderSource(computeShaderId, computeFile);
		glCompileShader(computeShaderId);

		// Error check shader program code after trying to compile it
		if (glGetShaderi(computeShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			// The shader program didn't compile
			throw new RuntimeException("Compute Shader: " + glGetShaderInfoLog(computeShaderId));
		}

		// The shader code did successfully compile
		glAttachShader(programId, computeShaderId);

		bindAttributes();

		// Linking
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Program Linking: " + glGetProgramInfoLog(programId));
		}

		// Validating
		glValidateProgram(programId);

		// TODO: glValidateProgram reports GL_FALSE but runs fine.. Why?
        /*
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program Validation: " + glGetProgramInfoLog(programId));
        }
        */

		getAllUniformLocations();
	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void destroy() {
		glDetachShader(programId, computeShaderId);
		glDeleteShader(computeShaderId);
		glDeleteProgram(programId);
	}
}
