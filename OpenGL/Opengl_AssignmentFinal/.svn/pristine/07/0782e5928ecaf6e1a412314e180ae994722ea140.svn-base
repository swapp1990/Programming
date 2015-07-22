#version 410

// Per-vertex inputs
layout (location = 0) in vec2 position; 

out vec2 st;

void main(void)
{
  gl_Position = vec4 (position * 0.333 + 0.667, 0.0, 1.0);
  st = (position + 1.0) * 0.5;
}
