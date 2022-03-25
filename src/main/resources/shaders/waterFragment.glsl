#version 400 core

in vec4 clipSpace;
in vec2 textCoOrds;
in vec3 toCamVec;
in vec3 fromSunVec;

out vec4 out_colour;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform float rippleFactor;
uniform vec3 lightColour;

const float waveStrength = 0.02;
const float shineDamper = 20;
const float reflectivity = 0.6;

void main(void) {
	vec2 normal_space = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 refractTextCoOrds = vec2(normal_space.x, normal_space.y);
	vec2 reflectTextCoOrds = vec2(normal_space.x, -normal_space.y);

	vec2 distortedTexCoOrds = texture(dudvMap, vec2(textCoOrds.x + rippleFactor, textCoOrds.y)).rg*0.1;
	distortedTexCoOrds = textCoOrds + vec2(distortedTexCoOrds.x, distortedTexCoOrds.y+rippleFactor);
	vec2 distortion = (texture(dudvMap, distortedTexCoOrds).rg * 2.0 - 1.0) * waveStrength;

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

	vec4 normalColour = texture(normalMap, distortedTexCoOrds);
	vec3 normal = vec3(normalColour.r * 2 - 1, normalColour.b, normalColour.g * 2 - 1);
	normal = normalize(normal);

	vec3 reflectedLight = reflect(normalize(fromSunVec), normal);
	float specular = max(dot(reflectedLight, viewVec), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlights = lightColour * specular * reflectivity;

	out_colour = mix(reflectColour, refractColour, refractiveness);
	out_colour = mix(out_colour, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0);
}