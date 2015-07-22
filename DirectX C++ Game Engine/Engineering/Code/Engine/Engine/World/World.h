#ifndef WORLD
#define WORLD
#pragma once
#include <vector>
#include <stdio.h>
#include <Windows.h>
#include "../Mesh.h"
/*#include "Actor.h"
#include "Renderer.h"
#include "Timing.h"
#include "SmartPointer.h"*/
#include "..\Utils\Singleton.h"
#include "..\Timing.h"
#include "..\GameObject.h"

using namespace BlackRock;
namespace BlackRock
{
	
class World
{
	friend BlackRock::Singleton<World>;
	World();
	~World();
	//std::vector<SmartPointer<Actor>> actorArray;
	//std::vector<Actor *> *actorArrayType;
	
	bool bQuit;
	float tempx;
public:
	float deltaTime;
	float timeElapsed_;
	bool keyIsPressed;
	unsigned int keyPressedCode;

	GameObject *player_;
	GameObject *playerDebugLine;
	std::vector<GameObject *> *enemyArray;
	//SharedPtr<Actor> CreateActor(const char * i_Name);

	//void addActor(const SmartPointer<Actor> actor);
	//void addActorType(Actor* actor);
	//Actor* getActorFromType(const char *type);

	//Renderer* renderer;
	//Graphics* graphics;
	//Timing* timing;
	D3DXVECTOR3 GetSeperativeForce(D3DXVECTOR3 m_posn, float distance);
	void EnemyChase();

	void Update();
	bool InitializeWorld(const HWND i_mainWindow);
	float GetFrameTime();

	void AddToMeshArray(Mesh *currentMesh);
	void startRendering();

	void changePosnX(float incrementValue);
	void changePosnY(float incrementValue);
	void changePosnZ(float incrementValue);
};
}
typedef BlackRock::Singleton<World> g_World;
#endif
