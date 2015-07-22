#version 410

layout(location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 texture_coord;

uniform mat4 model, view, proj;
/* view and projection matrices from the shadow caster (light source) */
uniform mat4 caster_P, caster_V;

// point in the light's space
out vec4 st_shadow;
out vec3 FragPos;
out vec3 normal;
out vec2 st;
out vec3 V;

void main() {
  mat4 mv_matrix = model * view;
  vec4 P = mv_matrix * vec4(vertex_position, 1.0);
  // Calculate normal in view-space
  normal = mat3(mv_matrix) * vertex_normal;
  //normal = mat3(transpose(inverse(model))) * vertex_normal; 
  // Calculate view vector
  V = -P.xyz;

  st = texture_coord;
  FragPos = vec3(model * vec4(vertex_position, 1.0f));

  	/* create a shadow map texture coordinate by working out the position in the
	from the light's viewpoint (the same way we do in the depth-writing shader */
  st_shadow = caster_P * caster_V * model * vec4 (vertex_position, 1.0);

  gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
	

}
