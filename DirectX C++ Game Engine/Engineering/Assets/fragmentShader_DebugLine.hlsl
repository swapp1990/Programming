/*
	This is an example of a fragment shade
*/
// Constants
//==========
// Per-Material
//-------------

// Textures
//=========

// Samplers
//=========

// Entry Point
//============

void main(in const float3 i_color_perVertex : COLOR0,
	out float4 o_color : COLOR0)
{
	o_color = float4(i_color_perVertex,1.0);
}
