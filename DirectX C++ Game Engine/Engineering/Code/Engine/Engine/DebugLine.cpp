#include "DebugLine.h"
#include <cstdint>
#include <cassert>
#include <fstream>
#include <sstream>
#include <iostream>
#include "../UserSettings/UserSettings.h"

namespace BlackRock
{
	D3DVERTEXELEMENT9 DebugLine::s_vertexElements[] =
	{
		{ 0, 0, D3DDECLTYPE_FLOAT3, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_POSITION, 0 },
		// COLOR0, D3DCOLOR == 4 bytes
		{ 0, 12, D3DDECLTYPE_D3DCOLOR, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_COLOR, 0 },
		D3DDECL_END()
	};

	DebugLine::DebugLine()
	{

		numberOfLines = 0;
	}

	bool DebugLine::Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow)
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

	void DebugLine::SetData(sRectangle* i_position)
	{
		sprite_data = new sRectangle(i_position->centerX, i_position->centerY, i_position->width, i_position->height, i_position->tex_u, i_position->tex_v);
	}

	void DebugLine::AddLine(const D3DVECTOR& i_start, const D3DVECTOR& i_end, const D3DCOLOR i_color)
	{
		startVectorList[numberOfLines] = i_start;
		endVectorList[numberOfLines] = i_end;
		const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 1, 0);
		VectorColorList[numberOfLines] = colorToClear;
		numberOfLines++;
	}

	void DebugLine::AddLine(const float i_start[], const float i_end[], const D3DCOLOR i_color)
	{
		startVectorList[numberOfLines].x = i_start[0];
		startVectorList[numberOfLines].y = i_start[1];
		startVectorList[numberOfLines].z = i_start[2];

		endVectorList[numberOfLines].x = i_end[0];
		endVectorList[numberOfLines].y = i_end[1];
		endVectorList[numberOfLines].z = i_end[2];
		const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 1, 0);
		VectorColorList[numberOfLines] = colorToClear;
		numberOfLines++;
	}

	void DebugLine::ChangeLine(const float i_start[], const float i_end[])
	{
		for (int number = 0; number < numberOfLines; number++)
		{
			startVectorList[number].x = i_start[0];
			startVectorList[number].y = i_start[1];
			startVectorList[number].z = i_start[2];

			endVectorList[number].x = i_end[0];
			endVectorList[number].y = i_end[1];
			endVectorList[number].z = i_end[2];
		}
	}

	bool DebugLine::Load(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow)
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

	ID3DXConstantTable * DebugLine::GetConstantTableVertex()
	{
		return m_pConstantTable_vertex;
	}

	bool DebugLine::LoadAndAllocateShaderProgram(const char* i_path, void*& o_compiledShader, std::string* o_errorMessage)
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

	bool DebugLine::LoadVertexShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = "data/vertexShader_DebugLine.bsl";
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
			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	bool DebugLine::LoadFragmentShader(IDirect3DDevice9* i_direct3dDevice)
	{
		// Load the source code from file and compile it
		ID3DXBuffer* compiledShader;
		{
			const char* sourceCodeFileName = "data/fragmentShader_DebugLine.bsl";
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

			/*HRESULT result2 = D3DX
			FromFile(i_direct3dDevice, assetPath_, &texture);
			assert(SUCCEEDED(result2));*/
			compiledShader = NULL;
		}
		return !wereThereErrors;
	}

	bool DebugLine::CreateVertexBuffer(const DWORD i_usage)
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
		numberOfVertices = numberOfLines * 2;
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
				int counter = 0;
				for (int number = 0; number < numberOfLines; number++)
				{
					vertexData[counter].x = startVectorList[number].x;
					vertexData[counter].y = startVectorList[number].y;
					vertexData[counter].z = startVectorList[number].z;
					const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 255, 0);
					vertexData[counter].color = colorToClear;

					counter++;
					vertexData[counter].x = endVectorList[number].x;
					vertexData[counter].y = endVectorList[number].y;
					vertexData[counter].z = endVectorList[number].z;
					vertexData[counter].color = colorToClear;

					/*vertexData[counter].x = 0.0f;
					vertexData[counter].y = 2.5f;
					vertexData[counter].z = 0.0f;
					const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 255, 0);
					vertexData[counter].color = colorToClear;

					counter++;
					vertexData[counter].x = 5.0f;
					vertexData[counter].y = 2.5f;
					vertexData[counter].z = 0.0f;
					vertexData[counter].color = colorToClear;*/
				}
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

	bool DebugLine::ChangeVertexBuffer(const DWORD i_usage)
	{
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
				int counter = 0;
				for (int number = 0; number < numberOfLines; number++)
				{
					vertexData[counter].x = startVectorList[number].x;
					vertexData[counter].y = startVectorList[number].y;
					vertexData[counter].z = startVectorList[number].z;
					const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 255, 0);
					vertexData[counter].color = colorToClear;

					counter++;
					vertexData[counter].x = endVectorList[number].x;
					vertexData[counter].y = endVectorList[number].y;
					vertexData[counter].z = endVectorList[number].z;
					vertexData[counter].color = colorToClear;
				}
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


	void DebugLine::Render() {
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_BeginEvent(0, L"Draw Debug");
#endif
		HRESULT result2 = s_direct3dDevice->SetVertexShader(m_vertexShader);
		assert(SUCCEEDED(result2));
		result2 = s_direct3dDevice->SetPixelShader(m_fragmentShader);
		assert(SUCCEEDED(result2));

		HRESULT resul2t = s_direct3dDevice->SetVertexDeclaration(s_vertexDeclaration);

		DWORD usage = 0;
		{
			usage |= D3DUSAGE_WRITEONLY | D3DUSAGE_DYNAMIC;
		}

		if (!ChangeVertexBuffer(usage))
		{

		}

		// There can be multiple streams of data feeding the display adaptor at the same time
		const unsigned int streamIndex = 0;
		// It's possible to start streaming data in the middle of a vertex buffer
		const unsigned int bufferOffset = 0;
		// The "stride" defines how large a single vertex is in the stream of data
		const unsigned int bufferStride = sizeof(sVertex);

		HRESULT result = s_direct3dDevice->SetStreamSource(streamIndex, s_vertexBuffer, bufferOffset, bufferStride);
		assert(SUCCEEDED(result));

		// Render objects from the current streams
		{
			// We are using Linelist as the "primitive" type,

			const D3DPRIMITIVETYPE lineList = D3DPT_LINELIST;
			// It's possible to start rendering primitives in the middle of the stream
			const unsigned int indexOfFirstVertexToRender = 0;
			const unsigned int primitiveCountToRender = numberOfLines; //Since a Line

			//Function to draw a single primitive(ie.Quad/Strip)
			result = s_direct3dDevice->DrawPrimitive(lineList, indexOfFirstVertexToRender, primitiveCountToRender);
			assert( SUCCEEDED( result ) );
		}

#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_EndEvent();
#endif
	}

}
