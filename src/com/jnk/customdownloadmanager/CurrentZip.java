package com.jnk.customdownloadmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CurrentZip

{
	private ZipFile zip;
	private String outputDir;
	private ZipEntry entry;
	private static final int BUFFER_SIZE = 1024;

	
	public CurrentZip(){
		
	}
	public CurrentZip(ZipFile zip, String outputDir)
	{
		super();
		this.zip = zip;
		this.outputDir = outputDir;
		theunpack();
		
	}
	private String theunpack(){
		
	//	Log.v("newZip", " clicked theunpack, oldZip");
		int size = zip.size();
		int count = 0;
//		try{
//		File todelete = new File("/mnt/sdcard/Android/data/com.example.testingtrolololozip/files/mnt/sdcard/");
//		File toKeep = new File("/mnt/sdcard/Android/data/com.example.testingtrolololozip/files/mnt/sdcard/app.zip");
//		Log.v("oldzip", " deleting old shiiiite");
//		deleteDirectoryRecursively(todelete, toKeep, true);
//		}catch(Exception E){
//			return "could not delete";
//		}
		
		
		long startTime = System.currentTimeMillis();
		
		for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) 
		{
			entry = (ZipEntry) e.nextElement();
			try
			{
				unpackEntry();
				
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return "fail";
			}
		}
		
		
		
		long difference = System.currentTimeMillis() - startTime;
		return "theunpack have finished: "+ difference;
	}

	public void unpackEntry() throws IOException
	{
		if (entry.isDirectory())
		{
			createDir(new File(outputDir, entry.getName()));
			
			return;
		}
		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists())
		{
			if (!createDir(outputFile.getParentFile()))
				throw new IOException();

		}
		try
		{
			byte[] buffer = new byte[BUFFER_SIZE];

			InputStream in = zip.getInputStream(entry);
			OutputStream out = new FileOutputStream(outputFile);
			int n = 0;
			try
			{
				while ((n = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, n);
				}
			} finally
			{
				try
				{
					out.close();
				} catch (IOException e)
				{
					System.out.println("io... out");
				}
				try
				{
					in.close();
				} catch (IOException e)
				{
					System.out.println("io... in");
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error copying stream ...");
		}
		
	}

	private static boolean createDir(File dir)
	{
		if (!dir.mkdirs())
		{
			if(dir.exists()) return true;
			System.out.println("Could not create dir: " + dir);
			return false;
		}
		return true;
	}

	public static boolean deleteDirectoryRecursively(File path, File ignore, boolean root)
	{
		if (path.exists() && path.isDirectory())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					if (ignore == null || !files[i].getAbsolutePath().equals(ignore.getAbsolutePath()))
						deleteDirectoryRecursively(files[i], ignore, false);
				} else if (ignore == null || !files[i].getAbsolutePath().equals(ignore.getAbsolutePath()))
					files[i].delete();
			}
		}
		if (!root)
			return path.delete();
		return false;
	}


}
