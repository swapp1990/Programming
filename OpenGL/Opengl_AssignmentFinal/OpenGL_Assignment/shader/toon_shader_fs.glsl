#version 410 core

in vec3 normal;
in vec2 st;
in vec3 FragPos;
in vec3 V;

uniform sampler2D basic_texture;

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

vec3 CalcToonEffect(vec3 normal)
{
	vec3 lightDir = normalize(dirn_light);
	float intensity = dot(lightDir,normal);
	float edgeDetection = (dot(normalize(V), normal) >  0.35) ? 1 : 0;
	vec3 color;
	if (intensity > 0.55)
		color = vec3(1.0,0.5,0.5);
	else if (intensity > 0.3)
		color = vec3(0.6,0.3,0.3);
	else if (intensity > 0.05)
		color = vec3(0.4,0.2,0.2);
	else
		color = vec3(0.2,0.1,0.1);
	color = edgeDetection * color;
	return color;
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
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular_power);
    // Combine results
    vec3 ambient_curr = ambient;
    vec3 diffuse = CalcToonEffect(normal);
    vec3 specular = specular_dirn * spec;
    return (ambient_curr + diffuse + specular);
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
    float distance = length(light_pos - fragPos);
    float attenuation = 1.0 / (1.0 + 0.1 * distance + 0.01 * (distance * distance)); 
	//float attenuation = 1.0 - ((distance/5.0)*(distance/0.5));
	 vec3 ambient_curr = ambient;
	 //vec3 diffuse = diffuse_albedo * diff;
	 vec3 diffuse = CalcToonEffect(normal);
	 vec3 specular = specular_albedo * spec;
	//ambient_curr *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
	return (ambient + diffuse + specular);
}

void main()
{
	vec3 norm = normalize(normal);
	vec3 V = normalize(V);
	vec3 viewDir = normalize(viewPos - FragPos);
	
	vec3 result = CalcDirLight(norm, V);
	result += CalcPointLight(norm, FragPos, V); 
	//vec3 result = CalcPointLight(norm, FragPos, V);
	vec4 texel = texture (basic_texture, st);
	frag_colour = texel * vec4(result, 1.0);
}




