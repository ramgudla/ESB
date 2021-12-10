package com.technotes.jmx;

public interface ApplicationCacheMBean
{
	int getMaxCacheSize ();

	void setMaxCacheSize ( int value );

	int getCachedObjects ();

	void clearCache ();
}