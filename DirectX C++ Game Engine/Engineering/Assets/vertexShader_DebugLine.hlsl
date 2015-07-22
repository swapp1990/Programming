// Constants
//==========
uniform float4x4 g_transform_worldToView;
uniform float4x4 g_transform_viewToScreen;
// Per-View
//---------

// Per-Instance
//-------------

// Entry Point
//============
void main(in const float3 i_position_world : POSITION, in const float3 i_color : COLOR0,
	out float4 o_position_screen : POSITION, out float3 o_color : COLOR0)
{
	// Calculate position
	{
		float4 position_view = mul(float4(i_position_world, 1.0), g_transform_worldToView);
		o_position_screen = mul(position_view, g_transform_viewToScreen);
		//o_position = float4(i_position.x, i_position.y, 0.0, 1.0);
	}

	// Set texture Coor
	{
		o_color = i_color;
	}
}
