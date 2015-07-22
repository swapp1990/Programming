#version 410 core

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 texture_coord;
layout (location = 3) in vec3 vertex_color;

uniform mat4 model, view, proj;
uniform mat4 caster_P, caster_V;

out vec4 st_shadow;
out vec3 normal;
out vec3 FragPos;
out vec3 V;
out vec2 st;

void main(void) {
  // Calculate normal in view-space
  //normal = mat3(transpose(inverse(model))) * vertex_normal; 
  normal = mat3(model * view) * vertex_normal;
  vec4 P = model * view * vec4(vertex_position, 1.0);
  V = -P.xyz;
  st = texture_coord;
  FragPos = vec3(model * vec4(vertex_position, 1.0f));

  st_shadow = caster_P * caster_V * model * vec4 (vertex_position, 1.0);

  gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
}
