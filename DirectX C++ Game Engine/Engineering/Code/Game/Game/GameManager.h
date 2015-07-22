#ifndef EAE6320_GAME_H
#define EAE6320_GAME_H
#pragma once

namespace Game
{
	int Initialize(HINSTANCE i_thisInstanceOfTheProgram, int i_initialWindowDisplayState);
	void Update();
	void ListenToKeys();
	void Close();
	void AnimateSprite();
}
#endif