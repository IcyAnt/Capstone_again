package com.MeadowEast.xue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button ecButton, ceButton, exitButton;
	public static File filesDir;
	public static String mode;
	static final String TAG = "XUE MainActivity";
	private SharedPreferences pref;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File sdCard = Environment.getExternalStorageDirectory();
		filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.xue/files");
		if(networkAvailability())
		{
			Timer recurringDownload = new Timer("Downloader");
			PeriodicDownload task = new PeriodicDownload();
			recurringDownload.scheduleAtFixedRate(task, 10, /*604800000*/ 60000);
			Log.d(MainActivity.TAG, "Download Task scheduled");
		}
        setContentView(R.layout.activity_main);
        ecButton   = (Button) findViewById(R.id.ecButton);
        ceButton   = (Button) findViewById(R.id.ceButton);
        exitButton = (Button) findViewById(R.id.exitButton);
    	ecButton.setOnClickListener(this);
    	ceButton.setOnClickListener(this);
    	exitButton.setOnClickListener(this);
		Log.d(TAG, "xxx filesDir="+filesDir);
    }

    public void onClick(View v){
    	Intent i;
    	switch (v.getId()){
    	case R.id.ecButton:
    		mode = "ec";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.ceButton:
    		mode = "ce";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.exitButton:
    		finish();
			break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
   
    private boolean networkAvailability() {
        ConnectivityManager CM 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = CM.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }

    private class PeriodicDownload extends TimerTask {
    	@Override
    	public void run()
    	{
    		
    		pref = getPreferences(MODE_PRIVATE);
    		String savedDateString = pref.getString("UPDATED", null);
    		Calendar d = Calendar.getInstance();
    		Date today = d.getTime();
    		Date savedDate = null;
    		if(savedDateString != null)
    		{
    			savedDate = stringToDate(savedDateString);
    			d.setTime(savedDate);
    			d.add(Calendar.DATE, 7);
    			savedDate = d.getTime();
    		}
    		if(savedDate != null && today.after(savedDate))
    		{	
	    		Log.d(MainActivity.TAG, "Begining Download Task");
	    		MainActivity.filesDir.mkdirs();
	        	HttpHead headRequest = new HttpHead("http://meadoweast.com/capstone/vocabUTF8.txt");
	        	HttpResponse head = null;
	        	File saveFile = new File(MainActivity.filesDir, "vocabUTF8.txt");
	        	File oldFile = null;
	        	String remoteDateString = null;
	        	try {
	        		HttpClient getHeader = new DefaultHttpClient();
	        		head = getHeader.execute(headRequest);
	        		Header header = head.getFirstHeader("Last-Modified");
	        		remoteDateString = header.getValue();
	        		//Log.d(MainActivity.TAG, remoteDateString);
	        	} catch (Throwable e) {
	        		Log.e(MainActivity.TAG, "Error on downloading last-modified header");
	        	}
	        	Long localDateLong = saveFile.lastModified();
	        	Date remoteDate = stringToDate(remoteDateString);
	        	Date localDate = new Date(localDateLong);
	        	Log.d(MainActivity.TAG, dateToString(localDate) + "; " + dateToString(remoteDate));
	        	//Log.i(MainActivity.TAG, dateToString(remoteDate) + "; " + dateToString(localDate));
	        	/*if(remoteDate.after(localDate)) {
	        		Log.d(MainActivity.TAG, "'TIS TRUE");
	        	} else {
	        		Log.d(MainActivity.TAG, "'TIS FALSE");
	        	}*/
	        	if(remoteDate != null && (!saveFile.exists() || remoteDate.after(localDate))) {
	        		Log.d(MainActivity.TAG, "Updating application");
	        		HttpGet get = new HttpGet("http://meadoweast.com/capstone/vocabUTF8.txt");
	        		get.setHeader("Content-Type", "text/plain; charset=utf-8");
	        		HttpResponse response = null;
	        		HttpEntity entity = null;
	        		try {
	        			HttpClient getFile = new DefaultHttpClient();
	        			response = getFile.execute(get);
	        		} catch (ClientProtocolException e) {
	        			Log.e(MainActivity.TAG, "HTTP protocol error", e);
	        		} catch (IOException e) {
	        			Log.e(MainActivity.TAG, "Communication error", e);
	        		}
	        		if (response != null) {
	        			entity = response.getEntity();
	        			if(entity != null)
	        			{
	        				try {
	        					oldFile = new File(MainActivity.filesDir, "vocabUTF8.txt");
	        					saveFile = new File(MainActivity.filesDir, "vocabUTF8(1).txt");
	        					FileOutputStream writeToFile = new FileOutputStream(saveFile);
	        					entity.writeTo(writeToFile);
	        					writeToFile.close();
	        					SharedPreferences.Editor edit = pref.edit();
	        					edit.putString("UPDATED", dateToString(today));
	        					edit.apply();
	        				} catch (IOException e1) {
	        					//e1.printStackTrace();
	        				}
	        			}
	        			if(!oldFile.exists())
	        			{
	        				saveFile.renameTo(oldFile);
	        				//return entity.getContentLength();
	        			}
	        			else if(oldFile.exists() && validateFile(saveFile, countLines(oldFile)))
	        			{
	        				oldFile.delete();
	        				saveFile.renameTo(oldFile);
	           				//return entity.getContentLength();
	        			}
	        		}
	        	}
	        } else {
	        		Log.d(MainActivity.TAG, "Application already up to date");
	    	}
    	}
    	
        private Date stringToDate(String dateString)
        {
        	Date returnDate = null;
        	try{
        		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        		returnDate = format.parse(dateString);
	    	} catch(ParseException e) {
	    		return null;
	    	}
	    	return returnDate;
	    }
	    private String dateToString(Date date)
	    {
	    	SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	    	String stringDate = format.format(date);
	    	return stringDate;
	    	
	    }
	    private int countLines(File file)
	    {
	    	try {
				BufferedReader counter = new BufferedReader(new FileReader(file));
				int lines = 0;
				try {
					while(counter.readLine() != null)
						lines++;
					counter.close();
					return lines;
				} catch (IOException e) {
					throw new FileNotFoundException();
				}
			} catch (FileNotFoundException e) {
				Log.e(MainActivity.TAG, "file counting failed");
			}
			return 0;
	    }
	    private boolean validateFile(File file, int lines)
	    {
	    	int theseLines = 0;
	    	boolean varReturn = true;
			try {
				FileReader fr = new FileReader ( file );
				BufferedReader in = new BufferedReader( fr );
				String line;
				while ((line = in.readLine(  )) != null){
					String fixedline = new String(line.getBytes(), "utf-8");
					String [] fields = fixedline.split("\\t");
					theseLines++;
					if (fields.length != 3){
						varReturn = false;
					}
				}
				in.close();
			}
			catch ( Exception e ) {
				Log.d(TAG, "Unable to get Chinese data from file" );
			}
			return (varReturn && (theseLines >= lines-1000));
	    }
    }
}
