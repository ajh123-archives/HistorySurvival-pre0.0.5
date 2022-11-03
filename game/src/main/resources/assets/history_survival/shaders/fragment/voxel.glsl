#version 410 core

in vec2 pass_textureCoOrds;

out vec4 out_colour;

uniform sampler2D modelTexture;

void main(void) {
    vec4 textureColor = texture(modelTexture, pass_textureCoOrds);

//    if (textureColor.a <0.5) {
//        discard;
//    }

    out_colour = textureColor;
}
