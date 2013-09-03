package com.jnk.customdownloadmanager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class DownloadManager
{
	private LinkedList<DownloadItem> completedDownloads = new LinkedList<DownloadItem>();
	private int threadCount;
	private ExecutorService threadPool = null;
	private int allowedAtteptsToDownload = 3;
	// CompletionService<DownloadItem> pool;
	int threadsInCue = 0;

	public DownloadManager(int threadCount)
	{
		this.threadCount = threadCount;
	}

	private void threadPoolCreator()
	{
		threadPool = Executors.newFixedThreadPool(threadCount);
		// threadPool = new ThreadPoolExecutor(threadCount, threadCount, 0L,
		// TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000));
	}

	public void addDownloadItemToQue(DownloadItem downloadItem)
	{

		if (threadPool == null || threadPool.isShutdown() || threadPool.isTerminated())
		{
			threadPoolCreator();
		}

		try
		{
			threadPool.execute(new DownloadTask(downloadItem));
			threadsInCue ++;

		} catch (Exception e)
		{

			e.printStackTrace();
		}

		// threadPool.shutdown(); // where should this be?

	}

	public void addFileToQue(String url, String destination)
	{
		addDownloadItemToQue(new DownloadItem(url, destination));

	}

	public void notifiedThatDownloadHasFinished(DownloadItem downloadItem)
	{

		if (!downloadItem.getDownloadStatus().equals("completed") && downloadItem.getAttemptsToDownload() < allowedAtteptsToDownload)
		{
			downloadItem.addAttemptToDownLoad();
			addDownloadItemToQue(downloadItem);

		} else
		{
			completedDownloads.add(downloadItem);
			threadsInCue --;
		}

	}

	class DownloadTask implements Runnable
	{
		DownloadItem itemToDownload;

		public DownloadTask(DownloadItem itemToDownload)
		{
			this.itemToDownload = itemToDownload;
		}

		@Override
		public void run()
		{
			try
			{

				URL url = new URL(itemToDownload.getUrl());
				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(itemToDownload.getDestiantion());
				Log.i("CustomDownLoadManager", "Error downloading: " + itemToDownload.getDestiantion());


				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1)
				{
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				// waiter();
				itemToDownload.setDownloadStatus("completed");
			} catch (Exception E)
			{
				E.printStackTrace();
				itemToDownload.setDownloadStatus(E + "");

			}
			notifiedThatDownloadHasFinished(itemToDownload);
			
			if(threadsInCue == 0) threadPool.shutdown();
		}
	}

	public LinkedList<DownloadItem> getCompletedDownloads()
	{
		return completedDownloads;
	}

	public LinkedList getIncompleteDownloads()
	{
		LinkedList<DownloadItem> incompletede = new LinkedList<DownloadItem>();
		for (DownloadItem curr : completedDownloads)
		{
			if (!curr.getDownloadStatus().equals("completed"))
			{
				incompletede.add(curr);
			}
		}

		return incompletede;

	}
}
