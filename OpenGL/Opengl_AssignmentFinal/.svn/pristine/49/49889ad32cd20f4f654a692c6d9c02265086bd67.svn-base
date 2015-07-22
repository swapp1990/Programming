#version 410

// vertex points in light coordinate space
in vec4 st_shadow;
in vec3 normal;
in vec2 st;
in vec3 L;
in vec3 V;
// the depth map
uniform sampler2D basic_texture;
uniform sampler2D depth_map;

uniform vec3 colour;
uniform float shad_resolution = 2048.0;

// Material properties
uniform vec3 diffuse_albedo = vec3(0.5, 0.5, 0.5);
uniform vec3 specular_albedo = vec3(0.7);
uniform float specular_power = 128.0;
uniform vec3 ambient = vec3(0.2, 0.2, 0.2);

out vec4 frag_colour;

float eval_shadow (vec4 texcoods) {
	// constant that you can use to slightly tweak the depth comparison
	float epsilon = 0.003;

	float shadow = texture2D (depth_map, texcoods.xy).r;
	
	if (shadow + epsilon < texcoods.z) {
		return 0.25; // shadowed
	}
	return 1.0; // not shadowed
}

float CalcShadowMapEffect(sampler2D shadowMap, vec4 init_shadowMapCoords)
{
	vec3 shadowMapCoords = (init_shadowMapCoords.xyz/init_shadowMapCoords.w) * vec3(0.5) + vec3(0.5);
	//texture2D(shadowMap, shadowMapCoords.xy).r;
	return step(shadowMapCoords.z, texture2D(shadowMap, shadowMapCoords.xy).r);
}

void main() {
	// Normalize the incoming N, L and V vectors
    vec3 N = normalize(normal);
    vec3 L = normalize(L);
    vec3 V = normalize(V);
	// Calculate R locally
    vec3 R = reflect(-L, N);

	// Compute the diffuse and specular components for each fragment
    vec3 diffuse = max(dot(N, L), 0.0) * diffuse_albedo;
    vec3 specular = pow(max(dot(R, V), 0.0), specular_power) * specular_albedo;

	vec4 texel = texture (basic_texture, st);
	//frag_colour = vec4 (colour, 1.0);
	
	vec4 shad_coord = st_shadow;
	/* we compute this in frag shader otherwise we get errors from interpolation*/
	shad_coord.xyz /= shad_coord.w;
	shad_coord.xyz += 1.0;
	shad_coord.xyz *= 0.5;
	/* this is the original sampling without a filter */
	float shadow_factor = eval_shadow (shad_coord);
	//float shadow_factor = CalcShadowMapEffect(depth_map, shad_coord);
	//frag_colour = texel * vec4 (vec3(1.0,1.0,1.0) * shadow_factor, 1.0) * vec4((ambient + diffuse + specular), 1.0);
	frag_colour = texel * vec4 (vec3(1.0,1.0,1.0) * shadow_factor, 1.0) * vec4((ambient + diffuse + specular), 1.0);
}
