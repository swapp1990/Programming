#version 410 core

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 texture_coord;
layout (location = 3) in vec3 vertex_color;


out vec2 st;
out vec3 normal;


void main(void) {
  // Calculate normal in view-space
  st = texture_coord;
  normal = vertex_normal;
  gl_Position = vec4 (vertex_position, 1.0);
}
