#pragma once
#ifndef GAMEOBJECT
#define GAMEOBJECT
#include "Graphics.h"

namespace BlackRock
{
class GameObject
{
		const char *assetPath_;

		int assetType_;

		void Initialize();
		Sprite::sRectangle *m_sprite_data;

		Sprite *m_currentSprite;
		Mesh *m_currentMesh;
		Material *m_currentMaterial;
		DebugLine *m_currentDebugLine;

	public:
		GameObject(const char *m_assetPath, int assetType);
		GameObject(int assetType);
		//General Functions
		void SetPosition(float m_posn[3]);
		void SetScale(float m_scale[3]);
		void SetRotation(float m_rotate[3]);

		float* GetRotation();
		float* GetPosition();
	
		//Sprite specific Functions
		void SetTextureCoor(float m_texc[2]);

		//Mesh specific Functions
		void SetMaterial(const char *materialPath_);
		void SetUVAnimateSpeed(float uvSpeed[2]);

		//DebugLine sppecific Functions
		void AddLine(float m_posn_beg[3], float m_posn_end[3]);
		void ChangeLine(float m_posn_beg[3], float m_posn_end[3]);
		
};
}

#endif 

