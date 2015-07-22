#ifndef EAE6320_CMESHBUILDER_H
#define EAE6320_CMESHBUILDER_H
#pragma once

#include "../BuilderHelper/cbBuilder.h"
#include "../../External/Lua/Includes.h"

using namespace eae6320;
class cMeshBuilder : public cbBuilder
{

	public:
		virtual bool Build(const std::vector<const std::string>&);

		bool LoadAsset(const char* i_path);
		bool LoadTableValues(lua_State& io_luaState);
		bool LoadTableValues_nested(lua_State& io_luaState, const char * field_value);
		bool LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value);

		static std::ofstream* _rout;
};

#endif

