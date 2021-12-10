package com.technotes.esb.server;

public abstract class ApplicationConnector
{

	public void open ( Processor processor )
	{
	}

	public ApplicationTransaction createTransaction ( SOAPTransaction transaction )
	{
		return null;
	}
}