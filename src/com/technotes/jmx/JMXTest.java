package com.technotes.jmx;

import java.lang.management.ManagementFactory;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class JMXTest
{
	public static void main ( String[] args ) throws Exception
	{
		ApplicationCache cache = new ApplicationCache();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("com.technotes.jmx:type=ApplicationCacheMBean");
		mbs.registerMBean(cache, name);
		// Start an RMI registry on port specified by example.rmi.agent.port
		// (default 3000).
		int port = Integer.parseInt(System.getProperty("example.rmi.agent.port", "3000"));
		LocateRegistry.createRegistry(port); // (prompt> rmiregistry 3000)
		// Environment map. This where we would enable security - left out of
		// this
		// for the sake of the example....
		HashMap<String, Object> env = new HashMap<String, Object>();

		JMXAuthenticator authenticator = new PasswordAuthenticator();
		env.put(JMXConnectorServer.AUTHENTICATOR, authenticator);

		// Create an RMI connector server.
		//
		// As specified in the JMXServiceURL the RMIServer stub will be
		// registered in the RMI registry running in the local host on
		// port 3000 with the name "server". This is the same name the
		// out-of-the-box
		// management agent uses to register the RMIServer stub too.
		//
		// The port specified in "service:jmx:rmi://"+hostname+":"+port
		// is the second port, where RMI connection objects will be exported.
		// Here we use the same port as that we choose for the RMI registry.
		// The port for the RMI registry is specified in the second part
		// of the URL, in "rmi://"+hostname+":"+port
		String hostname = InetAddress.getLocalHost().getHostName();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostname + ":" + port + "/server");
		// Now create the server from the JMXServiceURL
		JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
		// Start the RMI connector server.
		cs.start();
		imitateActivity(cache);
	}

	private static void imitateActivity ( ApplicationCache cache )
	{
		while (true)
		{
			try
			{
				cache.cacheObject(new Object());
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
