#include "Camera.h"
#include <stdio.h>
#include <iostream>
//#include ""
#include "../UserSettings/UserSettings.h"

namespace BlackRock
{
	Camera::Camera()
	{
		posn_x_camera = posn_y_camera = posn_z_camera = 0.0f;
		posn_y_camera = -5.0f;
		rotn_x_camera = rotn_y_camera = 0.0f;
		flyCam_ = false;
		camSpeed = -0.5f;
		//rotn_x_camera = -1.00f;
		//rotn_x_camera = 50.0f;
		//posn_y_camera = -10.0f;
		m_position_camera = { posn_x_camera, posn_y_camera, -20.0f };
		m_position_lookAt = { -3.0f, 1.5f, 0.0f };
	}

	void Camera::Initialize(IDirect3DDevice9* i_direct3dDevice, const HWND i_mainWindow)
	{
		s_direct3dDevice = i_direct3dDevice;
		s_mainWindow = i_mainWindow;
	}

	D3DXMATRIX Camera::GetTransform_worldToView()
	{
		//rotn_x_camera = rotn_x_camera * 3.14f / 180.0f;
		//rotn_y_camera = rotn_y_camera * 3.14f / 180.0f;
		D3DXMATRIX transform_viewToWorld;

		D3DXQUATERNION qX;
		D3DXQuaternionIdentity(&qX);
		D3DXVECTOR3 axisX(1, 0, 0);
		D3DXQuaternionRotationAxis(&qX, &axisX, rotn_x_camera);


		D3DXQUATERNION qY;
		D3DXQuaternionIdentity(&qY);
		D3DXVECTOR3 axisY(0, 1, 0);
		D3DXQuaternionRotationAxis(&qY, &axisY, rotn_y_camera);

		D3DXQUATERNION Final;
		Final = qX * qY;
		D3DXMatrixTransformation(&transform_viewToWorld,
			NULL, NULL, NULL, NULL, &Final, &m_position_camera);

		D3DXMATRIX transform_worldToView;

		D3DXMatrixInverse(&transform_worldToView, NULL, &transform_viewToWorld);

		//D3DXMatrixLookAtLH(&transform_worldToView, &m_position_camera, &m_position_lookAt, &D3DXVECTOR3(0, 1, 0));

		return transform_worldToView;
	}

	D3DXMATRIX Camera::GetTransform_viewToScreen()
	{
		//ViewToScreen
		D3DXMATRIX transform_viewToScreen;
		{
			float w_width = (float)UserSettings::GetWidth();
			float w_height = (float)UserSettings::GetHeight();
			const float aspectRatio = (float)w_width / w_height;
			float m_fieldOfView_y = D3DXToRadian(60.0f);
			float m_zNear = 10.1f;
			float m_zFar = 10000.0f;
			D3DXMatrixPerspectiveFovLH(&transform_viewToScreen, m_fieldOfView_y, aspectRatio, m_zNear, m_zFar);
		}
		return transform_viewToScreen;
	}

	void Camera::SetConstantTable(ID3DXConstantTable * i_pConstantTable_vertex, ID3DXConstantTable * i_pConstantTable_fragement)
	{
		s_pConstantTable_vertex = i_pConstantTable_vertex;
		s_pConstantTable_fragment = i_pConstantTable_fragement;
	}

	void Camera::SetConstantTableDebug(ID3DXConstantTable * i_pConstantTable_vertex)
	{
		s_pConstantTable_vertex_debug = i_pConstantTable_vertex;
	}

	void Camera::changePosnX_Camera(float incrementValue)
	{
		//posn_x_camera += incrementValue;
		rotn_y_camera += -incrementValue;
		char buf[2048];
		sprintf_s(buf, "rotn_y_camera: %f\n", rotn_y_camera);
		//OutputDebugString(buf);
		if (rotn_y_camera < -0.4f)
		{
			//rotn_y_camera = -0.4f;
		}
		else if (rotn_y_camera > 0.4f)
		{
			//rotn_y_camera = 0.4f;
		}
	}

	void Camera::changePosnY_Camera(float incrementValue)
	{
		rotn_x_camera += incrementValue;
		char buf[2048];
		sprintf_s(buf, "rotn_x_camera: %f\n", rotn_x_camera);
		//OutputDebugString(buf);
		if (rotn_x_camera < -0.4f)
		{
			rotn_x_camera = -0.4f;
		}
		else if (rotn_x_camera > 0.4f)
		{
			rotn_x_camera = 0.4f;
		}
	}

	void Camera::changePosnZ_Camera(float incrementValue)
	{
		float alpha = rotn_y_camera;
		char buf[2048];
		incrementValue += camSpeed;
		//alpha = alpha * 3.14f / 180.0f;
		posn_x_camera += incrementValue * sin(alpha);
		posn_z_camera += incrementValue * cos(alpha);
		float alpha2 = rotn_x_camera;
		//alpha2 = alpha2 * 3.14f / 180.0f;
		sprintf_s(buf, "rotn_x_camera: %f\n", alpha2);
		//OutputDebugString(buf);
		if (flyCam_)
		{
			posn_y_camera += incrementValue * -sin(alpha2);
		}

		//posn_x_camera += incrementValue * sin(alpha2);
		//posn_y = posn_y + incrementValue * cos(alpha);

	}

	void Camera::ResetCamera()
	{
		posn_x_camera = posn_y_camera = posn_z_camera = 0.0f;
		posn_y_camera = -5.0f;
		rotn_x_camera = rotn_y_camera = 0.0f;
	}

	void Camera::SetLookAt(D3DXVECTOR3 i_posn)
	{
		m_position_lookAt = i_posn;
	}

	void Camera::SetCamera()
	{
		//Set Shader Constants
		m_position_camera = { posn_x_camera, posn_y_camera, posn_z_camera };
#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_BeginEvent(0, L"Draw Camera");
#endif

		/*D3DXHANDLE handle_transform_worldToView_debug = s_pConstantTable_vertex_debug->GetConstantByName(NULL, "g_transform_worldToView");
		s_pConstantTable_vertex_debug->SetMatrix(s_direct3dDevice, handle_transform_worldToView_debug, &GetTransform_worldToView());

		D3DXHANDLE handle_transform_viewToScreen_debug = s_pConstantTable_vertex_debug->GetConstantByName(NULL, "g_transform_viewToScreen");
		s_pConstantTable_vertex_debug->SetMatrix(s_direct3dDevice, handle_transform_viewToScreen_debug, &GetTransform_viewToScreen());*/

		handle_transform_worldToView = s_pConstantTable_vertex->GetConstantByName(NULL, "g_transform_worldToView");
		s_pConstantTable_vertex->SetMatrix(s_direct3dDevice, handle_transform_worldToView, &GetTransform_worldToView());

		handle_transform_viewToScreen = s_pConstantTable_vertex->GetConstantByName(NULL, "g_transform_viewToScreen");
		s_pConstantTable_vertex->SetMatrix(s_direct3dDevice, handle_transform_viewToScreen, &GetTransform_viewToScreen());


		handle_cameraPosn = s_pConstantTable_fragment->GetConstantByName(NULL, "g_camera_position");
		s_pConstantTable_vertex->SetFloatArray(s_direct3dDevice, handle_cameraPosn, m_position_camera, 3);

#ifdef EAE6320_GRAPHICS_AREPIXEVENTSENABLED
		D3DPERF_EndEvent();
#endif
	}
}
