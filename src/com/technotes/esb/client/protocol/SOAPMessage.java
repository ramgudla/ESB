package com.technotes.esb.client.protocol;

import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.technotes.esb.utils.DomNodeManager;
import com.technotes.esb.utils.Factory;
import com.technotes.store.Store;

public class SOAPMessage
{

	public static Message toMessage ( String text )
	{
		Factory factory = Factory.getInstance();
		DomNodeManager domMgr = factory.getDomNodeManager();
		try
		{
			Node node = domMgr.stringToXml(text);
			Node headerNode = domMgr.evaluateNode(node, "Header");

			UUID id;

			Node msgIdNode = domMgr.evaluateNode(headerNode, "msg_id");
			if (msgIdNode == null)
			{
				id = UUID.randomUUID();
				domMgr.createTextElement(headerNode.getOwnerDocument(), "msg_id", id.toString(), headerNode);
				text = domMgr.xmlToString(node, true, true);
			}
			else
			{
				id = UUID.fromString(msgIdNode.getTextContent());
			}

			Node targetNode = domMgr.evaluateNode(node, ".//target");
			URI targetURI = getTargetURI(targetNode.getTextContent());

			Message msg = new Message(id, text, targetURI);
			return msg;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		catch (DOMException e)
		{
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private static URI getTargetURI ( String target )
	{
		if (target.startsWith("socket://"))
		{
			return URI.createURI(target);
		}
		return URI.createURI(Store.addresses.get(target));
	}

	public static Message addReplytoAddress ( Message msg, String address ) throws SAXException, IOException,
	        ParserConfigurationException, XPathExpressionException, TransformerException
	{
		Factory factory = Factory.getInstance();
		DomNodeManager domMgr = factory.getDomNodeManager();
		Node msgNode = domMgr.stringToXml(msg.text);
		Node headerNode = domMgr.evaluateNode(msgNode, ".//Header");
		Node replytoNode = domMgr.evaluateNode(headerNode, "reply_to");
		if (replytoNode != null)
		{
			replytoNode.setTextContent(address);
		}
		else
		{
			domMgr.createTextElement(headerNode.getOwnerDocument(), "reply_to", address, headerNode);
		}
		msg.text = domMgr.xmlToString(msgNode, true, true);
		return msg;
	}
}