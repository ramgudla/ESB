package com.technotes.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.technotes.esb.client.Connector;
import com.technotes.esb.client.SOAPMessageListener;
import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.exceptions.TimeOutException;
import com.technotes.esb.client.protocol.Message;
import com.technotes.esb.client.protocol.SOAPMessage;

public class Test1 {
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, TransformerException {

		// using sendAndWait
		Connector client = Connector.getInstance("Anonymous Client1");
		client.open();
		
		String request = 	"<Message>" + 
							   "<Header>" +
							      "<target>cn=Peer1</target>" +
							   "</Header>" +
							   "<Body>" +
							      "<GetCities/>" +
							   "</Body>" +
							"</Message>";
		
		System.out.println("Request : " + "\n" + request);

		try {
			String response = client.sendAndWait(SOAPMessage.toMessage(request));
			System.out.println("Response using sendAndWait : " + "\n" + response);
		} catch (TimeOutException e) {
			System.out.println("Timedout");
			e.printStackTrace();
		} catch (SendingFailedException e) {
			System.out.println("Failed to send.");
			e.printStackTrace();
		}
		client.close();

		// using setMessageListener
		client = Connector.getInstance("Anonymous Client3");
		client.open();
		request = 	"<Message>" + 
					   "<Header>" +
					      "<target>cn=Peer1</target>" +
					   "</Header>" +
					   "<Body>" +
					      "<GetCities/>" +
					   "</Body>" +
					"</Message>";
		client.setDefaultListener(new Test1().new MyListener());
		try {
			client.send(request);
		} catch (SendingFailedException e) {
			System.out.println("Failed to send.");
			e.printStackTrace();
		}
		//client.close();
	}
	
	class MyListener implements SOAPMessageListener
	{
		@Override
		public void onReceive(Message msg) {
			System.out.println("Response using setMessageListener :" + "\n" + msg.text);
		}
	}
}