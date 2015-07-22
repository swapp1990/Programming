#pragma once
#include "extern\glew\include\GL\glew.h"
#include "vmath.h"
#include "Texture.h"
#include <assert.h>

#include "extern\assimp\cimport.h" // C importer
#include "extern\assimp\scene.h" // collects data
#include "extern\assimp\postprocess.h" // various extra operations

#define MESH_PLANE 1;
class Mesh
{
	GLuint vbo_cube_vertices, vbo_cube_colors, vbo_cube_texcoords, vbo_cube_normals;
	GLuint ibo_cube_elements;
	GLuint shader_program;
	GLenum current_target, current_target2;

	GLuint current_tex_mipMap;
	bool blending_;
	float angle;

	GLuint create_plane(float size, float texture_c);

public:
	GLuint current_tex, current_tex2;
	bool cast_shadow;
	bool receive_shadow;
	bool show_backface;
	GLuint vaoObject;
	int vertex_count;
	int shader_Model_location;
	bool mipmapOn_;
	Mesh();
	Mesh(const char *fileName);
	Mesh(int type_index, GLuint program, float size = 1.0f, float tex_c = 1.0f);
	GLint uniform_mytexture_m;
	vmath::mat4 translation_matrix;
	vmath::mat4 rotation_matrix;
	vmath::mat4 scale_matrix;
	vmath::mat4 model_matrix;
	void Init();
	void Init(const char *fileName);
	void Render();
	Texture* LoadTexture();
	void makeCheckImage();
	vmath::vec3 mesh_posn;
	void SetTranslation(float x, float y, float z);
	void SetPosition(float x, float y, float z);
	void SetPosition(vmath::vec3 mesh_posn);
	void Move(float x, float y, float z);
	void SetRotation(float angle, float x, float y, float z);
	void SetScale(float scale);
	void SetScale(float scaleX, float scaleY, float scaleZ);

	void SetShaderProgram(GLuint program);
	void SetTexture(GLenum target, GLuint texture);
	void SetTexture(GLenum target1, GLuint texture1, GLenum target2, GLuint texture2);
	void SetTextureMipmap(GLuint texture);
	void InitializeDefaultCube();
	void InitializeDefaultPlane();

	bool load_mesh(const char* file_name, GLuint* vao, int* point_count);
	~Mesh();
};

