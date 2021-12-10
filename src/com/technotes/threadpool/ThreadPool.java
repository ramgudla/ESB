package com.technotes.threadpool;

import java.util.LinkedList;

public class ThreadPool
{
	private final int nThreads;

	private final WorkerThread[] threads;

	private final LinkedList queue;

	public ThreadPool ( int nThreads )
	{
		this.nThreads = nThreads;
		queue = new LinkedList();
		threads = new WorkerThread[nThreads];
		for (int i = 0; i < nThreads; i++)
		{
			threads[i] = new WorkerThread();
			threads[i].setDaemon(true);
			threads[i].start();
		}
	}

	public void execute ( Runnable r )
	{
		synchronized (queue)
		{
			queue.addLast(r);
			queue.notify();
		}
	}

	private class WorkerThread extends Thread
	{
		@Override
		public void run ()
		{
			Runnable r;
			while (true)
			{
				synchronized (queue)
				{
					while (queue.isEmpty())
					{
						try
						{
							queue.wait();
						}
						catch (InterruptedException ignored)
						{
						}
					}
					r = (Runnable) queue.removeFirst();
				}
				// If we don't catch RuntimeException,
				// the pool could leak threads
				try
				{
					r.run();
				}
				catch (RuntimeException e)
				{
					// You might want to log something here
				}
			}
		}
	}
}