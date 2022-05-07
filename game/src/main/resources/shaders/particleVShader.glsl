#version 140

in vec2 position;

out vec2 textureCoOrds1;
out vec2 textureCoOrds2;
out float blend;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec2 texCoOrdInfo;

void main(void)
{
	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	textureCoords /= texCoOrdInfo.x;
	textureCoOrds1 = textureCoords + texOffset1;
	textureCoOrds2 = textureCoords + texOffset2;
	blend = texCoOrdInfo.y;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
}