#version 410

layout(location = 0) in vec3 vertex_position;

uniform mat4 view, proj;

out vec3 st;

void main() {
	//For skybox the texture coordinates are simply the vertex coordinates of the cube.
	st = vertex_position;
	//For skybox view matrix, we should remove any translation components from the matrix, so it does not translate with movement of camera.
	mat4 view_wo_translation = mat4(mat3(view));
	gl_Position = proj * view_wo_translation * vec4 (vertex_position, 1.0);
}
