#include "Shader.h"
#include <assert.h>

Shader::Shader(const char* vert_file_name, const char* frag_file_name)
{
	GLuint vert, frag, programme;
	assert(create_shader(vert_file_name, &vert, GL_VERTEX_SHADER));
	assert(create_shader(frag_file_name, &frag, GL_FRAGMENT_SHADER));
	assert(create_programme(vert, frag, &programme));

	//
	shader_prog = programme;

	AddUniforms();
}

Shader::Shader(const char* vert_file_name, const char* geom_file_name, const char* frag_file_name)
{
	GLuint vert, geom, frag, programme;
	assert(create_shader(vert_file_name, &vert, GL_VERTEX_SHADER));
	assert(create_shader(geom_file_name, &geom, GL_GEOMETRY_SHADER));
	assert(create_shader(frag_file_name, &frag, GL_FRAGMENT_SHADER));
	assert(create_programme(vert, frag, geom, &programme));
	shader_prog = programme;

	AddUniforms();
}

void Shader::AddUniforms()
{
	uniform_counter = 0;
	uniform_locations[uniform_counter] = glGetUniformLocation(shader_prog, "model");
	uniform_counter++;
	uniform_locations[uniform_counter] = glGetUniformLocation(shader_prog, "view");
	uniform_counter++;
	uniform_locations[uniform_counter] = glGetUniformLocation(shader_prog, "proj");
	uniform_counter++;
	uniform_locations[uniform_counter] = glGetUniformLocation(shader_prog, "light_pos");
	uniform_counter++;
	uniform_locations[uniform_counter] = glGetUniformLocation(shader_prog, "dirn_light");
	uniform_counter++;
}

bool Shader::Create_Shader(const char* file_name, GLuint* shader, GLenum type)
{
	gl_log("creating shader from %s...\n", file_name);
	char shader_string[MAX_SHADER_LENGTH];
	assert(parse_file_into_str(file_name, shader_string, MAX_SHADER_LENGTH));
	*shader = glCreateShader(type);
	const GLchar* p = (const GLchar*)shader_string;
	glShaderSource(*shader, 1, &p, NULL);
	glCompileShader(*shader);
	// check for compile errors
	int params = -1;
	glGetShaderiv(*shader, GL_COMPILE_STATUS, &params);
	if (GL_TRUE != params) {
		gl_log_err("ERROR: GL shader index %i did not compile\n", *shader);
		print_shader_info_log(*shader);
		return false; // or exit or something
	}
	gl_log("shader compiled. index %i\n", *shader);
	return true;
}

bool Shader::Create_Program(GLuint vert, GLuint frag, GLuint* programme) 
{
	*programme = glCreateProgram();
	gl_log(
		"created programme %u. attaching shaders %u and %u...\n",
		*programme,
		vert,
		frag
		);
	glAttachShader(*programme, vert);
	glAttachShader(*programme, frag);
	// link the shader programme. if binding input attributes do that before link
	glLinkProgram(*programme);
	GLint params = -1;
	glGetProgramiv(*programme, GL_LINK_STATUS, &params);
	if (GL_TRUE != params) {
		gl_log_err(
			"ERROR: could not link shader programme GL index %u\n",
			*programme
			);

		//print_programme_info_log(*programme);
		return false;
	}
	assert(is_programme_valid(*programme));
	// delete shaders here to free memory
	glDeleteShader(vert);
	glDeleteShader(frag);
	return true;
}

GLuint Shader::GetShaderProgram()
{
	return shader_prog;
}
