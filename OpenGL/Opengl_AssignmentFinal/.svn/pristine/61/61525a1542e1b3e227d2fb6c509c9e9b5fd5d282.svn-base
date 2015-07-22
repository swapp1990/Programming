#version 410 core

in vec2 o_st;
in vec3 o_normal;

uniform sampler2D basic_texture;

// Material properties
uniform vec3 dirn_light = vec3(10.0, 2.0, 3.0);
uniform vec3 diffuse_dirn = vec3(0.1,0.9,0.1);
uniform vec3 specular_dirn = vec3(0.5);

uniform vec3 viewPos = vec3(0.0, 0.0, 12.0);
uniform vec3 light_pos = vec3(1.0,2.0,-1.0);
uniform vec3 diffuse_albedo = vec3(0.9,0.0,0.9);
uniform vec3 specular_albedo = vec3(1.0, 0.0, 1.0);
uniform float specular_power = 128.0;
uniform vec3 ambient = vec3(0.05, 0.05, 0.05);

out vec4 frag_colour;

vec3 CalcDirLight(vec3 normal)
{
    vec3 lightDir = normalize(dirn_light);
    // Diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // Specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular_power);
    // Combine results
    vec3 ambient_curr = ambient;
    vec3 diffuse = diffuse_dirn * diff;
    //vec3 specular = specular_dirn * spec;
    return (ambient_curr + diffuse);
}

void main()
{
	vec3 norm = normalize(o_normal);
	//vec3 V = normalize(V);

	vec3 result = CalcDirLight(norm);
	vec4 texel = texture (basic_texture, o_st);
	frag_colour = texel * vec4(result, 1.0);
}




