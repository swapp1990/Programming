#ifndef timing_h
#define timing_h
#pragma once

#include <cstdint>
#include <stddef.h>

#include "Utils\Singleton.h"
namespace BlackRock
{
class Timing
{
	friend class Singleton<Timing>;

	uint64_t m_timeCycles;
	uint64_t m_cyclesPerSecond;
	Timing();
	Timing( float startTimeSeconds );

	__int64 currentTick;
	__int64 frameTimeOld;
public:
	void init();

	void StartTimer();
	double GetTime();
	double GetFrameTime();
	__int64 GetCurrentTick();
	 float GetDifferenceinMS(__int64 sTick, __int64 eTick);
};
typedef Singleton<Timing> g_Timing;
}

#endif