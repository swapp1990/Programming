#ifndef EAE6320_CTEXTUREBUILDER_H
#define EAE6320_CTEXTUREBUILDER_H
#pragma once
#include "../BuilderHelper/cbBuilder.h"

using namespace eae6320;
class cTextureBuilder : public cbBuilder
{
	bool Initialize(const char* i_path_source);
	bool ShutDown();
public:
	virtual bool Build(const std::vector<const std::string>&);
};

#endif

