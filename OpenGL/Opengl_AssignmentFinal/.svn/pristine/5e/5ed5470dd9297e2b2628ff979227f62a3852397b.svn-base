/*
	This is an example of a fragment shade
*/
// Constants
//==========

// Per-Material
//-------------

// Textures
//=========
texture2D g_color_texture;

// Samplers
//=========
sampler2D g_color_sampler;
uniform float alphaScale;
// Entry Point
//============

void main(in const float2 i_uv : TEXCOORD0, 
	out float4 o_color : COLOR0)
{
	// Set the color of this fragment to the interpolated color value
	// (The interpolated color will depend on how close
	// the fragment is to each of the three vertices of the triangle;
	// the closer it is to a vertex, the more its color will be influenced
	// by that vertex)

	// "Sample" the texture to get the color at the given texture coordinates

	float4 color_sample = tex2D(g_color_sampler, i_uv).rgba;
		color_sample.a = color_sample.a * alphaScale;
		//float3 color_albedo = color_sample * i_color_perVertex;

		//o_color = float4(color_albedo, 1.0);

	o_color = color_sample;

}
