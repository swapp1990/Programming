#version 410 core

in vec4 st_shadow;
in vec3 normal;
in vec2 st;
in vec3 FragPos;
in vec3 V;

uniform sampler2D basic_texture;
uniform sampler2D normal_texture; 
uniform sampler2D depth_map;

uniform float shad_resolution = 2048.0;

// Material properties
uniform vec3 dirn_light = vec3(10.0, 2.0, 3.0);
uniform vec3 diffuse_dirn = vec3(0.2);
uniform vec3 specular_dirn = vec3(0.5);

uniform vec3 viewPos = vec3(0.0, 0.0, 12.0);
uniform vec3 light_pos = vec3(1.0,2.0,-1.0);
uniform vec3 diffuse_albedo = vec3(0.9,0.0,0.9);
uniform vec3 specular_albedo = vec3(1.0, 0.0, 1.0);
uniform float specular_power = 56.0;
uniform vec3 ambient = vec3(0.05, 0.05, 0.05);

out vec4 frag_colour;

float eval_shadow (vec4 texcoods)
{
	// constant that you can use to slightly tweak the depth comparison
	float epsilon = 0.00005;
	float shadow = texture2D (depth_map, texcoods.xy).r;
	if (shadow + epsilon < texcoods.z) {
		return 0.5; // shadowed
	}
	return 1.0; // not shadowed
}

vec3 CalcDirLight(vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(dirn_light);
    // Diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // Specular shading
    vec3 reflectDir = reflect(-lightDir, normal);

	vec3 cameraVector = viewDir; 
	vec3 halfVector = normalize(lightDir + cameraVector); 
	float nxHalf = max(0.0,dot(normal, halfVector)); 
    float spec = pow(nxHalf, specular_power);
   // float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular_power);
    // Combine results
    vec3 ambient_curr = ambient;
    vec3 diffuse = diffuse_dirn * diff;
    vec3 specular = specular_dirn * spec;
    return (ambient_curr + diffuse);
}

vec3 CalcPointLight(vec3 normal, vec3 fragPos, vec3 viewDir)
{
	vec3 lightDir = normalize(light_pos - fragPos);
	 // Diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
	// Specular shading
    vec3 reflectDir = reflect(-lightDir, normal);

	vec3 cameraVector = viewDir; 
	vec3 halfVector = normalize(lightDir + cameraVector); 
	float nxHalf = max(0.0,dot(normal, halfVector)); 
    float spec = pow(nxHalf, specular_power);
	//float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular_power);
	// Attenuation
    float distance = length(lightDir - fragPos);
    float attenuation = 1.0 / (1.0 + 0.1 * distance + 0.01 * (distance * distance)); 
	//float attenuation = 1.0 - ((distance/5.0)*(distance/0.5));
	 vec3 ambient_curr = ambient;
	 vec3 diffuse = diffuse_albedo * diff;
	 vec3 specular = specular_albedo * spec;
	//ambient_curr *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
	return (ambient + diffuse + specular);
}

void main()
{
	vec3 norm = normalize(normal);
	vec3 normal_map = normalize(texture2D(normal_texture, st).rgb * 2.0 - 1.0); 
	norm = normal_map;
	vec3 V = normalize(V);

	vec3 result = CalcDirLight(norm, V);
	result += CalcPointLight(norm, FragPos, V); 
	//vec3 result = CalcPointLight(norm, FragPos, V);
	vec4 texel = texture (basic_texture, st);

	vec4 shad_coord = st_shadow;
	/* biasing for shadow texture to set it from -1to1 -> 0to1*/
	shad_coord.xyz /= shad_coord.w;
	shad_coord.xyz += 1.0;
	shad_coord.xyz *= 0.5;
	float shadow_factor = eval_shadow (shad_coord);

	frag_colour = texel * vec4(result * shadow_factor, 1.0);
}




