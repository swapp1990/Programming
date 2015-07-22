#include "World.h"

/*#include <stdio.h>
#include <conio.h>
#include <iostream>*/

#include "../Graphics.h"
namespace BlackRock
{

static bool bKeyPressed = false;
static unsigned int keyCode = 0;
static void OnKeyPressed( unsigned int i_CharID )
{
	bKeyPressed = true;
	keyCode = i_CharID;
}

World::World()
{

	//actorArrayType = new std::vector<Actor *>;
	enemyArray = new std::vector<GameObject *>;
	//Cheesy::setKeyDownCallback( OnKeyPressed );
	deltaTime = 0.0f;
	bQuit = false;
	tempx = 0.0f;
}

void World::AddToMeshArray(Mesh *currentMesh)
{

}

bool World::InitializeWorld(const HWND i_mainWindow)
{
	g_Timing::Get().init();

	if (!g_Graphics::Get().Init(i_mainWindow))
	{
		return false;
	}
	//GoT Logo
	/*GameObject *sprite_logo = new GameObject("data/got.dds", 0);
	float sprite_logo_posn[] = { 0.0f, 0.7f, 0.0f };
	sprite_logo->SetPosition(sprite_logo_posn);
	float sprite_logo_scale[] = { 1.2f, 1.0f, 1.0f };
	sprite_logo->SetScale(sprite_logo_scale);*/

	//Running Cat
	/*GameObject *sprite_cat = new GameObject("data/runningcat.dds", 0);
	float sprite_cat_posn[] = { 0.6f, -0.6f, 0.0f };
	sprite_cat->SetPosition(sprite_cat_posn);
	float sprite_cat_tex_c[] = { 0.5f, 0.25f};
	sprite_cat->SetTextureCoor(sprite_cat_tex_c);*/

	//Plane
	/*GameObject *mesh_plane = new GameObject("data/Plane_Floor.mesh", 1);
	float mesh_plane_posn[] = { 13.5f, -1.5f, 3.3f };
	mesh_plane->SetPosition(mesh_plane_posn);
	float mesh_plane_scale[] = { 9.0f, 1.0f, 14.0f };
	mesh_plane->SetScale(mesh_plane_scale);
	float mesh_plane_scale_rotate[] = { -90.0f, 0.0f, 0.0f };
	mesh_plane->SetRotation(mesh_plane_scale_rotate);*/

	GameObject *mesh_plane = new GameObject("data/Floor_Mesh.mesh", 1);
	float mesh_plane_posn[] = { 0.0f, 0.0f, 0.0f };
	mesh_plane->SetPosition(mesh_plane_posn);
	float mesh_plane_scale[] = { 0.1f, 0.1f, 0.1f };
	mesh_plane->SetScale(mesh_plane_scale);

	GameObject *mesh_railing = new GameObject("data/Railings.mesh", 1);
	mesh_railing->SetPosition(mesh_plane_posn);
	mesh_railing->SetScale(mesh_plane_scale);

	//GameObject *plane_mesh = new GameObject("data/plane.mesh", 1);
	//plane_mesh->SetPosition(mesh_plane_posn);

	GameObject *mesh_metal_bars = new GameObject("data/metal_bars.mesh", 1);
	mesh_metal_bars->SetPosition(mesh_plane_posn);
	mesh_metal_bars->SetScale(mesh_plane_scale);

	GameObject *mesh_walls = new GameObject("data/Walls.mesh", 1);
	mesh_walls->SetPosition(mesh_plane_posn);
	mesh_walls->SetScale(mesh_plane_scale);

	GameObject *mesh_ceiling = new GameObject("data/Ceilings.mesh", 1);
	mesh_ceiling->SetPosition(mesh_plane_posn);
	mesh_ceiling->SetScale(mesh_plane_scale);

	GameObject *mesh_cementWalls = new GameObject("data/Cement_Wall.mesh", 1);
	mesh_cementWalls->SetPosition(mesh_plane_posn);
	mesh_cementWalls->SetScale(mesh_plane_scale);

	GameObject *mesh_randomObjects = new GameObject("data/RandomObjects.mesh", 1);
	mesh_randomObjects->SetPosition(mesh_plane_posn);
	mesh_randomObjects->SetScale(mesh_plane_scale);

	GameObject *mesh_plane2 = new GameObject("data/Plane_Floor.mesh", 1);
	float mesh_plane2_posn[] = { -13.0f, -1.5f, 3.3f };
	mesh_plane2->SetPosition(mesh_plane2_posn);
	float mesh_plane2_scale[] = { 0.1f, 0.1f, 0.1f };
	mesh_plane2->SetScale(mesh_plane2_scale);
	float mesh_plane2_scale_rotate[] = { -90.0f, 0.0f, 0.0f };
	mesh_plane2->SetRotation(mesh_plane2_scale_rotate);
	
	//3D Mario
	player_ = new GameObject("data/Models/Mario.mesh", 1);
	float mesh_mario_posn[] = {-3.0f, 1.5f, -3.5f };
	player_->SetPosition(mesh_mario_posn);
	float mesh_mario_scale[] = { 0.2f, 0.2f, 0.2f };
	player_->SetScale(mesh_mario_scale);
	float mesh_mario_rotate[] = { -90.0f, -90.0f, 0.0f };
	player_->SetRotation(mesh_mario_rotate);

	
	//DebugLines
	/*playerDebugLine = new GameObject(2);
	float endPosn[3] = { player_->GetPosition()[0] + 2.0f, player_->GetPosition()[1], player_->GetPosition()[2] };
	playerDebugLine->AddLine(player_->GetPosition(), endPosn);*/

	if (!g_Graphics::Get().Initialize(i_mainWindow))
	{
		
	}
	mesh_plane->SetMaterial("data/material_floor.txt");
	
	mesh_metal_bars->SetMaterial("data/material_metal.txt");
	
	mesh_walls->SetMaterial("data/material_walls.txt");
	mesh_ceiling->SetMaterial("data/material_cement.txt");
	mesh_cementWalls->SetMaterial("data/material_cement.txt");
	mesh_randomObjects->SetMaterial("data/material_metal.txt");
	mesh_railing->SetMaterial("data/material_railing.txt");
	mesh_plane2->SetMaterial("data/Star_Material.txt");
	
	float uvSpeed[2] = { -0.2f, 0.0f };
	//mesh_plane2->SetUVAnimateSpeed(uvSpeed);
	//mesh_plane->SetUVAnimateSpeed(uvSpeed);
	
	//initialize Mesh
	return true;
}

void World::changePosnX(float incrementValue)
{
	float rotn_y = player_->GetRotation()[1];
	rotn_y += incrementValue * 50.0f;
	D3DXVECTOR3 m_rotation(-90.0f, rotn_y, 0.0f);
	//char buf[2048];
	//sprintf(buf, "rotn_y_camera: %f\n", rotn_y);
	//OutputDebugString(buf);

	player_->SetRotation(m_rotation);
}

void World::changePosnZ(float incrementValue)
{
	//posn_y += incrementValue;
	/*rotn_y += incrementValue;
	D3DXVECTOR3 m_position(posn_x, posn_y, posn_z);
	D3DXVECTOR3 m_rotn(rotn_y, 0.0f, 0.0f);
	//mesh_Cube->SetPosition(m_position);
	mesh_Plane->SetRotation(m_rotn);
	char buf[2048];
	sprintf(buf, "rotn_y_camera: %f\n", m_rotn.x);
	OutputDebugString(buf);*/
}

void World::changePosnY(float incrementValue)
{
	float rotn_y = player_->GetRotation()[1];
	float alpha = 180.0f - rotn_y;
	float alpha2 = 180.0f - rotn_y;
	alpha = alpha * 3.14f / 180.0f;
	float posn_x = player_->GetPosition()[0];
	float posn_y = player_->GetPosition()[1];
	float posn_z = player_->GetPosition()[2];
	posn_x = posn_x + incrementValue * -1 * sin(alpha);
	posn_y = posn_y + incrementValue * cos(alpha);
	D3DXVECTOR3 m_position(posn_x, posn_y, posn_z);
	player_->SetPosition(m_position);
	float posnEnd[] = { m_position.x + 2.0f, m_position.y, m_position.z };
	//playerDebugLine->ChangeLine(m_position, posnEnd);
	//char buf[2048];
	//sprintf(buf, "rotn_y_camera: %f, posnx: %f, posny: %f\n", alpha2, cos(alpha), sin(alpha));
	//OutputDebugString(buf);
}

/*void World::addActor(const SmartPointer<Actor> actor)
{
	actorArray.push_back(actor);
}

void World::addActorType(Actor* actor)
{
	actorArrayType->push_back(actor);
}

Actor* World::getActorFromType(const char *type)
{
	for (std::vector<Actor *>::iterator i = actorArrayType->begin();  i != actorArrayType->end(); ++i)
	{
				Actor *actorType = (Actor *) *i;
				if(strcmp(actorType->getType(),type) == 0)
				{
					return actorType;
				}
	}
}*/

void World::Update()
{
	/*deltaTime = g_Timing::Get().GetFrameTime();
	if (deltaTime > 1)
		deltaTime = 0.0f;
	timeElapsed_ += deltaTime;*/

	g_Graphics::Get().CameraLookAt(player_->GetPosition());
	g_Graphics::Get().Render();
	/*
	for (std::vector<SmartPointer<Actor>>::iterator i = actorArray.begin();  i != actorArray.end(); ++i)
	{
		SmartPointer<Actor> actorObj = *i;
		actorObj->update(deltaTime);
		if(actorObj->markedForDeath_)
		{
			DEBUG_PRINT("Actor marked for death\n");
		}
	}*/
}

float World::GetFrameTime()
{
	float deltaTime = static_cast<float>(g_Timing::Get().GetFrameTime());
	if (deltaTime > 1)
		deltaTime = 0.0f;
	return deltaTime;
}

void World::startRendering()
{
	//Cheesy::Service( bQuit );
	//renderer->RenderAll(actorArray);
	//Update();
}

void World::EnemyChase()
{
	for (unsigned int i = 0; i < enemyArray->size(); i++)
	{
		GameObject *curr_mesh = (GameObject *)enemyArray->at(i);
		D3DXVECTOR3 playerVector = player_->GetPosition();
		D3DXVECTOR3 enemyVector = curr_mesh->GetPosition();
		D3DXVECTOR3 diffVector = playerVector - enemyVector;
		D3DXVec3Normalize(&diffVector, &diffVector);
		float distance = D3DXVec3Length(&(enemyVector - playerVector));
		D3DXVECTOR3 position2 = enemyVector + diffVector * 0.16f + GetSeperativeForce(curr_mesh->GetPosition(), distance) * 0.08f;
		curr_mesh->SetPosition(D3DXVECTOR3(position2.x, position2.y, playerVector.z));
	}
}

D3DXVECTOR3 World::GetSeperativeForce(D3DXVECTOR3 m_posn, float distance)
{
	D3DXVECTOR3 seperativeForce = { 0.0f, 0.0f, 0.0f };
	if (distance < 2.0f)
	{
		for (unsigned int i = 0; i < enemyArray->size(); i++)
		{
			GameObject *curr_mesh = (GameObject *)enemyArray->at(i);
			//if (curr_mesh->GetPosition() != m_posn)
			{
				D3DXVECTOR3 currentPosn = curr_mesh->GetPosition();
				seperativeForce += m_posn - currentPosn;
			}
		}
		seperativeForce.z = m_posn.z;

	}
	return seperativeForce;
}

World::~World()
{
	//delete actorArrayType;
}

}

