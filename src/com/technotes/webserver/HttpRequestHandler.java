package com.technotes.webserver;

import java.io.IOException;

import com.technotes.esb.client.Connector;
import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.exceptions.TimeOutException;
import com.technotes.esb.client.protocol.SOAPMessage;

public class HttpRequestHandler implements Runnable
{

	protected HttpRequest httpRequest = null;

	public HttpRequestHandler ( HttpRequest httpRequest )
	{
		this.httpRequest = httpRequest;
	}

	@Override
	public void run ()
	{
		try
		{
			String request = httpRequest.getPostBody();
			System.out.println(request);

			Connector connector = Connector.getInstance("webgateway");
			connector.open();

			String response = connector.sendAndWait(SOAPMessage.toMessage(request));
			connector.close();

			httpRequest.sendReply(response);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TimeOutException e)
		{
			e.printStackTrace();
		}
		catch (SendingFailedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			httpRequest.close();
		}
	}
}