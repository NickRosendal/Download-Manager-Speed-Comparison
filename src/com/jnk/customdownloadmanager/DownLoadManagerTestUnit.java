package com.jnk.customdownloadmanager;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DownLoadManagerTestUnit
{
	DownloadManager downloadManager;
	CurrentZip ourUnpacker;

	@Before
	public void setup()
	{

	}

	@Test
	public void canDownlaodAFile()
	{
		System.out.println("-- canDownlaodAFile --");
		File f = new File("test1.zip");
		if (f.exists())
		{
			f.delete();
		}
		downloadManager = new DownloadManager(3);

		downloadManager.addFileToQue("http://127.0.0.1/test1.zip", "test1.zip");

		while (downloadManager.getCompletedDownloads().size() < 1)
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("download compleate");
		assertTrue(f.exists());

	}

	@Test
	public void canUnpackAFile()
	{
		System.out.println("-- canUnpackAFile --");
		File f = new File("test1.zip");
		File downloadFolder = new File("zipTestArea");
		if (f.exists())
		{
			if (downloadFolder.exists())
			{
				ourUnpacker = new CurrentZip();
				ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
			}
			
			ZipFile zip;
			try
			{
				zip = new ZipFile(new File("test1.zip"));
				ourUnpacker = new CurrentZip(zip, "zipTestArea");

			} catch (ZipException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
		assertTrue(downloadFolder.exists());
	}
}
