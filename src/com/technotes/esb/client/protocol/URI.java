package com.technotes.esb.client.protocol;

public class URI
{

	private String protocol;

	private String host;

	private int port;

	URI ( String uri )
	{
		parseURI(uri);
	}

	public String getProtocol ()
	{
		return protocol;
	}

	public String getHost ()
	{
		return host;
	}

	public int getPort ()
	{
		return port;
	}

	private void parseURI ( String uri )
	{
		String[] temp = uri.split(":");
		this.protocol = temp[0];
		this.host = temp[1].substring(2);
		this.port = Integer.parseInt(temp[2]);
	}

	public static URI createURI ( String uri )
	{
		return new URI(uri);
	}

	@Override
	public String toString ()
	{
		return this.protocol + ":" + "//" + host + ":" + port;
	}
}