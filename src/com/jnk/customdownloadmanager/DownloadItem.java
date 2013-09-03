package com.jnk.customdownloadmanager;

class DownloadItem
{
	private String url;
	private String destination;
	private int attemptsToDownLoad = 0;
	private String downloadStatus;

	DownloadItem(String url, String destination)
	{
		this.url = url;
		this.destination = destination;
	}

	public String getUrl()
	{
		return url;
	}

	public String getDestiantion()
	{
		return destination;
	}

	public void addAttemptToDownLoad()
	{
		attemptsToDownLoad++;
	}

	public int getAttemptsToDownload()
	{
		return attemptsToDownLoad;
	}

	public String getDownloadStatus()
	{
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus)
	{
		this.downloadStatus = downloadStatus;
	}
}