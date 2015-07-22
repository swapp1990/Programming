#pragma once

#include "Bitmap.h"
#include "extern\glew\include\GL\glew.h"
class Texture
{
public:
	Texture(const tdogl::Bitmap& bitmap,
		bool mipmap,
		GLint minMagFiler = GL_LINEAR,
		GLint wrapMode = GL_CLAMP_TO_EDGE);

	~Texture();

	GLuint object() const;

	GLfloat originalWidth() const;

	GLfloat originalHeight() const;

private:
	GLuint _object;
	GLfloat _originalWidth;
	GLfloat _originalHeight;

	//copying disabled
	Texture(const Texture&);
	const Texture& operator=(const Texture&);
};

