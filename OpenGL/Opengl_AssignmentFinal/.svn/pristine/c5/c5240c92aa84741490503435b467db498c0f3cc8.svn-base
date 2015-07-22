/*
	This file defines the C/C++ functions used for building assets
*/

// Header Files
//=============

#include "AssetBuilder.h"

#include <iostream>
#include <ShlObj.h>
#include <sstream>
#include "../BuilderHelper/UtilityFunctions.h"
#include "../../External/Lua/Includes.h"

// Static Data Initialization
//===========================

namespace
{
	lua_State* s_luaState = NULL;
}

// Helper Function Declarations
//=============================

namespace
{
	// Initialization / Shut Down
	//---------------------------


	// Lua Functions
	//--------------

	int CopyFile( lua_State* io_luaState );
	int CreateDirectoryIfNecessary( lua_State* io_luaState );
	int DoesFileExist( lua_State* io_luaState );
	int GetLastWriteTime( lua_State* io_luaState );
	int OutputErrorMessage(lua_State* io_luaState);

	// Windows Functions
	//------------------

	bool GetEnvironmentVariable( const char* i_key, std::string& o_value );
	std::string GetFormattedWindowsError( const DWORD i_errorCode );
	std::string GetLastWindowsError( DWORD* o_optionalErrorCode = NULL );
	void OutputErrorMessage( const char* i_errorMessage, const char* i_optionalFileName = NULL );
}

// Interface
//==========

bool BuildAsset(const char* i_relativePath)
{
	// The only thing that this C/C++ function does
	// is call the corresponding Lua BuildAsset() function

	// To call a function it must be pushed onto the stack
	lua_getglobal(s_luaState, "BuildAsset");
	// This function has a single argument
	const int argumentCount = 1;
	{
		lua_pushstring(s_luaState, i_relativePath);
	}
	// This function always returns one value, and sometimes returns a second:
	//	* A boolean indicating success or failure
	//	* The builder process exit code (if a builder process ran)
	const int returnValueCount = 2;
	const int noMessageHandler = 0;
	int result = lua_pcall(s_luaState, argumentCount, returnValueCount, noMessageHandler);
	if (result == LUA_OK)
	{
		result = lua_toboolean(s_luaState, -2);
		const lua_Number builderExitCode = lua_tonumber(s_luaState, -1);	// This is purely for debugging
		lua_pop(s_luaState, returnValueCount);
		return result != 0;
	}
	else
	{
		std::cerr << lua_tostring(s_luaState, -1) << "\n";
		lua_pop(s_luaState, 1);

		return false;
	}
}

bool BuildAssets(const char* i_path_assetsToBuild)
{
	bool wereThereErrors = false;

	if (!Initialize())
	{
		wereThereErrors = true;
		goto OnExit;
	}

	// Load and execute the build script
	{
		std::string path;
		{
			std::string scriptDir;
			std::string errorMessage;
			if (eae6320::GetEnvironmentVariable("ScriptDir", scriptDir, &errorMessage))
			{
				path = scriptDir + "BuildAssets.lua";
			}
			else
			{
				wereThereErrors = true;
				eae6320::OutputErrorMessage(errorMessage.c_str());
				goto OnExit;
			}
		}
		// Load the script
		const int result = luaL_loadfile(s_luaState, path.c_str());
		if (result == LUA_OK)
		{
			// Execute it with the asset list path as an argument
			const int argumentCount = 1;
			{
				lua_pushstring(s_luaState, i_path_assetsToBuild);
			}
			// The return value should be true (on success) or false (on failure)
			const int returnValueCount = 1;
			const int noMessageHandler = 0;
			if (lua_pcall(s_luaState, argumentCount, returnValueCount, noMessageHandler) == LUA_OK)
			{
				// Note that lua_toboolean() follows the same rules as if something then statements in Lua:
				// false or nil will evaluate to false, and anything else will evaluate to true
				// (this means that if the script doesn't return anything it will result in a build failure)
				wereThereErrors = !lua_toboolean(s_luaState, -1);
				lua_pop(s_luaState, returnValueCount);
			}
			else
			{
				wereThereErrors = true;

				const char* errorMessage = lua_tostring(s_luaState, -1);
				std::cerr << errorMessage << "\n";
				lua_pop(s_luaState, 1);

				goto OnExit;
			}
		}
		else
		{
			wereThereErrors = true;

			const char* errorMessage = lua_tostring(s_luaState, -1);
			std::cerr << errorMessage << "\n";
			lua_pop(s_luaState, 1);

			goto OnExit;
		}
	}

OnExit:

	if (!ShutDown())
	{
		wereThereErrors = true;
	}

	return !wereThereErrors;
}

bool Initialize()
{
	// Create a new Lua state
	{
		s_luaState = luaL_newstate();
		if ( !s_luaState )
		{
			return false;
		}
	}
	// Open the standard libraries
	luaL_openlibs( s_luaState );
	// Register custom functions
	{
		lua_register( s_luaState, "CopyFile", CopyFile );
		lua_register( s_luaState, "CreateDirectoryIfNecessary", CreateDirectoryIfNecessary );
		lua_register( s_luaState, "DoesFileExist", DoesFileExist );
		lua_register( s_luaState, "GetLastWriteTime", GetLastWriteTime );
		lua_register(s_luaState, "OutputErrorMessage", OutputErrorMessage);
	}

	// Load and execute the build script
	/*{
		std::string path;
		{
			std::string scriptDir;
			std::string errorMessage;
			scriptDir = "../../../Scripts/";
			if ( !GetEnvironmentVariable( "ScriptDir", scriptDir ) )
			{
				return false;
			}
			path = scriptDir + "BuildAssets.lua";
		}
		const int result = luaL_dofile( s_luaState, path.c_str() );
		if (result == LUA_OK)
		{
			// Pop any values the script returned
			lua_pop(s_luaState, lua_gettop(s_luaState));
		}
		else
		{
			const char* errorMessage = lua_tostring(s_luaState, -1);
			std::cerr << errorMessage << "\n";
			lua_pop(s_luaState, 1);

			return false;
		}
	}*/

	return true;
}

bool ShutDown()
{
	if ( s_luaState )
	{
		lua_close( s_luaState );
		s_luaState = NULL;
	}

	return true;
}



// Helper Function Declarations
//=============================

namespace
{
	// Lua Functions
	//--------------

	int CopyFile( lua_State* io_luaState )
	{
		/*
		// Argument #1: The source path
		const char* i_path_source;
		if (lua_isstring(io_luaState, 1))
		{
			i_path_source = lua_tostring(io_luaState, 1);
			//std::cout << "Copied File" << i_path_source << "\n";
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}
		//EAE6320_TODO	// How do you get the source path from the Lua state?
		// Argument #2: The target path
		const char* i_path_target;
		if (lua_isstring(io_luaState, 2))
		{
			i_path_target = lua_tostring(io_luaState, 2);
			//std::cout << "Copied File" << i_path_target << "\n";
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}
		//EAE6320_TODO	// How do you get the target path from the Lua state?

		// Copy the file
		const BOOL noErrorIfTargetFileAlreadyExists = FALSE;
		std::string errorMessage;
		if (eae6320::CopyFile(i_path_source, i_path_target, noErrorIfTargetFileAlreadyExists, &errorMessage))
		{
			lua_pushboolean(io_luaState, true);
			const int returnValueCount = 1;
			return returnValueCount;
		}
		else
		{
			lua_pushboolean(io_luaState, false);
			lua_pushstring(io_luaState, errorMessage.c_str());
			const int returnValueCount = 2;
			return returnValueCount;
		}*/

		// Argument #1: The source path
		const char* i_path_source;
		if (lua_isstring(io_luaState, 1))
		{
			i_path_source = lua_tostring(io_luaState, 1);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}
		// Argument #2: The target path
		const char* i_path_target;
		if (lua_isstring(io_luaState, 2))
		{
			i_path_target = lua_tostring(io_luaState, 2);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #2 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 2));
		}

		// Copy the file
		const bool noErrorIfTargetAlreadyExists = false;
		std::string errorMessage;
		if (eae6320::CopyFile(i_path_source, i_path_target, noErrorIfTargetAlreadyExists, &errorMessage))
		{
			lua_pushboolean(io_luaState, true);
			const int returnValueCount = 1;
			return returnValueCount;
		}
		else
		{
			lua_pushboolean(io_luaState, false);
			lua_pushstring(io_luaState, errorMessage.c_str());
			const int returnValueCount = 2;
			return returnValueCount;
		}
	}

	int CreateDirectoryIfNecessary(lua_State* io_luaState)
	{
		// Argument #1: The path
		const char* i_path;
		if (lua_isstring(io_luaState, 1))
		{
			i_path = lua_tostring(io_luaState, 1);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}

		std::string errorMessage;
		if (eae6320::CreateDirectoryIfNecessary(i_path, &errorMessage))
		{
			return 0;
		}
		else
		{
			return luaL_error(io_luaState, errorMessage.c_str());
		}
	}

	int DoesFileExist(lua_State* io_luaState)
	{
		// Argument #1: The path
		const char* i_path;
		if (lua_isstring(io_luaState, 1))
		{
			i_path = lua_tostring(io_luaState, 1);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}

		DWORD errorCode;
		std::string errorMessage;
		if (eae6320::DoesFileExist(i_path, &errorMessage, &errorCode))
		{
			lua_pushboolean(io_luaState, true);
			return 1;
		}
		else
		{
			if ((errorCode == ERROR_FILE_NOT_FOUND) || (errorCode == ERROR_PATH_NOT_FOUND))
			{
				lua_pushboolean(io_luaState, false);
				return 1;
			}
			else
			{
				return luaL_error(io_luaState, errorMessage.c_str());
			}
		}
	}

	int GetLastWriteTime(lua_State* io_luaState)
	{
		// Argument #1: The path
		const char* i_path;
		if (lua_isstring(io_luaState, 1))
		{
			i_path = lua_tostring(io_luaState, 1);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}

		// Get the last time that the file was written to
		uint64_t lastWriteTime;
		std::string errorMessage;
		if (eae6320::GetLastWriteTime(i_path, lastWriteTime, &errorMessage))
		{
			lua_pushnumber(io_luaState, static_cast<lua_Number>(lastWriteTime));
			return 1;
		}
		else
		{
			return luaL_error(io_luaState, errorMessage.c_str());
		}
	}


	int OutputErrorMessage(lua_State* io_luaState)
	{
		// Argument #1: The error message
		const char* i_errorMessage;
		if (lua_isstring(io_luaState, 1))
		{
			i_errorMessage = lua_tostring(io_luaState, 1);
		}
		else
		{
			return luaL_error(io_luaState,
				"Argument #1 must be a string (instead of a %s)",
				luaL_typename(io_luaState, 1));
		}
		// Argument #2: An optional file name
		const char* i_optionalFileName = NULL;
		if (lua_isstring(io_luaState, 2))
		{
			i_optionalFileName = lua_tostring(io_luaState, 2);
		}

		// Output the error message
		eae6320::OutputErrorMessage(i_errorMessage, i_optionalFileName);

		return 0;
	}
}
