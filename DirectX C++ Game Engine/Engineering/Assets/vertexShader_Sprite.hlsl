// Constants
//==========

// Per-View
//---------

// Per-Instance
//-------------
uniform float2 g_uvCoordinates = { 1.0, 1.0};

uniform float g_totalSecondsElapsed;
uniform float2 g_uv_speed;
// Entry Point
//============
void main(in const float3 i_position : POSITION, in const float2 i_uv : TEXCOORD0, 
	out float4 o_position : POSITION, out float2 o_uv : TEXCOORD0)
{
	// Calculate position
	{
		// Set the "out" position directly from the "in" position:
		o_position = float4(i_position.x, i_position.y, 0.0, 1.0);
	}

	// Set texture Coor
	{
		o_uv = i_uv;
	}
}
