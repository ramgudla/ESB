package com.technotes.esb.utils;

import java.util.TimerTask;

import com.technotes.esb.client.TimeOutListener;

public class ResponseTimeoutTask extends TimerTask
{
	Object task = null;
	public ResponseTimeoutTask(Object task)
	{
		this.task = task;
	}
	
	/***
	 * This calls the onTimeOut implementation of the TimeOutListener interface
	 */
	@Override
	public void run()
	{
		synchronized(task)
		{
			((TimeOutListener)task).onTimeOut();
		}
	}
}
