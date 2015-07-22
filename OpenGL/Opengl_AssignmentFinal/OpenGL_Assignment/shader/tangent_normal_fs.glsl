#version 410 core

in vec4 st_shadow;
in vec3 normal;
in vec2 st;
in vec3 FragPos;
in vec3 V;
in vec3 eyeVec;
in vec3 pos_eye;
in vec3 n_eye;

uniform sampler2D basic_texture;
uniform sampler2D normal_texture; 
uniform sampler2D depth_map;
uniform samplerCube environment_map;
uniform float transperancy_factor = 0.5;

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

mat3 cotangent_frame(vec3 N, vec3 p, vec2 uv)
{
    // get edge vectors of the pixel triangle
    vec3 dp1 = dFdx( p );
    vec3 dp2 = dFdy( p );
    vec2 duv1 = dFdx( uv );
    vec2 duv2 = dFdy( uv );
 
    // solve the linear system
    vec3 dp2perp = cross( dp2, N );
    vec3 dp1perp = cross( N, dp1 );
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;
 
    // construct a scale-invariant frame 
    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
    return mat3( T * invmax, B * invmax, N );
}

vec3 perturb_normal( vec3 N, vec3 V, vec2 texcoord )
{
    // assume N, the interpolated vertex normal and 
    // V, the view vector (vertex to eye)
   vec3 map = texture(normal_texture, texcoord ).xyz;
   map = map * 255./127. - 128./127.;
   mat3 TBN = cotangent_frame(N, -V, texcoord);
   return normalize(TBN * map);
}

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
	float distSqr = dot(light_pos, light_pos);
	float att = clamp(1.0 - 1.0 * sqrt(distSqr), 0.0, 1.0);
	vec3 lVec = light_pos * inversesqrt(distSqr);
	vec3 vVec = normalize(eyeVec);

	vec3 norm = normalize(normal);
	vec4 texel = texture (basic_texture, st);
	vec3 normal_map = normalize(texture2D(normal_texture, st).rgb * 2.0 - 1.0); 

	float diff = max( dot(lVec, normal_map), 0.0 );
	vec3 diffuse = diffuse_albedo * diff;
	float spec = pow(clamp(dot(reflect(-lVec, normal_map), vVec), 0.0, 1.0), 
	                 specular_power);
	vec3 specular = specular_albedo * spec;
	
	vec3 result = diffuse + specular;
	//norm = normal_map;
	vec3 V = normalize(V);
	//result = CalcPointLight(norm, FragPos,V);
	//frag_colour = texel * vec4(result, 1.0);
	

	//vec3 result = CalcDirLight(norm, V);
	//result += CalcPointLight(norm, FragPos, V); 
	//vec3 result = CalcPointLight(norm, FragPos, V);

	vec2 uv = st;
	vec3 N = normalize(normal);
	vec3 L = normalize(light_pos);
	vec3 PN = perturb_normal(N, V, uv);

	vec4 tex01_color = texture(basic_texture, uv).rgba;
	vec4 final_color = vec4(0.2, 0.15, 0.15, 1.0) * tex01_color; 

	float lambertTerm = dot(PN, L);
	if (lambertTerm > 0.0)
	{
		final_color += vec4(diffuse,1.0) * lambertTerm * tex01_color;  
		vec3 E = normalize(V);
		vec3 R = reflect(-L, PN);
		float specT = pow( max(dot(R, E), 0.0), specular_power);
		final_color += vec4(specular,1.0) * specT;  
	}
	frag_colour = vec4(vec3(final_color),1.0);
	//frag_colour = texel * vec4(result, 1.0);
	//vec4 shad_coord = st_shadow;
	/* biasing for shadow texture to set it from -1to1 -> 0to1*/
	//shad_coord.xyz /= shad_coord.w;
	//shad_coord.xyz += 1.0;
	//shad_coord.xyz *= 0.5;
	//float shadow_factor = eval_shadow (shad_coord);

	//frag_colour = texel * vec4(result, 1.0);
}




