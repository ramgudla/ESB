package com.technotes.peer1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.technotes.esb.server.ApplicationTransaction;
import com.technotes.esb.server.BodyBlock;
import com.technotes.esb.server.SOAPTransaction;
import com.technotes.esb.utils.DomNodeManager;

public class CitiesTransaction implements ApplicationTransaction
{

	CitiesConnector citiesConnector;

	private SOAPTransaction soaptransaction;

	public CitiesTransaction ( CitiesConnector citiesConnector, SOAPTransaction soapTransaction )
	{
		this.citiesConnector = citiesConnector;
		this.soaptransaction = soapTransaction;
	}

	@Override
	public boolean process ( BodyBlock requestBlock, BodyBlock responseBlock )
	{
		try
		{
			DomNodeManager domMgr = this.citiesConnector.domMgr;
			Document doc = domMgr.getDocument();
			Node request = doc.importNode(requestBlock.node, true);
			Node response = doc.importNode(responseBlock.node, true);
			Node reqNode = domMgr.getFirstElement(domMgr.evaluateNode(request, "Body"));
			if (reqNode.getNodeName().equals("GetCities"))
			{
				Node responseNode = domMgr.evaluateNode(response, ".//GetCitiesResponse");
				Element citiesNode = doc.createElement("Cities");
				responseNode.appendChild(citiesNode);
				Element cityNode = doc.createElement("City");
				cityNode.setTextContent("Hyderabad");
				citiesNode.appendChild(cityNode);
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
