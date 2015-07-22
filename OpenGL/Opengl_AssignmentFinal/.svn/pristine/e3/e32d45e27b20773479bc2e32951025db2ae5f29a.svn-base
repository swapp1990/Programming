#pragma once
#include <stdio.h>
#include <stdlib.h>
#include "extern\glew\include\GL\glew.h"
#include "gl_utils.h"

#define MAX_SHADER_LENGTH 262144

class Shader
{
	public:
		Shader(const char* vert_file_name, const char* frag_file_name);
		Shader(const char* vert_file_name, const char* geom_file_name, const char* frag_file_name);
		GLuint GetShaderProgram();
		void AddUniforms();

		int uniform_locations[10];
	protected:
		bool Create_Shader(const char* file_name, GLuint* shader, GLenum type);
		bool Create_Program(GLuint vert, GLuint frag, GLuint* programme);
	private:
		GLuint shader_prog;
		int uniform_counter;

};