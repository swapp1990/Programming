/*
	This is an example of a vertex shader
*/
// Constants
//==========

// Per-View
//---------
uniform float4x4 g_transform_worldToView;
uniform float4x4 g_transform_viewToScreen;

// Per-Instance
//-------------
uniform float4x4 g_transform_modelToWorld;

uniform float g_totalSecondsElapsed;
uniform float2 g_uv_speed;


// Entry Point
//============
void main(in const float3 i_position_model : POSITION, in const float3 i_color : COLOR0, in const float2 i_uv : TEXCOORD0, in const float3 i_normal : NORMAL, in const float3 i_tangent : TANGENT, in const float3 i_binormal : BINORMAL,
	out float4 o_position_screen : POSITION, out float3 o_color : COLOR0, out float2 o_uv : TEXCOORD0, out float3 o_normal : NORMAL, out float3 o_tangent : TANGENT, out float3 o_binormal : BINORMAL)
{
	// Calculate position
	{
		float4 position_world = mul(float4(i_position_model, 1.0), g_transform_modelToWorld);
		float4 position_view = mul(position_world, g_transform_worldToView);
		o_position_screen = mul(position_view, g_transform_viewToScreen);
		// The position stored in the vertex is in "model space",
		// meaning that it is relative to the center (or "origin", or "pivot")
		// of the model.
		// The graphics hardware needs the position of the vertex
		// in normalized device coordinates,
		// meaning where the position of the vertex should be drawn
		// on the screen.
		// This position that we need to output, then,
		// is the result of taking the original vertex in "model space"
		// and transforming it into "screen space".

	}

	// Calculate color
	{
	// Set the output color directly from the input color:
	o_color = i_color;
	}
	o_uv = i_uv + (g_uv_speed * g_totalSecondsElapsed);

	{
		float3x3 g_rotation_modelToWorld =
		{
			g_transform_modelToWorld[0].xyz,
			g_transform_modelToWorld[1].xyz,
			g_transform_modelToWorld[2].xyz
		};
		o_normal = mul(i_normal, g_rotation_modelToWorld);
		o_tangent = mul(i_tangent, g_rotation_modelToWorld);
		o_binormal = mul(i_binormal, g_rotation_modelToWorld);
	}


}
