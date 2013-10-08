package com.MeadowEast.xue;

import java.io.*;
//import java.text.DateFormat;
//import java.text.ParseException;
import java.util.*;

import android.util.Log;

abstract public class LearningProject {
	
	private String name;
	public int n, seen;
	protected List<IndexSet> indexSets;
	protected Map<Integer, Date> timestamps;
	protected Deck deck;
	protected CardStatus cardStatus = null;
	protected Card card = null;	
	final static String TAG = "CC LearningProject";
	HashMap<Card, CardStatus> cardLevel = new HashMap<Card, CardStatus>();

	public LearningProject(String name, int n) {
		this.n = n;
		this.name = name;
		this.seen = 0;
		Log.d(TAG, "Creating index sets");
		indexSets = new ArrayList<IndexSet>();
		for (int i=0; i<5; ++i){
			indexSets.add(new IndexSet());
		}
		timestamps = new HashMap<Integer, Date>();
		Log.d(TAG, "Reading status");
		readStatus();
		Log.d(TAG, "Making deck");
		deck = makeDeck(n, 700);
		Log.d(TAG, "Exiting LearningProject constructor");
	}	
	

	// n is the size of the deck
	// target is used to limit the number at Levels 1 and 2, the ones
	// mainly being learned
 	private Deck makeDeck(int n, int target){
 		Date now = new Date();
		Random r = new Random();
		Deck d = new Deck();
		float[] cutoffs = new float[4];
		// Set cutoffs so that new cards are only introduced so as to maintain a limit
		// of target at Level 1 and 2*target at (Level 1 + Level 2).  We always spend
		// 50 percent of our time on Level 1 (and 7 percent and 3 percent each on Levels
		// 3 and 4).  The remaining 40 percent gets divided between Levels 0 and 2, with
		// Level 0 time approaching zero as the in-play number gets to 2*target
		
		// Note: When Level 1 and Level 2 are both half full, this cuts the time on Level 0
		// to 12.5 percent, which is kind of low.  Easy to fix by setting target higher.
		float factor1 = Math.max(0,  1-indexSets.get(1).size()/(float) target);
		float factor1n2 = Math.max(0, 1-(indexSets.get(1).size()+indexSets.get(2).size())/((float) 2*target));
		cutoffs[0] = .40f * factor1 * factor1n2;
		if (cutoffs[0] < 0) cutoffs[0] = 0f;
		cutoffs[1] = cutoffs[0] + .5f;
		cutoffs[2] = .90f;
		cutoffs[3] = .97f;
		if (indexSets.get(0).size() < n)
			addNewItems(n);
		while (d.size()<n){
			double x = r.nextFloat();
			int level;
			if (x < cutoffs[0]){
				level = 0;
			} else if (x < cutoffs[1]) {
				level = 1;
			} else if (x < cutoffs[2]) {
				level = 2;
			} else if (x < cutoffs[3]) {
				level = 3;
			} else {
				level = 4;
			}
			IndexSet targetSet = indexSets.get(level);
			if (targetSet.size() > 0){
				int index = targetSet.pickOne();
				// add it if it hasn't recently been seen
				long hoursSinceSeen = (now.getTime() - timestamps.get(index).getTime()) / (60 * 60 * 1000);
				if (hoursSinceSeen > 24){
					timestamps.put(index, now);
					d.put(new CardStatus(index, level));
				} else { // if it's too recent, put it back
					targetSet.add(index);
				}
			}
		}
		return d;
	}
 	
 	
	public HashMap<Card, CardStatus> getCurrent(){
		HashMap<Card, CardStatus> sh = new HashMap<Card,CardStatus>();
		sh.put(card,cardStatus);
		return sh;	
	}
	
	public void setCurrent(HashMap<Card, CardStatus> sh){
		Object[] cards= sh.keySet().toArray();
		card = (Card) cards[0];
		cardStatus = sh.get(card);
	}
	
	public boolean next() {
		if (deck.isEmpty()) return false;
		cardStatus = deck.get();
		seen++;
		card = AllCards.getCard(cardStatus.getIndex());
		
		return true;
	}
	
	public int currentIndex(){
		if (cardStatus==null)
			return -1;
		return cardStatus.getIndex();
	}
	
	abstract protected String prompt();
	abstract protected String answer();
	abstract protected String other();
	abstract public void addNewItems();
	abstract public void addNewItems(int n);
	
	public void right(){
		cardStatus.right();
		Log.d(""+cardStatus.getLevel(),""+cardStatus.getIndex()); //*********** Added this line print status to LogCat
		// put it in the appropriate index set
		indexSets.get(cardStatus.getLevel()).add(cardStatus.getIndex());
	}
	
	
	public void wrong(){
		cardStatus.wrong();
		Log.d(""+cardStatus.getLevel(),""+cardStatus.getIndex()); //*********** Added this line print status to LogCat
		// return to the deck
		deck.put(cardStatus);		
	}
	
	//*******
	/***** Change added here 
	 * 2 new methods to handle Undo Right and Wrong 
	**/
	public void UndoRight(){
		cardStatus.undoRight();
		Log.d(""+cardStatus.getLevel(),""+cardStatus.getIndex()); //*********** Added this line print status to LogCat
		// put it in the appropriate index set
	
		//***** Checks if last status of card was decked means it was answered wrong and now user changed it to right
		//***** then continue to next statement and remove it from the deck and added to index of answered correctly cards
		//***** and set the decked value of the card to false as it was removed from the deck
		if(cardStatus.decked){
			deck.remove(cardStatus);
			indexSets.get(cardStatus.getLevel()).add(cardStatus.getIndex());
			cardStatus.decked=false;
		}
	}
	
	public void UndoWrong(){
		cardStatus.UndoWrong();
		Log.d(""+cardStatus.getLevel(),""+cardStatus.getIndex()); //*********** Added this line print status to LogCat
		// return to the deck
		//*** if statement to check if it was answered right before Undo and not in decked
		//*** and now changed to answer to wrong then continue the next statement
		//*** and put it in the deck and set cardStatus to deck
		if(!cardStatus.decked){
			deck.put(cardStatus);
			cardStatus.decked=true;
		}
	}
	/**
	 *  ended here*/
	
	String deckStatus(){//******* added so if answer was right move to the top card
		String left = (deck.size()+1+" left");
		return seen > n ? left : seen + " of " + n + " seen, " + left; 
	}
	
	String queueStatus(){
		int [] n = new int[5];
		for (int i=0; i<5; ++i) n[i] = indexSets.get(i).size();
//		return String.format("  %7d  %4d  %4d  %4d  %4d  %7d  %5d  ", n[0], n[1], n[2], n[3], n[4],
//				n[2]+n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
		return String.format("    %d   %d + %d = %d    %d + %d = %d    %d",
				n[0], n[1], n[2], n[1]+n[2], n[3], n[4], n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
	}
	
	public void log(String s) throws IOException {
		Log.d(TAG, "Entering log okay");
		boolean append = true;
		File logfilehandle = new File(MainActivity.filesDir, name + ".log.txt");
		Log.d(TAG, "logfilehandle is: " +logfilehandle);
		FileWriter logfile = new FileWriter(logfilehandle, append);
		PrintWriter out = new PrintWriter(logfile);
		Date now = new Date();
		out.printf("%tD %tR %s\n", now, now, s);
		logfile.close();
		Log.d(TAG, "Exiting log okay");
	}
	
	public void writeStatus() throws IOException {
		File statusobjectfile = new File(MainActivity.filesDir, name + ".status.ser");
		FileOutputStream statusobjectFOS = new FileOutputStream(statusobjectfile);
		ObjectOutputStream statusobjectOOS = new ObjectOutputStream(statusobjectFOS);
		
		Log.d(TAG, "writing objects");
		statusobjectOOS.writeObject(timestamps);
		statusobjectOOS.writeObject(indexSets);
		statusobjectFOS.close();
	}
	
	@SuppressWarnings("unchecked")
	private void readStatus() {
		FileInputStream statusobjectFIS;
		ObjectInputStream statusobjectOIS;
		try {
			File statusobjectfile = new File(MainActivity.filesDir, name + ".status.ser");
			statusobjectFIS = new FileInputStream(statusobjectfile);
			statusobjectOIS = new ObjectInputStream(statusobjectFIS);
		} catch (Exception e) {
			Log.d(TAG, "No status file, adding 50 first items from AllCards");
			addNewItems(50);
			return;
		} 
		try {
			timestamps = (Map<Integer, Date>) statusobjectOIS.readObject();
			indexSets = (List<IndexSet>) statusobjectOIS.readObject();
			statusobjectFIS.close();
			Log.d(TAG, "OBJECT status file read without problems");
		} catch (Exception e) { Log.d(TAG, "Error in readStatus"); }
	}

}
