#include "Light.h"

#include <iostream>
#include <cassert>
#include <sstream>

namespace BlackRock
{
	void Light::Initialize()
	{
		lightPath_ = "data/light_settings.txt";
		if (!LoadAsset(lightPath_))
		{
			//error
		}
		else
		{
			//cool
		}
	}

	bool Light::LoadAsset(const char* i_path)
	{
		bool wereThereErrors = false;

		// Create a new Lua state
		lua_State* luaState = NULL;
		{
			luaState = luaL_newstate();
			if (!luaState)
			{
				wereThereErrors = true;
				std::cerr << "Failed to create a new Lua state\n";
				goto OnExit;
			}
		}

		// Load the asset file as a "chunk",
		// meaning there will be a callable function at the top of the stack
		{
			const int luaResult = luaL_loadfile(luaState, i_path);
			if (luaResult != LUA_OK)
			{
				wereThereErrors = true;
				std::cerr << lua_tostring(luaState, -1);
				// Pop the error message
				lua_pop(luaState, 1);
				goto OnExit;
			}
		}
		// Execute the "chunk", which should load the asset
		// into a table at the top of the stack
		{
			const int argumentCount = 0;
			const int returnValueCount = LUA_MULTRET;	// Return _everything_ that the file returns
			const int noMessageHandler = 0;
			const int luaResult = lua_pcall(luaState, argumentCount, returnValueCount, noMessageHandler);
			if (luaResult == LUA_OK)
			{
				// A well-behaved asset file will only return a single value
				const int returnedValueCount = lua_gettop(luaState);
				if (returnedValueCount == 1)
				{
					// A correct asset file _must_ return a table
					if (!lua_istable(luaState, -1))
					{
						wereThereErrors = true;
						std::cerr << "Asset files must return a table (instead of a " <<
							luaL_typename(luaState, -1) << ")\n";
						// Pop the returned non-table value
						lua_pop(luaState, 1);
						goto OnExit;
					}
				}
				else
				{
					wereThereErrors = true;
					std::cerr << "Asset files must return a single table (instead of " <<
						returnedValueCount << " values)\n";
					// Pop every value that was returned
					lua_pop(luaState, returnedValueCount);
					goto OnExit;
				}
			}
			else
			{
				wereThereErrors = true;
				std::cerr << lua_tostring(luaState, -1);
				// Pop the error message
				lua_pop(luaState, 1);
				goto OnExit;
			}
		}

		// If this code is reached the asset file was loaded successfully,
		// and its table is now at index -1
		if (!LoadTableValues(*luaState))
		{
			wereThereErrors = true;
		}

		// Pop the table
		lua_pop(luaState, 1);

	OnExit:

		if (luaState)
		{
			// If I haven't made any mistakes
			// there shouldn't be anything on the stack,
			// regardless of any errors encountered while loading the file:
			//assert(lua_gettop(luaState) == 0);

			//lua_close(luaState);
			//luaState = NULL;
		}

		return !wereThereErrors;
	}

	bool Light::LoadTableValues(lua_State& io_luaState)
	{
		if (!LoadTableValues_nested(io_luaState, "directionLight"))
		{
			return false;
		}
		if (!LoadTableValues_nested(io_luaState, "ambientLight"))
		{
			return false;
		}
		return true;
	}

	bool Light::LoadTableValues_nested(lua_State& io_luaState, const char * field_value)
	{
		bool wereThereErrors = false;

		lua_pushstring(&io_luaState, field_value);
		lua_gettable(&io_luaState, -2);
		if (lua_istable(&io_luaState, -1))
		{
			if (!LoadTableValues_parameters_values(io_luaState, field_value))
			{
				wereThereErrors = true;
				goto OnExit;
			}
		}
		else
		{
			wereThereErrors = true;
			std::cerr << "The value at \"" << field_value << "\" must be a table "
				"(instead of a " << luaL_typename(&io_luaState, -1) << ")\n";
			goto OnExit;
		}
	OnExit:
		// Pop the textures table
		lua_pop(&io_luaState, 1);
		return !wereThereErrors;
	}

	bool Light::LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value)
	{
		if (strcmp(field_value, "directionLight") == 0)
		{
			const char* key = "direction";
			lua_pushstring(&io_luaState, key);
			lua_gettable(&io_luaState, -2);
			if (lua_istable(&io_luaState, -1))
			{
				lua_pushinteger(&io_luaState, 1);
				lua_gettable(&io_luaState, -2);
				dirn_vector[0] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 2);
				lua_gettable(&io_luaState, -2);
				dirn_vector[1] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 3);
				lua_gettable(&io_luaState, -2);
				dirn_vector[2] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);

			const char* key2 = "color";
			lua_pushstring(&io_luaState, key2);
			lua_gettable(&io_luaState, -2);
			
			if (lua_istable(&io_luaState, -1))
			{
				lua_pushinteger(&io_luaState, 1);
				lua_gettable(&io_luaState, -2);
				dirn_color[0] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 2);
				lua_gettable(&io_luaState, -2);
				dirn_color[1] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 3);
				lua_gettable(&io_luaState, -2);
				dirn_color[2] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);
		}
		else if (strcmp(field_value, "ambientLight") == 0)
		{
			const char* key = "color";
			lua_pushstring(&io_luaState, key);
			lua_gettable(&io_luaState, -2);
			if (lua_istable(&io_luaState, -1))
			{
				lua_pushinteger(&io_luaState, 1);
				lua_gettable(&io_luaState, -2);
				ambient_color[0] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 2);
				lua_gettable(&io_luaState, -2);
				ambient_color[1] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
				lua_pushinteger(&io_luaState, 3);
				lua_gettable(&io_luaState, -2);
				ambient_color[2] = static_cast<float>(lua_tonumber(&io_luaState, -1));
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);
		}
		return true;
	}
}