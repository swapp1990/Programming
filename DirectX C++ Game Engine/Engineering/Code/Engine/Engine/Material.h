#include <d3d9.h>
#include <d3dx9shader.h>
#include <string>
#include "../../External/Lua/5.2.3/src/lua.hpp"

#include "Camera.h"
#include "Light.h"

namespace BlackRock
{
	class Material
	{
	private:
		//Values from Material.txt
		const char *vertexShaderPath_;
		const char *pixelShaderPath_;
		const char *materialPath_;
		const char *texturePath_;
		const char *texturePath_normal;
		float colorModifier_values[4];

		IDirect3DVertexShader9* m_vertexShader;
		IDirect3DPixelShader9* m_fragmentShader;
		IDirect3DTexture9* texture;
		IDirect3DTexture9* texture_normal;

		IDirect3DDevice9* s_direct3dDevice;
		HWND s_mainWindow;
		Light *m_Light;



		float valueX;
		float valueColorInc, valueColorInc2, valueColorInc3;
		float g_light_direction[3];
		float g_light_direction_color[3];
		float g_light_ambient[3];
		float uvIncrement;
		float uvSpeed[2];
		float alphaScale;
		int randomInt;
		float tempValue;

		float shininess_specular;
		
		//bool LoadVertexShader();
	public:
		ID3DXConstantTable * m_pConstantTable_vertex;
		ID3DXConstantTable * m_pConstantTable_fragment;
		D3DXHANDLE handle_meshPosnScreen;
		D3DXHANDLE handle_transform_modelToWorld;
		D3DXHANDLE handle_transform_worldToView;
		D3DXHANDLE handle_transform_viewToScreen;
		D3DXHANDLE AlphaHandle;
		Material(const char *i_currentPath);
		void Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow, Light* i_light = NULL);

		ID3DXConstantTable * GetConstantTableVertex();
		ID3DXConstantTable * GetConstantTableFragment();

		bool LoadAsset(const char* i_path);
		bool LoadTableValues(lua_State& io_luaState);
		bool LoadTableValues_nested(lua_State& io_luaState, const char * field_value);
		bool LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value);
		bool LoadTableValues_parameters_values2(lua_State& io_luaState);

		bool LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice);
		bool LoadVertexShader(IDirect3DDevice9* i_direct3dDevice);
		bool LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage);

		const char * GetVertexShaderPath();
		const char * GetPixelShaderPath();
		void SetSampler();
		void InitializeLight(float dirnLight[], float dirnColor[], float ambientColot[]);
		void SetLight(float values[]);
		void SetAlpha(float alpha);
		float GetAlpha();
		
		void SetUVAnimateSpeed(float speed[2]);
		void SetSpecularValue(int value);

		HRESULT Set(IDirect3DDevice9* i_direct3dDevice);
		bool Load(IDirect3DDevice9* i_direct3dDevice);
		//~Material();
	};
}


