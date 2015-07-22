#include "Texture.h"
#include "extern\glew\include\GL\glew.h"

using namespace tdogl;

static GLenum TextureFormatForBitmapFormat(Bitmap::Format format)
{
	switch (format) {
	case Bitmap::Format_Grayscale: return GL_LUMINANCE;
	case Bitmap::Format_GrayscaleAlpha: return GL_LUMINANCE_ALPHA;
	case Bitmap::Format_RGB: return GL_RGB;
	case Bitmap::Format_RGBA: return GL_RGBA;
	default: throw std::runtime_error("Unrecognised Bitmap::Format");
	}
}

Texture::Texture(const Bitmap& bitmap, bool mipmap, GLint minMagFiler, GLint wrapMode) :
_originalWidth((GLfloat)bitmap.width()),
_originalHeight((GLfloat)bitmap.height())
{
	//glEnable(GL_TEXTURE_2D);
	glGenTextures(1, &_object);
	glBindTexture(GL_TEXTURE_2D, _object);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minMagFiler);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);
	if (!mipmap)
	{
		glTexImage2D(GL_TEXTURE_2D,
			0,
			TextureFormatForBitmapFormat(bitmap.format()),
			(GLsizei)bitmap.width(),
			(GLsizei)bitmap.height(),
			0,
			TextureFormatForBitmapFormat(bitmap.format()),
			GL_UNSIGNED_BYTE,
			bitmap.pixelBuffer());
	}
	else
	{
		/*gluBuild2DMipmaps(GL_TEXTURE_2D,
			TextureFormatForBitmapFormat(bitmap.format()),
			(GLsizei)bitmap.width(),
			(GLsizei)bitmap.height(),
			TextureFormatForBitmapFormat(bitmap.format()),
			GL_UNSIGNED_BYTE,
			bitmap.pixelBuffer());*/
		//glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, _originalWidth, _originalHeight, 0, GL_UNSIGNED_BYTE, NULL);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexStorage2D(GL_TEXTURE_2D, 8, GL_RGBA8, _originalWidth, _originalHeight);
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, _originalWidth, _originalHeight, TextureFormatForBitmapFormat(bitmap.format()), GL_UNSIGNED_BYTE, bitmap.pixelBuffer());
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	glBindTexture(GL_TEXTURE_2D, 0);
}

Texture::~Texture()
{
	glDeleteTextures(1, &_object);
}

GLuint Texture::object() const
{
	return _object;
}

GLfloat Texture::originalWidth() const
{
	return _originalWidth;
}

GLfloat Texture::originalHeight() const
{
	return _originalHeight;
}

