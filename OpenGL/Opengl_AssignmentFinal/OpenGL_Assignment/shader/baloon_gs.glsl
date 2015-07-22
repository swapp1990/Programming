#version 410 core
layout(triangles) in;
// Three lines will be generated: 6 vertices
layout(triangle_strip, max_vertices=3) out;

in vec2 st[];
in vec3 normal[];

uniform mat4 model, view, proj;
uniform float time;

out vec2 o_st;
out vec3 o_normal;

void main(void) {
//// new
  vec3 P0 = gl_in[0].gl_Position.xyz;
  vec3 P1 = gl_in[1].gl_Position.xyz;
  vec3 P2 = gl_in[2].gl_Position.xyz;
  
  vec3 V0 = P0 - P1;
  vec3 V1 = P2 - P1;
  vec3 diff = V1 - V0;
  float diff_len = length(diff);

  vec3 N = normalize(cross(V1, V0));

/////
if (length(diff_len) > 0.001)
  {
    for(int i=0; i<gl_in.length(); i++)
    {
	  vec4 P = gl_in[i].gl_Position;
	  ///new
	  vec3 N = normalize(normal[i]);
	  float len = sqrt(P.x*P.x + P.z*P.z);
	  float scale = 2.0 + 1.0 * cos(time*16.0 + len);
	  //scale = 0.0;
	  P = vec4(P.xyz + (N * time * scale) + \
          (N * vec3(0.005, 0.005, 0.005)), 1.0);
	  ////
	  o_st = st[i];
	  o_normal = normal[i];
      gl_Position = proj * view * model * P;
      EmitVertex();
	}
	EndPrimitive();
	}
  }
