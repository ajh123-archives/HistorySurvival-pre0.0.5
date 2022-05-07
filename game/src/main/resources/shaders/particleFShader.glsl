#version 140

out vec4 out_color;

in vec2 textureCoOrds1;
in vec2 textureCoOrds2;
in float blend;

uniform sampler2D particleTexture;

void main(void)
{
	vec4 color1 = texture(particleTexture, textureCoOrds1);
	vec4 color2 = texture(particleTexture, textureCoOrds2);
	out_color = mix(color1, color2, blend);
}