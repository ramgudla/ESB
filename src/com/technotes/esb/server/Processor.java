package com.technotes.esb.server;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.technotes.esb.client.Connector;
import com.technotes.esb.client.SOAPMessageListener;
import com.technotes.esb.client.protocol.Message;
import com.technotes.esb.utils.DomNodeManager;
import com.technotes.esb.utils.Factory;
import com.technotes.store.Store;

public class Processor implements SOAPMessageListener
{
	Connector connector = null;

	private String name;

	private Factory factory = Factory.getInstance();

	private DomNodeManager domMgr = factory.getDomNodeManager();

	ApplicationConnector appConnector;

	public Processor ( String name )
	{
		this.name = name;
		connector = Connector.getInstance(name);
		try
		{
			appConnector = Store.appConnectors.get(name).newInstance();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public static void startProcessor ( String name )
	{
		Processor processor = new Processor(name);
		processor.open();
		System.out.println(String.format("Peer with name '%s' is started.", name));
		// Suspend this thread.
		Object o = new Object();
		synchronized (o)
		{
			try
            {
	            o.wait();
            }
            catch (InterruptedException e)
            {
            }
		}
	}

	private void open ()
	{
		connector.setDefaultListener(this);
		connector.open();
		appConnector.open(this);
	}

	@Override
	public void onReceive ( Message msg )
	{
		try
		{
			System.out.println("Received message to Processor.");
			Node requestNode = domMgr.stringToXml(msg.text);
			new SOAPTransaction(requestNode, this);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}
}