package com.technotes.esb.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.protocol.Message;

/**
 * This class is useful for synchronous handling of sendAndCallback.
 */
public class SynchronousRequestResponseHandler implements SOAPMessageListener
{
	private static final Map<Connector, SynchronousRequestResponseHandler> handlers = new ConcurrentHashMap<Connector, SynchronousRequestResponseHandler>();

	private final Map<UUID, ResponseWrapper> responseContainer = new ConcurrentHashMap<UUID, ResponseWrapper>();

	private final Connector connector;

	private SynchronousRequestResponseHandler ( Connector connector )
	{
		this.connector = connector;
	}

	/**
	 * It returns handler for a given connector instance.
	 * 
	 * @param connector
	 * @return
	 */
	public synchronized static SynchronousRequestResponseHandler getHandler ( Connector connector )
	{
		if (connector != null)
		{
			SynchronousRequestResponseHandler handler = handlers.get(connector);
			if (handler == null)
			{
				handler = new SynchronousRequestResponseHandler(connector);
				handlers.put(connector, handler);
			}
			return handler;
		}
		return null;
	}

	/**
	 * It removes handler for the given connector instance.
	 * 
	 * @param connector
	 */
	public synchronized static void removeHandler ( Connector connector )
	{
		if (connector != null)
		{
			handlers.remove(connector);
		}
	}

	public Message sendAndWaitNonTransactional ( Message msg ) throws InterruptedException, SendingFailedException
	{
		ResponseWrapper response = new ResponseWrapper();
		responseContainer.put(msg.id, response);
		try
		{
			connector.sendAndCallback(msg, this);
			return response.getResponse();
		}
		finally
		{
			responseContainer.remove(msg.id);
		}
	}

	/*
	 * Notify the response which is waiting
	 */
	public void notifyWaitingResponse ( Message msg )
	{
		ResponseWrapper response = responseContainer.get(msg.id);
		if (response != null)
		{
			response.setTimedOut();
		}
	}

	@Override
	public void onReceive ( Message msg )
	{
		connector.listeners.remove(msg.id);
		ResponseWrapper wrapper = responseContainer.get(msg.id);
		wrapper.setResponseMessage(msg);
	}

	private class ResponseWrapper
	{
		private boolean isTimedOut = false;

		private Message response = null;

		synchronized Message getResponse () throws InterruptedException
		{
			while (!isTimedOut && response == null)
			{
				wait();
			}
			return response;
		}

		private synchronized void setTimedOut ()
		{
			isTimedOut = true;
			notify();
		}

		private synchronized void setResponseMessage ( Message msg )
		{
			response = msg;
			notify();
		}
	}
}