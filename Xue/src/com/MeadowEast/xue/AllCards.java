package com.MeadowEast.xue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

public class AllCards {
	static final String TAG = "CC AllCards";
	private static AllCards db = new AllCards();
	public static Card getCard(int i){
		return db.cardArray[i];
	}
	public static int length(){
		return db.cardArray.length;
	}
	private Card [] cardArray;
	private AllCards() {		
		InputStream is = null;
		try {
			is = MainActivity.ctx.getAssets().open("vocabUTF8.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//File file =  new File(MainActivity.filesDir, "vocabUTF8.txt");
		ArrayList<Card> allCards = new ArrayList<Card>();
		//Log.d(TAG, "File is "+ file);
		//Log.d(TAG, "Path is "+ file.getAbsolutePath());

		try {
			InputStreamReader fr = new InputStreamReader ( is );
			Log.d(TAG, "FileReader okay");
			BufferedReader in = new BufferedReader( fr );
			Log.d(TAG, "BufferedReader okay");
			
			String line;
			while ((line = in.readLine(  )) != null){
				String fixedline = new String(line.getBytes(), "utf-8");
				String [] fields = fixedline.split("\\t");
				if (fields.length == 3){
					Card c = new Card(fields[0], fields[1], fields[2]);
					allCards.add(c);
				} else {
					Log.d(TAG, "Bad line: "+fields.length+" elements");
					Log.d(TAG, fixedline);
				}
			}
			in.close();
		}
		catch ( Exception e ) {
			Log.d(TAG, "Unable to get Chinese data from file" );
		}
		cardArray = allCards.toArray(new Card[0]);
	}
}