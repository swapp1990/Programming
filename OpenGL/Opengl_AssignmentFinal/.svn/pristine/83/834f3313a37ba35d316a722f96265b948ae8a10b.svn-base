#version 410

in vec3 st;

uniform samplerCube cube_texture;

out vec4 frag_colour;

void main() {
	vec4 texel = texture (cube_texture, st);
	frag_colour = texel;
}
