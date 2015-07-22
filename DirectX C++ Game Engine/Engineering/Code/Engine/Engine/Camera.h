#pragma once
#pragma once
#ifndef EAE6320_CAMERA_H
#define EAE6320_CAMERA_H

#include <d3d9.h>
#include <d3dx9shader.h>

namespace BlackRock
{
	class Camera
	{
		D3DXVECTOR3 m_position_camera;
		D3DXVECTOR3 m_position_lookAt;
		IDirect3DDevice9* s_direct3dDevice;
		ID3DXConstantTable * s_pConstantTable_vertex;
		ID3DXConstantTable * s_pConstantTable_vertex_debug;
		ID3DXConstantTable * s_pConstantTable_fragment;
		HWND s_mainWindow;
		float posn_x_camera, posn_y_camera, posn_z_camera;
		float rotn_x_camera, rotn_y_camera;
	public:
		bool flyCam_;
		float camSpeed;
		Camera();
		void Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow);
		void SetConstantTable(ID3DXConstantTable * i_pConstantTable_vertex, ID3DXConstantTable * i_pConstantTable_fragement);
		void SetConstantTableDebug(ID3DXConstantTable * i_pConstantTable_vertex);
		D3DXMATRIX GetTransform_worldToView();
		D3DXMATRIX GetTransform_viewToScreen();

		D3DXHANDLE handle_transform_worldToView;
		D3DXHANDLE handle_transform_viewToScreen;
		D3DXHANDLE handle_cameraPosn;

		void changePosnX_Camera(float incrementValue);
		void changePosnY_Camera(float incrementValue);
		void changePosnZ_Camera(float incrementValue);
		void SetCamera();
		void ResetCamera();
		void SetLookAt(D3DXVECTOR3 i_posn);

		~Camera();
	};
}
#endif
