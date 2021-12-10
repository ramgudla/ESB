package com.technotes.esb.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.technotes.esb.client.exceptions.SendingFailedException;
import com.technotes.esb.client.protocol.Message;
import com.technotes.esb.client.protocol.SOAPMessage;
import com.technotes.esb.client.protocol.SocketProtocol;
import com.technotes.esb.client.protocol.URI;
import com.technotes.store.Store;
import com.technotes.threadpool.ThreadPool;
import com.technotes.threadpool.WorkerRunnable;

public class Middleware
{

	String name;

	private MessageListener listener;

	ServerSocket serverSocket;

	URI inboundURI;

	SocketReader sr;

	private static final int INVALID = 0;

	private static final int OPENED = 1;

	private static final int CLOSED = 2;

	private volatile int state = INVALID;

	Middleware ( String name, MessageListener listener )
	{
		this.name = name;
		this.listener = listener;
	}

	public void open ()
	{
		initialize();
		sr = new SocketReader();
		sr.setDaemon(true);
		sr.start();
		state = OPENED;
	}

	public boolean isOpen ()
	{
		return state == OPENED;
	}

	public boolean isClosed ()
	{
		return state == CLOSED;
	}

	private void initialize ()
	{
		try
		{
			String address = Store.addresses.get(name);
			if (address == null)
			{
				this.serverSocket = new ServerSocket(0);
				address = "socket://" + InetAddress.getLocalHost().getHostName() + ":" + serverSocket.getLocalPort();
				inboundURI = URI.createURI(address);
			}
			else
			{
				inboundURI = URI.createURI(Store.addresses.get(name));
				this.serverSocket = new ServerSocket(inboundURI.getPort());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void send ( Message msg ) throws SendingFailedException
	{
		try
		{
			Message message = SOAPMessage.addReplytoAddress(msg, getAddress());

			Socket connection = new Socket(message.target.getHost(), message.target.getPort());
			DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream(),
			        100000));
			SocketProtocol.write(outputStream, message);
			outputStream.close();
			connection.close();
		}
		catch (Exception ex)
		{
			throw new SendingFailedException(ex);
		}
	}

	public String getAddress ()
	{
		return inboundURI.toString();
	}

	public void close ()
	{
		if (isClosed())
		{
			return;
		}
		try
		{
			this.serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.sr.shouldBeRunning = false;
		state = CLOSED;
	}

	class SocketReader extends Thread
	{

		volatile boolean shouldBeRunning = true;

		private ThreadPool threadPool;

		SocketReader ()
		{
		}

		@Override
		public void run ()
		{
			this.threadPool = new ThreadPool(10);
			while (this.shouldBeRunning)
			{
				try
				{
					Socket connectionSocket = serverSocket.accept();
					connectionSocket.setTcpNoDelay(true);
					// This call causes the socket to throw an
					// InterruptedIOException
					// if the socket receives no data for a certain amount of
					// time.
					connectionSocket.setSoTimeout(100000);
					// Make sure socket connection is closed immediately on
					// socket.close();
					connectionSocket.setSoLinger(true, 0);
					final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(
					        connectionSocket.getInputStream(), 100000));
					Message msg = SocketProtocol.read(inputStream);
					inputStream.close();
					connectionSocket.close();
					this.threadPool.execute(new WorkerRunnable(msg, listener));
				}
				catch (SocketException se)
				{
				}
				catch (IOException e)
				{
					break;
				}
				// outputStream = connectionSocket.getOutputStream();
			}
		}
	}
}