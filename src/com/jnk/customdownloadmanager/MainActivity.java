package com.jnk.customdownloadmanager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{

	Button button1File, buttonMultipleFiles, buttonMultipleFilesMultibleThreads, buttonUncomressedFiles, buttonDownloadOneUnpackOne;
	final String SERVERADRESS = "http://10.36.98.82/";
	final int THREADSTOUSE = 4;
	final int TIMESTORUN = 1;
	Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		mHandler = new Handler();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button1File = (Button) findViewById(R.id.buttondownloadone);
		buttonMultipleFiles = (Button) findViewById(R.id.buttondownloadmultible);
		buttonUncomressedFiles = (Button) findViewById(R.id.buttondownloaduncompressed);
		buttonMultipleFilesMultibleThreads = (Button) findViewById(R.id.buttondownloadmiltiblefilesmultiblethreads);
		buttonDownloadOneUnpackOne = (Button) findViewById(R.id.buttondownloadoneunpackone);
		button1File.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						Log.i("CustomDownLoadManager", "downloading and unpacking a single file");
						ArrayList<Long> result = download1FileMultibleTimesAndUnpackIt(TIMESTORUN);
						String fin = "";
						int total = 0;
						for (Long row : result)
						{
							total += row;
							fin += " " + row;
						}
						Log.i("CustomDownLoadManager", "Single file download took: " + fin);
						Log.i("CustomDownLoadManager", "total time:" + total + " avg time: " + (total / result.size()));
						makeAAlert("Single file download took: " + fin);
					}

				});

			}
		});
		buttonDownloadOneUnpackOne.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						Log.i("CustomDownLoadManager", "downloading one, unpacking one is starting");
						downloadOneWhileUnpackingAnother();
//				//		ArrayList<Long> result = download1FileMultibleTimesAndUnpackIt(TIMESTORUN);
//						String fin = "";
//						int total = 0;
//						for (Long row : result)
//						{
//							total += row;
//							fin += " " + row;
//						}
//						Log.i("CustomDownLoadManager", "downloading one, unpacking one is starting took: " + fin);
//						Log.i("CustomDownLoadManager", "total time:" + total + " avg time: " + (total / result.size()));
//						makeAAlert("Single file download took: " + fin);
					}

				});

			}
		});
		buttonMultipleFiles.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						Log.i("CustomDownLoadManager", "multible download starting");
						ArrayList<Long> result = downloadMultibleFilesAndExtractInOneThread(TIMESTORUN);
						String fin = "";
						int total = 0;
						for (Long row : result)
						{
							total += row;
							fin += " " + row;
						}
						Log.i("CustomDownLoadManager", "multible download took: " + fin);
						Log.i("CustomDownLoadManager", "total time:" + total + " avg time: " + (total / result.size()));
						makeAAlert("multible download took: " + fin);
					}

				});

			}
		});
		buttonMultipleFilesMultibleThreads.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						Log.i("CustomDownLoadManager", "multible files, multible threads starting");
						ArrayList<Long> result = downloadMultibleFilesAndExtractInMultibleThreads(TIMESTORUN);
						String fin = "";
						int total = 0;
						for (Long row : result)
						{
							total += row;
							fin += " " + row;
						}
						Log.i("CustomDownLoadManager", "multible files, multible threads took: " + fin);
						Log.i("CustomDownLoadManager", "total time:" + total + " avg time: " + (total / result.size()));
						makeAAlert("multible files, multible threads took: " + fin);
					}

				});
			}
		});
		buttonUncomressedFiles.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Log.i("CustomDownLoadManager", "Uncompressed download startingXXXXX");

				
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						Log.i("CustomDownLoadManager", "Uncompressed download starting");
						ArrayList<Long> result = downloadFilesuncompressed(TIMESTORUN);
						String fin = "";
						int total = 0;
						for (Long row : result)
						{
							total += row;
							fin += " " + row;
						}
						Log.i("CustomDownLoadManager", "multible download took: " + fin);
						Log.i("CustomDownLoadManager", "total time:" + total + " avg time: " + (total / result.size()));
						makeAAlert("multible download took: " + fin);
					}

				});
			}
		});
	}

	private void makeAAlert(String message)
	{
		new AlertDialog.Builder(MainActivity.this).setTitle("Test Done").setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		})

		.show();
	}

	public ArrayList<Long> download1FileMultibleTimesAndUnpackIt(int timesToDownLoad)
	{
		ArrayList<Long> downloadDurations = new ArrayList<Long>();
		for (int i = 0; i < timesToDownLoad; i++)
		{
			// Setup
			DownloadManager downloadManager = new DownloadManager(THREADSTOUSE);
			CurrentZip ourUnpacker;

			File downloadFolder = new File(getExternalFilesDir(null) + "/singleZipTestArea");
			if (downloadFolder.exists())
			{
				ourUnpacker = new CurrentZip();
				ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
			}

			// Execution
			long lStartTime = new Date().getTime();
			downloadManager.addFileToQue(SERVERADRESS + "app_Original.zip", getExternalFilesDir(null) + "/app_Original.zip");

			while (downloadManager.getCompletedDownloads().size() < 1)
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			ZipFile zip;
			try
			{
				zip = new ZipFile(new File(getExternalFilesDir(null) + "/app_Original.zip"));
				ourUnpacker = new CurrentZip(zip, getExternalFilesDir(null) + "/singleZipTestArea");
			} catch (ZipException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			long lEndTime = new Date().getTime(); // end time

			long difference = lEndTime - lStartTime;
			Log.i("CustomDownLoadManager", "download attept status: " + downloadManager.getCompletedDownloads().get(0).getDownloadStatus());
			downloadDurations.add(difference);
		}
		return downloadDurations;
	}

	public ArrayList<Long> downloadMultibleFilesAndExtractInMultibleThreads(int timesToDownLoad)
	{
		ExecutorService threadPool = Executors.newFixedThreadPool(THREADSTOUSE);
		ArrayList<Long> downloadDurations = new ArrayList<Long>();
		for (int i = 0; i < timesToDownLoad; i++)
		{
			File downloadFolder = new File(getExternalFilesDir(null) + "/multipleZipMultipleThreadsArea");
			if (downloadFolder.exists())
			{
				CurrentZip ourUnpacker;

				ourUnpacker = new CurrentZip();
				ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
			}
			long lStartTime = new Date().getTime();

			// threadPool.execute(new DownloadAndUnzipTask("app1.zip"));
			// threadPool.execute(new DownloadAndUnzipTask("app2.zip"));
			// threadPool.execute(new DownloadAndUnzipTask("app3.zip"));
			// threadPool.execute(new DownloadAndUnzipTask("app4.zip"));
			threadPool.execute(new DownloadAndUnzipTask("test1.zip"));
			threadPool.execute(new DownloadAndUnzipTask("test2.zip"));
			threadPool.execute(new DownloadAndUnzipTask("test3.zip"));
			threadPool.execute(new DownloadAndUnzipTask("test4.zip"));
		
			while (!threadPool.isTerminated())
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				threadPool.shutdown();
			}
			long lEndTime = new Date().getTime(); // end time

			long difference = lEndTime - lStartTime;
			downloadDurations.add(difference);
		}
		return downloadDurations;
	}

	public void downloadOneWhileUnpackingAnother()
	{
		//delete
		CurrentZip ourUnpacker;

		File downloadFolder = new File(getExternalFilesDir(null) + "/downloadOneWhileUnpackingAnother");
		if (downloadFolder.exists())
		{
			ourUnpacker = new CurrentZip();
			ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
		}
		
		//downloads
		final DownloadManager mymanager = new DownloadManager(1);
		mymanager.addFileToQue(SERVERADRESS + "app1.zip", getExternalFilesDir(null) + "/app1.zip");
		mymanager.addFileToQue(SERVERADRESS + "app2.zip", getExternalFilesDir(null) + "/app2.zip");
		mymanager.addFileToQue(SERVERADRESS + "app3.zip", getExternalFilesDir(null) + "/app3.zip");
		mymanager.addFileToQue(SERVERADRESS + "app4.zip", getExternalFilesDir(null) + "/app4.zip");


		// unpackking thread.
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				long startTime, endTime;
				
				startTime = new Date().getTime();
				for (int i = 1; i < 5; i++)
				{
					while (mymanager.getCompletedDownloads().size() < i)
					{
							try
							{
								Thread.sleep(1000);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					//unpack
					

					ZipFile zip;
					try
					{
						Log.i("CustomDownLoadManager", "starting to unpack: ");
						zip = new ZipFile(new File(mymanager.getCompletedDownloads().get(0).getDestiantion()));
						new CurrentZip(zip, getExternalFilesDir(null) + "/downloadOneWhileUnpackingAnother");
						Log.i("CustomDownLoadManager", "one file unpacked: ");
					} catch (ZipException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				endTime= new Date().getTime();
				long difference = endTime - startTime;
				Log.i("CustomDownLoadManager", "all downloads downloaded in: "+difference);
				//here we should be done
			}
		}).start();

	}

	class DownloadAndUnzipTask implements Runnable
	{
		String fileToUse;

		DownloadAndUnzipTask(String fileToUse)
		{
			this.fileToUse = fileToUse;
		}

		@Override
		public void run()
		{
			DownloadManager downloadManager = new DownloadManager(THREADSTOUSE);

			// Execution
			downloadManager.addFileToQue(SERVERADRESS + fileToUse, getExternalFilesDir(null) + "/" + fileToUse);
			while (downloadManager.getCompletedDownloads().size() < 1)
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			ZipFile zip;
			try
			{
				zip = new ZipFile(new File(downloadManager.getCompletedDownloads().get(0).getDestiantion()));
				new CurrentZip(zip, getExternalFilesDir(null) + "/multipleZipMultipleThreadsArea");
			} catch (ZipException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}

	public ArrayList<Long> downloadMultibleFilesAndExtractInOneThread(int timesToDownLoad)
	{
		ArrayList<Long> downloadDurations = new ArrayList<Long>();
		for (int i = 0; i < timesToDownLoad; i++)
		{
			// setup
			DownloadManager downloadManager = new DownloadManager(THREADSTOUSE);
			CurrentZip ourUnpacker;

			File downloadFolder = new File(getExternalFilesDir(null) + "/multipleZipTestArea");
			if (downloadFolder.exists())
			{
				ourUnpacker = new CurrentZip();
				ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
			}

			// Execution
			long lStartTime = new Date().getTime();
			downloadManager.addFileToQue(SERVERADRESS + "app1.zip", getExternalFilesDir(null) + "/app1.zip");
			downloadManager.addFileToQue(SERVERADRESS + "app2.zip", getExternalFilesDir(null) + "/app2.zip");
			downloadManager.addFileToQue(SERVERADRESS + "app3.zip", getExternalFilesDir(null) + "/app3.zip");
			downloadManager.addFileToQue(SERVERADRESS + "app4.zip", getExternalFilesDir(null) + "/app4.zip");
			for (int j = 0; j < 4; j++)
			{
				while (downloadManager.getCompletedDownloads().size() < j + 1)
				{
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				ZipFile zip;
				try
				{
					zip = new ZipFile(new File(downloadManager.getCompletedDownloads().get(j).getDestiantion()));
					new CurrentZip(zip, getExternalFilesDir(null) + "/multipleZipTestArea");
				} catch (ZipException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			long lEndTime = new Date().getTime(); // end time

			long difference = lEndTime - lStartTime;
			downloadDurations.add(difference);
		}
		return downloadDurations;
	}

	private ArrayList<Long> downloadFilesuncompressed(int timesToDownLoad)
	{

		ArrayList<Long> downloadDurations = new ArrayList<Long>();
		for (int k = 0; k < timesToDownLoad; k++)
		{
			File downloadFolder = new File(getExternalFilesDir(null) + "/uncompressedArea");
			if (downloadFolder.exists())
			{
				CurrentZip ourUnpacker = new CurrentZip();
				ourUnpacker.deleteDirectoryRecursively(downloadFolder, null, false);
			}
			Log.i("CustomDownLoadManager", "All old files deleted, ready for main task");
			try
			{
				AssetManager assetManager = getAssets();

				InputStream fstream = assetManager.open("listoffiles.rtf");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				DownloadManager downloadManager = new DownloadManager(THREADSTOUSE);

				// Read File Line By Line
				int i = 0;
				long lStartTime = new Date().getTime();
				while ((strLine = br.readLine()) != null)
				{
					createDir("/uncompressedArea" + strLine);
					downloadManager.addFileToQue(SERVERADRESS + strLine, getExternalFilesDir(null) + "/uncompressedArea" + strLine);
					i++;
				}
				Log.i("CustomDownLoadManager", "ALL FILES NOW ADDED TO QUEUE!");
				// Close the input stream
				in.close();
				while (downloadManager.getCompletedDownloads().size() < i)
				{
					Log.i("CustomDownLoadManager", i + " " + downloadManager.getCompletedDownloads().size());
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				long lEndTime = new Date().getTime(); // end time

				long difference = lEndTime - lStartTime;
				downloadDurations.add(difference);

				LinkedList<DownloadItem> incompleted = downloadManager.getIncompleteDownloads();

				Log.i("CustomDownLoadManager", "Error downloading: " + incompleted.size() + " files");

				while (!incompleted.isEmpty())
				{
					DownloadItem currentDownloadItem = incompleted.poll();
					Log.i("CustomDownLoadManager", "Error downloading: " + currentDownloadItem.getDownloadStatus());
				}

			} catch (Exception e)
			{// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
		return downloadDurations;
	}

	private void createDir(String dirToCreate) throws NameNotFoundException
	{

		File directory = new File(getExternalFilesDir(null) + dirToCreate.substring(0, dirToCreate.lastIndexOf("/")));
		// Log.i("CustomDownLoadManager", "I will create dir: " +
		// directory.toString());
		directory.mkdirs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
