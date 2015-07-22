#version 410

// vertex points in light coordinate space
in vec4 st_shadow;
in vec3 normal;
in vec3 FragPos;
in vec2 st;
in vec3 V;

// the depth map
uniform sampler2D basic_texture;
uniform sampler2D depth_map;

uniform vec3 colour;
uniform float shad_resolution = 2048.0;

uniform float specular_power = 128.0;
uniform vec3 ambient = vec3(0.1);
//dirn light
uniform vec3 dirn_light = vec3(10.0, 2.0, 3.0);
uniform vec3 diffuse_dirn = vec3(0.2);
uniform vec3 specular_dirn = vec3(0.5);
// Point Light
uniform vec3 light_pos = vec3(1.0,2.0,-1.0);
uniform vec3 diffuse_albedo = vec3(0.9,0.0,0.9);
uniform vec3 specular_albedo = vec3(1.0, 0.0, 1.0);


out vec4 frag_colour;

float eval_shadow (vec4 texcoods) {
	// constant that you can use to slightly tweak the depth comparison
	float epsilon = 0.00005;

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

vec3 CalcDirLight(vec3 N, vec3 V)
{
	// Calculate R locally
	vec3 L = normalize(dirn_light);
    vec3 R = reflect(-L, N);
	float diff = max(dot(N, L), 0.0);
    vec3 diffuse = diff * diffuse_dirn;
	float spec = pow(max(dot(V, R), 0.0), specular_power);
    vec3 specular = spec * specular_dirn;
	return (ambient + diffuse + specular);
}

vec3 CalcPointLight(vec3 N, vec3 FragPos, vec3 V)
{
	vec3 L = normalize(light_pos - FragPos);
	vec3 R = reflect(-L, N);
	// Diffuse shading
    float diff = max(dot(N, L), 0.0);
	vec3 diffuse = diff * diffuse_albedo;
	// Specular shading
	float spec = pow(max(dot(V, R), 0.0), specular_power);
	vec3 specular = spec * specular_albedo;
		// Attenuation
		float distance = length(light_pos - FragPos);
		float attenuation = 1.0 / (1.0 + 0.1 * distance + 0.01 * (distance * distance)); 
	diffuse *= attenuation;
    specular *= attenuation;
	return (ambient + diffuse + specular);
}

void main() {
	// Normalize the incoming N, L and V vectors
    vec3 N = normalize(normal);
    vec3 V = normalize(V);

	//calculate dirn light
	vec3 result = CalcDirLight(N,V);

	//calculate point light
	result += CalcPointLight(N,FragPos,V);

	vec4 texel = texture (basic_texture, st);

	//shadow
	vec4 shad_coord = st_shadow;
	/* biasing for shadow texture to set it from -1to1 -> 0to1*/
	shad_coord.xyz /= shad_coord.w;
	shad_coord.xyz += 1.0;
	shad_coord.xyz *= 0.5;
	/* this is the original sampling without a filter */
	float shadow_factor = eval_shadow (shad_coord);
	//float shadow_factor = CalcShadowMapEffect(depth_map, shad_coord);
	//shadow

	//frag_colour = texel * vec4 (vec3(1.0,1.0,1.0) * shadow_factor, 1.0) * vec4(result, 1.0);
	frag_colour = texel *  vec4(result * shadow_factor, 1.0);
}
