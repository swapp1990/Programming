#version 410

layout(location = 0) in vec3 vertex_position; 
layout(location = 1) in vec3 vertex_normal; 

uniform mat4 view, proj, model; // proj, view, model matrices

out vec3 position_eye;
out vec3 normal_eye;

void main () {
	position_eye = vec3 (V * M * vec4 (vertex_position, 1.0));
	normal_eye = mat3(transpose(inverse(model))) * vertex_normal;
	gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
}
