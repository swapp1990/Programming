#pragma once
#include <d3d9.h>
#include <d3dx9shader.h>
#include <vector>
class Font
{
	ID3DXFont *g_font;
	float font_height;
	float font_width;
	bool isItalic_;
	RECT font_rect;

public:

	struct sTextStruct
	{
		const char *m_string;
		float x, y; //Posn
		float w, h;	//width,height
		float fontSize;
		float r, g, b; //Color
		sTextStruct() {}
		sTextStruct(const char *i_string, float i_x, float i_y, float i_w, float i_h, float i_fontSize, float i_r, float i_g, float i_b)
			:m_string(i_string), x(i_x), y(i_y), w(i_w), h(i_h), fontSize(i_fontSize), r(i_r), g(i_g), b(i_b) {}
	};

	sTextStruct *temp_struct;
	std::vector<sTextStruct *> *text_array;
	void Initialize(IDirect3DDevice9* s_direct3dDevice, 
		float font_height = 12.0f,
		float font_width = 0.0f,
		bool isItalic_ = false);

	void SetText(const char *i_input);
	sTextStruct*& AddText(const char *i_input, float i_x = 0.0f, float i_y = 0.0f, float height = 10.0f, float width = 20.0f,
					float i_r = 1.0f, float i_g = 1.0f, float i_b = 1.0f);
	void Render();
	Font();
	~Font();
};

