#version 410 core

// Per-vertex inputs
layout (location = 0) in vec4 position; 
layout (location = 1) in vec2 vertTexCoord;
layout (location = 2) in vec3 vertNormal;

out VS_OUT
{
	vec4 color;
} vs_out;

 // Matrices we'll need
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

// Position of light
// Light and material properties
uniform vec3 light_pos = vec3(100.0, 100.0, 100.0);
uniform vec3 diffuse_albedo = vec3(0.2, 0.2, 0.2);
uniform vec3 specular_albedo = vec3(0.7);
uniform float specular_power = 128.0;
uniform vec3 ambient = vec3(0.2, 0.2, 0.2);

void main(void)
{
   // Pass the tex coord straight through to the fragment shader
	// Calculate view-space coordinate
    vec4 P = mv_matrix * position;
    // Calculate normal in view-space
    vec3 N = mat3(mv_matrix) * vertNormal;
	 // Calculate light vector
    vec3 L = light_pos - P.xyz;
    // Calculate view vector
    vec3 V = -P.xyz;

	 // Normalize all three vectors
    N = normalize(N);
    L = normalize(L);
    V = normalize(V);
    // Calculate R by reflecting -L around the plane defined by N
    vec3 R = reflect(-L, N);

	 // Calculate the diffuse and specular contributions
    vec3 diffuse = max(dot(N, L), 0.0) * diffuse_albedo;
	vec3 specular = pow(max(dot(R, V), 0.0), specular_power) * specular_albedo;

   vs_out.color = vec4(ambient + diffuse + specular,0.0);  
	 // Calculate the clip-space position of each vertex
   gl_Position = proj_matrix * P;
}
