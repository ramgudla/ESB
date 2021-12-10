package com.technotes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.technotes.threadpool.ThreadPool;

public class Webserver
{

	private ServerSocket serverSocket;

	private int serverPort = 8080;

	private ThreadPool threadPool = new ThreadPool(10);

	Webserver ()
	{
	}

	public static void main ( String[] args ) throws UnknownHostException, IOException, InterruptedException
	{
		Webserver webserver = new Webserver();
		webserver.run();
	}

	private void run ()
	{
		openServerSocket();
		System.out.println("Webserver started.");
		while (true)
		{
			Socket clientConnection = null;
			try
			{
				clientConnection = this.serverSocket.accept();
				service(clientConnection);
			}
			catch (IOException e)
			{
				System.out.println("Webserver stopped.");
				return;
			}
		}
	}

	private void openServerSocket ()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.serverPort);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot open port: " + this.serverPort, e);
		}
	}

	private void service ( Socket clientConnection )
	{
		HttpRequest httpRequest = new HttpRequest(clientConnection);
		threadPool.execute(new HttpRequestHandler(httpRequest));
	}
}