#version 410 core

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 texture_coord;
layout (location = 3) in vec3 vertex_color;

uniform mat4 model, view, proj;

out vec3 N; 
out vec3 V; //eye

out vec2 st;

void main(void) {

  mat4 mv_matrix = model * view;
  vec4 P = mv_matrix * vec4(vertex_position, 1.0);
  // Calculate normal in view-space
  N = mat3(mv_matrix) * vertex_normal;
  // Calculate view vector
  V = -P.xyz;

  st = texture_coord;
  gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
}
