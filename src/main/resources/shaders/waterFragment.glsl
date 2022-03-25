#version 400 core

in vec4 clipSpace;
in vec2 text_co_ords;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;

uniform float ripple_factor;

const float waveStrength = 0.02;

void main(void) {
	vec2 normal_space = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 reflect = vec2(normal_space.x, -normal_space.y);
	vec2 refract = vec2(normal_space.x, normal_space.y);

	vec2 distortion1 = (texture(dudvMap, vec2(text_co_ords.x + ripple_factor, text_co_ords.y)).rg * 2.0 -1) * waveStrength;
	vec2 distortion2 = (texture(dudvMap, vec2(-text_co_ords.x + ripple_factor, text_co_ords.y + ripple_factor)).rg * 2.0 -1) * waveStrength;
	vec2 distortion = distortion1 + distortion2;

	refract += distortion;
	refract += clamp(refract, -0.001, -0.999);

	reflect += distortion;
	reflect.x = clamp(reflect.x, 0.001, 0.999);
	reflect.y = clamp(reflect.y, -0.999, -0.001);

	vec4 reflectColour = texture(reflectionTexture, reflect);
	vec4 refractColour = texture(refractionTexture, refract);

	out_Color = mix(reflectColour, refractColour, 0.5);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2);
}