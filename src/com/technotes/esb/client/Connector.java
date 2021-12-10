package com.technotes.esb.client;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.exceptions.TimeOutException;
import com.technotes.esb.client.protocol.Message;
import com.technotes.esb.client.protocol.SOAPMessage;

public class Connector implements MessageListener
{

	private static final long DEFAULT_TIMEOUT = 30000;

	private SOAPMessageListener defaultListener;

	public Middleware middleware;

	Map<UUID, SOAPMessageListener> listeners = new TreeMap<UUID, SOAPMessageListener>();

	private static Dictionary<String, Connector> connectors = new Hashtable<String, Connector>();

	private Connector ( String name )
	{
		middleware = new Middleware(name, this);
	}

	public void open ()
	{
		if (isOpen())
		{
			return;
		}
		middleware.open();
	}

	public boolean isOpen ()
	{
		return middleware.isOpen();
	}

	public static Connector getInstance ( String name )
	{
		Connector connector = connectors.get(name);
		if (connector == null)
		{
			connector = new Connector(name);
			connectors.put(name, connector);
		}
		return connector;
	}

	public void send ( String text ) throws SendingFailedException
	{
		middleware.send(SOAPMessage.toMessage(text));
	}

	public String sendAndWait ( Message msg ) throws TimeOutException, SendingFailedException
	{
		return sendAndWait(msg, DEFAULT_TIMEOUT);
	}

	public String sendAndWait ( Message msg, long timeout ) throws TimeOutException, SendingFailedException
	{
		ResponseListener listener = new ResponseListener(msg.id);
		listeners.put(msg.id, listener);
		middleware.send(msg);
		return listener.suspend(timeout);
	}

	public void sendAndCallback ( Message msg, SOAPMessageListener listener ) throws SendingFailedException
	{
		listeners.put(msg.id, listener);
		middleware.send(msg);
	}

	public void setDefaultListener ( SOAPMessageListener listener )
	{
		this.defaultListener = listener;
	}

	@Override
	public void onReceive ( Message msg )
	{
		if (defaultListener != null)
		{
			defaultListener.onReceive(msg);
		}
		else
		{
			listeners.get(msg.id).onReceive(msg);
		}
	}

	public void close ()
	{
		this.middleware.close();
		connectors.remove(middleware.name);
	}

	class ResponseListener implements SOAPMessageListener
	{

		volatile Message response;

		private UUID key;

		private ResponseListener ( UUID id )
		{
			this.key = id;
		}

		@Override
		synchronized public void onReceive ( Message msg )
		{
			listeners.remove(key); // unregister listener
			this.response = msg;
			notifyAll();
		}

		synchronized public String suspend ( long timeout ) throws TimeOutException
		{
			while (true)
			{
				if (this.response != null)
				{
					return this.response.text;
				}
				try
				{
					if (timeout > 0)
					{
						wait(timeout);
						if (this.response == null)
						{
							listeners.remove(key);
							throw new TimeOutException();
						}
					}
					else
					{
						wait();
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					return null;
				}
			}
		}
	}
}