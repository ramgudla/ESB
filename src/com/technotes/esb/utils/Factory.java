package com.technotes.esb.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;

public class Factory
{
	private static Factory factory;

	private DocumentBuilderFactory docBuilderFactory;

	private XPathFactory xpathFactory;

	private TransformerFactory transformerFactory;

	private DocumentBuilder documentBuilder;

	private Factory ( DocumentBuilderFactory docBuilderFactory, XPathFactory xpathFactory,
	        TransformerFactory transformerFactory )
	{
		this.docBuilderFactory = docBuilderFactory;
		this.xpathFactory = xpathFactory;
		try
		{
			this.documentBuilder = this.docBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex)
		{
		}
		this.transformerFactory = transformerFactory;
	}

	public static Factory getInstance ()
	{
		return getInstance(null);
	}

	public static synchronized Factory getInstance ( ClassLoader classLoader )
	{
		if (factory == null)
		{
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setIgnoringComments(true);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			XPathFactory xpathFactory = XPathFactory.newInstance();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			factory = new Factory(docBuilderFactory, xpathFactory, transformerFactory);
		}
		return factory;
	}

	public DomNodeManager getDomNodeManager ()
	{
		try
		{
			return new DomNodeManager(documentBuilder, transformerFactory.newTransformer(), xpathFactory.newXPath());
		}
		catch (TransformerConfigurationException ex)
		{
		}
		return null;
	}
}