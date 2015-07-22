#pragma once

#ifdef _DEBUG
#define DEBUG_MENU
#endif

#include <d3d9.h>
#include <d3dx9shader.h>
#include <vector>
#include "Font.h"
#include "Mesh.h"

#ifdef DEBUG_MENU
class DebugMenu
{
	struct sDebugWidgetStruct
	{
		Font::sTextStruct *debug_text;
		int widgetType_; //0 - Checkbox, 1 - Slider, 2 - Button
		int counter_;
		bool selected_;
		sDebugWidgetStruct(Font::sTextStruct *i_debug_text, int i_widgetType, bool i_isSelected, int i_counter = 1)
			: debug_text(i_debug_text), widgetType_(i_widgetType), counter_(i_counter), selected_(i_isSelected) {}
	};


	LPD3DXMESH meshBox;
	IDirect3DDevice9* i_direct3dDevice;
public:
	DebugMenu();
	DebugMenu(IDirect3DDevice9* s_direct3dDevice);
	void Render();
	void DisableDebugMenu();
	float startPosnX_, startPosnY_;
	int sliderCount;

	bool isDebugEnabled_;

	int widgetCount;
	~DebugMenu();
	Font *g_font;
	//Slider
	float min, max;
	float *slider_value;
	//Checkbox
	const char *checkbox_text;
	bool *checkBox_value;
	//Button
	const char *button_text;
	typedef int(*funcptr)();
	funcptr pfi;
	std::vector<sDebugWidgetStruct *> *debugWidgets_interactive;
	int current_selection, counter;
	void MakeSelected(sDebugWidgetStruct *debug_widget);
	void MakeUnSelected(sDebugWidgetStruct *debug_widget);
	void CreateSlider(const char *name, float min, float max, float *i_slider_value);
	void CreateCheckBox(char *name, bool *value);
	void CreateButton(char *name, int(*myfunc)());
	void CreateText(char *name, char *value);
	void CreateDebugCube();
	void CreateDebugSphere();
	void SpaceKeyPressed();
	void ArrowkeyPressed();
};
#else
class DebugMenu
{
public:
	inline DebugMenu(IDirect3DDevice9* s_direct3dDevice) {}
	// All of these functions will compile out to nothing.
	inline void CreateSlider(const char *name, float min, float max, float *i_slider_value)	{}
	inline void CreateCheckBox(char *name, bool* value)						{}
	inline void CreateButton(char *name, int(*myfunc)())	{}
	inline void CreateText(char *name, char *value) {}
	inline void CreateDebugCube(){}
	inline void CreateDebugSphere(){}
	inline void SpaceKeyPressed(){}
	inline void ArrowkeyPressed(){}
	inline void DisableDebugMenu(){}
};
#endif

