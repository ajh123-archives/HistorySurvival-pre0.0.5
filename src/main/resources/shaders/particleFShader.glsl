#version 140

in vec2 textureCoOrds;

out vec4 out_colour;

uniform sampler2D particleTexture;

void main(void){

	out_colour = texture(particleTexture, textureCoOrds);

}