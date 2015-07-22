#version 410 core

in vec3 N;
in vec3 V;
in vec2 st;

uniform sampler2D basic_texture;
uniform vec3 l_dir = vec3(7.0,7.0,0.0);
// Material properties
uniform vec3 diffuse_albedo = vec3(0.5, 0.5, 0.5);
uniform vec3 specular_albedo = vec3(0.7);
uniform vec3 ambient = vec3(0.2, 0.2, 0.2);
uniform float specular_power = 128.0; //shininess

out vec4 frag_colour;

void main()
{
	// set the specular term to black
    vec3 spec = vec3(0.0);
	// Normalize the incoming N, L and V vectors
    vec3 N = normalize(N);
    vec3 V = normalize(V);

	float intensity = max(dot(N,l_dir), 0.0);
 
    // if the vertex is lit compute the specular color
    if (intensity > 0.0) {
        // compute the half vector
        vec3 h = normalize(l_dir + V);  
        // compute the specular term into spec
        float intSpec = max(dot(h,N), 0.0);
        spec = specular_albedo * pow(intSpec,specular_power);
    }

	vec4 texel = texture (basic_texture, st);
    frag_colour = texel * vec4(max(intensity *  diffuse_albedo + spec, ambient),1.0);
}
