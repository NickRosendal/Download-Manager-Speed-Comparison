package com.jnk.customdownloadmanager;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPollDemo
{
	int amountOfThreadsToUse = 4;

	ThreadPollDemo()
	{
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		CompletionService<String> pool = new ExecutorCompletionService<String>(threadPool);

		for (int i = 0; i < 100; i++)
		{
			System.out.print(i + " ");
			pool.submit(new StringTask(i));
		}
		for (int i = 0; i < 100; i++)
		{
			try
			{
				String result = pool.take().get();
				System.out.println(result);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		threadPool.shutdown();
		while (!threadPool.isTerminated())
			;
		System.out.println("all done");
	}

	private final class StringTask implements Callable<String>
	{
		int number;

		public StringTask(int i)
		{
			number = i;
		}

		public String call()
		{
			int ran = new Random().nextInt(5);
			return number + " Run " + ran;
		}
	}

}
