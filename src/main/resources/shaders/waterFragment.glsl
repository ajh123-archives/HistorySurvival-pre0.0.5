#version 400 core

in vec4 clipSpace;
in vec2 textCoOrds;
in vec3 toCamVec;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform float rippleFactor;

const float waveStrength = 0.02;

void main(void) {
	vec2 normal_space = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 refractTextCoOrds = vec2(normal_space.x, normal_space.y);
	vec2 reflectTextCoOrds = vec2(normal_space.x, -normal_space.y);

	vec2 distortion1 = (texture(dudvMap, vec2(textCoOrds.x + rippleFactor, textCoOrds.y)).rg * 2.0 -1) * waveStrength;
	vec2 distortion2 = (texture(dudvMap, vec2(-textCoOrds.x + rippleFactor, textCoOrds.y + rippleFactor)).rg * 2.0 -1) * waveStrength;
	vec2 distortion = distortion1 + distortion2;

	refractTextCoOrds += distortion;
	refractTextCoOrds = clamp(refractTextCoOrds, 0.001, 0.999);

	reflectTextCoOrds += distortion;
	reflectTextCoOrds.x = clamp(reflectTextCoOrds.x, 0.001, 0.999);
	reflectTextCoOrds.y = clamp(reflectTextCoOrds.y, -0.999, -0.001);

	vec4 reflectColour = texture(reflectionTexture, reflectTextCoOrds);
	vec4 refractColour = texture(refractionTexture, refractTextCoOrds);

	vec3 viewVec = normalize(toCamVec);
	float refractiveness = dot(viewVec, vec3(0, 1, 0));
	refractiveness = pow(refractiveness, 0.5);

	out_Color = mix(reflectColour, refractColour, refractiveness);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2);
}