#include "Material.h"

#include <iostream>
#include <cassert>
#include <sstream>


namespace BlackRock
{
	Material::Material(const char *i_currentPath)
	{
		// Load and execute the asset file
		materialPath_ = i_currentPath;
		vertexShaderPath_ = "";
		pixelShaderPath_ = "";

		m_vertexShader = NULL;
		m_fragmentShader = NULL;
		texture = NULL;
		texture_normal = NULL;
		texturePath_normal = NULL;
		m_pConstantTable_vertex = NULL;
		valueX = 0.0f;
		uvIncrement = 0.0f;
		uvSpeed[0] = 0.0f;
		uvSpeed[1] = 0.0f;
		shininess_specular = 10.0f;
		valueColorInc = -0.01f;
		valueColorInc2 = 0.005f;
		valueColorInc3 = -0.003f;
		alphaScale = 1.0f;
		tempValue = 0.0f;
		//colorModifier_values[4] = { 0.0f};

		if (!LoadAsset(materialPath_))
		{
			//error
		}
		else
		{
			//cool
		}
	}

	void Material::Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow, Light* i_light)
	{
		s_direct3dDevice = i_direct3dDevice;
		s_mainWindow = i_mainWindow;
		m_Light = i_light;
		InitializeLight(m_Light->dirn_vector, m_Light->dirn_color, m_Light->ambient_color);
		uvIncrement = 0.0f;
		if (!Load(s_direct3dDevice))
		{
			//error
		}
	}

	void Material::InitializeLight(float dirnLight[], float dirnColor[], float ambientColot[])
	{
		memcpy(g_light_direction, dirnLight, sizeof(g_light_direction));
		memcpy(g_light_direction_color, dirnColor, sizeof(g_light_direction_color));
		memcpy(g_light_ambient, ambientColot, sizeof(g_light_ambient));
	}

	bool Material::LoadAsset(const char* i_path)
	{
		bool wereThereErrors = false;

		// Create a new Lua state
		lua_State* luaState = NULL;
		{
			luaState = luaL_newstate();
			if (!luaState)
			{
				wereThereErrors = true;
				std::cerr << "Failed to create a new Lua state\n";
				goto OnExit;
			}
		}

		// Load the asset file as a "chunk",
		// meaning there will be a callable function at the top of the stack
		{
			const int luaResult = luaL_loadfile(luaState, i_path);
			if (luaResult != LUA_OK)
			{
				wereThereErrors = true;
				std::cerr << lua_tostring(luaState, -1);
				// Pop the error message
				lua_pop(luaState, 1);
				goto OnExit;
			}
		}
		// Execute the "chunk", which should load the asset
		// into a table at the top of the stack
		{
			const int argumentCount = 0;
			const int returnValueCount = LUA_MULTRET;	// Return _everything_ that the file returns
			const int noMessageHandler = 0;
			const int luaResult = lua_pcall(luaState, argumentCount, returnValueCount, noMessageHandler);
			if (luaResult == LUA_OK)
			{
				// A well-behaved asset file will only return a single value
				const int returnedValueCount = lua_gettop(luaState);
				if (returnedValueCount == 1)
				{
					// A correct asset file _must_ return a table
					if (!lua_istable(luaState, -1))
					{
						wereThereErrors = true;
						std::cerr << "Asset files must return a table (instead of a " <<
							luaL_typename(luaState, -1) << ")\n";
						// Pop the returned non-table value
						lua_pop(luaState, 1);
						goto OnExit;
					}
				}
				else
				{
					wereThereErrors = true;
					std::cerr << "Asset files must return a single table (instead of " <<
						returnedValueCount << " values)\n";
					// Pop every value that was returned
					lua_pop(luaState, returnedValueCount);
					goto OnExit;
				}
			}
			else
			{
				wereThereErrors = true;
				std::cerr << lua_tostring(luaState, -1);
				// Pop the error message
				lua_pop(luaState, 1);
				goto OnExit;
			}
		}

		// If this code is reached the asset file was loaded successfully,
		// and its table is now at index -1
		if (!LoadTableValues(*luaState))
		{
			wereThereErrors = true;
		}

		// Pop the table
		lua_pop(luaState, 1);

	OnExit:

		if (luaState)
		{
			// If I haven't made any mistakes
			// there shouldn't be anything on the stack,
			// regardless of any errors encountered while loading the file:
			//assert(lua_gettop(luaState) == 0);

			//lua_close(luaState);
			//luaState = NULL;
		}

		return !wereThereErrors;
	}

	bool Material::LoadTableValues(lua_State& io_luaState)
	{
		if (!LoadTableValues_nested(io_luaState, "constants"))
		{
			return false;
		}
		if (!LoadTableValues_nested(io_luaState, "shaders"))
		{
			return false;
		}
		if (!LoadTableValues_nested(io_luaState, "textures"))
		{
			return false;
		}

		return true;
		
	}

	bool Material::LoadTableValues_nested(lua_State& io_luaState, const char * field_value)
	{
		bool wereThereErrors = false;

		lua_pushstring(&io_luaState, field_value);
		lua_gettable(&io_luaState, -2);
		if (lua_istable(&io_luaState, -1))
		{
			if (!LoadTableValues_parameters_values(io_luaState, field_value))
			{
				wereThereErrors = true;
				goto OnExit;
			}
		}
		else
		{
			wereThereErrors = true;
			std::cerr << "The value at \"" << field_value << "\" must be a table "
				"(instead of a " << luaL_typename(&io_luaState, -1) << ")\n";
			goto OnExit;
		}
		OnExit:
		// Pop the textures table
		lua_pop(&io_luaState, 1);
		return !wereThereErrors;
	}

	bool Material::LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value)
	{
		if (strcmp(field_value, "constants") == 0)
		{
			const char* key = "g_colorModifier";
			lua_pushstring(&io_luaState, key);
			lua_gettable(&io_luaState, -2);
			if (lua_istable(&io_luaState, -1))
			{
				lua_pushinteger(&io_luaState, 1);
				lua_gettable(&io_luaState, -2);
				colorModifier_values[0] = static_cast< float >(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 2);
				lua_gettable(&io_luaState, -2);
				colorModifier_values[1] = static_cast< float >(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 3);
				lua_gettable(&io_luaState, -2);
				colorModifier_values[2] = static_cast< float >(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);
		}
		else if (strcmp(field_value, "shaders") == 0)
		{
			const char* key = "vertex";
			lua_pushstring(&io_luaState, key);
			lua_gettable(&io_luaState, -2);
			vertexShaderPath_ = lua_tostring(&io_luaState, -1);
			lua_pop(&io_luaState, 1);
			const char* key2 = "pixel";
			lua_pushstring(&io_luaState, key2);
			lua_gettable(&io_luaState, -2);
			pixelShaderPath_ = lua_tostring(&io_luaState, -1);
			lua_pop(&io_luaState, 1);
		}
		else if (strcmp(field_value, "textures") == 0)
		{
			const char* key = "path";
			lua_pushstring(&io_luaState, key);
			lua_gettable(&io_luaState, -2);
			texturePath_ = lua_tostring(&io_luaState, -1);
			lua_pop(&io_luaState, 1);
			const char* key2 = "path_normal";
			lua_pushstring(&io_luaState, key2);
			lua_gettable(&io_luaState, -2);
			texturePath_normal = lua_tostring(&io_luaState, -1);
			lua_pop(&io_luaState, 1);
		}
		return true;
	}

	const char * Material::GetVertexShaderPath()
	{
		return vertexShaderPath_;
	}

	const char * Material::GetPixelShaderPath()
	{
		return pixelShaderPath_;
	}

	bool Material::Load(IDirect3DDevice9* i_direct3dDevice)
	{
		if (!LoadVertexShader(i_direct3dDevice))
		{
			//goto OnError;
			return false;
		}
		if (!LoadFragmentShader(i_direct3dDevice))
		{
			return false;
		}

		return true;
	}

	HRESULT Material::Set(IDirect3DDevice9* i_direct3dDevice)
	{
		HRESULT result = i_direct3dDevice->SetVertexShader(m_vertexShader);
		assert(SUCCEEDED(result));
		result = i_direct3dDevice->SetPixelShader(m_fragmentShader);
		assert(SUCCEEDED(result));
		return result;
	}

	bool Material::LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage)
	{
		bool wereThereErrors = false;

		// Load the compiled shader from disk
		o_compiledShader = NULL;
		HANDLE fileHandle = INVALID_HANDLE_VALUE;
		{
			// Open the file
			{
				const DWORD desiredAccess = FILE_GENERIC_READ;
				const DWORD otherProgramsCanStillReadTheFile = FILE_SHARE_READ;
				SECURITY_ATTRIBUTES* useDefaultSecurity = NULL;
				const DWORD onlySucceedIfFileExists = OPEN_EXISTING;
				const DWORD useDefaultAttributes = FILE_ATTRIBUTE_NORMAL;
				const HANDLE dontUseTemplateFile = NULL;
				fileHandle = CreateFile(i_path, desiredAccess, otherProgramsCanStillReadTheFile,
					useDefaultSecurity, onlySucceedIfFileExists, useDefaultAttributes, dontUseTemplateFile);
				if (fileHandle == INVALID_HANDLE_VALUE)
				{
					wereThereErrors = true;
					if (o_errorMessage)
					{
						std::stringstream errorMessage;
						errorMessage << "Windows failed to open the shader file: "; // << eae6320::GetLastWindowsError();
						*o_errorMessage = errorMessage.str();
					}
					goto OnExit;
				}
			}
			// Get the file's size
			size_t fileSize;
			{
				LARGE_INTEGER fileSize_integer;
				if (GetFileSizeEx(fileHandle, &fileSize_integer) != FALSE)
				{
					// This is unsafe if the file's size is bigger than a size_t
					fileSize = static_cast<size_t>(fileSize_integer.QuadPart);
				}
				else
				{
					wereThereErrors = true;
					if (o_errorMessage)
					{
						std::stringstream errorMessage;
						errorMessage << "Windows failed to get the size of shader: ";// << eae6320::GetLastWindowsError();
						*o_errorMessage = errorMessage.str();
					}
					goto OnExit;
				}
			}
			// Read the file's contents into temporary memory
			o_compiledShader = malloc(fileSize);
			if (o_compiledShader)
			{
				DWORD bytesReadCount;
				OVERLAPPED* readSynchronously = NULL;
				if (ReadFile(fileHandle, o_compiledShader, fileSize,
					&bytesReadCount, readSynchronously) == FALSE)
				{
					wereThereErrors = true;
					if (o_errorMessage)
					{
						std::stringstream errorMessage;
						errorMessage << "Windows failed to read the contents of shader: ";// << eae6320::GetLastWindowsError();
						*o_errorMessage = errorMessage.str();
					}
					goto OnExit;
				}
			}
			else
			{
				wereThereErrors = true;
				if (o_errorMessage)
				{
					std::stringstream errorMessage;
					errorMessage << "Failed to allocate " << fileSize << " bytes to read in the shader program " << i_path;
					*o_errorMessage = errorMessage.str();
				}
				goto OnExit;
			}
		}

	OnExit:

		if (wereThereErrors && o_compiledShader)
		{
			free(o_compiledShader);
			o_compiledShader = NULL;
		}
		if (fileHandle != INVALID_HANDLE_VALUE)
		{
			if (CloseHandle(fileHandle) == FALSE)
			{
				if (!wereThereErrors && o_errorMessage)
				{
					std::stringstream errorMessage;
					errorMessage << "Windows failed to close the shader file handle: ";// << eae6320::GetLastWindowsError();
					*o_errorMessage = errorMessage.str();
				}
				wereThereErrors = true;
			}
			fileHandle = INVALID_HANDLE_VALUE;
		}

		return !wereThereErrors;
	}

	bool Material::LoadVertexShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = GetVertexShaderPath();
			const D3DXMACRO* noMacros = NULL;
			ID3DXInclude* noIncludes = NULL;
			const char* entryPoint = "main";
			const char* profile = "vs_3_0";
			const DWORD noFlags = D3DXSHADER_DEBUG;
			ID3DXBuffer* errorMessages = NULL;
			ID3DXConstantTable** noConstants = NULL;
			//HRESULT result = D3DXCompileShaderFromFile(sourceCodeFileName, noMacros, noIncludes, entryPoint, profile, noFlags,
				//&compiledShader, &errorMessages, noConstants);
			std::string *errorMessage = NULL;
			void* o_compiledShader;
			HRESULT result = LoadAndAllocateShaderProgram(sourceCodeFileName, o_compiledShader, errorMessage);
			compiledShader = reinterpret_cast<ID3DXBuffer*>(o_compiledShader);
			if (SUCCEEDED(result))
			{
				if (errorMessages)
				{
					errorMessages->Release();
				}
			}
			else
			{
				if (errorMessages)
				{
					std::string errorMessage = std::string("DirectX failed to compiled the vertex shader from the file ") +
						sourceCodeFileName + ":\n" +
						reinterpret_cast<char*>(errorMessages->GetBufferPointer());
					//MessageBox(s_mainWindow, errorMessage.c_str(), "No Vertex Shader", MB_OK | MB_ICONERROR);
					errorMessages->Release();
					return false;
				}
				else
				{
					std::string errorMessage = "DirectX failed to compiled the vertex shader from the file ";
					errorMessage += sourceCodeFileName;
					//MessageBox(s_mainWindow, errorMessage.c_str(), "No Vertex Shader", MB_OK | MB_ICONERROR);
					return false;
				}
			}
		}
		// Create the vertex shader object
		bool wereThereErrors = false;
		{

			HRESULT result = i_direct3dDevice->CreateVertexShader(reinterpret_cast<DWORD*>(compiledShader),
				&m_vertexShader);
			if (FAILED(result))
			{
				//MessageBox(s_mainWindow, "DirectX failed to create the vertex shader", "No Vertex Shader", MB_OK | MB_ICONERROR);
				wereThereErrors = true;
			}

			result = D3DXGetShaderConstantTable(reinterpret_cast<DWORD*>(compiledShader), &m_pConstantTable_vertex);
			if (FAILED(result))
			{
				wereThereErrors = true;
			}
			
			handle_meshPosnScreen = m_pConstantTable_vertex->GetConstantByName(NULL, "g_meshPosition_screen");
			float val[2] = { 0.0, 0.0 };
			m_pConstantTable_vertex->SetFloatArray(i_direct3dDevice, handle_meshPosnScreen, val, 2);

			
			//float val[3] = { 0.0, 0.0, 0.0 };
			/*D3DXVECTOR3 m_position(0.0f, 0.0f, 0.0f);
			D3DXMATRIX transform;
			{
				D3DXMatrixTransformation(&transform, NULL, NULL, NULL, NULL,
					NULL, &m_position);
			}

			handle_transform_modelToWorld = m_pConstantTable_vertex->GetConstantByName(NULL, "g_transform_modelToWorld");
			m_pConstantTable_vertex->SetMatrix(i_direct3dDevice, handle_transform_modelToWorld, &transform);*/
			//mainCamera_->SetConstantTable(m_pConstantTable_vertex);
			//mainCamera_->SetCamera();
			//compiledShader->Release();
			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	ID3DXConstantTable * Material::GetConstantTableVertex()
	{
		return m_pConstantTable_vertex;
	}

	ID3DXConstantTable * Material::GetConstantTableFragment()
	{
		return m_pConstantTable_fragment;
	}

	bool Material::LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = GetPixelShaderPath();
			const D3DXMACRO* noMacros = NULL;
			ID3DXInclude* noIncludes = NULL;
			const char* entryPoint = "main";
			const char* profile = "ps_3_0";
			const DWORD noFlags = 0;
			ID3DXBuffer* errorMessages = NULL;
			ID3DXConstantTable** noConstants = NULL;
			std::string *errorMessage = NULL;
			void* o_compiledShader;
			HRESULT result = LoadAndAllocateShaderProgram(sourceCodeFileName, o_compiledShader, errorMessage);
			compiledShader = reinterpret_cast<ID3DXBuffer*>(o_compiledShader);
			if (SUCCEEDED(result))
			{
				if (errorMessages)
				{
					errorMessages->Release();
				}
			}
			else
			{
				if (errorMessages)
				{
					std::string errorMessage = std::string("DirectX failed to compiled the fragment shader from the file ") +
						sourceCodeFileName + ":\n" +
						reinterpret_cast<char*>(errorMessages->GetBufferPointer());
					//MessageBox(s_mainWindow, errorMessage.c_str(), "No Fragment Shader", MB_OK | MB_ICONERROR);
					errorMessages->Release();
					return false;
				}
				else
				{
					std::string errorMessage = "DirectX failed to compiled the fragment shader from the file ";
					errorMessage += sourceCodeFileName;
					//MessageBox(s_mainWindow, errorMessage.c_str(), "No Fragment Shader", MB_OK | MB_ICONERROR);
					return false;
				}
			}
		}
		// Create the fragment shader object
		bool wereThereErrors = false;
		{
			HRESULT result = i_direct3dDevice->CreatePixelShader(reinterpret_cast<DWORD*>(compiledShader),
				&m_fragmentShader);
			if (FAILED(result))
			{
				//MessageBox(s_mainWindow, "DirectX failed to create the fragment shader", "No Fragment Shader", MB_OK | MB_ICONERROR);
				wereThereErrors = true;
			}

			result = D3DXGetShaderConstantTable(reinterpret_cast<DWORD*>(compiledShader), &m_pConstantTable_fragment);
			if (FAILED(result))
			{
				wereThereErrors = true;
			}
			HRESULT result2 = D3DXCreateTextureFromFile(s_direct3dDevice, texturePath_, &texture);
			assert(SUCCEEDED(result2));
			D3DXHANDLE handle;
			handle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_color_perMaterial");

			AlphaHandle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_transparency");
			m_pConstantTable_fragment->SetFloat(i_direct3dDevice, AlphaHandle, alphaScale);
			if (texturePath_normal != NULL)
			{
				HRESULT result2 = D3DXCreateTextureFromFile(s_direct3dDevice, texturePath_normal, &texture_normal);
				assert(SUCCEEDED(result2));
			}
			
			m_pConstantTable_fragment->SetFloatArray(i_direct3dDevice, handle, colorModifier_values, 4);
			handle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_light_direction");
			
			m_pConstantTable_fragment->SetFloatArray(i_direct3dDevice, handle, g_light_direction, 3);

			handle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_light_direction_color");

			m_pConstantTable_fragment->SetFloatArray(i_direct3dDevice, handle, g_light_direction_color, 3);

			handle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_light_ambient");
			m_pConstantTable_fragment->SetFloatArray(i_direct3dDevice, handle, g_light_ambient, 3);
			//
			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	void Material::SetSampler()
	{
		D3DXHANDLE samplerHandle = m_pConstantTable_fragment->GetConstantByName(NULL,
			"g_color_sampler");
		if (samplerHandle != NULL)
		{
			DWORD samplerRegister = static_cast<DWORD>(m_pConstantTable_fragment->GetSamplerIndex(samplerHandle));
			HRESULT result3 = s_direct3dDevice->SetTexture(samplerRegister, texture);
			assert(SUCCEEDED(result3));
		}

		D3DXHANDLE shininessHandle = m_pConstantTable_fragment->GetConstantByName(NULL,
			"g_specular_shininess");
		m_pConstantTable_fragment->SetFloat(s_direct3dDevice, shininessHandle, shininess_specular);

		D3DXHANDLE samplerHandleNormal = m_pConstantTable_fragment->GetConstantByName(NULL,
			"g_normal_sampler");
		if (samplerHandleNormal != NULL)
		{
			DWORD samplerNormalRegister = static_cast<DWORD>(m_pConstantTable_fragment->GetSamplerIndex(samplerHandleNormal));
			if (texturePath_normal != NULL)
			{
				HRESULT result3 = s_direct3dDevice->SetTexture(samplerNormalRegister, texture_normal);
				assert(SUCCEEDED(result3));
			}
		}

		D3DXHANDLE HandleUV = m_pConstantTable_vertex->GetConstantByName(NULL,
			"g_uv_speed");
		D3DXHANDLE HandleTotalSeconds = m_pConstantTable_vertex->GetConstantByName(NULL,
			"g_totalSecondsElapsed");
		if (HandleUV != NULL)
		{
			uvIncrement += 0.001f;
			m_pConstantTable_vertex->SetFloatArray(s_direct3dDevice, HandleUV, uvSpeed, 2);
			m_pConstantTable_vertex->SetFloat(s_direct3dDevice, HandleTotalSeconds, uvIncrement);
		}
		m_pConstantTable_fragment->SetFloat(s_direct3dDevice, AlphaHandle, alphaScale);

	}

	void Material::SetAlpha(float alpha)
	{
		alphaScale = alpha;
		if (alphaScale > 1.0f)
		{
			alphaScale = 1.0f;
		}
	}

	float Material::GetAlpha()
	{
		return alphaScale;
	}

	void Material::SetUVAnimateSpeed(float speed[2])
	{
		uvSpeed[0] = speed[0];
		uvSpeed[1] = speed[1];
	}

	void Material::SetSpecularValue(int value)
	{
		shininess_specular = static_cast<float>(value);
	}

	void Material::SetLight(float values[])
	{
		D3DXHANDLE handle = m_pConstantTable_fragment->GetConstantByName(NULL, "g_light_direction");
		//valueX += 0.01f;
		//float g_light_direction[3] = { values[0], values[1], values[2] };
		D3DXVECTOR3 offset = D3DXVECTOR3(g_light_direction[0] + values[0], g_light_direction[1], g_light_direction[2] + values[2]);
		D3DXVECTOR3 m_direction = D3DXVECTOR3(g_light_direction[0], g_light_direction[1], g_light_direction[2]);
		static D3DXVECTOR3 xzPosition(m_direction);
		xzPosition = offset;
		D3DXVec3Normalize(&m_direction, &xzPosition);
		//m_pConstantTable_fragment->SetVector(s_direct3dDevice, handle, &m_direction);
		float g_light_directio2n[3] = { m_direction[0], m_direction[1], m_direction[2] };
		m_pConstantTable_fragment->SetFloatArray(s_direct3dDevice, handle, g_light_directio2n, 3);

		D3DXHANDLE handle_color = m_pConstantTable_fragment->GetConstantByName(NULL, "g_light_direction_color");
		if (randomInt == 0)
		{
			g_light_direction_color[0] += valueColorInc;
			tempValue += valueColorInc;
		}
		else if (randomInt == 1)
		{
			g_light_direction_color[1] += valueColorInc;
			tempValue += valueColorInc;
		}
		else if (randomInt == 2)
		{
			g_light_direction_color[2] += valueColorInc;
			tempValue += valueColorInc;
		}
		
		//g_light_direction_color[1] += valueColorInc2;
		//g_light_direction_color[2] += valueColorInc3;
		if (tempValue < 0.0f || tempValue > 1.0f)
		{
			randomInt = rand() % 3;
			valueColorInc = -valueColorInc;
			valueColorInc = 0.0f;
			
			g_light_direction_color[0] = 0.0f;
			g_light_direction_color[1] = 0.0f;
			g_light_direction_color[2] = 0.0f;
			//valueColorInc2 = -valueColorInc2;
			//valueColorInc3 = -valueColorInc3;
		}

		D3DXVECTOR3 m_color = D3DXVECTOR3(g_light_direction_color[0], g_light_direction_color[1], g_light_direction_color[2]);

		m_pConstantTable_fragment->SetFloatArray(s_direct3dDevice, handle_color, g_light_direction_color, 3);
	}
}



