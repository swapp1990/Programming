#version 410 core

// Per-vertex inputs
layout (location = 0) in vec4 position; 
layout (location = 1) in vec2 vertTexCoord;
layout (location = 2) in vec3 vertNormal;

out VS_OUT
{
	vec3 N;
    vec3 L;
    vec3 V;
	vec4 color;
} vs_out;

 // Matrices we'll need
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

// Position of light
uniform vec3 light_pos = vec3(100.0, 40.0, 100.0);

void main(void)
{
   // Pass the tex coord straight through to the fragment shader
	// Calculate view-space coordinate
    vec4 P = mv_matrix * position;
    // Calculate normal in view-space
    vs_out.N = mat3(mv_matrix) * vertNormal;
	 // Calculate light vector
    vs_out.L = light_pos - P.xyz;
    // Calculate view vector
    vs_out.V = -P.xyz;

   vs_out.color = vec4(0.5, 0.2, 0.5, 0.0);  
	 // Calculate the clip-space position of each vertex
   gl_Position = proj_matrix * P;

}
