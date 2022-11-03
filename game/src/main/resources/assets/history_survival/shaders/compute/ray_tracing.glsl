#version 430 core

layout(binding = 0, rgba32f) uniform image2D framebuffer;

// The camera specification
uniform vec3 eye;
uniform vec3 ray00;
uniform vec3 ray10;
uniform vec3 ray01;
uniform vec3 ray11;