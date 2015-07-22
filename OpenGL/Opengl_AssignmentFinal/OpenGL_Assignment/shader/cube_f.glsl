#version 410 core
uniform sampler2D mytexture;

out vec4 color;  

in VS_OUT
{
	vec3 f_color;
	vec2 f_texcoord;
} fs_in;

void main(void) {
  vec2 flipped_texcoord = vec2(fs_in.f_texcoord.x, fs_in.f_texcoord.y);
  //color = vec4(fs_in.f_color, 1.0);
  color = texture(mytexture, flipped_texcoord);
}
