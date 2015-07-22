#version 410 
out vec4 color;  

in vec2 st;
uniform sampler2D depth_tex;

void main(void)
{
	float d = texture2D (depth_tex, st).r;
	if(d == 1.0)
	{
		color = vec4 (1.0, 0.0, 1.0, 1.0);
	}
	else
	{
		color = vec4 (d, d, d, 1.0);
	}
	
}
