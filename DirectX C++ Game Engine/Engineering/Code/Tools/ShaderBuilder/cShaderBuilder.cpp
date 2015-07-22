#include "cShaderBuilder.h"

#include <iostream>
#include <sstream>

// Interface
//==========

namespace
{
	enum eShaderProgramType
	{
		VERTEX,
		FRAGMENT,

		UNKNOWN
	};
}

// Build
//------

bool cShaderBuilder::Build(const std::vector<const std::string>& i_arguments)
{
	// Get which shader program type to compile
	eShaderProgramType shaderProgramType = UNKNOWN;
	{
		if (i_arguments.size() >= 1)
		{
			const std::string& argument = i_arguments[0];
			if (argument == "vertex")
			{
				shaderProgramType = VERTEX;
				//printf("argument %s\n", argument.c_str());
			}
			else if (argument == "fragment")
			{
				shaderProgramType = FRAGMENT;
				//printf("argument %s\n", argument.c_str());
			}
			else
			{
				std::stringstream errorMessage;
				errorMessage << "\"" << argument << "\" is not a valid shader program type";
				OutputErrorMessage(errorMessage.str().c_str(), m_path_source);
				return false;
			}
		}
		else
		{
			OutputErrorMessage(
				"A Shader must be built with an argument defining which type of shader program (e.g. \"vertex\" or \"fragment\") to compile",
				m_path_source);
			return false;
		}
	}

	/*bool wereThereErrors = false;

	// Copy the source to the target
	{
		const bool dontFailIfTargetAlreadyExists = false;
		std::string errorMessage;
		if (!eae6320::CopyFile(m_path_source, m_path_target, dontFailIfTargetAlreadyExists))
		{
			wereThereErrors = true;
			std::stringstream decoratedErrorMessage;
			decoratedErrorMessage << "Windows failed to copy \"" << m_path_source << "\" to \"" << m_path_target << "\": " << errorMessage;
			eae6320::OutputErrorMessage(decoratedErrorMessage.str().c_str(), __FILE__);
		}
	}

	return !wereThereErrors;*/
	// Get the path to the shader compiler
	std::string path_fxc;
	{
		// Get the path to the DirectX SDK
		std::string path_sdk;
		{
			std::string errorMessage;
			if (!GetEnvironmentVariable("DXSDK_DIR", path_sdk, &errorMessage))
			{
				std::stringstream decoratedErrorMessage;
				decoratedErrorMessage << "Windows failed to get the path to the DirectX SDK: " << errorMessage;
				OutputErrorMessage(decoratedErrorMessage.str().c_str(), __FILE__);
				return false;
			}
		}
		path_fxc = path_sdk + "Utilities/bin/" +
#ifndef _WIN64
			"x86"
#else
			"x64"
#endif
			+ "/fxc.exe";
	}
	// Create the command to run
	std::string command;
	{
		std::stringstream commandToBuild;
		commandToBuild << "\"" << path_fxc << "\"";
		// Target profile
		switch (shaderProgramType)
		{
		case VERTEX:
			commandToBuild << " /Tvs_3_0";
			break;
		case FRAGMENT:
			commandToBuild << " /Tps_3_0";
			break;
		}
		// Entry point
		commandToBuild << " /Emain"
#ifdef _DEBUG
			// Disable optimizations so that debugging is easier
			<< " /Od"
			// Enable debugging
			<< " /Zi"
#endif
			// Target file
			<< " /Fo\"" << m_path_target << "\""
			// Don't output the logo
			<< " /nologo"
			// Source file
			<< " \"" << m_path_source << "\""
			;
		command = commandToBuild.str();
	}
	// Execute the command
	{
		DWORD exitCode;
		std::string errorMessage;
		if (ExecuteCommand(command.c_str(), &exitCode, &errorMessage))
		{
			return exitCode == EXIT_SUCCESS;
		}
		else
		{
			OutputErrorMessage(errorMessage.c_str(), m_path_source);
			return false;
		}
	}
}
