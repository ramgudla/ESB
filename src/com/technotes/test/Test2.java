package com.technotes.test;

import java.io.IOException;
import java.util.Timer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.technotes.esb.client.Connector;
import com.technotes.esb.client.SynchronousRequestResponseHandler;
import com.technotes.esb.client.TimeOutListener;
import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.protocol.Message;
import com.technotes.esb.client.protocol.SOAPMessage;
import com.technotes.esb.utils.ResponseTimeoutTask;

public class Test2 {

	class MyTask implements TimeOutListener {
		private Connector client = null;
		SynchronousRequestResponseHandler syncHandler = null;

		MyTask() {
			client = Connector.getInstance("Anonymous Client2");
			client.open();
			syncHandler = SynchronousRequestResponseHandler.getHandler(client);
		}

		String request =	"<Message>" + 
							   "<Header>" +
							      "<target>cn=Peer2</target>" +
							   "</Header>" +
							   "<Body>" +
							      "<GetNames/>" +
							   "</Body>" +
							"</Message>";

		final Message msg = SOAPMessage.toMessage(request);

		void sendSOAPRequest() {
			// using sync handler
			try {
				ResponseTimeoutTask task = new ResponseTimeoutTask(this);
				Timer timer = new Timer(true);
				timer.schedule(task, 300000);
				Message response = syncHandler.sendAndWaitNonTransactional(msg);
				System.out.println(response.text);
			} catch (SendingFailedException e) {
				System.out.println("Failed to send.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.close();
		}

		@Override
		public void onTimeOut() {
			System.out.println("Timed out.");
			syncHandler.notifyWaitingResponse(msg);
		}
	}

	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException, TransformerException {
		new Test2().new MyTask().sendSOAPRequest();
	}
}