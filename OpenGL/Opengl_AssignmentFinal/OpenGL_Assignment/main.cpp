#include "gl_utils.h"
#include "maths_funcs.h"
#include "extern\glew\include\GL\glew.h"
#include "extern\glfw\include\GLFW\glfw3.h"
#include "extern\soil\src\SOIL.h"

#include "extern\assimp\cimport.h" // C importer
#include "extern\assimp\scene.h" // collects data
#include "extern\assimp\postprocess.h" // various extra operations

#include <cstdlib>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <fstream>
#include <iostream>
#include <assert.h>
#include <vector>
#include <windows.h>

#include "Texture.h";
#include "Bitmap.h"
#include "Shader.h"

#include "vmath.h"
#include "arcball.h"
#include "object.h"

#include "Mesh.h"

#define GL_LOG_FILE "gl.log"
#define VERTEX_SHADER_FILE "shader/simple_texture_vs.glsl"
#define FRAGMENT_SHADER_FILE "shader/simple_texture_fs.glsl"
#define CUBE_VERT_FILE "shader/cube_vs.glsl"
#define CUBE_FRAG_FILE "shader/cube_fs.glsl"
#define MONKEY_VERT_FILE "shader/reflect_vs.glsl"
#define MONKEY_FRAG_FILE "shader/reflect_fs.glsl"
#define MESH_FILE_SPHERE "sphere.obj"
#define MESH_FILE_CUBE "cube_texture.obj"

#define FRONT "negz.jpg"
#define BACK "posz.jpg"
#define TOP "posy.jpg"
#define BOTTOM "negy.jpg"
#define LEFT "negx.jpg"
#define RIGHT "posx.jpg"


static void error_callback(int error, const char* description)
{
	fputs(description, stderr);
}
GLuint CreateProgram2();
static bool changeprogram_;
sb6::utils::arcball arcball;
sb6::object     object;
static bool mouseDown_;
float eyeZPosn;
float eyeXPosn;

int width = 0;
int height = 0;
short BitsPerPixel = 0;
std::vector<unsigned char> Pixels;
Texture* gTexture = NULL;

GLuint program;
GLuint texture_id;
GLint attribute_coord3d, attribute_v_color, attribute_texcoord;
GLint uniform_mvp, uniform_mytexture, uniform_pMatrix;
unsigned char* image;
//int width, height;
Mesh *cube_pic, *sphere_blend, *teapot;
Mesh *floor_mesh;
std::vector<Mesh *> *mesh_array;
std::vector<Shader *> *shader_array;
// keep track of window size for things like the viewport and the mouse cursor
int g_gl_width = 640;
int g_gl_height = 480;
GLFWwindow* g_window = NULL;
float transperency_value = 0.5f;
bool mipmapOn_ = true;


static void getMousePosition(GLFWwindow* window, double& x, double& y)
{
	glfwGetCursorPos(window, &x, &y);
}

vmath::vec3 point_light_posn(0.0f, 2.0f, 12.0f);
vmath::vec3 dirn_light_vec(10.0, 2.0, 3.0);
vmath::vec3 *currentLightSource;
bool projectiveshadow_stencil;
bool lightMovement_;
void create_shadow_caster();
bool isToonShader_;
bool isExplode_;
bool isBaloon_;

int type = 0;
float time = 0;
float break_i = 0.1;
float sign_break = 0.1f;
static void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods)
{
	if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
		glfwSetWindowShouldClose(window, GL_TRUE);
	if (key == GLFW_KEY_F)
	{
		changeprogram_ = true;
	}
	else if (key == GLFW_KEY_G)
	{
		changeprogram_ = false;
	}

	if (key == GLFW_KEY_UP)
	{
		eyeZPosn += 0.1f;
	}
	else if (key == GLFW_KEY_DOWN)
	{
		eyeZPosn -= 0.1f;
	}
	if (key == GLFW_KEY_LEFT)
	{
		//printf("Left \n");
		eyeXPosn += 0.1f;
	}
	else if (key == GLFW_KEY_RIGHT)
	{
		eyeXPosn -= 0.1f;
	}

	if (key == GLFW_KEY_D)
	{
		cube_pic->Move(0.05f/2.0f, 0.0f, 0.0f);
		teapot->Move(0.05f / 1.5f, 0.0f, 0.0f);
	}
	if (key == GLFW_KEY_A)
	{
		cube_pic->Move(-0.05f / 2.0f, 0.0f, 0.0f);
		teapot->Move(-0.05f / 1.5f, 0.0f, 0.0f);
	}
	if (key == GLFW_KEY_W)
	{
		cube_pic->Move(0.0f, 0.0f, +0.05f / 2.0f);
		teapot->Move(0.0f, 0.0f, +0.05f / 1.5f);
	}
	if (key == GLFW_KEY_S)
	{
		cube_pic->Move(0.0f, 0.0f, -0.05f / 2.0f);
		teapot->Move(0.0f, 0.0f, -0.05f / 1.5f);
	}

	if (key == GLFW_KEY_T)
	{
		if (transperency_value > 1.0f)
			transperency_value = 0.0f;
		transperency_value += 0.01f;
	}

	if (key == GLFW_KEY_1)
	{
		type = 1;
		time = 0;
	}
	if (key == GLFW_KEY_2)
	{
		type = 2;
		time = 0;
	}
	if (key == GLFW_KEY_3)
	{
		type = 3;
		time = 0;
	}
	if (key == GLFW_KEY_0)
	{
		type = 0;
		time = 0;
	}

	if (key == GLFW_KEY_J)
	{
		(*currentLightSource)[0] += 0.3f;
		//printf("Lightx %f\n", light_posn_x);
		create_shadow_caster();
	}
	else if (key == GLFW_KEY_L)
	{
		(*currentLightSource)[0] -= 0.3f;
		//printf("Lightx %f\n", light_posn_x);
		create_shadow_caster();
	}

	if (key == GLFW_KEY_I)
	{
		(*currentLightSource)[1] += 0.3f;
		//printf("Lighty %f\n", light_posn_y);
		create_shadow_caster();
	}
	else if (key == GLFW_KEY_K)
	{
		(*currentLightSource)[1] -= 0.3f;
		//printf("Lighty %f\n", light_posn_y);
		create_shadow_caster();
	}

	if (key == GLFW_KEY_U)
	{
		(*currentLightSource)[2] += 0.3f;
		//printf("Lightz %f\n", light_posn_z);
		create_shadow_caster();
	}
	else if (key == GLFW_KEY_O)
	{
		(*currentLightSource)[2] -= 0.3f;
		//printf("Lightz %f\n", light_posn_z);
		create_shadow_caster();
	}

	if (key == GLFW_KEY_M && action == GLFW_PRESS)
	{
		//mipmapOn_ = !mipmapOn_;
		//floor_mesh->mipmapOn_ = !floor_mesh->mipmapOn_;
		if (cube_pic->receive_shadow)
		{
			cube_pic->receive_shadow = false;
			teapot->receive_shadow = false;
		}
		else
		{
			cube_pic->receive_shadow = true;
			teapot->receive_shadow = true;
		}
	}

	if (key == GLFW_KEY_N && action == GLFW_PRESS)
	{
		//mipmapOn_ = !mipmapOn_;
		if (currentLightSource == &dirn_light_vec)
			currentLightSource = &point_light_posn;
		else
			currentLightSource = &dirn_light_vec;
	}
	if (key == GLFW_KEY_B && action == GLFW_PRESS)
	{
		//projectiveshadow_stencil = !projectiveshadow_stencil;
		isBaloon_ = !isBaloon_;
	}
	if (key == GLFW_KEY_T && action == GLFW_PRESS)
	{
		isExplode_ = !isExplode_;
	}
	if (key == GLFW_KEY_Y && action == GLFW_PRESS)
	{
		break_i += sign_break;
		if (break_i >= 0.9f)
		{
			break_i = -0.1f;
			sign_break = -0.1f;
		}
		time = 0.0f;
		printf("breaki %f\n", break_i);
	}
}

// Variables for mouse position to solve the arcball vectors
int iPrevMouseX = 0;
int iPrevMouseY = 0;
int iCurMouseX = 0;
int iCurMouseY = 0;
bool bRotate = false;
// Variables for mouse interaction
bool bPerVertex;
bool bShiftPressed = false;
bool bZoom = false;
//bool bRotate = false;
bool bPan = false;

static void sMouseButton(GLFWwindow* mainWindow, int button, int action, int mods)
{
	double xd, yd;
	glfwGetCursorPos(mainWindow, &xd, &yd);

	if (button == GLFW_MOUSE_BUTTON_1)
	{
		if (action == GLFW_PRESS)
		{
			//printf("Press mouse");
			bRotate = true;
			iPrevMouseX = iCurMouseX = (int) xd;
			iPrevMouseY = iCurMouseY = (int) yd;
		}
		if (action == GLFW_RELEASE)
		{
			bRotate = false;
		}
	}
}

static void sMouseMotion(GLFWwindow*, double x, double y)
{
	if (bRotate)
	{
		//printf("Move mouse");
		iCurMouseX = (int) x;
		iCurMouseY = (int) y;
	}

}

vmath::vec3 getArcballVector(int x, int y) {
	// find vector from origin to point on sphere
	vmath::vec3 vecP = vmath::vec3(1.0f*x / g_gl_width * 2 - 1.0f, 1.0f*y / g_gl_height * 2 - 1.0f, 0.0f);
	// inverse y due to difference in origin location on the screen
	vecP[1] = -vecP[1];
	float vecPsquared = vecP[0] * vecP[0] + vecP[1] * vecP[1];
	// solve for vector z component
	if (vecPsquared <= 1)
		vecP[2] = sqrt(1 - vecPsquared);
	else
		vecP = vmath::normalize(vecP);
	return vecP;
}

GLFWwindow* SetupGLFWWindow(int width, int height)
{
	GLFWwindow* window;
	glfwSetErrorCallback(error_callback);
	//Initialize glfw
	if (!glfwInit())
		exit(EXIT_FAILURE);
	//Create Window
	window = glfwCreateWindow(640, 480, "Swapp's Window", NULL, NULL);
	if (!window)
	{
		glfwTerminate();
		exit(EXIT_FAILURE);
	}
	//Set context of OpenGL to this window.
	glfwMakeContextCurrent(window);
	//??
	glfwSwapInterval(1);
	//Setup keyboard callback. All the keyboard interaction in this function.
	glfwSetKeyCallback(window, key_callback);
	glfwSetMouseButtonCallback(window, sMouseButton);
	glfwSetCursorPosCallback(window, sMouseMotion);
	return window;
}

// loads the file "wooden-crate.jpg" into gTexture
Texture* LoadTexture() {
	Texture* gTexture = NULL;
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile("wooden-crate.jpg");
	bmp.flipVertically();
	gTexture = new Texture(bmp, false);
	return gTexture;
}

Texture* LoadTexture(const char *fileName) {
	Texture* gTexture = NULL;
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile(fileName);
	bmp.flipVertically();
	gTexture = new Texture(bmp, false);
	return gTexture;
}

Texture* LoadTexture(const char *fileName, GLint wrapMode) {
	Texture* gTexture = NULL;
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile(fileName);
	bmp.flipVertically();
	gTexture = new Texture(bmp, false, GL_LINEAR, wrapMode);
	return gTexture;
}

Texture* LoadTexture(const char *fileName, GLint minMagFiler, GLint wrapMode) {
	Texture* gTexture = NULL;
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile(fileName);
	bmp.flipVertically();
	gTexture = new Texture(bmp, true, minMagFiler, wrapMode);
	return gTexture;
}

bool load_mesh(const char* file_name, GLuint* vao, int* point_count) {
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
	if (mesh->HasPositions()) {
		points = (GLfloat*)malloc(*point_count * 3 * sizeof (GLfloat));
		for (int i = 0; i < *point_count; i++) {
			const aiVector3D* vp = &(mesh->mVertices[i]);
			points[i * 3] = (GLfloat)vp->x;
			points[i * 3 + 1] = (GLfloat)vp->y;
			points[i * 3 + 2] = (GLfloat)vp->z;
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

GLuint CreateCubeMesh(GLuint shader_programme)
{
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

	GLuint vbo_cube_vertices, vbo_cube_colors;
	GLuint ibo_cube_elements;

	GLushort cube_elements[] = {
		// front
		0, 1, 2,
		2, 3, 0,
		// top
		3, 2, 6,
		6, 7, 3,
		// back
		7, 6, 5,
		5, 4, 7,
		// bottom
		4, 5, 1,
		1, 0, 4,
		// left
		4, 0, 3,
		3, 7, 4,
		// right
		1, 5, 6,
		6, 2, 1,
	};

	glGenBuffers(1, &ibo_cube_elements);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo_cube_elements);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_elements), cube_elements, GL_STATIC_DRAW);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


	// Make a cube out of triangles (two triangles per side)
	GLfloat vertexData[] = {
		//  X     Y     Z       U     V          Normal      
		// bottom
		-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f,
		0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
		-0.5f, -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,
		0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
		0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f,
		-0.5f, -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,

		// top
		-0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		-0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
		0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		-0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
		0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,

		// front
		-0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		-0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
		0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
		-0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

		// back
		-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
		-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f,
		0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
		0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
		-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f,
		0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f,

		// left
		-0.5f, -0.5f, 0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
		-0.5f, 0.5f, -0.5f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
		-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
		-0.5f, -0.5f, 0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
		-0.5f, 0.5f, 0.5f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
		-0.5f, 0.5f, -0.5f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,

		// right
		0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
		0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
		0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f
	};


	// make and bind the VBO Buffer
	GLuint vbo = 0;
	glGenBuffers(1, &vbo);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);

	glBufferData(GL_ARRAY_BUFFER,
		sizeof(vertexData),
		vertexData,
		GL_STATIC_DRAW);

	//make and bind the VAO Vertex Array
	GLuint vao = 0;
	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);

	//GLint vertex_attrib = glGetAttribLocation(shader_programme, "position");
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), NULL);

	//GLint tex_coor_attrib = glGetAttribLocation(shader_programme, "vertTexCoord");
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(1, 2, GL_FLOAT, GL_TRUE, 8 * sizeof(GLfloat), (const GLvoid*)(3 * sizeof(GLfloat)));

	//GLint normals_attrib = glGetAttribLocation(shader_programme, "vertNormal");
	glEnableVertexAttribArray(2);
	glVertexAttribPointer(2, 3, GL_FLOAT, GL_TRUE, 8 * sizeof(GLfloat), (const GLvoid*)(5 * sizeof(GLfloat)));
	return vao;
}

vmath::mat4 CreateProjectionMatrix(float aspect_ratio)
{
	vmath::mat4 proj_matrix = vmath::perspective(67.0f, aspect_ratio, 0.1f, 1000.0f);
	return proj_matrix;
}

vmath::mat4 SetCamera(float eyeXPosn, float eyeZPosn)
{
	vmath::vec3 eye = vmath::vec3(0.0f, 0.0f, 12.0f);
	vmath::vec3 at = vmath::vec3(0.0f, 0.0f, 0.0f);
	vmath::vec3 up = vmath::vec3(0.0f, 1.0f, 0.0f);
	vmath::mat4 mv_matrix = vmath::lookat(eye, at, up);
	return mv_matrix;
}

static GLenum TextureFormatForBitmapFormat(tdogl::Bitmap::Format format)
{
	switch (format) {
	case tdogl::Bitmap::Format_Grayscale: return GL_LUMINANCE;
	case tdogl::Bitmap::Format_GrayscaleAlpha: return GL_LUMINANCE_ALPHA;
	case tdogl::Bitmap::Format_RGB: return GL_RGB;
	case tdogl::Bitmap::Format_RGBA: return GL_RGBA;
	default: throw std::runtime_error("Unrecognised Bitmap::Format");
	}
}

GLuint make_big_cube(float value) {
	//float value = 100.0f;
	float points[] = {
		-value, value, -value,
		-value, -value, -value,
		value, -value, -value,
		value, -value, -value,
		value, value, -value,
		-value, value, -value,

		-value, -value, value,
		-value, -value, -value,
		-value, value, -value,
		-value, value, -value,
		-value, value, value,
		-value, -value, value,

		value, -value, -value,
		value, -value, value,
		value, value, value,
		value, value, value,
		value, value, -value,
		value, -value, -value,

		-value, -value, value,
		-value, value, value,
		value, value, value,
		value, value, value,
		value, -value, value,
		-value, -value, value,

		-value, value, -value,
		value, value, -value,
		value, value, value,
		value, value, value,
		-value, value, value,
		-value, value, -value,

		-value, -value, -value,
		-value, -value, value,
		value, -value, -value,
		value, -value, -value,
		-value, -value, value,
		value, -value, value
	};
	GLuint vbo;
	glGenBuffers(1, &vbo);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glBufferData(
		GL_ARRAY_BUFFER, 3 * 36 * sizeof (GLfloat), &points, GL_STATIC_DRAW);

	GLuint vao;
	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);
	glEnableVertexAttribArray(0);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
	return vao;
}

bool load_cube_map_side(
	GLuint texture, GLenum side_target, const char* file_name
	)
{
	glBindTexture(GL_TEXTURE_CUBE_MAP, texture); //??GL_TEXTURE_CUBE_MAP

	//Texture* gTexture = LoadTexture(file_name);
	tdogl::Bitmap bmp = tdogl::Bitmap::bitmapFromFile(file_name);
	// copy image data into 'target' side of cube map
	glTexImage2D(side_target,
		0,
		TextureFormatForBitmapFormat(bmp.format()),
		(GLsizei)bmp.width(),
		(GLsizei)bmp.height(),
		0,
		TextureFormatForBitmapFormat(bmp.format()),
		GL_UNSIGNED_BYTE,
		bmp.pixelBuffer());
	//GLuint tex = gTexture->object();
	//glBindTexture(GL_TEXTURE_2D, 0);
	return true;
}

void create_cube_map(
	const char* front,
	const char* back,
	const char* top,
	const char* bottom,
	const char* left,
	const char* right,
	GLuint* tex_cube
	)
{
	// generate a cube-map texture to hold all the sides
	glActiveTexture(GL_TEXTURE0);
	glGenTextures(1, tex_cube);

	// load each image and copy into a side of the cube-map texture
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, front));
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, back));
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, top));
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, bottom));
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, left));
	assert(load_cube_map_side(*tex_cube, GL_TEXTURE_CUBE_MAP_POSITIVE_X, right));

	// format cube map texture
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
}


GLuint CreateTriangle()
{
	float points[] = {
		0.0f, 0.5f, 0.0f,
		0.5f, -0.5f, 0.0f,
		-0.5f, -0.5f, 0.0f
	};

	GLuint vbo = 0;
	glGenBuffers(1, &vbo);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glBufferData(GL_ARRAY_BUFFER, 9 * sizeof (float), points, GL_STATIC_DRAW);

	GLuint vao = 0;
	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);
	glEnableVertexAttribArray(0);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
	return vao;
}

// camera matrices. it's easier if they are global
mat4 view_mat;
mat4 proj_mat;
vec3 cam_pos(0.0f, 0.0f, 5.0f);

GLuint create_plane(float size, float texture_c)
{
	//float size = 12.0f;
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
	glBindBuffer(GL_ARRAY_BUFFER, points_vbo);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
	glBindBuffer(GL_ARRAY_BUFFER, texcoords_vbo);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 0, NULL);
	glEnableVertexAttribArray(0);
	glEnableVertexAttribArray(2);
	return vao;
}

bool load_obj_file(
	const char* file_name,
	float*& points,
	float*& tex_coords,
	float*& normals,
	int& point_count
	) {

	float* unsorted_vp_array = NULL;
	float* unsorted_vt_array = NULL;
	float* unsorted_vn_array = NULL;
	int current_unsorted_vp = 0;
	int current_unsorted_vt = 0;
	int current_unsorted_vn = 0;

	FILE* fp = fopen(file_name, "r");
	if (!fp) {
		fprintf(stderr, "ERROR: could not find file %s\n", file_name);
		return false;
	}

	// first count points in file so we know how much mem to allocate
	point_count = 0;
	int unsorted_vp_count = 0;
	int unsorted_vt_count = 0;
	int unsorted_vn_count = 0;
	int face_count = 0;
	char line[1024];
	while (fgets(line, 1024, fp)) {
		if (line[0] == 'v') {
			if (line[1] == ' ') {
				unsorted_vp_count++;
			}
			else if (line[1] == 't') {
				unsorted_vt_count++;
			}
			else if (line[1] == 'n') {
				unsorted_vn_count++;
			}
		}
		else if (line[0] == 'f') {
			face_count++;
		}
	}
	printf(
		"found %i vp %i vt %i vn unique in obj. allocating memory...\n",
		unsorted_vp_count, unsorted_vt_count, unsorted_vn_count
		);
	unsorted_vp_array = (float*)malloc(unsorted_vp_count * 3 * sizeof (float));
	unsorted_vt_array = (float*)malloc(unsorted_vt_count * 2 * sizeof (float));
	unsorted_vn_array = (float*)malloc(unsorted_vn_count * 3 * sizeof (float));
	points = (float*)malloc(3 * face_count * 3 * sizeof (float));
	tex_coords = (float*)malloc(3 * face_count * 2 * sizeof (float));
	normals = (float*)malloc(3 * face_count * 3 * sizeof (float));
	printf(
		"allocated %i bytes for mesh\n",
		(int)(3 * face_count * 8 * sizeof (float))
		);

	rewind(fp);
	while (fgets(line, 1024, fp)) {
		// vertex
		if (line[0] == 'v') {

			// vertex point
			if (line[1] == ' ') {
				float x, y, z;
				x = y = z = 0.0f;
				sscanf(line, "v %f %f %f", &x, &y, &z);
				unsorted_vp_array[current_unsorted_vp * 3] = x;
				unsorted_vp_array[current_unsorted_vp * 3 + 1] = y;
				unsorted_vp_array[current_unsorted_vp * 3 + 2] = z;
				current_unsorted_vp++;

				// vertex texture coordinate
			}
			else if (line[1] == 't') {
				float s, t;
				s = t = 0.0f;
				sscanf(line, "vt %f %f", &s, &t);
				unsorted_vt_array[current_unsorted_vt * 2] = s;
				unsorted_vt_array[current_unsorted_vt * 2 + 1] = t;
				current_unsorted_vt++;

				// vertex normal
			}
			else if (line[1] == 'n') {
				float x, y, z;
				x = y = z = 0.0f;
				sscanf(line, "vn %f %f %f", &x, &y, &z);
				unsorted_vn_array[current_unsorted_vn * 3] = x;
				unsorted_vn_array[current_unsorted_vn * 3 + 1] = y;
				unsorted_vn_array[current_unsorted_vn * 3 + 2] = z;
				current_unsorted_vn++;
			}

			// faces
		}
		else if (line[0] == 'f') {
			// work out if using quads instead of triangles and print a warning
			int slashCount = 0;
			int len = strlen(line);
			for (int i = 0; i < len; i++) {
				if (line[i] == '/') {
					slashCount++;
				}
			}
			if (slashCount != 6) {
				fprintf(
					stderr,
					"ERROR: file contains quads or does not match v vp/vt/vn layout - \
															make sure exported mesh is triangulated and contains vertex points, \
																														texture coordinates, and normals\n"
																														);
				return false;
			}

			int vp[3], vt[3], vn[3];
			sscanf(
				line,
				"f %i/%i/%i %i/%i/%i %i/%i/%i",
				&vp[0], &vt[0], &vn[0], &vp[1], &vt[1], &vn[1], &vp[2], &vt[2], &vn[2]
				);

			/* start reading points into a buffer. order is -1 because obj starts from
			1, not 0 */
			// NB: assuming all indices are valid
			for (int i = 0; i < 3; i++) {
				if ((vp[i] - 1 < 0) || (vp[i] - 1 >= unsorted_vp_count)) {
					fprintf(stderr, "ERROR: invalid vertex position index in face\n");
					return false;
				}
				if ((vt[i] - 1 < 0) || (vt[i] - 1 >= unsorted_vt_count)) {
					fprintf(stderr, "ERROR: invalid texture coord index %i in face.\n", vt[i]);
					return false;
				}
				if ((vn[i] - 1 < 0) || (vn[i] - 1 >= unsorted_vn_count)) {
					printf("ERROR: invalid vertex normal index in face\n");
					return false;
				}
				points[point_count * 3] = unsorted_vp_array[(vp[i] - 1) * 3];
				points[point_count * 3 + 1] = unsorted_vp_array[(vp[i] - 1) * 3 + 1];
				points[point_count * 3 + 2] = unsorted_vp_array[(vp[i] - 1) * 3 + 2];
				tex_coords[point_count * 2] = unsorted_vt_array[(vt[i] - 1) * 2];
				tex_coords[point_count * 2 + 1] = unsorted_vt_array[(vt[i] - 1) * 2 + 1];
				normals[point_count * 3] = unsorted_vn_array[(vn[i] - 1) * 3];
				normals[point_count * 3 + 1] = unsorted_vn_array[(vn[i] - 1) * 3 + 1];
				normals[point_count * 3 + 2] = unsorted_vn_array[(vn[i] - 1) * 3 + 2];
				point_count++;
			}
		}
	}
	fclose(fp);
	free(unsorted_vp_array);
	free(unsorted_vn_array);
	free(unsorted_vt_array);
	printf(
		"allocated %i points\n",
		point_count
		);
	return true;
}


mat4 g_sphere_Ms[4];
// a world position for each sphere in the scene
vec3 sphere_pos_wor[] = {
	vec3(-2.0, 0.0, 0.0),
	vec3(2.0, 0.0, 0.0),
	vec3(-2.0, 0.0, -2.0),
	vec3(1.5, 1.0, -1.0)
};
GLuint g_sphere_vao;
int g_sphere_point_count;
void init_spheres() {
	GLfloat* vp = NULL; // array of vertex points
	GLfloat* vn = NULL; // array of vertex normals
	GLfloat* vt = NULL; // array of texture coordinates
	assert(load_obj_file("sphere.obj", vp, vt, vn, g_sphere_point_count));
	/*GLuint g_sphere_vao;
	int g_sphere_point_count = 0;
	assert(load_mesh(MESH_FILE, &g_sphere_vao, &g_sphere_point_count));*/
	glGenVertexArrays(1, &g_sphere_vao);
	glBindVertexArray(g_sphere_vao);

	GLuint points_vbo = 0;
	if (NULL != vp) {
		glGenBuffers(1, &points_vbo);
		glBindBuffer(GL_ARRAY_BUFFER, points_vbo);
		glBufferData(
			GL_ARRAY_BUFFER,
			3 * g_sphere_point_count * sizeof (GLfloat),
			vp,
			GL_STATIC_DRAW
			);
		glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		glEnableVertexAttribArray(0);
	}
}

/*---------------------------------SHADOW MAP FUNC-----------------------------------*/
GLuint m_shadowDepth_fbo;
GLuint m_shadowMap_tex;
void InitializeFrameBuffer()
{
	// create framebuffer
	glEnable(GL_DEPTH_TEST); // enable depth-testing
	//glDepthFunc(GL_LESS); // depth-testing interprets a smaller value as "closer"
	glGenFramebuffers(1, &m_shadowDepth_fbo);
	glBindFramebuffer(GL_FRAMEBUFFER, m_shadowDepth_fbo);
	//create texture to bind the frame buffer
	glGenTextures(1, &m_shadowMap_tex);
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, m_shadowMap_tex);

	glTexImage2D(
		GL_TEXTURE_2D,
		0,
		GL_DEPTH_COMPONENT16,
		2048,
		2048,
		0,
		GL_DEPTH_COMPONENT,
		GL_FLOAT,
		0
		);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	//attach the shadow map texture to the depth attachment point of the FBO. 
	glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, m_shadowMap_tex, 0);

	// tell framebuffer not to use any colour drawing outputs
	GLenum draw_bufs[] = { GL_NONE };
	glDrawBuffers(1, draw_bufs);
	glReadBuffer(GL_NONE);

	// bind default framebuffer again
	glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

GLuint shadowMap_debug_quad_vao;
int shadowMap_debug_quad_point_count;
void create_shadowMap_debug() {
	// create geometry and vao for screen-space quad
	GLfloat ss_quad_pos[] = {
		-1.0, 1.0,
		-1.0, -1.0,
		1.0, 1.0,
		1.0, 1.0,
		-1.0, -1.0,
		1.0, -1.0
	};

	// create VBOs, VAO, and set attribute locations
	shadowMap_debug_quad_point_count = sizeof (ss_quad_pos) / sizeof (GLfloat) / 2;

	/* create VBO and VAO here */
	glGenVertexArrays(1, &shadowMap_debug_quad_vao);
	glBindVertexArray(shadowMap_debug_quad_vao);
	GLuint points_vbo = 0;
	glGenBuffers(1, &points_vbo);
	glBindBuffer(GL_ARRAY_BUFFER, points_vbo);
	glBufferData(
		GL_ARRAY_BUFFER, sizeof (ss_quad_pos), ss_quad_pos, GL_STATIC_DRAW
		);
	glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 0, NULL);
	glEnableVertexAttribArray(0);
}

mat4 g_caster_V;
mat4 g_caster_P;
void create_shadow_caster() {
	// create a view matrix for the shadow caster
	vec3 light_pos(point_light_posn[0], point_light_posn[1], point_light_posn[2]);
	vec3 light_target(0.0, 0.0f, 0.0f);
	vec3 up_dir(normalise(vec3(0.0f, 1.0f, 0.0f)));
	g_caster_V = look_at(light_pos, light_target, up_dir);

	// create a projection matrix for the shadow caster
	float near_1 = 0.1f;
	float far_1 = 1000.0f;
	float fov_1 = 50.0f;
	float aspect = 1.0f;
	//float aspect = (float)640 / (float)480;
	//vmath::mat4 proj_matrix = vmath::perspective(67.0f, aspect, 0.1f, 1000.0f);
	g_caster_P = perspective(fov_1, aspect, near_1, far_1);
}
/*---------------------------------SHADOW MAP FUNC-----------------------------------*/

//float g_LightPosition[4] = { 4.0f, 7.0f, 0.0f, 0.0f };

void CreateShadowMatrix(float m[16], vmath::vec3 vPoint, vmath::vec3 vN, vmath::vec3 l)
{
	// Here we calculate the distance from the equation D = -(Ax + By + Cz)
	float d = -((vN[0] * vPoint[0]) + (vN[1] * vPoint[1]) + (vN[2] * vPoint[2]));

	// Calculate the dot product of the normal and the light
	float dot = vN[0] * l[0] + vN[1] * l[1] + vN[2] * l[2] + d*0.0f;//d*l[3];

	// Now comes the juicy part of converting all of our projection information
	// a matrix.  The equation for the creating the matrix is below:

	/*
	| dot-light.x*Normal.x	   -light.y*Normal.x	 -light.z*Normal.x	   -light.w*Normal.x |
	M = |    -light.x*Normal.y	dot-light.y*Normal.y	 -light.z*Normal.y	   -light.w*Normal.y |
	|	 -light.x*Normal.z	   -light.y*Normal.z  dot-light.z*Normal.z	   -light.w*Normal.z |
	|    -light.x*Distance	   -light.y*Distance	 -light.z*Distance	dot-light.w*Distance |
	*/

	// Follow the matrix equation below to fill in our matrix for the current surface
	m[0] = dot - l[0] * vN[0];  m[1] = -l[1] * vN[0];	m[2] = -l[2] * vN[0];  m[3] = -0.0f * vN[0];
	m[4] = -l[0] * vN[1];  m[5] = dot - l[1] * vN[1];	m[6] = -l[2] * vN[1];  m[7] = -0.0f * vN[1];
	m[8] = -l[0] * vN[2];  m[9] = -l[1] * vN[2];	m[10] = dot - l[2] * vN[2];  m[11] = -0.0f * vN[2];
	m[12] = -l[0] * d;	  m[13] = -l[1] * d;		m[14] = -l[2] * d;	  m[15] = dot - 0.0f * d;
}

int main() {
	assert(restart_gl_log());
	isToonShader_ = true;
	isExplode_ = false;
	isBaloon_ = false;
	lightMovement_ = false;
	projectiveshadow_stencil = true;
	currentLightSource = &point_light_posn;
	const int screen_width = 640;
	const int screen_height = 480;
	//GLFW Initialize
	GLFWwindow* window = SetupGLFWWindow(screen_width, screen_height);
	//Glew Initialize 
	GLenum err = glewInit();

	glEnable(GL_CULL_FACE); // cull face
	glCullFace(GL_BACK); // cull back face
	glFrontFace(GL_CCW); // set counter-clock-wise vertex order to mean the front
	glClearColor(0.2, 0.2, 0.2, 1.0); // grey background to help spot mistakes
	glViewport(0, 0, g_gl_width, g_gl_height);

	/*---------------------------------SHADOW MAP INIT-----------------------------------*/
	//Create Shadow Frame Buffer
	InitializeFrameBuffer();
	create_shadowMap_debug();
	create_shadow_caster();
	/*---------------------------------SHADOW MAP INIT-----------------------------------*/

	mesh_array = new std::vector<Mesh *>();
	shader_array = new std::vector<Shader *>();

	/* load the mesh using assimp */
	/*GLuint sphere_vao;
	int sphere_point_count = 0;
	assert(load_mesh(MESH_FILE_SPHERE, &sphere_vao, &sphere_point_count));

	GLuint cube_vao;
	int cube_point_count = 0;
	assert(load_mesh(MESH_FILE_CUBE, &cube_vao, &cube_point_count));*/

	//GLuint room_cube_vao = make_big_cube(100.0f);

	/*---------------------------------CUBE MAP-----------------------------------*/
	GLuint skybox_cube_vao = make_big_cube(100.0f);
	GLuint cube_map_texture;
	//create_cube_map(FRONT, BACK, TOP, BOTTOM, LEFT, RIGHT, &cube_map_texture);

	/*---------------------------------TEXTURE-----------------------------------*/
	Texture* gTexture = LoadTexture("swap.jpg");
	GLuint tex = gTexture->object();
	Texture* gTextureMarble = LoadTexture("marble.jpg");
	GLuint texSphere = gTextureMarble->object();
	Texture* gTexturegrass = LoadTexture("grass.png");
	GLuint texGrass = gTexturegrass->object();
	Texture* gTextureFloor = LoadTexture("checkerboard.jpg", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT);
	GLuint texFloor = gTextureFloor->object();
	Texture* gTexWoodPlank = LoadTexture("NewPlank.png", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT); 
	GLuint texWoodPlank = gTexWoodPlank->object();
	Texture* gTextureFloorWoMipmap = LoadTexture("checkerboard.jpg", GL_REPEAT);
	GLuint texFloor_wo_mipmap = gTextureFloorWoMipmap->object();
	Texture* gTextureConcreteBox = LoadTexture("concrete.jpg");
	GLuint texConcrete = gTextureConcreteBox->object();

	Texture* gTextureBrick_d = LoadTexture("bricks_d.png", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT);
	GLuint texBrick_d = gTextureBrick_d->object();
	Texture* gTextureBrick_n = LoadTexture("bricks_n.png", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT);
	GLuint texBrick_n = gTextureBrick_n->object();

	Texture* gTextureFloor_d = LoadTexture("sand_d.jpg", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT);
	GLuint texFloor_d = gTextureFloor_d->object();
	Texture* gTextureFloor_n = LoadTexture("sand_n.png", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT);
	GLuint texFloor_n = gTextureFloor_n->object();

	Texture* gblue = LoadTexture("light_blue.jpg", GL_LINEAR_MIPMAP_LINEAR, GL_REPEAT); 
	GLuint texlightBlue = gblue->object();

	Texture* sphere_color = LoadTexture("color_map.jpg");
	GLuint texSphere_d = sphere_color->object();
	Texture* sphere_normal = LoadTexture("normal_map.jpg");
	GLuint texSphere_n = sphere_normal->object();
	
	/*-------------------------------CREATE SHADERS-------------------------------*/
	//Simple Texture Plain
	Shader *texture_simple = new Shader("shader/simple_texture_vs.glsl", "shader/simple_texture_fs.glsl");
	shader_array->push_back(texture_simple);
	GLuint simple_texture_sp = texture_simple->GetShaderProgram();

	Shader *tangent_normal = new Shader("shader/tangent_normal_vs.glsl", "shader/tangent_normal_fs.glsl");
	shader_array->push_back(tangent_normal);
	GLuint tangent_normal_sp = tangent_normal->GetShaderProgram();
	int  g_tangent_normal_L_loc = glGetUniformLocation(tangent_normal_sp, "light_pos");
	int  g_tangent_normal_DL_loc = glGetUniformLocation(tangent_normal_sp, "dirn_light");

	Shader *texture_normal = new Shader("shader/texture_w_normal_vs.glsl", "shader/texture_w_normal_fs.glsl");
	shader_array->push_back(texture_normal);
	GLuint texture_normal_sp = texture_normal->GetShaderProgram();
	int  g_texture_normal_M_loc = glGetUniformLocation(texture_normal_sp, "model");
	int  g_texture_normal_L_loc = glGetUniformLocation(texture_normal_sp, "light_pos");
	int  g_texture_normal_DL_loc = glGetUniformLocation(texture_normal_sp, "dirn_light");
	int  g_texture_normal_caster_V_loc = glGetUniformLocation(texture_normal_sp, "caster_V");
	int  g_texture_normal_caster_P_loc = glGetUniformLocation(texture_normal_sp, "caster_P");

	//Phong Shader per-fragment Point
	Shader *phong_simple = new Shader("shader/phong_pf_vs.glsl", "shader/phong_pf_fs.glsl");
	shader_array->push_back(phong_simple);
	GLuint phong_pf_sp = phong_simple->GetShaderProgram();
	int phong_pf_L_loc = glGetUniformLocation(phong_pf_sp, "light_pos");
	int phong_pf_DL_loc = glGetUniformLocation(phong_pf_sp, "dirn_light");
	//Simple Color Shader
	Shader *color_shadow = new Shader("shader/projective_shadow_vs.glsl", "shader/projective_shadow_fs.glsl");
	shader_array->push_back(color_shadow);
	GLuint shadow_color_sp = phong_simple->GetShaderProgram();

	//Phong shader only direction
	Shader *phong_dirn = new Shader("shader/phong_dir_vs.glsl", "shader/phong_dir_fs.glsl");
	shader_array->push_back(phong_dirn);
	GLuint phong_dir_sp = phong_dirn->GetShaderProgram();

	//Phong Shader per-fragment blending 2 textures
	Shader *phong_blend = new Shader("shader/phong_pfBlend_vs.glsl", "shader/phong_pfBlend_fs.glsl");
	shader_array->push_back(phong_blend);
	GLuint phong_pf_blend_sp = phong_blend->GetShaderProgram();
	int phong_pf_blend_trans_factor = glGetUniformLocation(phong_pf_blend_sp, "transperancy_factor");

	//Skybox shader
	Shader *skybox_cube = new Shader("shader/skybox_cube_vs.glsl", "shader/skybox_cube_fs.glsl");
	shader_array->push_back(skybox_cube);
	GLuint skybox_cube_sp = skybox_cube->GetShaderProgram();

	//Reflect Environment Shader
	Shader *reflect_s = new Shader("shader/reflect_vs_old.glsl", "shader/reflect_fs_old.glsl");
	shader_array->push_back(reflect_s);
	GLuint monkey_sp = reflect_s->GetShaderProgram();

	//Toon Shader
	Shader *toon_s = new Shader("shader/toon_shader_vs.glsl", "shader/toon_shader_fs.glsl");
	shader_array->push_back(toon_s);
	GLuint toon_sp = toon_s->GetShaderProgram();
	int g_toon_DL_loc = glGetUniformLocation(toon_sp, "dirn_light");

	//Explosion Shader
	Shader *explosion_s = new Shader("shader/explosion_vs.glsl","shader/explosion_gs.glsl", "shader/explosion_fs.glsl");
	shader_array->push_back(explosion_s);
	int g_explosion_time_loc = glGetUniformLocation(explosion_s->GetShaderProgram(), "time");
	int g_explosion_type_loc = glGetUniformLocation(explosion_s->GetShaderProgram(), "e_type");
	int g_explosion_break_loc = glGetUniformLocation(explosion_s->GetShaderProgram(), "break_mesh");
	//Baloon Shader
	Shader *baloon_s = new Shader("shader/baloon_vs.glsl", "shader/baloon_gs.glsl", "shader/baloon_fs.glsl");
	shader_array->push_back(baloon_s);
	int g_baloon_time_loc = glGetUniformLocation(baloon_s->GetShaderProgram(), "time");
	

	//shader debug vertex shaders
	GLuint g_debug_sp = create_programme_from_files("shader/shadow_vs.glsl", "shader/shadow_fs.glsl");

	GLuint g_depth_sp = create_programme_from_files("shader/depth.vert", "shader/depth.frag");
	int g_depth_M_loc = glGetUniformLocation(g_depth_sp, "M");
	int g_depth_V_loc = glGetUniformLocation(g_depth_sp, "V");
	int g_depth_P_loc = glGetUniformLocation(g_depth_sp, "P");

	GLuint g_phongWithShadow_sp = create_programme_from_files("shader/phong_pf_shadow_vs.glsl", "shader/phong_pf_shadow_fs.glsl");
	int  g_phongWithShadow_M_loc = glGetUniformLocation(g_phongWithShadow_sp, "model");
	int  g_phongWithShadow_V_loc = glGetUniformLocation(g_phongWithShadow_sp, "view");
	int  g_phongWithShadow_P_loc = glGetUniformLocation(g_phongWithShadow_sp, "proj");
	int  g_phongWithShadow_L_loc = glGetUniformLocation(g_phongWithShadow_sp, "light_pos");
	int  g_phongWithShadow_DL_loc = glGetUniformLocation(g_phongWithShadow_sp, "dirn_light");
	int  g_phongWithShadow_caster_V_loc = glGetUniformLocation(g_phongWithShadow_sp, "caster_V");
	int  g_phongWithShadow_caster_P_loc = glGetUniformLocation(g_phongWithShadow_sp, "caster_P");
	int  g_phongWithShadow_shad_resolution_loc = glGetUniformLocation(g_phongWithShadow_sp, "shad_resolution");
	//int camera_pos_location = glGetUniformLocation(monkey_sp, "cameraPos");
	/*-------------------------------CREATE SHADERS-------------------------------*/
	vmath::vec3 axis_in_camera_coord = (0.0f, 1.0f, 0.0f);

	float aspect = (float)g_gl_width / (float)g_gl_height; // aspect ratio
	// matrix components
	vmath::mat4 proj_mat_2 = CreateProjectionMatrix(aspect);
	vmath::mat4 translation_matrix = vmath::mat4::identity();
	vmath::mat4 rotation_matrix = vmath::mat4::identity();

	/*-------------------------------CREATE MESH-------------------------------*/
	floor_mesh = new Mesh(1, simple_texture_sp, 12.0f, 2.2f);
	floor_mesh->SetPosition(0.0f, -4.9f, 0.0f);
	floor_mesh->SetShaderProgram(texture_normal_sp);
	floor_mesh->SetTexture(GL_TEXTURE_2D, texBrick_d);
	floor_mesh->SetTextureMipmap(texBrick_d);
	floor_mesh->receive_shadow = false;
	floor_mesh->cast_shadow = false;
	//mesh_array->push_back(floor_mesh);

	cube_pic = new Mesh("cube_texture.obj");
	cube_pic->SetPosition(0.0f, -5.0f, 0.0f);
	cube_pic->SetScale(3.0f);
	cube_pic->SetShaderProgram(phong_pf_sp);
	cube_pic->SetTexture(GL_TEXTURE_2D, texConcrete);
	cube_pic->cast_shadow = true;
	cube_pic->receive_shadow = false;
	cube_pic->show_backface = false;
	mesh_array->push_back(cube_pic);

	teapot = new Mesh("wt_teapot.obj");
	teapot->SetTranslation(0.0f, -2.0f, 5.0f);
	teapot->SetScale(2.0f);
	//teapot->SetRotation(-90.0f, 1.0f, 0.0f, 0.0f);
	teapot->SetShaderProgram(toon_sp);
	teapot->SetTexture(GL_TEXTURE_2D, texFloor);
	teapot->cast_shadow = true;
	teapot->show_backface = false;
	teapot->receive_shadow = true;
	//mesh_array->push_back(teapot);

	Mesh *walls = new Mesh("cube_texture.obj");
	walls->SetPosition(-10.0f, -5.0f, -10.0f);
	walls->SetScale(20.0f);
	walls->SetShaderProgram(phong_pf_sp);
	walls->SetTexture(GL_TEXTURE_2D, texlightBlue);
	walls->receive_shadow = false;
	walls->cast_shadow = false;
	walls->show_backface = true;
	//mesh_array->push_back(walls);

	Mesh *point_light_sphere = new Mesh("sphere.obj");
	point_light_sphere->SetTranslation(7.0f, 7.0f, 0.0f);
	point_light_sphere->SetScale(0.2f);
	point_light_sphere->SetShaderProgram(phong_pf_sp);
	point_light_sphere->cast_shadow = false;
	point_light_sphere->receive_shadow = false;
	point_light_sphere->SetTexture(GL_TEXTURE_2D, tex);
	mesh_array->push_back(point_light_sphere);

	/*Mesh *tot_reflect_sphere = new Mesh("sphere.obj");
	tot_reflect_sphere->SetTranslation(3.0f, 4.0f, 0.0f);
	tot_reflect_sphere->SetShaderProgram(monkey_sp);
	tot_reflect_sphere->SetTexture(GL_TEXTURE_CUBE_MAP, cube_map_texture);
	//mesh_array->push_back(tot_reflect_sphere);

	Mesh *tot_reflect_sphere2 = new Mesh("sphere.obj");
	tot_reflect_sphere2->SetTranslation(-3.0f, 4.0f, 0.0f);
	tot_reflect_sphere2->SetShaderProgram(monkey_sp);
	tot_reflect_sphere2->SetTexture(GL_TEXTURE_CUBE_MAP, cube_map_texture);
	//mesh_array->push_back(tot_reflect_sphere2);

	sphere_blend = new Mesh("sphere.obj");
	sphere_blend->SetTranslation(0.7f, 1.5f, 1.0f + 2.0f);
	sphere_blend->SetScale(1.5f);
	sphere_blend->SetShaderProgram(phong_pf_blend_sp);
	sphere_blend->SetTexture(GL_TEXTURE_2D, texSphere, GL_TEXTURE_CUBE_MAP, cube_map_texture);
	//mesh_array->push_back(sphere_blend);*/

	Mesh *toon_sphere = new Mesh("bunny.obj");
	toon_sphere->SetTranslation(1.0f, 2.0f, 1.0f);
	toon_sphere->SetScale(1.0f);
	toon_sphere->SetShaderProgram(tangent_normal_sp);
	//toon_sphere->SetTexture(GL_TEXTURE_CUBE_MAP, cube_map_texture);
	//mesh_array->push_back(tot_reflect_sphere);
	/*-------------------------------CREATE MESH-------------------------------*/

	GLuint billBoard = create_plane(1.0f, 1.0f);

	for (int i = 0; i < shader_array->size(); i++)
	{
		Shader *curr_shader = (Shader *)shader_array->at(i);
		glUseProgram(curr_shader->GetShaderProgram());
		glUniformMatrix4fv(curr_shader->uniform_locations[2], 1, GL_FALSE, proj_mat_2);
	}

	vmath::mat4 mv_matrix_sphere = SetCamera(eyeXPosn, eyeZPosn);

	glUseProgram(g_phongWithShadow_sp);
	glUniformMatrix4fv( g_phongWithShadow_V_loc, 1, GL_FALSE, mv_matrix_sphere);
	glUniformMatrix4fv( g_phongWithShadow_P_loc, 1, GL_FALSE, proj_mat_2);
	glUniformMatrix4fv( g_phongWithShadow_caster_V_loc, 1, GL_FALSE, g_caster_V.m);
	glUniformMatrix4fv( g_phongWithShadow_caster_P_loc, 1, GL_FALSE, g_caster_P.m);
	glUniform1f( g_phongWithShadow_shad_resolution_loc, (GLfloat)2048);

	glUseProgram(texture_normal_sp);
	glUniformMatrix4fv(g_texture_normal_caster_V_loc, 1, GL_FALSE, g_caster_V.m);
	glUniformMatrix4fv(g_texture_normal_caster_P_loc, 1, GL_FALSE, g_caster_P.m);
	
	//float time = 0.0f;
	float time_ba = 0.0f;
	bool explode = true;
	bool baloon = false;

	while (!glfwWindowShouldClose(window)) {
		static double previous_seconds = glfwGetTime();
		double current_seconds = glfwGetTime();
		double elapsed_seconds = current_seconds - previous_seconds;
		previous_seconds = current_seconds;

		glUseProgram(explosion_s->GetShaderProgram());
		glUniform1f(g_explosion_time_loc, time);
		glUniform1i(g_explosion_type_loc, type);
		glUniform1f(g_explosion_break_loc, break_i);
		
		glUseProgram(baloon_s->GetShaderProgram());
		glUniform1f(g_baloon_time_loc, time_ba);
		if (isExplode_)
		{
			if (explode)
				time += 0.001f;
			else
				time -= 0.001f;

			if (time >= 4.0f)
			{
				explode = false;
			}
			else if (time <= 0.0f)
			{
				explode = true;
			}
		}
		if (isBaloon_)
		{
			if (baloon)
				time_ba += 0.001f;
			else
				time_ba -= 0.001f;

			if (time_ba >= 0.2f)
			{
				baloon = false;
			}
			else if (time_ba <= 0.1f)
			{
				baloon = true;
			}
		}
		else
		{
			//time_ba = 0.0f;
		}
		
		//printf("Left %f\n", time);
		if (iCurMouseX != iPrevMouseX || iCurMouseY != iPrevMouseY) {
			// Arcball Rotation
		}

		//_update_fps_counter(g_window);
		// wipe the drawing surface clear
			// bind framebuffer that renders to texture instead of screen
			glBindFramebuffer(GL_FRAMEBUFFER, m_shadowDepth_fbo);
			// set the viewport to the size of the shadow map
			glViewport(0, 0, 2048, 2048);
			// clear the shadow map to black (or white)
			glClearColor(1.0, 1.0, 1.0, 1.0);
			// no need to clear the colour buffer
			glClear(GL_DEPTH_BUFFER_BIT);
			// bind out shadow-casting shader from the previous section
			glUseProgram(g_depth_sp);
			// send in the view and projection matrices from the light
			glUniformMatrix4fv(g_depth_V_loc, 1, GL_FALSE, g_caster_V.m);
			glUniformMatrix4fv(g_depth_P_loc, 1, GL_FALSE, g_caster_P.m);

			// bind the sphere vao and draw them
			/*glBindVertexArray(g_sphere_vao);
			for (int i = 0; i < 4; i++) {
				glUniformMatrix4fv(g_depth_M_loc, 1, GL_FALSE, g_sphere_Ms[i].m);
				glDrawArrays(GL_TRIANGLES, 0, g_sphere_point_count);
			}*/

			//Shadow Casters
			for (int i = 0; i < mesh_array->size(); i++)
			{
				Mesh *curr_mesh = (Mesh *)mesh_array->at(i);
				if (curr_mesh->cast_shadow)
				{
					glUniformMatrix4fv(g_depth_M_loc, 1, GL_FALSE, curr_mesh->model_matrix);
					curr_mesh->Render();
				}
			}
			glUniformMatrix4fv(g_depth_M_loc, 1, GL_FALSE, teapot->model_matrix);
			glBindVertexArray(teapot->vaoObject);
			glDrawArrays(GL_TRIANGLES, 0, teapot->vertex_count);
			// bind the default framebuffer again
			glBindFramebuffer(GL_FRAMEBUFFER, 0);

			glDisable(GL_POLYGON_OFFSET_FILL);

		glCullFace(GL_BACK); // cull back face
		glFrontFace(GL_CCW); // set counter-clock-wise vertex order to mean the front
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, g_gl_width, g_gl_height);


		glUseProgram(phong_pf_sp);
		glUniform3fv(phong_pf_L_loc, 1, point_light_posn);
		glUniform3fv(phong_pf_DL_loc, 1, dirn_light_vec);
		glUseProgram(tangent_normal_sp);
		glUniform3fv(g_tangent_normal_L_loc, 1, point_light_posn);
		glUniform3fv(g_tangent_normal_DL_loc, 1, dirn_light_vec);
		
		glUseProgram(g_phongWithShadow_sp);
		glUniform3fv( g_phongWithShadow_L_loc, 1, point_light_posn);
		glUniform3fv( g_phongWithShadow_DL_loc, 1, dirn_light_vec);
		glUseProgram(texture_normal_sp);
		glUniform3fv(g_texture_normal_L_loc, 1, point_light_posn);
		glUniform3fv(g_texture_normal_DL_loc, 1, dirn_light_vec);
		glUseProgram(toon_sp);
		glUniform3fv(g_toon_DL_loc, 1, dirn_light_vec);
		glUniformMatrix4fv( g_phongWithShadow_caster_V_loc, 1, GL_FALSE, g_caster_V.m);
		glUniformMatrix4fv( g_phongWithShadow_caster_P_loc, 1, GL_FALSE, g_caster_P.m);
		glUseProgram(texture_normal_sp);
		glUniformMatrix4fv(g_texture_normal_caster_V_loc, 1, GL_FALSE, g_caster_V.m);
		glUniformMatrix4fv(g_texture_normal_caster_P_loc, 1, GL_FALSE, g_caster_P.m);
		/*-------------------------------CAMERA ROTATION-------------------------------*/
		if (iCurMouseX != iPrevMouseX || iCurMouseY != iPrevMouseY)
		{
			// Arcball Rotation
			if (bRotate)
			{
				//printf("Move mouse 2");
				vmath::vec3 va = getArcballVector(iPrevMouseX, iPrevMouseY);
				vmath::vec3 vb = getArcballVector(iCurMouseX, iCurMouseY);
				float fAngle = acos(fmin(1.0f, vmath::dot(va, vb)));
				axis_in_camera_coord = vmath::cross(va, vb);
				axis_in_camera_coord = vmath::normalize(axis_in_camera_coord);
				iPrevMouseX = iCurMouseX;
				iPrevMouseY = iCurMouseY;
				rotation_matrix *= vmath::rotate(vmath::degrees(fAngle), axis_in_camera_coord);
			}
		}
		vmath::mat4 mv_matrix = SetCamera(eyeXPosn, eyeZPosn);
		mv_matrix *= rotation_matrix;
		/*-------------------------------CAMERA ROTATION-------------------------------*/

		/*-------------------------------RENDER SKYBOX-------------------------------*/
		glDepthMask(GL_FALSE);
		/*glUseProgram(skybox_cube_sp);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, cube_map_texture);

		glBindVertexArray(skybox_cube_vao);
		//glDrawArrays(GL_TRIANGLES, 0, 36);*/
		//glUniformMatrix4fv(skybox_cube_V_location, 1, GL_FALSE, mv_matrix);
		glDepthMask(GL_TRUE);
		/*-------------------------------RENDER SKYBOX-------------------------------*/

		//glUseProgram(phong_pf_blend_sp);
		//glUniformMatrix4fv(phong_pf_blend_V_location, 1, GL_FALSE, mv_matrix);
		//glUniform1f(phong_pf_blend_trans_factor, transperency_value);

		glUseProgram(g_phongWithShadow_sp);
		glUniformMatrix4fv( g_phongWithShadow_V_loc, 1, GL_FALSE, mv_matrix);

		vmath::mat4 model_matrix = vmath::mat4::identity();
		point_light_sphere->SetPosition(point_light_posn[0], point_light_posn[1], point_light_posn[2]);

		/*-------------------------------MESH - WITH SHADOWS-------------------------------*/
		glUseProgram(g_phongWithShadow_sp);
		int tex_a_location = glGetUniformLocation(g_phongWithShadow_sp, "basic_texture");
		glUniform1i(tex_a_location, 0);

		int tex_b_location = glGetUniformLocation(g_phongWithShadow_sp, "depth_map");
		glUniform1i(tex_b_location, 1);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, m_shadowMap_tex);
		
		for (int i = 0; i < mesh_array->size(); i++)
		{
			Mesh *curr_mesh = (Mesh *)mesh_array->at(i);
			if (curr_mesh->receive_shadow)
			{
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, curr_mesh->current_tex);
				glUniformMatrix4fv( g_phongWithShadow_M_loc, 1, GL_FALSE, curr_mesh->model_matrix);
				if (!curr_mesh->show_backface)
					glCullFace(GL_BACK);
				else
					glCullFace(GL_FRONT);
				curr_mesh->Render();
			}
		}
		/*-------------------------------MESH - WITH SHADOWS-------------------------------*/

		glClear(GL_STENCIL_BUFFER_BIT);							// (1) Clear the stencil buffer
		if (projectiveshadow_stencil)
			//glEnable(GL_STENCIL_TEST);								// (2) Turn on stencil testing

		glStencilFunc(GL_ALWAYS, 1, 1);						// (3) Store a 1 for all pixels
		glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);			// (4) Replace the stencil value with 1

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, floor_mesh->current_tex);
		glUniformMatrix4fv(g_texture_normal_M_loc, 1, GL_FALSE, floor_mesh->model_matrix);
		//glBindVertexArray(floor_mesh->vaoObject);
		//glDrawArrays(GL_TRIANGLES, 0, floor_mesh->vertex_count);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, walls->current_tex);
		glCullFace(GL_FRONT);
		glUniformMatrix4fv( g_phongWithShadow_M_loc, 1, GL_FALSE, walls->model_matrix);
		//glBindVertexArray(walls->vaoObject);
		//glDrawArrays(GL_TRIANGLES, 0, walls->vertex_count);
		
		/*-------------------------------UPDATE SHADERS-------------------------------*/
		for (int i = 0; i < shader_array->size(); i++)
		{
			Shader *curr_shader = (Shader *)shader_array->at(i);
			glUseProgram(curr_shader->GetShaderProgram());
			glUniformMatrix4fv(curr_shader->uniform_locations[1], 1, GL_FALSE, mv_matrix);
		}
		/*-------------------------------UPDATE SHADERS-------------------------------*/

		/*-------------------------------MESH - WITHOUT SHADOWS-------------------------------*/
		glUseProgram(phong_pf_sp);
		glCullFace(GL_BACK);
		//glActiveTexture(GL_TEXTURE0);
		//glBindTexture(GL_TEXTURE_2D, texConcrete);
		for (int i = 0; i < mesh_array->size(); i++)
		{
			Mesh *curr_mesh = (Mesh *)mesh_array->at(i);
			if (!curr_mesh->receive_shadow)
			{
				glUniformMatrix4fv(phong_simple->uniform_locations[0], 1, GL_FALSE, curr_mesh->model_matrix);
				curr_mesh->Render();
			}
		}

		glUseProgram(texture_normal_sp);

		//set textures
		
		glActiveTexture(GL_TEXTURE0);
		glEnable(GL_TEXTURE_2D);
		int texture_location = glGetUniformLocation(texture_normal_sp, "basic_texture");
		glUniform1i(texture_location, 0);
		glBindTexture(GL_TEXTURE_2D, texFloor_d);

		glActiveTexture(GL_TEXTURE1);
		glEnable(GL_TEXTURE_2D);
		int normal_location = glGetUniformLocation(texture_normal_sp, "normal_texture");
		glUniform1i(normal_location, 1);
		glBindTexture(GL_TEXTURE_2D, texFloor_n);

		glActiveTexture(GL_TEXTURE2);
		glEnable(GL_TEXTURE_2D);
		int shadow_location = glGetUniformLocation(texture_normal_sp, "depth_map");
		glUniform1i(shadow_location, 2);
		glBindTexture(GL_TEXTURE_2D, m_shadowMap_tex);

		glUniformMatrix4fv(texture_normal->uniform_locations[0], 1, GL_FALSE, floor_mesh->model_matrix);
		glBindVertexArray(floor_mesh->vaoObject);
		glDrawArrays(GL_TRIANGLES, 0, floor_mesh->vertex_count);

		glActiveTexture(GL_TEXTURE0);
		glEnable(GL_TEXTURE_2D);
		texture_location = glGetUniformLocation(texture_normal_sp, "basic_texture");
		glUniform1i(texture_location, 0);
		glBindTexture(GL_TEXTURE_2D, texBrick_d);

		glActiveTexture(GL_TEXTURE1);
		glEnable(GL_TEXTURE_2D);
		normal_location = glGetUniformLocation(texture_normal_sp, "normal_texture");
		glUniform1i(normal_location, 1);
		glBindTexture(GL_TEXTURE_2D, texBrick_n);

		glCullFace(GL_FRONT);
		glUniformMatrix4fv(texture_normal->uniform_locations[0], 1, GL_FALSE, walls->model_matrix);
		glBindVertexArray(walls->vaoObject);
		glDrawArrays(GL_TRIANGLES, 0, walls->vertex_count);

		glUseProgram(toon_sp);
		glCullFace(GL_BACK);
		glUniformMatrix4fv(toon_s->uniform_locations[0], 1, GL_FALSE, teapot->model_matrix);
		//glBindVertexArray(teapot->vaoObject);
		//glDrawArrays(GL_TRIANGLES, 0, teapot->vertex_count);

		if (!isToonShader_)
		{
			glUseProgram(tangent_normal_sp);
			glUniformMatrix4fv(tangent_normal->uniform_locations[0], 1, GL_FALSE, toon_sphere->model_matrix);
			glActiveTexture(GL_TEXTURE0);
			glEnable(GL_TEXTURE_2D);
			texture_location = glGetUniformLocation(tangent_normal_sp, "basic_texture");
			glUniform1i(texture_location, 0);
			glBindTexture(GL_TEXTURE_2D, texSphere_d);

			glActiveTexture(GL_TEXTURE1);
			glEnable(GL_TEXTURE_2D);
			normal_location = glGetUniformLocation(tangent_normal_sp, "normal_texture");
			glUniform1i(normal_location, 1);
			glBindTexture(GL_TEXTURE_2D, texSphere_n);
		}
		else
		{
			glUseProgram(explosion_s->GetShaderProgram());
			glUniformMatrix4fv(explosion_s->uniform_locations[0], 1, GL_FALSE, toon_sphere->model_matrix);
		}
		glDisable(GL_CULL_FACE);
		//glCullFace(GL_FRONT); // cull back face
		//glFrontFace(GL_CCW);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texFloor);
		glBindVertexArray(toon_sphere->vaoObject);
		glDrawArrays(GL_TRIANGLES, 0, toon_sphere->vertex_count);

		glEnable(GL_CULL_FACE);
		glUseProgram(baloon_s->GetShaderProgram());
		glUniformMatrix4fv(baloon_s->uniform_locations[0], 1, GL_FALSE, teapot->model_matrix);
		glBindVertexArray(teapot->vaoObject);
		glDrawArrays(GL_TRIANGLES, 0, teapot->vertex_count);
		
		
		/*-------------------------------MESH - WITHOUT SHADOWS-------------------------------*/

		/*-------------------------------PROJECTIVE SHADOWS-------------------------------*/
		std::vector<vmath::vec3> *proj_planes = new std::vector<vmath::vec3>();
		std::vector<vmath::vec3> *proj_normals = new std::vector<vmath::vec3>();
			vmath::vec3 floor_plane(-12.0f, -4.85f, 1.0f);
			vmath::vec3 wall_plane(-9.8f, -3.0f, 1.0f);
			proj_planes->push_back(floor_plane);
			proj_planes->push_back(wall_plane);
			vmath::vec3 floor_normal(0.0f, 1.0f, 0.0f);
			vmath::vec3 wall_normal(1.0f, 0.0f, 0.0f);
			proj_normals->push_back(floor_normal);
			proj_normals->push_back(wall_normal);

		for (int i = 0; i < 2; i++)
		{
			vmath::vec3 curr_vec = (vmath::vec3) proj_planes->at(i);
			vmath::vec3 curr_nor = (vmath::vec3) proj_normals->at(i);
			vmath::mat4 shadow_matrix;
			CreateShadowMatrix(shadow_matrix, curr_vec, curr_nor, dirn_light_vec);
			
			glStencilFunc(GL_EQUAL, 1, 1);						// (3) Store a 1 for all pixels
			glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);			// (4) Replace the stencil value with 1
			glUseProgram(texture_simple->GetShaderProgram());
			vmath::mat4 mv_matrix_proj = SetCamera(eyeXPosn, eyeZPosn);
			mv_matrix_proj *= rotation_matrix;
			mv_matrix_proj = mv_matrix_proj * shadow_matrix;
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glUniformMatrix4fv(texture_simple->uniform_locations[1], 1, GL_FALSE, mv_matrix_proj);
			glUniformMatrix4fv(texture_simple->uniform_locations[0], 1, GL_FALSE, cube_pic->model_matrix);
			glBindVertexArray(cube_pic->vaoObject);
			glDrawArrays(GL_TRIANGLES, 0, cube_pic->vertex_count);

			glStencilFunc(GL_EQUAL, 1, 1);                      // (8) Draw where stencil value is 1
			glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);			         // (9) Increase value after pixel drawn

			glUniformMatrix4fv(texture_simple->uniform_locations[0], 1, GL_FALSE, teapot->model_matrix);
			glBindVertexArray(teapot->vaoObject);
			glDrawArrays(GL_TRIANGLES, 0, teapot->vertex_count);
		}
		glDisable(GL_STENCIL_TEST);               // (14) We are done using the stencils
		/*-------------------------------PROJECTIVE SHADOWS-------------------------------*/

		/* Debug Shadow Map */
		glUseProgram(g_debug_sp);
		int tex_c_location = glGetUniformLocation(g_debug_sp, "depth_tex");
		glUniform1i(tex_c_location, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, m_shadowMap_tex);
		glBindVertexArray(shadowMap_debug_quad_vao);
		//glDrawArrays(GL_TRIANGLES, 0, shadowMap_debug_quad_point_count);

		glfwPollEvents();

		// control keys
		bool cam_moved = false;
		// put the stuff we've been drawing onto the display
		glfwSwapBuffers(window);
	}

	// close GL context and any other GLFW resources
	glfwTerminate();
	return 0;
}