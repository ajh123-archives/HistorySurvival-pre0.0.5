#version 410 core

in vec3 position;
in vec2 textureCoOrds;

out vec2 pass_textureCoOrds;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void) {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCamera;

    pass_textureCoOrds = textureCoOrds;
}
