#pragma once
#ifndef EAE6320_Sprite_H
#define EAE6320_Sprite_H

#include <d3d9.h>
#include <d3dx9shader.h>
#include <string>
namespace BlackRock
{
	class Sprite
	{
		struct sVertex
		{
			float x, y, z;
			float u, v;	// D3DCOLOR = 4 bytes, or 8 bits [0,255] per RGBA channel
		};

		const char * assetPath_;
		const char *vertexShaderPath_;
		const char *pixelShaderPath_;
		//const char *materialPath_;
		const char *texturePath_;

		IDirect3DVertexShader9* m_vertexShader;
		IDirect3DPixelShader9* m_fragmentShader;
		IDirect3DTexture9* texture;

		int numberOfVertices;
		int SpriteAtlasIndex;

		ID3DXConstantTable * s_pConstantTable_vertex;
		IDirect3DVertexDeclaration9* s_vertexDeclaration;
		IDirect3DVertexBuffer9* s_vertexBuffer;

		IDirect3DDevice9* s_direct3dDevice;
		HWND s_mainWindow;

		float uvSpeed[2];
		float uvIncrement;
	public:
		ID3DXConstantTable * m_pConstantTable_vertex;
		struct sRectangle
		{
			float centerX, centerY, width, height, tex_u, tex_v;
			sRectangle(float i_centerX, float i_centerY, float i_width, float i_height, float i_tex_u, float i_tex_v)
				: centerX(i_centerX), centerY(i_centerY), width(i_width), height(i_height), tex_u(i_tex_u), tex_v(i_tex_v) {}
		};

		sRectangle *Sprite_data;
		ID3DXConstantTable * m_pConstantTable_fragment;
		static D3DVERTEXELEMENT9 s_vertexElements[];
		Sprite(const char *assetPath_);

		bool Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow);
		void SetData(sRectangle* i_position);

		void Render();
		HRESULT Set(IDirect3DDevice9* i_direct3dDevice);
		void SetSampler();

		void setSpriteAtlasIndex(int index);
		void SetUVAnimateSpeed(float speed[2]);
		bool LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice);
		bool LoadVertexShader(IDirect3DDevice9* i_direct3dDevice);
		bool Load(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow);
		bool LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage);

		bool CreateVertexBuffer(const DWORD i_usage);
		bool ChangeVertexBuffer(const DWORD i_usage);
		bool AnimateSpriteSheet(int indexX, int indexY);
		
	};
}
#endif

