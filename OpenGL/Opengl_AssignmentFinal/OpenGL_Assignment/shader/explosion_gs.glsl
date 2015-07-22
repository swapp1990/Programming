#version 410 core
layout(triangles) in;
// Three lines will be generated: 6 vertices
layout(triangle_strip, max_vertices=3) out;

in vec2 st[];
in vec3 normal[];

uniform mat4 model, view, proj;
uniform float time;
uniform float break_mesh;
uniform int e_type;

out vec2 o_st;
out vec3 o_normal;
out vec3 V;

void main(void) {

//// new
  vec3 P0 = gl_in[0].gl_Position.xyz;
  vec3 P1 = gl_in[1].gl_Position.xyz;
  vec3 P2 = gl_in[2].gl_Position.xyz;
  
  vec3 V0 = P0 - P1;
  vec3 V1 = P2 - P1;
  vec3 diff = V1 - V0;
  float diff_len = length(diff);

/////
//if (length(diff_len) > 0.001)
  {
	int count = 0;
    for(int i=0; i<gl_in.length(); i++)
    {
	  count++;
	  vec4 P = gl_in[i].gl_Position;
	  ///new
	  vec3 N = normalize(cross(V1, V0));
	  float scale = 0.0;
	  if(time < 4.0)
	  {
		//gradual y degree
		if(e_type == 0)
		{
		  if(N.y > (-1.0 + time/2))
		  {
	  			N.x = N.y = N.z = 0.0f;
		  }
		  else if(N.y > 0.0)
		  {
			N.y = -N.y;
			scale = scale + time/2;
		  }
		  else
		  {
			scale = scale + time/2;
		  }
		}
		else if(e_type == 1)
		{
		  if(N.x >break_mesh)
		  {
			N.x = N.y = N.z = 0.0f;
		  }
		  else
		  {
		    if(N.y > 0.0)
		    {
			N.y = -N.y;
			}
			scale = scale + time/2;
		  }
		}
		//explosion outwards
		else if(e_type == 2)
		{
			scale = scale + time;
		}  
		  //Bullet time
		else if(e_type == 3)
		{
			if((N.x > -0.74 || N.y < 0.1 || N.z < 0.1) && time < 0.5)
			{
				N.x = N.y = N.z = 0.0f;
			}
			else if((N.x > -0.74 || N.y < 0.1 || N.z < 0.1) && (N.x < 0.74 ||  N.y > -0.1 || N.z > -0.1) && time > 0.5)
			{
				N.x = N.y = N.z = 0.0f;
			}
			else
			{
				if(N.y > 0.0)
				{
						N.y = -N.y;
				}
				scale = scale + time/2;
			}
		}
	  }

	  float len = sqrt(P.x*P.x + P.z*P.z);
	  float time_2 = time; //* (i);

	  //float scale = 2.0 + 1.0 * cos(time_2*16.0 + len);

	   vec4 temp = vec4(P.xyz + (N * (scale) * time),1.0);
	   vec4 temp2 = vec4(P.xyz - (N * 0.005),1.0);
         // + (N * vec3(0.005, 0.005, 0.005)), 1.0);
		 if(e_type < 2)
		 {
			P.y = temp.y;
			P.x = temp2.x;
			P.z = temp2.z;
		 }
		 else
		 {
			P = temp;
		 }

	 
	  ////
	  o_st = st[i];
	  o_normal = normal[i];
	  V = vec3(model * view  * P);
      gl_Position = proj * view * model * P;
      EmitVertex();
	}

	EndPrimitive();

}

}
