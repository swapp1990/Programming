#version 410

in vec3 normal;
in vec2 st;
uniform sampler2D basic_texture;
out vec4 frag_colour;

void main() {
	vec4 texel = texture (basic_texture, st);
	frag_colour = vec4(0.0,0.0,0.0,0.5);
}