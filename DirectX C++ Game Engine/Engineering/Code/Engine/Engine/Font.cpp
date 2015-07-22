#include "Font.h"
#include <assert.h>

Font::Font() : g_font(NULL)
{

	SetRect(&font_rect, 0, 0, 10, 20);

	SetRect(&font_rect, 0, 0, 10.0f, 20.0f);

	text_array = new std::vector<sTextStruct *>;
}

void Font::Initialize(IDirect3DDevice9* s_direct3dDevice, 
						float font_height, 
						float font_width,
						bool isItalic_)
{
	HRESULT hr = D3DXCreateFont(s_direct3dDevice,     //D3D Device

		font_height,               //Font height

		font_width,                //Font width

		FW_NORMAL,        //Font Weight

		1,                //MipLevels

		isItalic_,            //Italic

		DEFAULT_CHARSET,  //CharSet

		OUT_DEFAULT_PRECIS, //OutputPrecision

		ANTIALIASED_QUALITY, //Quality

		DEFAULT_PITCH | FF_DONTCARE,//PitchAndFamily

		"Arial",          //pFacename,

		&g_font);         //ppFont

	assert(SUCCEEDED(hr));
}

Font::sTextStruct*& Font::AddText(const char *i_input, float i_x, float i_y, float height, float width, float i_r, float i_g, float i_b)
{
	sTextStruct temp_struct(i_input, i_x, i_y, height, width, 20.0f, i_r, i_g, i_b);
	sTextStruct *return_struct = new sTextStruct(temp_struct.m_string, temp_struct.x, temp_struct.y, temp_struct.w,
		temp_struct.h, temp_struct.fontSize, temp_struct.r, temp_struct.g, temp_struct.b);
	text_array->push_back(return_struct);
	return return_struct;
}

void Font::Render()
{

	const char *default_string;

	//A pre-formatted string showing the current frames per second
	for (unsigned int i = 0; i < text_array->size(); i++)
	{
		sTextStruct *currText = (sTextStruct *)text_array->at(i);

		SetRect(&font_rect, (int)currText->x, (int)currText->y, (int)currText->w, (int)currText->h);

		SetRect(&font_rect, currText->x, currText->y, currText->w, currText->h);

		D3DCOLOR color = D3DCOLOR_COLORVALUE(currText->r, currText->g, currText->b, 1.0f);
		g_font->DrawText(NULL,        //pSprite
			currText->m_string,  //pString
			-1,          //Count
			&font_rect,  //pRect
			DT_LEFT | DT_NOCLIP,//Format,
			color); //Color
	}
	
}


Font::~Font()
{
	if (g_font){
		g_font->Release();
		g_font = NULL;
	}
}
