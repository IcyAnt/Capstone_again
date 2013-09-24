package com.MeadowEast.xue;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends Activity implements OnClickListener, OnLongClickListener {
	static final String TAG = "LearnActivity";
//	static final int ECDECKSIZE = 40;
//	static final int CEDECKSIZE = 60;
	
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status;
	Button advance, okay;
	

	Chronometer timer;
	long savedTimer = 0;
			
	SharedPreferences prefs;
	
//	boolean sound_toggle;
	int ECDECKSIZE;
	int CEDECKSIZE;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        
        
        
        
        itemsShown = 0;
        prompt  = (TextView) findViewById(R.id.promptTextView);
        status  = (TextView) findViewById(R.id.statusTextView);
        other   = (TextView) findViewById(R.id.otherTextView);
        answer  = (TextView) findViewById(R.id.answerTextView);
        advance  = (Button) findViewById(R.id.advanceButton);
        okay     = (Button) findViewById(R.id.okayButton);
    	   
    	findViewById(R.id.advanceButton).setOnClickListener(this);
    	findViewById(R.id.okayButton).setOnClickListener(this);
    	
    	findViewById(R.id.promptTextView).setOnLongClickListener(this);
    	findViewById(R.id.answerTextView).setOnLongClickListener(this);
    	findViewById(R.id.otherTextView).setOnLongClickListener(this);
    	
    	timer = (Chronometer) findViewById(R.id.chronometer);
    	
    	if (MainActivity.mode.equals("ec")){
    		try {
    			ECDECKSIZE = Integer.parseInt(prefs.getString("ec_decksize", ""));
    		} catch(NumberFormatException e){
    			SharedPreferences.Editor editor = prefs.edit();
    			editor.putString("ECDECKSIZE","40");
    			editor.commit();
    			ECDECKSIZE = 40;
    		}
    		if (ECDECKSIZE <= 0){
    			SharedPreferences.Editor editor = prefs.edit();
    			editor.putString("ECDECKSIZE","40");
    			editor.commit();
    			ECDECKSIZE = 40;
    		}
    		lp = new EnglishChineseProject(ECDECKSIZE, this);	
    	}
    	else{
    		try {
    			CEDECKSIZE = Integer.parseInt(prefs.getString("ce_decksize", ""));
    		} catch(NumberFormatException e){
    			SharedPreferences.Editor editor = prefs.edit();
    			editor.putString("CEDECKSIZE","60");
    			editor.commit();
    			CEDECKSIZE = 60;
    		}
    		if (CEDECKSIZE <= 0){
    			SharedPreferences.Editor editor = prefs.edit();
    			editor.putString("CEDECKSIZE","60");
    			editor.commit();
    			CEDECKSIZE = 60;
    		}
    		lp = new ChineseEnglishProject(CEDECKSIZE, this);
    	}
    	clearContent();
    	doAdvance();
    	timer.setBase(SystemClock.elapsedRealtime());
		timer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	private void doAdvance(){
		if (itemsShown == 0){
			if (lp.next()){
				prompt.setText(lp.prompt());
				status.setText(lp.deckStatus());
				itemsShown++;
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
		} else if (itemsShown == 1){
			answer.setText(lp.answer());
			itemsShown++;
		} else if (itemsShown == 2){
			other.setText(lp.other());
			advance.setText("next");
			itemsShown++;
		} else if (itemsShown == 3){
			// Got it wrong
			advance.setText("show");
			lp.wrong();
			lp.next();
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());

		}
	}
	
	private void clearContent(){
		prompt.setText("");
		answer.setText("");
		other.setText("");
	}
	
	private void doOkay(){
		if (okay.getText().equals("done"))
			try {
				lp.log(lp.queueStatus());
				lp.writeStatus();
				finish();
				return;
				//System.exit(0);
			} catch (IOException e) {
				Log.d(TAG, "couldn't write Status");
				return;
			}
		// Do nothing unless answer has been seen
		if (itemsShown < 2) return;
		// Got it right
		lp.right();
		if (lp.next()){
			advance.setText("show");
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());

		} else {
			((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			okay.setText("done");
			clearContent();
		}
	}
    
    public void onClick(View v){
    	switch (v.getId()){
    	case R.id.advanceButton:
    		doAdvance();
			break;
    	case R.id.okayButton:
    		doOkay();
			break;
//    	case R.id.promptTextView:
//    	case R.id.answerTextView:
//    	case R.id.otherTextView:
//    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
//    		break;
    	}
    }

    public boolean onLongClick(View v){
    	switch (v.getId()){
    	case R.id.promptTextView:
    	case R.id.answerTextView:
    	case R.id.otherTextView:
    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
    		break;
    	}
    	return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "llkj");
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.reallyQuit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LearnActivity.this.finish();    
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
            return true;
        } else {
        	return super.onKeyDown(keyCode, event);
        }
    }
    
    @Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    savedTimer = timer.getBase()-SystemClock.elapsedRealtime();
	    timer.stop();
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    timer.setBase(SystemClock.elapsedRealtime()+savedTimer);
	    timer.start();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	Intent settings = new Intent(this, SettingsActivity.class);
            	startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } 
	
	private OnPreferenceChangeListener myCheckboxListener = new OnPreferenceChangeListener() {

	    public boolean onPreferenceChange(Preference preference, Object newValue) {
	        // Read new value from Object newValue here
	        return true;
	    }
	};
}
