#version 410 core

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 texture_coord;
layout (location = 3) in vec3 vertex_color;

uniform mat4 model, view, proj;

out vec3 normal;
out vec3 FragPos;
out vec3 V;
out vec2 st;
out vec3 eyeVec;

//environment map
out vec3 pos_eye;
out vec3 n_eye;

void main(void) {
  // Calculate normal in view-space
  //normal = mat3(transpose(inverse(model))) * vertex_normal; 
  normal = mat3(model * view) * vertex_normal;
  vec3 tangent; 
  vec3 binormal; 
  vec3 c1 = cross(vertex_normal, vec3(0.0, 0.0, 1.0)); 
  vec3 c2 = cross(vertex_normal, vec3(0.0, 1.0, 0.0)); 
	if(length(c1)>length(c2))
	{
		tangent = c1;	
	}
	else
	{
		tangent = c2;	
	}
	tangent = normalize(tangent);
  binormal = cross(vertex_normal, tangent); 
  binormal = normalize(binormal);
  vec4 P = model * view * vec4(vertex_position, 1.0);
  V = -P.xyz;
	eyeVec.x = dot(V, tangent);
	eyeVec.y = dot(V, binormal);
	eyeVec.z = dot(V, vertex_normal);

	pos_eye = vec3 (view * model * vec4 (vertex_position, 1.0));
	n_eye = vec3 (view * model * vec4 (vertex_normal, 0.0));

  st = texture_coord;
  FragPos = vec3(model * vec4(vertex_position, 1.0f));
  gl_Position = proj * view * model * vec4 (vertex_position, 1.0);
}
