package com.technotes.jmx;

import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class ApplicationCache extends NotificationBroadcasterSupport implements ApplicationCacheMBean
{

	private int maxCacheSize = 100;

	private List cache = new ArrayList();

	private long sequenceNumber = 1;

	@Override
	public synchronized void clearCache ()
	{
		cache.clear();
	}

	@Override
	public synchronized int getCachedObjects ()
	{
		return cache.size();
	}

	@Override
	public synchronized int getMaxCacheSize ()
	{
		return maxCacheSize;
	}

	@Override
	public synchronized void setMaxCacheSize ( int value )
	{
		if (value < 1)
		{
			throw new IllegalArgumentException("Value must be >= 1");
		}
		int oldSize = this.maxCacheSize;
		this.maxCacheSize = value;
		Notification n = new AttributeChangeNotification(this, sequenceNumber++, System.currentTimeMillis(),
		        "MaxCacheSize changed", "MaxCacheSize", "int", oldSize, this.maxCacheSize);
		sendNotification(n);
	}

	public synchronized void cacheObject ( Object o )
	{
		while (cache.size() >= maxCacheSize)
		{
			cache.remove(0);
		}
		cache.add(o);
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo ()
	{
		String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] { info };
	}
}