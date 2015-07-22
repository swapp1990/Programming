#pragma once
#ifndef EAE6320_LIGHT_H
#define EAE6320_LIGHT_H

#include "../../External/Lua/5.2.3/src/lua.hpp"

namespace BlackRock
{
	class Light 
	{
	private:
		const char* lightPath_;
		
		bool LoadAsset(const char* i_path);
		bool LoadTableValues(lua_State& io_luaState);
		bool LoadTableValues_nested(lua_State& io_luaState, const char * field_value);
		bool LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value);

	public:
		void Initialize();
		float dirn_color[3];
		float ambient_color[3];
		float dirn_vector[3];
	};
}

#endif

