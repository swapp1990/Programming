#include "Sprite.h"
#include <cstdint>
#include <cassert>
#include <fstream>
#include <sstream>
#include <iostream>
#include "../UserSettings/UserSettings.h"

namespace BlackRock
{
	D3DVERTEXELEMENT9 Sprite::s_vertexElements[] =
	{
		{ 0, 0, D3DDECLTYPE_FLOAT3, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_POSITION, 0 },
		// COLOR0, D3DCOLOR == 4 bytes
		{ 0, 12, D3DDECLTYPE_FLOAT2, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_TEXCOORD, 0 },
		D3DDECL_END()
	};

	Sprite::Sprite(const char *i_assetPath)
	{
		assetPath_ = i_assetPath;
		texture = NULL;
		SpriteAtlasIndex = 0;
		uvSpeed[0] = 0;
		uvSpeed[1] = 0;
		uvIncrement = 0.0f;
	}

	bool Sprite::Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow)
	{
		s_direct3dDevice = i_direct3dDevice;
		s_mainWindow = i_mainWindow;
		// The usage tells Direct3D how this vertex and index buffers will be used. 
		DWORD usage = 0;
		{
			// Our code will only ever write to the buffer
			usage |= D3DUSAGE_WRITEONLY | D3DUSAGE_DYNAMIC;
			// The type of vertex processing should match what was specified when the device interface was created with CreateDevice()
			{

			}
		}

		if (!CreateVertexBuffer(usage))
		{
			goto OnError;
		}

		return true;

	OnError:
		//ShutDown();
		return false;
	}

	void Sprite::SetData(sRectangle* i_position)
	{
		Sprite_data = new sRectangle(i_position->centerX, i_position->centerY, i_position->width, i_position->height, i_position->tex_u, i_position->tex_v);
	}

	bool Sprite::Load(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow)
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

		if (!Initialize(i_direct3dDevice, i_mainWindow))
		{
			return false;
		}
		return true;
	}

	bool Sprite::LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage)
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

	bool Sprite::LoadVertexShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = "data/vertexShader_Sprite.bsl";
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
			D3DXHANDLE handle;
			handle = m_pConstantTable_vertex->GetConstantByName(NULL, "g_uvCoordinates");
			float value[2] = { 1.0f, 1.0f };
			m_pConstantTable_vertex->SetFloatArray(i_direct3dDevice, handle, value, 2);

			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	void Sprite::SetUVAnimateSpeed(float speed[2])
	{
		uvSpeed[0] = speed[0];
		uvSpeed[1] = speed[1];
	}

	bool Sprite::LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = "data/fragmentShader_Sprite.bsl";
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

			HRESULT result2 = D3DXCreateTextureFromFile(i_direct3dDevice, assetPath_, &texture);
			assert(SUCCEEDED(result2));
			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	bool Sprite::CreateVertexBuffer(const DWORD i_usage)
	{
		HRESULT result = s_direct3dDevice->CreateVertexDeclaration(s_vertexElements, &s_vertexDeclaration);
		if (SUCCEEDED(result))
		{
			result = s_direct3dDevice->SetVertexDeclaration(s_vertexDeclaration);
			if (FAILED(result))
			{
				MessageBox(s_mainWindow, "DirectX failed to set the vertex declaration", "No Vertex Declaration", MB_OK | MB_ICONERROR);
				return false;
			}
		}
		else
		{
			MessageBox(s_mainWindow, "DirectX failed to create a Direct3D9 vertex declaration", "No Vertex Declaration", MB_OK | MB_ICONERROR);
			return false;
		}
		numberOfVertices = 4;
		const unsigned int bufferSize = numberOfVertices * sizeof(sVertex);
		// We will define our own vertex format
		const DWORD useSeparateVertexDeclaration = 0;
		// Place the vertex buffer into memory that Direct3D thinks is the most appropriate
		const D3DPOOL useDefaultPool = D3DPOOL_DEFAULT;
		HANDLE* const notUsed = NULL;

		// The usage tells Direct3D how this vertex buffer will be used
		result = s_direct3dDevice->CreateVertexBuffer(bufferSize, i_usage, useSeparateVertexDeclaration, useDefaultPool,
			&s_vertexBuffer, notUsed);
		if (FAILED(result))
		{
			MessageBox(s_mainWindow, "DirectX failed to create a vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
			return false;
		}

		// Fill the vertex buffer with the triangle's vertices
		{
			// I. Before the vertex buffer can be changed it must be "locked"
			sVertex* vertexData;
			{
				const unsigned int lockEntireBuffer = 0;
				const DWORD useDefaultLockingBehavior = 0;
				result = s_vertexBuffer->Lock(lockEntireBuffer, lockEntireBuffer,
					reinterpret_cast<void**>(&vertexData), useDefaultLockingBehavior);
				if (FAILED(result))
				{
					MessageBox(s_mainWindow, "DirectX failed to lock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
					return false;
				}
			}
			// II. Fill the buffer
			{
				float aspectRatioW = (float)UserSettings::GetWidth() / 500;
				float aspectRatioH = (float)UserSettings::GetHeight() / 500;

				float actualWidth = Sprite_data->width/aspectRatioW;
				float actualHeight = Sprite_data->height / aspectRatioH;
				/*float aspectRatio = (float)UserSettings::GetWidth() / (float)UserSettings::GetHeight();
				
				if (aspectRatio >= 1)
				{
					actualWidth /= aspectRatio;
				}
				else
				{
					actualHeight *= aspectRatio;
				}*/
			/*	float aspectRatioWidth = (float)UserSettings::GetWidth() / 1280;
				float scaleWidth = aspectRatioWidth - 1.0f;
				actualWidth = Sprite_data->width - (scaleWidth * Sprite_data->width);

				float aspectRatioHeight = (float)UserSettings::GetHeight() / 800;
				float scaleHeight = aspectRatioHeight - 1.0f;
				actualHeight = Sprite_data->height - (scaleHeight * Sprite_data->height);*/

				vertexData[0].x = Sprite_data->centerX - (actualWidth) / 2;
				vertexData[0].y = Sprite_data->centerY + (actualHeight) / 2;
				vertexData[0].z = 0.0f;
				vertexData[0].u = 0.0f + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[0].v = 0.0f;

				vertexData[1].x = Sprite_data->centerX + (actualWidth) / 2;
				vertexData[1].y = Sprite_data->centerY + (actualHeight) / 2;
				vertexData[1].z = 0.0f;
				vertexData[1].u = Sprite_data->tex_u + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[1].v = 0.0f;

				vertexData[2].x = Sprite_data->centerX - (actualWidth) / 2;
				vertexData[2].y = Sprite_data->centerY - (actualHeight) / 2;
				vertexData[2].z = 0.0f;
				vertexData[2].u = 0.0f + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[2].v = Sprite_data->tex_v;

				vertexData[3].x = Sprite_data->centerX + (actualWidth) / 2;
				vertexData[3].y = Sprite_data->centerY - (actualHeight) / 2;
				vertexData[3].z = 0.0f;
				vertexData[3].u = Sprite_data->tex_u + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[3].v = Sprite_data->tex_v;
			}
			// III. The buffer must be "unlocked" before it can be used
			{
				result = s_vertexBuffer->Unlock();
				if (FAILED(result))
				{
					MessageBox(s_mainWindow, "DirectX failed to unlock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
					return false;
				}
			}
		}
		return true;
	}

	HRESULT Sprite::Set(IDirect3DDevice9* i_direct3dDevice)
	{
		HRESULT result = i_direct3dDevice->SetVertexShader(m_vertexShader);
		assert(SUCCEEDED(result));
		result = i_direct3dDevice->SetPixelShader(m_fragmentShader);
		assert(SUCCEEDED(result));
		return result;
	}

	void Sprite::SetSampler()
	{
		D3DXHANDLE samplerHandle = m_pConstantTable_fragment->GetConstantByName(NULL,
			"g_color_sampler");
		if (samplerHandle != NULL)
		{
			DWORD samplerRegister = static_cast<DWORD>(m_pConstantTable_fragment->GetSamplerIndex(samplerHandle));
			HRESULT result3 = s_direct3dDevice->SetTexture(samplerRegister, texture);
			assert(SUCCEEDED(result3));
		}
	}

	bool Sprite::ChangeVertexBuffer(const DWORD i_usage)
	{
		numberOfVertices = 4;

		//SpriteAtlasIndex = 1;

		// Fill the vertex buffer with the triangle's vertices
		{
			// I. Before the vertex buffer can be changed it must be "locked"
			sVertex* vertexData;
			{
				const unsigned int lockEntireBuffer = 0;
				const DWORD useDefaultLockingBehavior = 0;
				HRESULT result = s_vertexBuffer->Lock(lockEntireBuffer, lockEntireBuffer,
					reinterpret_cast<void**>(&vertexData), useDefaultLockingBehavior);
				if (FAILED(result))
				{
					MessageBox(s_mainWindow, "DirectX failed to lock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
					return false;
				}
			}
			// II. Fill the buffer
			{
				float aspectRatioW = (float)UserSettings::GetWidth() / 500;
				float aspectRatioH = (float)UserSettings::GetHeight() / 500;

				float actualWidth = Sprite_data->width / aspectRatioW;
				float actualHeight = Sprite_data->height / aspectRatioH;
				/*float aspectRatio = (float)UserSettings::GetWidth() / (float)UserSettings::GetHeight();

				if (aspectRatio >= 1)
				{
				actualWidth /= aspectRatio;
				}
				else
				{
				actualHeight *= aspectRatio;
				}*/

				//float actualWidth = Sprite_data->width - (scaleWidth * Sprite_data->width);
				/*float aspectRatioWidth = (float)UserSettings::GetWidth() / 1280;
				float scaleWidth = aspectRatioWidth - 1.0f;
				actualWidth = Sprite_data->width - (scaleWidth * Sprite_data->width);

				float aspectRatioHeight = (float)UserSettings::GetHeight() / 800;
				float scaleHeight = aspectRatioHeight - 1.0f;
				actualHeight = Sprite_data->height - (scaleHeight * Sprite_data->height);*/

				vertexData[0].x = Sprite_data->centerX - (actualWidth) / 2;
				vertexData[0].y = Sprite_data->centerY + (actualHeight) / 2;
				vertexData[0].z = 0.0f;
				vertexData[0].u = 0.0f + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[0].v = 0.0f;

				vertexData[1].x = Sprite_data->centerX + (actualWidth) / 2;
				vertexData[1].y = Sprite_data->centerY + (actualHeight) / 2;
				vertexData[1].z = 0.0f;
				vertexData[1].u = Sprite_data->tex_u + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[1].v = 0.0f;

				vertexData[2].x = Sprite_data->centerX - (actualWidth) / 2;
				vertexData[2].y = Sprite_data->centerY - (actualHeight) / 2;
				vertexData[2].z = 0.0f;
				vertexData[2].u = 0.0f + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[2].v = Sprite_data->tex_v;

				vertexData[3].x = Sprite_data->centerX + (actualWidth) / 2;
				vertexData[3].y = Sprite_data->centerY - (actualHeight) / 2;
				vertexData[3].z = 0.0f;
				vertexData[3].u = Sprite_data->tex_u + Sprite_data->tex_u * SpriteAtlasIndex;
				vertexData[3].v = Sprite_data->tex_v;
			}
			// III. The buffer must be "unlocked" before it can be used
			{
				HRESULT result = s_vertexBuffer->Unlock();
				if (FAILED(result))
				{
					MessageBox(s_mainWindow, "DirectX failed to unlock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
					return false;
				}
			}
		}
		return true;
	}

	bool Sprite::AnimateSpriteSheet(int indexX, int indexY)
	{
		sVertex* vertexData;
		{
			const unsigned int lockEntireBuffer = 0;
			const DWORD useDefaultLockingBehavior = 0;
			HRESULT result = s_vertexBuffer->Lock(lockEntireBuffer, lockEntireBuffer,
				reinterpret_cast<void**>(&vertexData), useDefaultLockingBehavior);
			if (FAILED(result))
			{
				MessageBox(s_mainWindow, "DirectX failed to lock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
				return false;
			}
		}
		// II. Fill the buffer
		{
			vertexData[0].u = 0.0f + Sprite_data->tex_u * indexX;
			vertexData[0].v = 0.0f + Sprite_data->tex_v * indexY;

			vertexData[1].u = Sprite_data->tex_u + Sprite_data->tex_u * indexX;
			vertexData[1].v = 0.0f + Sprite_data->tex_v * indexY;

			vertexData[2].u = 0.0f + Sprite_data->tex_u * indexX;
			vertexData[2].v = Sprite_data->tex_v + Sprite_data->tex_v * indexY;

			vertexData[3].u = Sprite_data->tex_u + Sprite_data->tex_u * indexX;
			vertexData[3].v = Sprite_data->tex_v + Sprite_data->tex_v * indexY;
		}
		// III. The buffer must be "unlocked" before it can be used
		{
			HRESULT result = s_vertexBuffer->Unlock();
			if (FAILED(result))
			{
				MessageBox(s_mainWindow, "DirectX failed to unlock the vertex buffer", "No Vertex Buffer", MB_OK | MB_ICONERROR);
				return false;
			}
		}
		return true;
	}

	void Sprite::setSpriteAtlasIndex(int index)
	{
		SpriteAtlasIndex = index;
		DWORD usage = 0;
		{
			usage |= D3DUSAGE_WRITEONLY | D3DUSAGE_DYNAMIC;
		}

		if (!ChangeVertexBuffer(usage))
		{
			
		}
	}


	void Sprite::Render() {
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_BeginEvent(0, L"Draw Sprite");
#endif
		HRESULT result3 = Set(s_direct3dDevice);
		assert(SUCCEEDED(result3)); 
		SetSampler();

		HRESULT resul2t = s_direct3dDevice->SetVertexDeclaration(s_vertexDeclaration);
		// There can be multiple streams of data feeding the display adaptor at the same time
		const unsigned int streamIndex = 0;
		// It's possible to start streaming data in the middle of a vertex buffer
		const unsigned int bufferOffset = 0;
		// The "stride" defines how large a single vertex is in the stream of data
		const unsigned int bufferStride = sizeof(sVertex);

		HRESULT result = s_direct3dDevice->SetStreamSource(streamIndex, s_vertexBuffer, bufferOffset, bufferStride);
		assert(SUCCEEDED(result));

		s_direct3dDevice->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
		s_direct3dDevice->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
		s_direct3dDevice->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_INVSRCALPHA);

		s_direct3dDevice->SetRenderState(D3DRS_ZWRITEENABLE, FALSE);
		s_direct3dDevice->SetRenderState(D3DRS_ZENABLE, D3DZB_FALSE);

		// Render objects from the current streams
		{
			// We are using triangles as the "primitive" type,
			// and we have defined the vertex buffer as a triangle list
			// (meaning that every triangle is defined by three vertices)
			const D3DPRIMITIVETYPE primitiveType = D3DPT_TRIANGLESTRIP;
			// It's possible to start rendering primitives in the middle of the stream
			const unsigned int indexOfFirstVertexToRender = 0;
			const unsigned int indexOfFirstIndexToUse = 0;
			//--const unsigned int vertexCountToRender = 4;
			const unsigned int primitiveCountToRender = 2; //Since a Quad

			//Function to draw a single primitive(ie.Quad/Strip)
			result = s_direct3dDevice->DrawPrimitive( primitiveType, indexOfFirstVertexToRender, primitiveCountToRender );
			assert( SUCCEEDED( result ) );
		}

		s_direct3dDevice->SetRenderState(D3DRS_ZWRITEENABLE, TRUE);
		s_direct3dDevice->SetRenderState(D3DRS_ZENABLE, D3DZB_TRUE);
		s_direct3dDevice->SetRenderState(D3DRS_ZFUNC, D3DCMP_LESSEQUAL);
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_EndEvent();
#endif
	}

}
