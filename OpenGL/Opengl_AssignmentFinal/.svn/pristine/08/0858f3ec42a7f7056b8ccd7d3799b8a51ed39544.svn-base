#version 410 core

layout (location = 0) in vec3 coord3d;
layout (location = 1) in vec3 v_color;
layout (location = 2) in vec2 tex_coord;

out VS_OUT
{
	vec3 f_color;
	vec2 f_texcoord;
} vs_out;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
// Position of light

// Material properties
uniform vec3 diffuse_albedo = vec3(0.2, 0.2, 0.2);
uniform vec3 specular_albedo = vec3(0.7);
uniform float specular_power = 128.0;
uniform vec3 ambient = vec3(0.2, 0.2, 0.2);

void main(void) {

  gl_Position = mvp * vec4(coord3d, 1.0);
  vs_out.f_color = v_color;
  vs_out.f_texcoord = tex_coord;
}
