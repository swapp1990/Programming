#include "Timing.h"

#include <Windows.h>

namespace BlackRock
{

	Timing::Timing()
 {
	 frameTimeOld = 0;
 }

 Timing::Timing( float startTimeSeconds = 0.0f )
 {
	 frameTimeOld = 0;
 }

 void Timing::init()
 {
	 LARGE_INTEGER frequency;
	 LARGE_INTEGER clockCycles;
	 if( QueryPerformanceFrequency(&frequency) ) //	 // returns a 64 bit integer value, representing the frequency, or number of counts/cycles per second.
		 m_cyclesPerSecond = frequency.QuadPart;

	 if( QueryPerformanceCounter(&clockCycles) )  //function to get the current time in counts, which is also stored in a 64 bit integer variable. 
		 m_timeCycles = clockCycles.QuadPart;
	 //m_timeCycels is the startTimer.

 }

 double Timing::GetTime() //function gets the current time in counts using the QueryPerformanceCounter()
 {
	 LARGE_INTEGER currentTime;
	 QueryPerformanceCounter(&currentTime);

	 return double(currentTime.QuadPart-m_timeCycles)/m_cyclesPerSecond;
	 //to get the time in seconds
 }

 double Timing::GetFrameTime() //call once every frame.  This will return the time in seconds each frame takes to process, which we can then use to make sure our camera and animations are updated smoothly.
 {
	 LARGE_INTEGER currentTime;
	 __int64 tickCount;
	 QueryPerformanceCounter(&currentTime);

	tickCount = currentTime.QuadPart-frameTimeOld;   //cycles to complete this frame.
	frameTimeOld = currentTime.QuadPart;

	if(tickCount < 0.0f)
		tickCount = static_cast<long long>(0.0f);

	return float(tickCount)/m_cyclesPerSecond;  //returns time for the current frame.
 }

 float Timing::GetDifferenceinMS(__int64 sTick, __int64 eTick)
 {
	 __int64 tickCount;
	 tickCount = sTick-eTick;  
	 if(tickCount < 0.0f)
		 tickCount = static_cast<long long>(0.0f);

	return float(tickCount)/m_cyclesPerSecond;
 }

 __int64 Timing::GetCurrentTick()
 {
	 return currentTick;
 }
}
