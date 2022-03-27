#version 410 core

in vec2 pass_textCoOrds;

out vec4 out_colour;

uniform vec3 colour;
uniform sampler2D fontAtlas;

uniform float width;        // 0.5
uniform float edge;         // 0.1
uniform float borderWidth;  // 0.7
uniform float borderEdge;   // 0.1
uniform vec2 offset;        // vec2(0.0, 0.0)
uniform vec3 outlineColor;  // vec3(1.0, 1.0, 1.0);

void main(void){
    float distance = 1.0-texture(fontAtlas, pass_textCoOrds).a;
    float alpha = 1.0-smoothstep(width, width+edge, distance);

    float distance2 = 1.0 - texture(fontAtlas, pass_textCoOrds + offset).a;
    float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);

    float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
    vec3 overallColour = mix(outlineColor, colour, alpha / overallAlpha);

    out_colour = vec4(overallColour, overallAlpha);
}