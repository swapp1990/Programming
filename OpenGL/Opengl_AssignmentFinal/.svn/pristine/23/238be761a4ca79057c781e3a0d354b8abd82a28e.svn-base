/*
	This is an example of a fragment shade
*/
// Constants
//==========

// Per-Material
//-------------

uniform float3 g_color_perMaterial = { 0.1, 0.1, 0.1 };
uniform float3 g_light_direction = { 0.0, -1.0, 0.0 };
uniform float3 g_light_direction_color = { 1.0, 1.0, 1.0 };
uniform float3 g_light_ambient = { 0.2, 0.2, 0.2 };

uniform float3 g_camera_position = { 0.0, 0.0, 0.0 };

uniform float g_specular_shininess;
uniform float g_transparency;
// Textures
//=========

// NOTE: These are actually not necessary to declare in Direct3D 9.
// I debated whether it would be more confusing to include the declarations or to leave them out,
// and finally decided it would be best to show them even though you're unnecessary.
// It is up to you to decide how to represent textures and samplers in your material file format.

texture2D g_color_texture;

// Samplers
//=========

sampler2D g_color_sampler;
sampler2D g_normal_sampler;


// Entry Point
//============


void main(in const float3 i_bitangent_world  : BINORMAL, in const float3 i_tangent_world : TANGENT, in const float3 i_normal_world : NORMAL, in const float2 i_uv : TEXCOORD0, in const float3 i_color_perVertex : COLOR0, in const float3 i_position_world : TEXCOORD1,
	out float4 o_color : COLOR0)
{
	// Set the color of this fragment to the interpolated color value
	// (The interpolated color will depend on how close
	// the fragment is to each of the three vertices of the triangle;
	// the closer it is to a vertex, the more its color will be influenced
	// by that vertex)

	// "Sample" the texture to get the color at the given texture coordinates
	float3 color_sample = tex2D(g_color_sampler, i_uv).rgb;
		
		float3 color_albedo = color_sample * g_color_perMaterial * i_color_perVertex;

		float3 normal_world = normalize(i_normal_world);

		
	/*float3 normal_world;
	{
		float3 normal_texture;
		{
			// Sample the normal map
			normal_texture = tex2D(g_normal_sampler, i_uv).xyz;
			// Convert from [0,1] -> [-1,1]
			normal_texture = (normal_texture * 2.0) - 1.0;
		}
		float3x3 rotation_textureToWorld =
		{
			normalize(i_tangent_world),
			normalize(i_bitangent_world),
			normalize(i_normal_world)
		};
		normal_world = mul(normal_texture, rotation_textureToWorld);
		normal_world = normalize(normal_world);
	}*/

	float3 lighting_diffuse;
	{
		float3 diffuseAmount;
		{
			diffuseAmount = dot(normal_world, -g_light_direction);
			lighting_diffuse = g_light_direction_color * diffuseAmount;
		}
	}

	float3 lighting_specular;
	{
		float specularAmount;
		{
			float dotProduct_specular;
			{
				// Direction of the reflected light (the light direction is already normalized)
				float3 reflectedDirection = reflect(g_light_direction, normal_world);
					// Direction of the view ray
					float3 viewDirection = normalize(g_camera_position - i_position_world);
					// How close (parallel) the view ray is to the reflected light
					dotProduct_specular = dot(reflectedDirection, viewDirection);
				dotProduct_specular = saturate(dotProduct_specular);
			}
			// A pow() function is used as a hack to approximate how big or small the specular highlight is
			// (which makes an object seem more "shiny", or smooth as opposed to rough)
			specularAmount = pow(dotProduct_specular, g_specular_shininess);
		}
		lighting_specular = g_light_direction_color * specularAmount;
	}

	float3 color_lit = color_albedo * (lighting_diffuse + lighting_specular + g_light_ambient);
		o_color = float4(color_lit, g_transparency);
	// The output color is the texture color
	// modified by the interpolated per-vertex color
	// and the per-material constant
	//o_color = float4(color_sample * g_color_perMaterial * i_color_perVertex,
	// For now the A value should _always_ be 1.0
	//1.0);
}
