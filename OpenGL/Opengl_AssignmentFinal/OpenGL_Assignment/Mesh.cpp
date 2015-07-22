#include "Mesh.h"
#include <cstring>
#include "Texture.h"

Mesh::Mesh()
{
	vaoObject = 0;
	vertex_count = 0;
	blending_ = false;
	//Init();
	angle = 45.0f;
	translation_matrix = vmath::translate(0.0f, 0.0f, 0.0f);
	rotation_matrix = vmath::rotate(0.0f, 0.0f, 0.0f, 0.0f);
	scale_matrix = vmath::scale(1.0f);
}

Mesh::Mesh(const char *fileName)
{
	vaoObject = 0;
	vertex_count = 0;
	blending_ = false;
	mipmapOn_ = false;
	Init(fileName);
	angle = 45.0f;
	translation_matrix = vmath::translate(-0.5f, -1.0f, 3.0f);
	rotation_matrix = vmath::rotate(0.0f, 0.0f, 0.0f, 0.0f);
	scale_matrix = vmath::scale(1.0f);
}

Mesh::Mesh(int type_index, GLuint program, float i_size, float i_tex_c)
{
	//Mesh();
	vaoObject = 0;
	vertex_count = 0;
	blending_ = false;
	mipmapOn_ = false;
	//Init();
	cast_shadow = false;
	receive_shadow = false;
	show_backface = false;
	angle = 45.0f;
	translation_matrix = vmath::translate(0.0f, -3.0f, 0.0f);
	rotation_matrix = vmath::rotate(-90.0f, 1.0f, 0.0f, 0.0f);
	scale_matrix = vmath::scale(1.0f);
	current_target = GL_TEXTURE_2D;
	Texture* default_texture = LoadTexture();
	current_tex = default_texture->object();
	if (type_index == 1)
	{
		vaoObject = create_plane(i_size, i_tex_c);
		vertex_count = 6;
	}

}

GLuint Mesh::create_plane(float size, float texture_c)
{
	GLfloat points[] = {
		-size, -size, 0.0f,
		size, -size, 0.0f,
		size, size, 0.0f,
		size, size, 0.0f,
		-size, size, 0.0f,
		-size, -size, 0.0f
	};

	GLfloat texcoords[] = {
		0.0f, 0.0f,
		texture_c, 0.0f,
		texture_c, texture_c,
		texture_c, texture_c,
		0.0f, texture_c,
		0.0f, 0.0f
	};

	GLuint points_vbo;
	glGenBuffers(1, &points_vbo);
	glBindBuffer(GL_ARRAY_BUFFER, points_vbo);
	glBufferData(GL_ARRAY_BUFFER, 18 * sizeof (GLfloat), points, GL_STATIC_DRAW);

	GLuint texcoords_vbo;
	glGenBuffers(1, &texcoords_vbo);
	glBindBuffer(GL_ARRAY_BUFFER, texcoords_vbo);
	glBufferData(GL_ARRAY_BUFFER, 12 * sizeof (GLfloat), texcoords, GL_STATIC_DRAW);

	GLuint vao;
	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);

	//Vertex
	glBindBuffer(GL_ARRAY_BUFFER, points_vbo);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);

	//Texture Coor
	glBindBuffer(GL_ARRAY_BUFFER, texcoords_vbo);
	glEnableVertexAttribArray(2);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 0, NULL);

	return vao;
}

void Mesh::Init(const char *fileName)
{
	GLuint current_vao;
	int current_point_count = 0;
	assert(load_mesh(fileName, &current_vao, &current_point_count));
	vaoObject = current_vao;
	vertex_count = current_point_count;
	translation_matrix = vmath::translate(-0.5f, -1.0f, 3.0f);
}

void Mesh::Init()
{
	InitializeDefaultPlane();
}

void Mesh::InitializeDefaultCube()
{

	//Intialize Resourcces to draw
	GLfloat cube_vertices[] = {
		// front
		-1.0, -1.0, 1.0,
		1.0, -1.0, 1.0,
		1.0, 1.0, 1.0,
		-1.0, 1.0, 1.0,
		// back
		-1.0, -1.0, -1.0,
		1.0, -1.0, -1.0,
		1.0, 1.0, -1.0,
		-1.0, 1.0, -1.0,

	};
	glGenBuffers(1, &vbo_cube_vertices);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_vertices);
	glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_STATIC_DRAW);

	GLfloat cube_texcoords[] = {
		// front
		0.0, 0.0,
		1.0, 0.0,
		1.0, 1.0,
		0.0, 1.0,

		// back
		1.0, 0.0,
		1.0, 1.0,
		0.0, 1.0,
		0.0, 0.0,

	};

	//for (int i = 1; i < 6; i++)
		//memcpy(&cube_texcoords[i * 4 * 2], &cube_texcoords[i-1], 2 * 4 * sizeof(GLfloat));
	glGenBuffers(1, &vbo_cube_texcoords);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_texcoords);
	glBufferData(GL_ARRAY_BUFFER, sizeof(cube_texcoords), cube_texcoords, GL_STATIC_DRAW);

	GLfloat cube_colors[] = {
		// front colors
		1.0, 0.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 0.0, 1.0,
		1.0, 1.0, 1.0,
		// back colors
		1.0, 0.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 0.0, 1.0,
		1.0, 1.0, 1.0,
	};
	glGenBuffers(1, &vbo_cube_colors);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_colors);
	glBufferData(GL_ARRAY_BUFFER, sizeof(cube_colors), cube_colors, GL_STATIC_DRAW);

	GLfloat cube_normals[] = {
		0.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, -1.0f,
		0.0f, -1.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
	};
	glGenBuffers(1, &vbo_cube_normals);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_normals);
	glBufferData(GL_ARRAY_BUFFER, sizeof(cube_normals), cube_normals, GL_STATIC_DRAW);

	GLushort cube_elements[] = {
		// front
		0, 1, 2,
		2, 3, 0,
		// top
		1, 5, 6,
		6, 2, 1,
		// back
		7, 6, 5,
		5, 4, 7,
		// bottom
		4, 0, 3,
		3, 7, 4,
		// left
		4, 5, 1,
		1, 0, 4,
		// right
		3, 2, 6,
		6, 7, 3,
	};
	glGenBuffers(1, &ibo_cube_elements);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo_cube_elements);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_elements), cube_elements, GL_STATIC_DRAW);


	glGenVertexArrays(1, &vaoObject);
	glBindVertexArray(vaoObject);
	glEnableVertexAttribArray(0);
	// Describe our vertices array to OpenGL (it can't guess its format automatically)
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_vertices);
	glVertexAttribPointer(
		0, // attribute
		3,                 // number of elements per vertex, here (x,y,z)
		GL_FLOAT,          // the type of each element
		GL_FALSE,          // take our values as-is
		0,                 // no extra data between each position
		0                  // offset of first element
		);

	glEnableVertexAttribArray(1);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_colors);
	glVertexAttribPointer(
		1, // attribute
		3,                 // number of elements per vertex, here (R,G,B)
		GL_FLOAT,          // the type of each element
		GL_FALSE,          // take our values as-is
		0,                 // no extra data between each position
		0                  // offset of first element
		);

	//Just like VBOs and VAOs, textures are objects that need to be generated first by calling a function. 

	//glGenTextures(1, &tex);
	//textures have to be bound to apply operations to them.
	//glBindTexture(GL_TEXTURE_2D, tex);
	float pixels[] = {
		0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f
	};
	//glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_FLOAT, pixels);
	//glBindVertexArray(0);
	glEnableVertexAttribArray(2);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_texcoords);
	glVertexAttribPointer(
		2, // attribute
		2,                  // number of elements per vertex, here (x,y)
		GL_FLOAT,           // the type of each element
		GL_TRUE,           // take our values as-is
		0,                  // no extra data between each position
		0                   // offset of first element
		);

	glEnableVertexAttribArray(3);
	glBindBuffer(GL_ARRAY_BUFFER, vbo_cube_normals);
	glVertexAttribPointer(
		3, // attribute
		3,                  // number of elements per vertex, here (x,y)
		GL_FLOAT,           // the type of each element
		GL_TRUE,           // take our values as-is
		0,                  // no extra data between each position
		0                   // offset of first element
		);
	
	/*glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);
	glDisableVertexAttribArray(2);
	glDisableVertexAttribArray(3);*/
	//glBindVertexArray(0);

}

Texture* Mesh::LoadTexture() {
	Texture* gTexture = NULL;
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile("wooden-crate.jpg");
	bmp.flipVertically();
	gTexture = new Texture(bmp, false);
	return gTexture;
}

void Mesh::InitializeDefaultPlane()
{
}

void Mesh::SetTranslation(float x, float y, float z)
{
	translation_matrix = vmath::translate(x, y, z);
}

void Mesh::SetPosition(float x, float y, float z)
{
	translation_matrix = vmath::translate(x, y, z);
	mesh_posn = vmath::vec3(x, y, z);
	model_matrix = vmath::mat4::identity();
	model_matrix *= translation_matrix;
	model_matrix *= rotation_matrix;
	model_matrix *= scale_matrix;
}

void Mesh::SetPosition(vmath::vec3 mesh_posn)
{
	SetPosition(mesh_posn[0], mesh_posn[1], mesh_posn[2]);
	//translation_matrix = vmath::translate(x, y, z);
}

void Mesh::SetRotation(float angle, float x, float y, float z)
{
	rotation_matrix = vmath::rotate(angle, x, y, z);
}

void Mesh::SetScale(float i_scale)
{
	scale_matrix = vmath::scale(i_scale);
}

void Mesh::SetScale(float scaleX, float scaleY, float scaleZ)
{
	scale_matrix = vmath::scale(scaleX, scaleY, scaleZ);
}

void Mesh::SetShaderProgram(GLuint program)
{
	shader_program = program;
	glUseProgram(shader_program);
	shader_Model_location = glGetUniformLocation(program, "model");
	model_matrix = vmath::mat4::identity();
	model_matrix *= translation_matrix;
	model_matrix *= rotation_matrix;
	model_matrix *= scale_matrix;
}

void Mesh::SetTexture(GLenum target, GLuint texture)
{
	current_target = target;
	current_tex = texture;
}

void Mesh::SetTexture(GLenum target1, GLuint texture1, GLenum target2, GLuint texture2)
{
	current_target = target1;
	current_tex = texture1;
	current_target2 = target2;
	current_tex2 = texture2;
	blending_ = true;
	glBindTexture(current_target, current_tex);
}

void Mesh::SetTextureMipmap(GLuint texture)
{
	current_tex_mipMap = texture;
}

void Mesh::Move(float x, float y, float z)
{
	vmath::mat4 i_translation_matrix = vmath::translate(x, y, z);
	mesh_posn = vmath::vec3(x, y, z);
	model_matrix *= i_translation_matrix;
}

void Mesh::Render()
{
	/*glUseProgram(shader_program);
	if (!blending_)
		if (!mipmapOn_)
			glBindTexture(current_target, current_tex);
		else
			glBindTexture(current_target, current_tex_mipMap);
	else
	{
		int tex_a_location = glGetUniformLocation(shader_program, "tex0");
		int tex_b_location = glGetUniformLocation(shader_program, "tex1");
		glUniform1i(tex_a_location, 0);
		glUniform1i(tex_b_location, 1);
		glActiveTextureARB(GL_TEXTURE0);
		glBindTexture(current_target, current_tex);
		glActiveTextureARB(GL_TEXTURE1);
		glBindTexture(current_target2, current_tex2);
	}
	glUniformMatrix4fv(shader_Model_location, 1, GL_FALSE, model_matrix);*/
	//glUseProgram(shader_program);
	//glBindTexture(current_target, current_tex);
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, current_tex);
	glBindVertexArray(vaoObject);
	glDrawArrays(GL_TRIANGLES, 0, vertex_count);
}

/* load a mesh using the assimp library */
bool Mesh::load_mesh(const char* file_name, GLuint* vao, int* point_count) {
	const aiScene* scene = aiImportFile(file_name, aiProcess_Triangulate);
	if (!scene) {
		fprintf(stderr, "ERROR: reading mesh %s\n", file_name);
		return false;
	}
	printf("  %i animations\n", scene->mNumAnimations);
	printf("  %i cameras\n", scene->mNumCameras);
	printf("  %i lights\n", scene->mNumLights);
	printf("  %i materials\n", scene->mNumMaterials);
	printf("  %i meshes\n", scene->mNumMeshes);
	printf("  %i textures\n", scene->mNumTextures);

	/* get first mesh in file only */
	const aiMesh* mesh = scene->mMeshes[0];
	printf("    %i vertices in mesh[0]\n", mesh->mNumVertices);

	/* pass back number of vertex points in mesh */
	*point_count = mesh->mNumVertices;

	/* generate a VAO, using the pass-by-reference parameter that we give to the
	function */
	glGenVertexArrays(1, vao);
	glBindVertexArray(*vao);

	/* we really need to copy out all the data from AssImp's funny little data
	structures into pure contiguous arrays before we copy it into data buffers
	because assimp's texture coordinates are not really contiguous in memory.
	i allocate some dynamic memory to do this. */
	GLfloat* points = NULL; // array of vertex points
	GLfloat* normals = NULL; // array of vertex normals
	GLfloat* texcoords = NULL; // array of texture coordinates
	GLfloat* colors = NULL; //array of colors
	if (mesh->HasPositions()) {
		points = (GLfloat*)malloc(*point_count * 3 * sizeof (GLfloat));
		for (int i = 0; i < *point_count; i++) {
			const aiVector3D* vp = &(mesh->mVertices[i]);
			points[i * 3] = (GLfloat)vp->x;
			points[i * 3 + 1] = (GLfloat)vp->y;
			points[i * 3 + 2] = (GLfloat)vp->z;
		}
	}
	colors = (GLfloat*)malloc(*point_count * 3 * sizeof (GLfloat));
	{
		for (int i = 0; i < *point_count; i++) {
			const aiVector3D* vp = &(mesh->mVertices[i]);
			colors[i * 3] = (GLfloat)200.0f;
			colors[i * 3 + 1] = (GLfloat)200.0f;
			colors[i * 3 + 2] = (GLfloat)200.0f;
		}
	}
	if (mesh->HasNormals()) {
		normals = (GLfloat*)malloc(*point_count * 3 * sizeof (GLfloat));
		for (int i = 0; i < *point_count; i++) {
			const aiVector3D* vn = &(mesh->mNormals[i]);
			normals[i * 3] = (GLfloat)vn->x;
			normals[i * 3 + 1] = (GLfloat)vn->y;
			normals[i * 3 + 2] = (GLfloat)vn->z;
		}
	}
	if (mesh->HasTextureCoords(0)) {
		texcoords = (GLfloat*)malloc(*point_count * 2 * sizeof (GLfloat));
		for (int i = 0; i < *point_count; i++) {
			const aiVector3D* vt = &(mesh->mTextureCoords[0][i]);
			texcoords[i * 2] = (GLfloat)vt->x;
			texcoords[i * 2 + 1] = (GLfloat)vt->y;
		}
	}

	/* copy mesh data into VBOs */
	if (mesh->HasPositions()) {
		GLuint vbo;
		glGenBuffers(1, &vbo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(
			GL_ARRAY_BUFFER,
			3 * *point_count * sizeof (GLfloat),
			points,
			GL_STATIC_DRAW
			);
		glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		glEnableVertexAttribArray(0);
		free(points);
	}
	if (mesh->HasNormals()) {
		GLuint vbo;
		glGenBuffers(1, &vbo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(
			GL_ARRAY_BUFFER,
			3 * *point_count * sizeof (GLfloat),
			normals,
			GL_STATIC_DRAW
			);
		glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		glEnableVertexAttribArray(1);
		free(normals);
	}
	if (mesh->HasTextureCoords(0)) {
		GLuint vbo;
		glGenBuffers(1, &vbo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(
			GL_ARRAY_BUFFER,
			2 * *point_count * sizeof (GLfloat),
			texcoords,
			GL_STATIC_DRAW
			);
		glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 0, NULL);
		glEnableVertexAttribArray(2);
		free(texcoords);
	}

	if (mesh->HasTangentsAndBitangents()) {
		// NB: could store/print tangents here
	}

	aiReleaseImport(scene);
	printf("mesh loaded\n");

	return true;
}

Mesh::~Mesh()
{
}
