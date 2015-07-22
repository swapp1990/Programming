#version 410

in vec3 pos_eye;
in vec3 n_eye;
in vec2 st;
in vec3 L;
in vec3 V;

uniform sampler2D tex0;
uniform samplerCube tex1;
uniform mat4 view; // view matrix
uniform float transperancy_factor = 0.5;
out vec4 frag_colour;

// Material properties
uniform vec3 diffuse_albedo = vec3(0.7, 0.7, 0.7);
uniform vec3 specular_albedo = vec3(0.2);
uniform float specular_power = 100.0;
uniform vec3 ambient = vec3(0.4, 0.4, 0.4);

void main () {
	// Normalize the incoming N, L and V vectors
    vec3 N = normalize(n_eye);
    vec3 L = normalize(L);
    vec3 V = normalize(V);
	// Calculate R locally
    vec3 R = reflect(-L, N);

	// Compute the diffuse and specular components for each fragment
    vec3 diffuse = max(dot(N, L), 0.0) * diffuse_albedo;
    vec3 specular = pow(max(dot(R, V), 0.0), specular_power) * specular_albedo;

	/* reflect ray around normal from eye to surface */
	vec3 incident_eye = normalize (pos_eye);
	vec3 normal = normalize (n_eye);

	vec3 reflected = reflect (incident_eye, normal);
	// convert from eye to world space
	reflected = vec3 (inverse (view) * vec4 (reflected, 0.0));
	//
	vec4 texel_a = texture (tex0, st);
	vec4 texel_b = texture (tex1, reflected);
	vec4 blend_color = texel_a*(1-transperancy_factor) + texel_b*transperancy_factor;
	frag_colour = blend_color * vec4(ambient + diffuse + specular, 1.0);
}
