package com.technotes.esb.server;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.utils.DomNodeManager;
import com.technotes.esb.utils.Factory;

public class SOAPTransaction
{

	private BodyBlock request;

	private BodyBlock response;

	private Processor processor;

	public SOAPTransaction ( Node requestNode, Processor processor )
	{
		this.request = new BodyBlock(requestNode);
		this.response = new BodyBlock(createEmptyRsponseNode());
		this.processor = processor;
		processRequest();
		sendReply(response.node);
	}
	
	private void processRequest()
	{
		ApplicationTransaction appTransaction = processor.appConnector.createTransaction(this);
		appTransaction.process(request, response);
	}
	
	private void sendReply ( Node node )
	{
		try
		{
			Factory factory = Factory.getInstance();
			DomNodeManager domMgr = factory.getDomNodeManager();
			processor.connector.send(domMgr.xmlToString(node, true, true));
		}
		catch (SendingFailedException e)
		{
			System.out.println("Failed to send response.");
		}
        catch (TransformerException e)
        {
        	System.out.println("Failed to send response.");
        }
	}

	private Node createEmptyRsponseNode ()
	{
		Factory factory = Factory.getInstance();
		DomNodeManager domMgr = factory.getDomNodeManager();
		Document doc = domMgr.getDocument();
		Element msgNode = doc.createElement("Message");
		Node clonedRequest = doc.importNode(request.node.cloneNode(true), true);
		try
		{
			Node headerNode = domMgr.evaluateNode(clonedRequest, "Header");
			Node replytoNode = domMgr.evaluateNode(headerNode, "reply_to");
			Node targetNode = domMgr.evaluateNode(headerNode, "target");
			targetNode.setTextContent(replytoNode.getTextContent());
			msgNode.appendChild(headerNode);
			Element bodyNode = doc.createElement("Body");
			Node reqNode = domMgr.getFirstElement(domMgr.evaluateNode(clonedRequest, "Body"));
			Node responseNode = doc.createElement(reqNode.getNodeName() + "Response");
			bodyNode.appendChild(responseNode);
			msgNode.appendChild(bodyNode);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		return msgNode;
	}
}