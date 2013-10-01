package com.MeadowEast.xue;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends Activity implements OnClickListener, OnLongClickListener {
	static final String TAG = "LearnActivity";
	static final int ECDECKSIZE = 40;
	static final int CEDECKSIZE = 60;
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status;
	Button advance, okay;
	public static MediaPlayer mp;
	public static MediaPlayer mpwrong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mpwrong.create(this, R.raw.buzzer);
    	mp.create(this, R.raw.magic);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");
        

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
    	
    	if (MainActivity.mode.equals("ec"))
    		lp = new EnglishChineseProject(ECDECKSIZE);	
    	else
    		lp = new ChineseEnglishProject(CEDECKSIZE);
    	clearContent();
    	doAdvance();
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
    		Log.d("Xue ISSELECTED", "About to check");
    		int start = ((TextView) v).getSelectionStart();
    		int end = ((TextView) v).getSelectionEnd();
    		if(start < end)
    		{
    			Log.d("Xue ISSELECTED", "yes");
    			CharSequence selectedSequence = ((TextView) v).getText();
    			selectedSequence = selectedSequence.subSequence(start, end);
    			String selected = selectedSequence.toString();
    			//selected = selected.substring(start, end);
    			Log.d("Xue ISSELECTED", selected);
    			Uri dictionary = Uri.parse("http://www.mdbg.net/chindict/chindict.php?page=worddict&wdrst=0&wdqb=" + selected);
    			Intent webview = new Intent(Intent.ACTION_VIEW, dictionary);
    			startActivity(webview);
    		}
    		else {
    			Log.d("Xue ISSELECTED", "no");
	    		Intent email = new Intent(Intent.ACTION_SENDTO);
	    		Uri uri = Uri.parse("mailto:kgall@hunter.cuny.edu");
	    		email.setData(uri);
	    		//email.putExtra(Intent.EXTRA_EMAIL, new String[]{"kgall@hunter.cuny.edu"});
	    		if(MainActivity.mode == "ec")
	    		{
	    			email.putExtra(Intent.EXTRA_SUBJECT, "Xue English to Chinese index " + lp.currentIndex());
	    		}
	    		else {
	    			email.putExtra(Intent.EXTRA_SUBJECT, "Xue Chinese to English index " + lp.currentIndex());
	    		}
	    		email.putExtra(Intent.EXTRA_TEXT, "English: " + lp.getEnglish() +"\nPinyin: "+ lp.getPinyin() + "\nHanzi: " + lp.getHanzi() + "\nComments (评论): \n");
	    		startActivity(Intent.createChooser(email, "Send Comments"));
	    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
    		}
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
}
