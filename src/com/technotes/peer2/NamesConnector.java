package com.technotes.peer2;

import com.technotes.esb.server.ApplicationConnector;
import com.technotes.esb.server.ApplicationTransaction;
import com.technotes.esb.server.Processor;
import com.technotes.esb.server.SOAPTransaction;
import com.technotes.esb.utils.DomNodeManager;
import com.technotes.esb.utils.Factory;

public class NamesConnector extends ApplicationConnector
{

	Factory factory;

	DomNodeManager domMgr;

	@Override
	public void open ( Processor processor )
	{
		factory = Factory.getInstance();
		domMgr = factory.getDomNodeManager();
	}

	@Override
	public ApplicationTransaction createTransaction ( SOAPTransaction soapTransaction )
	{
		return new NamesTransaction(this, soapTransaction);
	}
}
