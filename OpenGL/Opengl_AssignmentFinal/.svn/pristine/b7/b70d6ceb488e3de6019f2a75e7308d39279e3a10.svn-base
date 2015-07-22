#pragma once
#ifndef EAE6320_SPRITE_H
#define EAE6320_SPRITE_H

#include <d3d9.h>
#include <d3dx9shader.h>
#include <string>
namespace BlackRock
{
	class DebugLine
	{
		struct sVertex
		{
			float x, y, z;
			D3DCOLOR color;	// D3DCOLOR = 4 bytes, or 8 bits [0,255] per RGBA channel
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
		int spriteAtlasIndex;

		ID3DXConstantTable * s_pConstantTable_vertex;
		IDirect3DVertexDeclaration9* s_vertexDeclaration;
		IDirect3DVertexBuffer9* s_vertexBuffer;

		IDirect3DDevice9* s_direct3dDevice;
		HWND s_mainWindow;

		D3DVECTOR startVectorList[100];
		D3DVECTOR endVectorList[100];
		D3DCOLOR VectorColorList[100];
		int numberOfLines;
	public:
		ID3DXConstantTable * m_pConstantTable_vertex;
		ID3DXConstantTable * GetConstantTableVertex();
		struct sRectangle
		{
			float centerX, centerY, width, height, tex_u, tex_v;
			sRectangle(float i_centerX, float i_centerY, float i_width, float i_height, float i_tex_u, float i_tex_v)
				: centerX(i_centerX), centerY(i_centerY), width(i_width), height(i_height), tex_u(i_tex_u), tex_v(i_tex_v) {}
		};

		sRectangle *sprite_data;
		ID3DXConstantTable * m_pConstantTable_fragment;
		static D3DVERTEXELEMENT9 s_vertexElements[];
		DebugLine();

		bool Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow);
		void SetData(sRectangle* i_position);

		void Render();

		bool LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice);
		bool LoadVertexShader(IDirect3DDevice9* i_direct3dDevice);
		bool Load(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow);
		bool LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage);

		bool CreateVertexBuffer(const DWORD i_usage);
		bool ChangeVertexBuffer(const DWORD i_usage);

		void AddLine(const D3DVECTOR& i_start, const D3DVECTOR& i_end, const D3DCOLOR i_color = D3DCOLOR_XRGB(255, 255, 255));
		void AddLine(const float i_start[], const float i_end[], const D3DCOLOR i_color = D3DCOLOR_XRGB(255, 255, 255));
		void ChangeLine(const float i_start[], const float i_end[]);
	};
}
#endif

