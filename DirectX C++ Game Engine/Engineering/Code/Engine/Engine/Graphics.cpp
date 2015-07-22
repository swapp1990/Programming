#define D3D_DEBUG_INFO
#include <d3d9.h>
#include <d3dx9shader.h>
#include <cstdint>
#include <cassert>
#include <iostream>
#include <math.h> 

#include "Graphics.h"
#include "../UserSettings/UserSettings.h"

namespace BlackRock
{
	bool ResetCamera_ = false;
	static int ResetCamera()
	{

		int temp;
		ResetCamera_ = !ResetCamera_;
		return 0;
	}

	Graphics::Graphics()
	{
		mainCamera_ = new Camera();

		m_light_1 = new Light();

		material_array = new std::vector<Material *>;
		mesh_array = new std::vector<Mesh *>;
		debugLine_array = new std::vector<DebugLine *>;
		debug_mesh_array = new std::vector<Mesh *>;
		sprite_array = new std::vector<Sprite *>;

		//Sprite_timer = new Sprite("data/numbers.dds");
		//Sprite_cat = new Sprite("data/runningcat.dds");
		temp1 = new DebugLine();
		g_font = new Font();
		slider_value = new float;
		*slider_value = 20.0f;

		defaultText = new Font::sTextStruct();
		posn_x_camera = posn_y_camera = 0.0f;
		posn_x_light = posn_z_light= 0.0f;
		posn_y_light = 0.0f;

		incrementValue = 0.5f;
		//slider_value = new int();
		tempx = 0.0f;
		isDebugModeOn_ = true;
	}
	//static variables
	HWND s_mainWindow = NULL;
	IDirect3D9* s_direct3dInterface = NULL;
	IDirect3DDevice9* s_direct3dDevice = NULL;

	bool Graphics::Init(const HWND i_mainWindow)
	{
		if (!CreateInterface(i_mainWindow))
		{
			return false;
		}
		// Create an interface to a Direct3D device
		if (CreateDevice(i_mainWindow))
		{
			s_mainWindow = i_mainWindow;
		}
		else
		{
			goto OnError;
		}

		for (DWORD i = 0; i < 8; ++i)
		{
			s_direct3dDevice->SetSamplerState(i, D3DSAMP_MINFILTER, D3DTEXF_LINEAR);
			s_direct3dDevice->SetSamplerState(i, D3DSAMP_MAGFILTER, D3DTEXF_LINEAR);
			s_direct3dDevice->SetSamplerState(i, D3DSAMP_MIPFILTER, D3DTEXF_LINEAR);
		}

		m_light_1->Initialize();
		g_font->Initialize(s_direct3dDevice, 20.0f);
		debug_menu = new DebugMenu(s_direct3dDevice);
		float *camSpeed = new float;
		camSpeed = &mainCamera_->camSpeed;

#ifdef _DEBUG
		debug_menu->CreateSlider("Speed: ", 20.0f, 30.0f, camSpeed);
		//debug_menu->CreateSlider("TurnSpeed: ", 20.0f, 30.0f, camSpeed);
		debug_menu->CreateButton("Reset Cam:", &ResetCamera);
		debug_menu->CreateText("FPS: ", "60.0");
		debug_menu->CreateDebugCube();
		debug_menu->CreateDebugSphere();
		bool *flyCamFlag = new bool;
		flyCamFlag = &mainCamera_->flyCam_;
		debug_menu->CreateCheckBox("FlyCam: ", flyCamFlag);

#endif
		mainCamera_->Initialize(s_direct3dDevice, s_mainWindow);

		mainCamera_->Initialize(s_direct3dDevice, s_mainWindow);


		default_material = new Material("data/Models/Mario_Material.txt");
		default_material->Initialize(s_direct3dDevice, s_mainWindow, m_light_1);

		mainCamera_->SetConstantTable(default_material->GetConstantTableVertex(), default_material->GetConstantTableFragment());

		return true;
	OnError:

		ShutDown();
		return false;
	}


	bool Graphics::Initialize(const HWND i_mainWindow)
	{
		// Initialize the interface to the Direct3D9 library

		//Posn = {centerx, centerY, width, height, texU, texV}
		// centerX & centerY between -1 to 1.

		//Initialize all materials
		Sprite::sRectangle position_timerLabel(0.9f, 0.8f, 0.1f, 0.2f, 0.1f, 0.33f);
		Sprite::sRectangle position_timerCat(0.6f, -0.6f, 1.0f, 1.0f, 0.5f, 0.25f);
		for (unsigned int i = 0; i < material_array->size(); i++)
		{
			Material *currMat = (Material *)material_array->at(i);
			currMat->Initialize(s_direct3dDevice, s_mainWindow, m_light_1);
		}

		//Initialize all mesh
		for (unsigned int i = 0; i < mesh_array->size(); i++)
		{
			Mesh *currMesh = (Mesh *)mesh_array->at(i);
			currMesh->SetMaterial(default_material);
			//currMesh->Render();
			if (!currMesh->Initialize(s_direct3dDevice, s_mainWindow))
			{
				goto OnError;
			}
		}

		//Initialize all debug mesh
		for (unsigned int i = 0; i < debug_mesh_array->size(); i++)
		{
			Mesh *currMesh = (Mesh *)debug_mesh_array->at(i);
			currMesh->SetMaterial(default_material);
			//currMesh->Render();
			if (!currMesh->Initialize(s_direct3dDevice, s_mainWindow))
			{
				goto OnError;
			}
		}
		
		defaultText = g_font->AddText("Default", 10.0f, 20.0f, 40.0f, 80.0f);
		g_font->AddText("Swapp", 100.0f, 20.0f, 40.0f, 80.0f);
#ifdef _DEBUG
		
		defaultText = g_font->AddText("Default", 10.0f, 20.0f, 40.0f, 80.0f);
		g_font->AddText("Swapp", 100.0f, 20.0f, 40.0f, 80.0f);

		const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 1, 0);
		temp1->AddLine(D3DXVECTOR3(0.0f, -5.0f, 15.0f), D3DXVECTOR3(10.0f, -5.0f, 15.0f), colorToClear);
		debugLine_array->push_back(temp1);
#endif
		for (unsigned int i = 0; i < debugLine_array->size(); i++)
		{
			DebugLine *currDebugLine = (DebugLine *)debugLine_array->at(i);
			if (!currDebugLine->Load(s_direct3dDevice, s_mainWindow))
			{
				goto OnError;
			}
		}
		
		mainCamera_->SetConstantTableDebug(temp1->GetConstantTableVertex());
		
		mainCamera_->SetCamera();

		//Function Pointer Test

		return true;
	OnError:

		ShutDown();
		return false;
	}

	Camera* Graphics::GetCurrentCamera()
	{
		return mainCamera_;
	}



	void Graphics::CameraLookAt(float lookAtPsn[])
	{
		mainCamera_->SetLookAt(lookAtPsn);
	}

	void Graphics::InitializeMaterial(Material *newMaterial)
	{
		newMaterial->Initialize(s_direct3dDevice, s_mainWindow, m_light_1);
	}


	void Graphics::Render()
	{
		// Every frame an entirely new image will be created.
		// Before drawing anything, then, the previous image will be erased
		// by "clearing" the image buffer (filling it with a solid color)
		{
			const D3DRECT* subRectanglesToClear = NULL;
			const DWORD subRectangleCount = 0;
			const DWORD clearColorAndDepth = D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER;
			const D3DCOLOR colorToClear = D3DCOLOR_XRGB(0, 0, 0);
			const float depthToClear = 1.0f;
			const DWORD noStencilBuffer = 0;
			HRESULT result = s_direct3dDevice->Clear(subRectangleCount, subRectanglesToClear,
				clearColorAndDepth, colorToClear, depthToClear, noStencilBuffer);
			assert(SUCCEEDED(result));
		}

		// The actual function calls that draw geometry must be made between paired calls to
		// BeginScene() and EndScene()
		{
		HRESULT result = s_direct3dDevice->BeginScene();
		assert(SUCCEEDED(result));
		{
			{
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_BeginEvent(0, L"Mesh Rendering");
#endif
				
				float values_light_dirn[3] = { posn_x_light, posn_y_light, posn_z_light };
				default_material->SetLight(values_light_dirn);
				//debug_menu->Render();
				for (unsigned int i = 0; i < mesh_array->size(); i++)
				{
					Mesh *currMesh = (Mesh *)mesh_array->at(i);
					currMesh->Render();
				}
				
				mainCamera_->SetCamera();
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_EndEvent();
#endif
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_BeginEvent(0, L"Font Rendering");
#endif
				//g_font->Render();


				defaultText->m_string = "Default_2";
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_EndEvent();
#endif

#ifdef _DEBUG
				if (debug_menu->isDebugEnabled_)
				{
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
					D3DPERF_BeginEvent(0, L"Debug Rendering");
#endif
					for (unsigned int i = 0; i < debugLine_array->size(); i++)
					{
						DebugLine *currDebugLine = (DebugLine *)debugLine_array->at(i);
						currDebugLine->Render();
					}
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
					D3DPERF_BeginEvent(0, L"Debug Mesh Rendering");
#endif
					s_direct3dDevice->SetRenderState(D3DRS_FILLMODE, D3DFILL_WIREFRAME);
					for (unsigned int i = 0; i < debug_mesh_array->size(); i++)
					{
						Mesh *currMesh = (Mesh *)debug_mesh_array->at(i);
						currMesh->Render();
					}
					debug_menu->Render();
					s_direct3dDevice->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
					D3DPERF_EndEvent();
#endif

#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
					D3DPERF_EndEvent();
#endif

				}
#endif

#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_BeginEvent(0, L"Sprite Rendering");
#endif
				
				for (unsigned int i = 0; i < sprite_array->size(); i++)
				{
					Sprite *currSprite = (Sprite *)sprite_array->at(i);
					currSprite->Render();
				}

				
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
				D3DPERF_EndEvent();
#endif
			}
		}
		result = s_direct3dDevice->EndScene();
		assert(SUCCEEDED(result));
	}

		char buf[2048];
		sprintf_s(buf, "test_value: %f\n", mainCamera_->camSpeed);
		//OutputDebugString(buf);
		// Everything has been drawn to the "back buffer", which is just an image in memory.
		// In order to display it, the contents of the back buffer must be "presented"
		// (to the front buffer)
		{
			const RECT* noSourceRectangle = NULL;
			const RECT* noDestinationRectangle = NULL;
			const HWND useDefaultWindow = NULL;
			const RGNDATA* noDirtyRegion = NULL;
			HRESULT result = s_direct3dDevice->Present(noSourceRectangle, noDestinationRectangle, useDefaultWindow, noDirtyRegion);
			if (ResetCamera_)
			{
				ResetCamera_ = false;
				mainCamera_->ResetCamera();
			}
			assert(SUCCEEDED(result));
		}
	}

	void Graphics::SetMeshArray(std::vector<Mesh *> *i_meshArray)
	{
		/*D3DXVECTOR3 m_position2(0.0f, -1.2f, 5.0f);
		m_meshArray = i_meshArray;
		for (unsigned int i = 0; i < m_meshArray->size(); i++)
		{
			Mesh *currentMesh = (Mesh *)m_meshArray->at(i);
			if (!currentMesh->Initialize(s_direct3dDevice, s_mainWindow))
			{
				//goto OnError;
			}
			currentMesh->SetMaterial(m_materialFloor);
			currentMesh->SetPosition(m_position2);
			currentMesh->SetRotation(D3DXVECTOR3(350.0f, 0.0f, 0.0f));
			currentMesh->SetScale(D3DXVECTOR3(1.3f, 1.3f,1.0f));
		}*/
	}

	void Graphics::changePosnX_Camera(float incrementValue)
	{
	
		mainCamera_->changePosnX_Camera(incrementValue);
	}
	
	void Graphics::changePosnY_Camera(float incrementValue)
	{
		posn_y_camera += incrementValue;
		mainCamera_->changePosnY_Camera(incrementValue);
	}

	void Graphics::changePosnZ_Camera(float incrementValue)
	{
		mainCamera_->changePosnZ_Camera(incrementValue);
	}

	void Graphics::changePosnX_Light(float incrementValue)
	{
		posn_x_light += incrementValue;
		char buf[2048];
		sprintf_s(buf, "light_x: %f\n", posn_x_light);
		OutputDebugString(buf);
	}

	void Graphics::changePosnZ_Light(float incrementValue)
	{
		posn_z_light += incrementValue;
		char buf[2048];
		sprintf_s(buf, "light_z: %f\n", posn_z_light);
		OutputDebugString(buf);
	}

	void Graphics::DebugMenu_Clicked()
	{
		char buf[2048];
		sprintf_s(buf, "Space Created\n");
		OutputDebugString(buf);
		debug_menu->SpaceKeyPressed();
	}

	void Graphics::DebugMenu_Move()
	{
		debug_menu->ArrowkeyPressed();
	}

	void Graphics::DebugMenu_Disable()
	{
		debug_menu->DisableDebugMenu();
	}

	void Graphics::changeAtlasIndex(int index)
	{
		//Sprite_timer->setSpriteAtlasIndex(index);
	}

	void Graphics::changeSpriteSheet(int indexX, int indexY)
	{
		//Sprite_cat->AnimateSpriteSheet(indexX, indexY);
	}

	bool Graphics::ShutDown()
	{
		bool wereThereErrors = false;

		if (s_direct3dInterface)
		{
			if (s_direct3dDevice)
			{
				s_direct3dDevice->Release();
				s_direct3dDevice = NULL;
			}

			s_direct3dInterface->Release();
			s_direct3dInterface = NULL;
		}

		s_mainWindow = NULL;

		return !wereThereErrors;
	}

	// Helper Functions
	//=================

	// Initialize
	//-----------

	bool Graphics::CreateDevice(const HWND i_mainWindow)
	{
		const UINT useDefaultDevice = D3DADAPTER_DEFAULT;
		const D3DDEVTYPE useHardwareRendering = D3DDEVTYPE_HAL;
		const DWORD useHardwareVertexProcessing = D3DCREATE_HARDWARE_VERTEXPROCESSING;
		D3DPRESENT_PARAMETERS presentationParameters = { 0 };
		{
			presentationParameters.BackBufferWidth = UserSettings::GetWidth();
			presentationParameters.BackBufferHeight = UserSettings::GetHeight();
			presentationParameters.BackBufferFormat = D3DFMT_X8R8G8B8;
			presentationParameters.BackBufferCount = 1;
			presentationParameters.MultiSampleType = D3DMULTISAMPLE_NONE;
			presentationParameters.SwapEffect = D3DSWAPEFFECT_DISCARD;
			presentationParameters.hDeviceWindow = i_mainWindow;
			bool g_shouldRenderFullScreen = UserSettings::IsFullScreenModeEnabled() != 0;
			presentationParameters.Windowed = g_shouldRenderFullScreen ? FALSE : TRUE;
			presentationParameters.EnableAutoDepthStencil = TRUE;
			presentationParameters.AutoDepthStencilFormat = D3DFMT_D16;
			presentationParameters.PresentationInterval = D3DPRESENT_INTERVAL_DEFAULT;
		}
		HRESULT result = s_direct3dInterface->CreateDevice(useDefaultDevice, useHardwareRendering,
			i_mainWindow, useHardwareVertexProcessing, &presentationParameters, &s_direct3dDevice);
		if (SUCCEEDED(result))
		{
			return true;
		}
		else
		{
			MessageBox(i_mainWindow, "DirectX failed to create a Direct3D9 device", "No D3D9 Device", MB_OK | MB_ICONERROR);
			return false;
		}
	}

	bool Graphics::CreateInterface(const HWND i_mainWindow)
	{
		// D3D_SDK_VERSION is #defined by the Direct3D header files,
		// and is just a way to make sure that everything is up-to-date
		s_direct3dInterface = Direct3DCreate9(D3D_SDK_VERSION);
		if (s_direct3dInterface)
		{
			return true;
		}
		else
		{
			MessageBox(i_mainWindow, "DirectX failed to create a Direct3D9 interface", "No D3D9 Interface", MB_OK | MB_ICONERROR);
			return false;
		}
	}

	void Graphics::ChangeLights()
	{
		///Lihgts
		
		/*if (posn_x_light > 14.0f)
		{
			posn_x_light = 13.5f;
			incrementValue = -incrementValue;
		}
		else if (posn_x_light < -10.0f)
		{
			incrementValue = -incrementValue;
		}
		posn_x_light += incrementValue;*/
	}

}
