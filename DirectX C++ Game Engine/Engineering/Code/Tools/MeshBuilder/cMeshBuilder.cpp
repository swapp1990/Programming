#include "cMeshBuilder.h"

#include <iostream>
#include <sstream>
#include <cassert>
#include <iostream>
#include <fstream>

std::ofstream* cMeshBuilder::_rout = NULL;

bool cMeshBuilder::Build(const std::vector<const std::string>& i_arguments)
{
	bool wereThereErrors = false;
	const int vertexCount = 4;
	const int indexCount = 4;
	//std::ofstream ofile(m_path_target, std::ios::binary);
	//ofile.write((char*)&vertexCount, sizeof(int));
	//ofile.write((char*)&indexCount, sizeof(int));
	_rout = new std::ofstream(m_path_target, std::ios::binary);
	float vertex1[6] = { -5.0f, 0.0f, 5.0f, 50.0f, 50.0f, 50.0f };
	//ofile.write((char*)&vertex1, sizeof(float)*6);
	//std::cout << "Created meshBuilder \n";
	LoadAsset(m_path_source);
	// Copy the source to the target
	{
		const bool dontFailIfTargetAlreadyExists = false;
		std::string errorMessage;
		/*if (!eae6320::CopyFile(m_path_source, m_path_target, dontFailIfTargetAlreadyExists))
		{
			wereThereErrors = true;
			std::stringstream decoratedErrorMessage;
			decoratedErrorMessage << "Windows failed to copy \"" << m_path_source << "\" to \"" << m_path_target << "\": " << errorMessage;
			eae6320::OutputErrorMessage(decoratedErrorMessage.str().c_str(), __FILE__);
		}*/
	}

	return !wereThereErrors;
}

bool cMeshBuilder::LoadAsset(const char* i_path)
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
		//i_path = "../../../Assets/cube.txt";
		const int luaResult = luaL_loadfile(luaState, i_path);
		if (luaResult != LUA_OK)
		{
			wereThereErrors = true;
			std::cerr << lua_tostring(luaState, -1);
			// Pop the error message
			lua_pop(luaState, 1);
			goto OnExit;
		}
		else
		{
			//std::cout << "Loaded Lua File \n";
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

bool cMeshBuilder::LoadTableValues(lua_State& io_luaState)
{
	if (!LoadTableValues_nested(io_luaState, "vertex_count"))
	{
		return false;
	}

	if (!LoadTableValues_nested(io_luaState, "vertex"))
	{
		return false;
	}

	if (!LoadTableValues_nested(io_luaState, "index_count"))
	{
		return false;
	}

	if (!LoadTableValues_nested(io_luaState, "indices"))
	{
		return false;
	}
	return true;
}

bool cMeshBuilder::LoadTableValues_nested(lua_State& io_luaState, const char * field_value)
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
		//not a table
		//wereThereErrors = true;
		/*std::cerr << "The value at \"" << field_value << "\" must be a table "
		"(instead of a " << luaL_typename(&io_luaState, -1) << ")\n";*/
		int vertexValue = static_cast< int >(lua_tonumber(&io_luaState, -1));
		//std::cout << "VertexCount is: " << vertexValue << "\n";

		_rout->write((char*)&vertexValue, sizeof(int));

		wereThereErrors = false;
		goto OnExit;
	}
OnExit:
	// Pop the textures table
	lua_pop(&io_luaState, 1);
	return !wereThereErrors;
}

bool cMeshBuilder::LoadTableValues_parameters_values(lua_State& io_luaState, const char * field_value)
{
	if (strcmp(field_value, "vertex") == 0)
	{
		//lua_pushnil(&io_luaState);
		//std::cout << "in Para-2metres: \n";
		//std::cout << "Length " << luaL_len(&io_luaState,-2) << "\n";
		//while (lua_next(&io_luaState, -2))
		int lengthVector = luaL_len(&io_luaState, -1);
		//std::cout << "Length " << lengthVector << "\n";
		const int numberOfParameters = 17;
		float * vertexinfo = new float[lengthVector * numberOfParameters];
		unsigned int vertexInfo_index = 0;
		for (int i = 0; i < lengthVector; i++)
		{
			lua_pushinteger(&io_luaState, i+1);
			lua_gettable(&io_luaState, -2);
			//if (lua_istable(&io_luaState, -1))
			const int lengthInfo = static_cast<const int>(luaL_len(&io_luaState, -1));
			float key3[numberOfParameters] = {};
			for (int j = 0; j < lengthInfo; j++)
			{
				lua_pushinteger(&io_luaState, j+1);
				lua_gettable(&io_luaState, -2);
				
				*(vertexinfo + vertexInfo_index) = static_cast< float >(lua_tonumber(&io_luaState, -1));
				key3[j] = static_cast< float >(lua_tonumber(&io_luaState, -1));
				vertexInfo_index++;
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);
		}
		_rout->write((char*)vertexinfo, sizeof(float)* numberOfParameters * lengthVector);
		delete[] vertexinfo;
	}
	else if (strcmp(field_value, "indices") == 0)
	{
		int lengthFaces = luaL_len(&io_luaState, -1);
		int * indicesinfo = new int[lengthFaces * 6];
		unsigned int indicesInfo_index = 0;
		for (int i = 0; i < lengthFaces; i++)
		{
			lua_pushinteger(&io_luaState, i + 1);
			lua_gettable(&io_luaState, -2);
			//if (lua_istable(&io_luaState, -1))
			const int lengthInfo = static_cast<const int>(luaL_len(&io_luaState, -1));
			for (int j = 0; j < lengthInfo; j++)
			{
				lua_pushinteger(&io_luaState, j + 1);
				lua_gettable(&io_luaState, -2);

				*(indicesinfo + indicesInfo_index) = static_cast< int >(lua_tonumber(&io_luaState, -1));
				indicesInfo_index++;
				lua_pop(&io_luaState, 1);
			}
			lua_pop(&io_luaState, 1);
		}
		_rout->write((char*)indicesinfo, sizeof(int)* 6 * lengthFaces);
		delete[] indicesinfo;
	}
	return true;
}
