package com.technotes.webserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

class HttpRequest
{

	Socket clientConnection;

	BufferedReader dataIn;

	DataOutputStream dataOut;

	HttpRequest ( final Socket clientConnection )
	{
		this.clientConnection = clientConnection;
	}

	public String getPostBody () throws IOException
	{
		dataIn = new BufferedReader(new InputStreamReader(clientConnection.getInputStream())); // initiating
		                                                                                       // bufferReader
		StringBuffer request = new StringBuffer();
		String line;
		int length = 0;
		while ((line = dataIn.readLine()) != null)
		{
			if (line.isEmpty())
			{ // last line of request message header is a blank line (\r\n\r\n)
				StringBuilder body = new StringBuilder();
				if (length > 0)
				{
					int read;
					while ((read = dataIn.read()) != -1)
					{
						body.append((char) read);
						if (body.length() == length)
							break;
					}
					request.append(body); // adding the body to request
				}
				break; // quit while loop when last line of header is reached
			}

			// checking line if it has information about Content-Length
			// weather it has message body or not
			if (line.startsWith("Content-Length: "))
			{ // get the content-length
				int index = line.indexOf(':') + 1;
				String len = line.substring(index).trim();
				length = Integer.parseInt(len);
			}
		} // end of while to read headers

		return request.toString();
	}

	/**
	 * Decodes the percent encoding scheme. <br/>
	 * For example: "an+example%20string" -> "an example string"
	 */
	private String decodePercent ( String str ) throws InterruptedException
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < str.length(); i++)
			{
				char c = str.charAt(i);
				switch (c)
				{
					case '+':
						sb.append(' ');
						break;
					case '%':
						sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
						i += 2;
						break;
					default:
						sb.append(c);
						break;
				}
			}
			return new String(sb.toString().getBytes());
		}
		catch (Exception e)
		{
			System.out.println("BAD REQUEST: Bad percent-encoding.");
			return null;
		}
	}

	public void close ()
	{
		close(dataIn);
		close(dataOut);
		close(clientConnection);
	}

	/**
	 * close method closes the given stream.
	 * 
	 * @param stream
	 */
	private void close ( Object stream )
	{
		if (stream == null)
			return;

		try
		{
			if (stream instanceof Reader)
			{
				((Reader) stream).close();
			}
			else if (stream instanceof Writer)
			{
				((Writer) stream).close();
			}
			else if (stream instanceof InputStream)
			{
				((InputStream) stream).close();
			}
			else if (stream instanceof OutputStream)
			{
				((OutputStream) stream).close();
			}
			else if (stream instanceof Socket)
			{
				((Socket) stream).close();
			}
			else
			{
				System.err.println("Unable to close object: " + stream);
			}
		}
		catch (Exception e)
		{
			System.err.println("Error closing stream: " + e);
		}
	}

	public void sendReply ( String response ) throws IOException
	{
		int contentLength = response.getBytes().length;
		dataOut = new DataOutputStream(new BufferedOutputStream(clientConnection.getOutputStream(), 100000));
		dataOut.writeBytes("HTTP/1.1 200 OK");
		dataOut.writeBytes("Content-Type: text/xml\r\n");
		dataOut.writeBytes("Content-Length: " + contentLength + "\r\n\r\n");
		dataOut.write(response.getBytes(), 0, contentLength); // write response
		dataOut.flush(); // flush binary output stream buffer
	}
}