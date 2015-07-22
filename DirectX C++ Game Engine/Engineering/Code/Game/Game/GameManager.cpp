#include "precompiled.h"
#include "GameManager.h"

#include "../../Engine/Engine/Graphics.h"
#include "../../Engine/Engine/World/World.h"
#include "../../Engine/UserSettings/UserSettings.h"
#include "WindowsProgram.h"

using namespace BlackRock;
#define KEYDOWN(vk_code) ((GetAsyncKeyState(vk_code) & 0x8000) ? 1 : 0)
	bool shutDown_;
	int w_width;
	int w_height;

	float timeElapsed_ = 0;
	float secondsElapsed_ = 0;
	int counterTime = 0;

	int animateCounter = 0;

	namespace
	{
		bool IsVirtualKeyPressed(const int i_virtualKeyCode);
		bool IsVirtualKeyUp(const int i_virtualKeyCode);
	}

	namespace
	{
		bool IsVirtualKeyPressed(const int i_virtualKeyCode)
		{
			short keyState = GetAsyncKeyState(i_virtualKeyCode);
			const short isKeyDownMask = ~1;
			return (keyState & isKeyDownMask) != 0;
		}

		bool IsVirtualKeyUp(const int i_virtualKeyCode)
		{
			if ((GetAsyncKeyState(i_virtualKeyCode) & 1) != 0)
			{
				return true;
			}
			else
				return false;

		}
	}

	namespace Game
	{
		int Initialize(HINSTANCE i_thisInstanceOfTheProgram, int i_initialWindowDisplayState)
		{
			shutDown_ = false;
			counterTime = 0;

			w_width = UserSettings::GetWidth();
			w_height = UserSettings::GetHeight();
			//w_width = 1280;
			//w_height = 800;
			//Call Windows function to create main window.
			if (CreateMainWindowWIthSize(i_thisInstanceOfTheProgram, i_initialWindowDisplayState, w_width, w_height))
			{
				int exitCode;
				//Call Initialize World of Engine (to initialize Graphics system and others like Collision, Renderer, etc.)
				if (!g_World::Get().InitializeWorld(GetMainWindowHandle()))
				{
					return -1;
				}

				bool wereThereErrors = WaitForMainWindowToClose(exitCode);
				if (wereThereErrors)
				{
					// If the program already had errors while waiting for the main window to close
					// the existing error exit code can be returned
					return exitCode;
				}
				else
				{
					// In a real program you might decide to define a special constant for this situation,
					// but for this example program -1 is a good arbitrary error code
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}

		void Update()
		{
			timeElapsed_ += g_World::Get().GetFrameTime();
			if (timeElapsed_ > 0.1f)
			{
				timeElapsed_ = 0;
				//counterTime++;
				g_Graphics::Get().changeAtlasIndex(counterTime);
				g_World::Get().EnemyChase();
				g_Graphics::Get().ChangeLights();
				AnimateSprite();
			}
			secondsElapsed_ += g_World::Get().GetFrameTime();
			char buf[2048];
			sprintf_s(buf, "Seconds: %f\n", secondsElapsed_);
			//OutputDebugString(buf);
			if (secondsElapsed_ > 0.05f)
			{
				counterTime++;

				secondsElapsed_ = 0;
				g_Graphics::Get().changeAtlasIndex(counterTime);
			}
			ListenToKeys();
			g_World::Get().Update();
		}

		void AnimateSprite()
		{
			if (animateCounter == 0)
			{
				g_Graphics::Get().changeSpriteSheet(1, 4);
			}
			if (animateCounter == 1)
			{
				g_Graphics::Get().changeSpriteSheet(2, 1);
			}
			if (animateCounter == 2)
			{
				g_Graphics::Get().changeSpriteSheet(1, 1);
			}
			if (animateCounter == 3)
			{
				g_Graphics::Get().changeSpriteSheet(2, 2);
			}
			if (animateCounter == 4)
			{
				g_Graphics::Get().changeSpriteSheet(1, 2);
			}
			if (animateCounter == 6)
			{
				g_Graphics::Get().changeSpriteSheet(2, 3);
			}
			if (animateCounter == 7)
			{
				g_Graphics::Get().changeSpriteSheet(1, 3);
			}
			if (animateCounter == 8)
			{
				g_Graphics::Get().changeSpriteSheet(2, 4);
			}
			if (animateCounter > 8)
			{
				animateCounter = 0;
			}
			animateCounter++;
		}

		void Close()
		{
			if (g_Graphics::Get().ShutDown())
			{
			}
		}

		void ListenToKeys()
		{
			short int test = (GetAsyncKeyState(0x4D) & 0x8000);


			if (test < 0)
			{
				//cout << "Up key pressed" << endl;
				//std::cout << "KeyUp\n";
			}

			if (IsVirtualKeyPressed(VK_UP))
			{
				g_Graphics::Get().changePosnZ_Camera(0.6f);
			}
			else if (IsVirtualKeyPressed(VK_DOWN))
			{
				g_Graphics::Get().changePosnZ_Camera(-0.6f);
			}
			if (IsVirtualKeyPressed(VK_RIGHT))
			{
				//g_World::Get().changePosnX(0.05f);
				g_Graphics::Get().changePosnX_Camera(-0.025f);
			}
			else if (IsVirtualKeyPressed(VK_LEFT))
			{
				//g_World::Get().changePosnX(-0.05f);
				g_Graphics::Get().changePosnX_Camera(0.025f);
			}
			//Space
			if (IsVirtualKeyUp(VK_SPACE))
			{
				g_Graphics::Get().DebugMenu_Clicked();
			}
			//Camera Movement
			if (IsVirtualKeyPressed(0x57)) //w
			{
				g_Graphics::Get().changePosnY_Camera(-0.01f);
			}
			if (IsVirtualKeyUp(0x41)) //a
			{
				//g_Graphics::Get().changePosnX_Camera(0.01f);
				g_Graphics::Get().DebugMenu_Move();
			}
				//Debug MOde Off
				if (IsVirtualKeyUp(0x44)) //d
				{
					g_Graphics::Get().DebugMenu_Disable();
				}
				if (IsVirtualKeyPressed(0x53)) //s
				{
					g_Graphics::Get().changePosnY_Camera(0.02f);
				}
				if (IsVirtualKeyPressed(0x44)) //d
				{
					//g_Graphics::Get().changePosnX_Camera(-0.01f);
				}
				//Direction Light Movement
				else if (IsVirtualKeyPressed(0x49)) //i
				{
					g_Graphics::Get().changePosnZ_Light(0.1f);
				}
				else if (IsVirtualKeyPressed(0x4B)) //k
				{
					g_Graphics::Get().changePosnZ_Light(-0.1f);
				}
				else if (IsVirtualKeyPressed(0x4A)) //j
				{
					g_Graphics::Get().changePosnX_Light(0.1f);
				}
				else if (IsVirtualKeyPressed(0x4C)) //l
				{
					g_Graphics::Get().changePosnX_Light(-0.1f);
				}

				//Debug MOde On
				else if (IsVirtualKeyPressed(0x4E)) //n
				{
					//g_Graphics::Get().isDebugModeOn_ = true;
				}

				//Number change
				else if (IsVirtualKeyPressed(0x30)) //0
				{
					g_Graphics::Get().changeAtlasIndex(0);
				}
				else if (IsVirtualKeyPressed(0x31)) //1
				{
					g_Graphics::Get().changeAtlasIndex(1);
				}
				else if (IsVirtualKeyPressed(0x32)) //2
				{
					g_Graphics::Get().changeAtlasIndex(2);
				}
				else if (IsVirtualKeyPressed(0x33)) //3
				{
					g_Graphics::Get().changeAtlasIndex(3);
				}
				else if (IsVirtualKeyPressed(0x34)) //4
				{
					g_Graphics::Get().changeAtlasIndex(4);
				}
				else if (IsVirtualKeyPressed(0x35)) //5
				{
					g_Graphics::Get().changeAtlasIndex(5);
				}
				else if (IsVirtualKeyPressed(0x36)) //6
				{
					g_Graphics::Get().changeAtlasIndex(6);
				}
				else if (IsVirtualKeyPressed(0x37)) //7
				{
					g_Graphics::Get().changeAtlasIndex(7);
				}
				else if (IsVirtualKeyPressed(0x38)) //8
				{
					g_Graphics::Get().changeAtlasIndex(8);
				}
				else if (IsVirtualKeyPressed(0x39)) //9
				{
					g_Graphics::Get().changeAtlasIndex(9);
				}
			}
		}
	