#version 450 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

void main(void) {
    vec3 unitNormal = normalize(surfaceNormal); // normalize makes the size of the vector = 1. Only direction of the vector matters here. Size is irrelevant
    vec3 unitLightVector = normalize(toLightVector);

    float nDotl = dot(unitNormal, unitLightVector); // dot product calculation of 2 vectors. nDotl is how bright this pixel should be. difference of the position and normal vector to the light source
    float brightness = max(nDotl, 0.2); // clamp the brightness result value to between 0 and 1. values less than 0 are clamped to 0.2. to leave a little more diffuse light
    vec3 diffuse = brightness * lightColor; // calculate final color of this pixel by how much light it has

    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 lightDirection = -unitLightVector; // light direction vector is the opposite of the toLightVector
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal); // specular reflected light vector

    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);    // determines how bright the specular light should be relative to the "camera" by taking the dot product of the two vectors
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);  // raise specularFactor to the power of the shineDamper value. makes the low specular values even lower but doesnt effect the high specular values too much
    vec3 finalSpecular = dampedFactor * reflectivity * lightColor;

    vec4 textureColor = texture(modelTexture, pass_textureCoords);

    if (textureColor.a <0.5) {
        discard;
    }

    out_Color = vec4(diffuse, 1.0) *  textureColor + vec4(finalSpecular, 1.0);        // returns color of the pixel from the texture at specified texture coordinates
}
