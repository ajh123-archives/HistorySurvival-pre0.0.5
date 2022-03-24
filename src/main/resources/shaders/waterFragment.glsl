#version 400 core

in vec4 clipSpace;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

void main(void) {
	vec2 normal_space = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 reflect = vec2(normal_space.x, -normal_space.y);
	vec2 refract = vec2(normal_space.x, normal_space.y);

	vec4 reflectColour = texture(reflectionTexture, reflect);
	vec4 refractColour = texture(refractionTexture, refract);

	out_Color = mix(reflectColour, refractColour, 0.5);
}