package com.technotes.esb.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DomNodeManager
{

	private DocumentBuilder documentBuilder;

	private Transformer transformer;

	private XPath xpath;

	public DomNodeManager ( final DocumentBuilder docBuilder, final Transformer xmlTransformer, final XPath xPath )
	{
		this.documentBuilder = docBuilder;
		this.transformer = xmlTransformer;
		this.xpath = xPath;
	}

	public Node stringToXml ( final String xmlString ) throws SAXException, IOException, ParserConfigurationException
	{
		if (xmlString == null)
		{
			throw new IllegalArgumentException("xmlString cannot be null");
		}
		final Node node = this.documentBuilder.parse(new InputSource(new StringReader(xmlString))).getFirstChild();

		return node;
	}

	public String xmlToString ( final Node node, final boolean omitXMLDecl, final boolean formatContent )
	        throws TransformerException
	{
		Node nodeCopy = node;
		if (Node.DOCUMENT_NODE == nodeCopy.getNodeType())
		{
			nodeCopy = ((Document) nodeCopy).getDocumentElement();
		}
		if (formatContent)
		{
			this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		else
		{
			this.transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}
		this.transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
		this.transformer.setOutputProperty(OutputKeys.METHOD, "xml");

		if (omitXMLDecl)
		{
			this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		final DOMSource domsource = new DOMSource(nodeCopy);
		final StringWriter stringWriter = new StringWriter();
		final StreamResult streamResult = new StreamResult(stringWriter);
		this.transformer.transform(domsource, streamResult);
		return stringWriter.toString();
	}

	public Document getDocument ()
	{
		final Document doc = this.documentBuilder.newDocument();
		return doc;
	}

	public NodeList evaluateNodes ( final Object source, final String xpathExp ) throws XPathExpressionException
	{
		final XPathExpression xpathExpression = this.xpath.compile(xpathExp);
		return (NodeList) xpathExpression.evaluate(source, XPathConstants.NODESET);
	}

	public Node evaluateNode ( final Object source, final String xpathExp ) throws XPathExpressionException
	{
		final XPathExpression xpathExpression = this.xpath.compile(xpathExp);
		return (Node) xpathExpression.evaluate(source, XPathConstants.NODE);
	}

	public Element createTextElement ( final Document doc, final String elementName, final String textContent,
	        final Node parentNode )
	{
		final Element newNode = doc.createElement(elementName);
		newNode.setTextContent(textContent);
		return (Element) parentNode.appendChild(newNode);
	}

	public Element getFirstElement ( final Node parent )
	{
		Node n = parent.getFirstChild();
		while (n != null && Node.ELEMENT_NODE != n.getNodeType())
		{
			n = n.getNextSibling();
		}
		if (n == null)
		{
			return null;
		}
		return (Element) n;
	}
}