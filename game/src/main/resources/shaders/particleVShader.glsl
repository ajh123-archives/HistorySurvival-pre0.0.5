#version 140

in vec2 position;
in mat4 modelViewMatrix;
in vec4 texOffsets;
in float blendFactor;

out vec2 textureCoOrds1;
out vec2 textureCoOrds2;
out float blend;

uniform mat4 projectionMatrix;
uniform float numberOfRows;

void main(void)
{
	vec2 textureCoOrds = position + vec2(0.5, 0.5);
	textureCoOrds.y = 1.0 - textureCoOrds.y;
	textureCoOrds /= numberOfRows;
	textureCoOrds1 = textureCoOrds + texOffsets.xy;
	textureCoOrds2 = textureCoOrds + texOffsets.zw;
	blend = blendFactor;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
}