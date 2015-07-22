#version 410

layout(location = 0) in vec3 vertex_position; // positions from mesh
layout(location = 1) in vec3 vertex_normal; // normals from mesh
layout(location = 2) in vec2 texture_coord;

uniform mat4 view, proj, model; // proj, view, model matrices

// Position of light
uniform vec3 light_pos = vec3(0.0, 0.0, -10.0);

out vec3 pos_eye;
out vec3 n_eye;
out vec2 st;

out vec3 L;
out vec3 V;

void main () {
	//Phong shading
	mat4 mv_matrix = model * view;
	vec4 P = mv_matrix * vec4(vertex_position, 1.0);
	// Calculate light vector
	L = light_pos - P.xyz;
	// Calculate view vector
	V = -P.xyz;

	st = texture_coord;
	pos_eye = vec3 (view * model * vec4 (vertex_position, 1.0));
	n_eye = vec3 (view * model * vec4 (vertex_normal, 0.0));
	gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
}
