#version 410 core

in vec2 pass_textCoOrds;

out vec4 out_colour;

uniform vec3 colour;
uniform sampler2D fontAtlas;

void main(void){
    out_colour = vec4(colour, texture(fontAtlas, pass_textCoOrds).a);
}