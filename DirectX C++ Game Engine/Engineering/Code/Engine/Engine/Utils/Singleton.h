#ifndef SINGLETON
#define SINGLETON
#pragma once
#include <assert.h>
namespace BlackRock
{
template <class T>
class Singleton
{
	static T * m_pInstance;

		// make non-copyable
	Singleton( Singleton const & );
	Singleton & operator=( Singleton const & );
public:
	Singleton();
	~Singleton();

	static T & Get();
	static void Release();
};

}

#include "Singleton.inl"
#endif

