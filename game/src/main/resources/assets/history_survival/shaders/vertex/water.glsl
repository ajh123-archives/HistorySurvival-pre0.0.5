#version 400 core

in vec2 position;

out vec4 clipSpace;
out vec2 textCoOrds;
out vec3 toCamVec;
out vec3 fromSunVec;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 camPos;
uniform vec3 lightPos;

const float tiling = 4.0;

void main(void) {
	vec4 worldPos = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);

	clipSpace = projectionMatrix * viewMatrix * worldPos;
	gl_Position = clipSpace;
	textCoOrds = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling;

	toCamVec = camPos - worldPos.xyz;
	fromSunVec = worldPos.xyz - lightPos;
}