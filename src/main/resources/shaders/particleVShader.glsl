#version 140

in vec2 position;

out vec2 textureCoOrds;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main(void){
	textureCoOrds = position + vec2(0.5, 0.5);
	textureCoOrds.y = 1.0 - textureCoOrds.y;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}