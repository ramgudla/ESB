package com.technotes.peer2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.technotes.esb.server.ApplicationTransaction;
import com.technotes.esb.server.BodyBlock;
import com.technotes.esb.server.SOAPTransaction;
import com.technotes.esb.utils.DomNodeManager;

public class NamesTransaction implements ApplicationTransaction
{

	NamesConnector namesConnector;

	private SOAPTransaction soaptransaction;

	public NamesTransaction ( NamesConnector namesConnector, SOAPTransaction soapTransaction )
	{
		this.namesConnector = namesConnector;
		this.soaptransaction = soapTransaction;
	}

	@Override
	public boolean process ( BodyBlock requestBlock, BodyBlock responseBlock )
	{
		try
		{
			DomNodeManager domMgr = this.namesConnector.domMgr;
			Document doc = domMgr.getDocument();
			Node request = doc.importNode(requestBlock.node, true);
			Node response = doc.importNode(responseBlock.node, true);
			Node reqNode = domMgr.getFirstElement(domMgr.evaluateNode(request, "Body"));
			if (reqNode.getNodeName().equals("GetNames"))
			{
				Node responseNode = domMgr.evaluateNode(response, ".//GetNamesResponse");
				Element namesNode = doc.createElement("Names");
				responseNode.appendChild(namesNode);
				Element nameNode = doc.createElement("Name");
				nameNode.setTextContent("Ramky");
				namesNode.appendChild(nameNode);
				doc.normalize();
				responseBlock.node = response;
				return true;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return false;
	}
}
