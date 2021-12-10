package com.technotes.store;

import java.util.Map;
import java.util.TreeMap;

import com.technotes.esb.server.ApplicationConnector;

public class Store
{
	public static Map<String, String> addresses = new TreeMap<String, String>();

	public static Map<String, Class<? extends ApplicationConnector>> appConnectors = new TreeMap<String, Class<? extends ApplicationConnector>>();

	static
	{
		addresses.put("cn=Peer1", "socket://localhost:123");
		appConnectors.put("cn=Peer1", com.technotes.peer1.CitiesConnector.class);

		addresses.put("cn=Peer2", "socket://localhost:321");
		appConnectors.put("cn=Peer2", com.technotes.peer2.NamesConnector.class);
	}
}