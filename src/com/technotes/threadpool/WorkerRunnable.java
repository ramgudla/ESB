package com.technotes.threadpool;

import com.technotes.esb.client.MessageListener;
import com.technotes.esb.client.protocol.Message;

public class WorkerRunnable implements Runnable
{

	private MessageListener listener = null;

	private Message message;

	public WorkerRunnable ( Message msg, MessageListener listener )
	{
		this.message = msg;
		this.listener = listener;
	}

	@Override
	public void run ()
	{
		if (listener != null)
		{
			listener.onReceive(message);
		}
	}
}