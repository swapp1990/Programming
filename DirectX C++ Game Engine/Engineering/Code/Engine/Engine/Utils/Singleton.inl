namespace BlackRock
{

template<class T>
Singleton<T>::~Singleton()
{
	if( m_pInstance )
	{
		delete m_pInstance;
			
		m_pInstance = 0;
	}
}

template<class T>
T & Singleton<T>::Get()
{
	if( m_pInstance == 0 ) 
		m_pInstance = new T;

	assert( m_pInstance );

	return *m_pInstance;
}

template<class T>
void Singleton<T>::Release()
{
	if( m_pInstance )
	{
		delete m_pInstance;
		
		m_pInstance = 0;
	}
}

template <class T> T * Singleton<T>::m_pInstance = 0;
}