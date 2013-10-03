package com.MeadowEast.xue;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class LearnActivity extends Activity implements OnLongClickListener {
	static final String TAG = "LearnActivity";
	static final int ECDECKSIZE = 40;
	static final int CEDECKSIZE = 60;
	
	LearningProject lp;

	TextView prompt, answer, other, status,itemsShown;
	//Button advance, okay;
	
	/** ----- begin change ----- */
	
	
	private ViewFlipper vf;//Simple ViewAnimator that will animate between two or more views that have been added to it. 
	//Only one child is shown at a time. 
	//If requested, can automatically flip between each child at a regular interval
	
	int nDone = 0;
	LinearLayout screenlayout;//this layout receives the swipe gesture
	 
	OnSwipeTouchListener mTouchListener;//swipe listener
	/** end change */
	
	
	/**
	 * Called when the activity is starting.
	 * @param savedInstanceState	If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
	 
	 */
	 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");

        prompt  = (TextView) findViewById(R.id.promptTextView);
        status  = (TextView) findViewById(R.id.statusTextView);
        other   = (TextView) findViewById(R.id.otherTextView);
        answer  = (TextView) findViewById(R.id.answerTextView);
        itemsShown  = (TextView) findViewById(R.id.itemshow);
        itemsShown.setTag(0);
        //----- begin change -----
        
        prompt.setTextSize(30);
		answer.setTextSize(30);
		other.setTextSize(30);
		status.setTextSize(20);
		
		//ViewFlipper is for animation
        vf = (ViewFlipper)this.findViewById(R.id.viewFlipper1);
        
        screenlayout = (LinearLayout)this.findViewById(R.id.screenlayout);
        
        mTouchListener = new OnSwipeTouchListener() {
    	    public void onSwipeTop() {
    	    	//show next if user is right
    	    	doOkay(1);
    	    }
    	    public void onSwipeRight() {
    	    	//set the animation 
    	    	vf.setInAnimation(AnimationUtils.loadAnimation(LearnActivity.this,
    	                 R.anim.push_right_in));
    			vf.setOutAnimation(AnimationUtils.loadAnimation(LearnActivity.this,
    	                 R.anim.push_right_out));
    			
    			
    			if (vf.getDisplayedChild() != 0)//not first view
    			{
    				vf.showPrevious();//show previous view
    				
    				LinearLayout tmplayout = (LinearLayout) vf.getCurrentView();
    				prompt  = (TextView) tmplayout.findViewById(R.id.promptTextView);
    		        status  = (TextView) tmplayout.findViewById(R.id.statusTextView);
    		        other   = (TextView) tmplayout.findViewById(R.id.otherTextView);
    		        answer  = (TextView) tmplayout.findViewById(R.id.answerTextView);
    		        itemsShown  = (TextView) tmplayout.findViewById(R.id.itemshow);
    		        
    		        status.setText(lp.deckStatus());
    			}
    			
    	    }
    	    public void onSwipeLeft() {
    	    	
    	    	//show next view if he got wrong
    	        doOkay(0);
    	    }
    	    public void onSwipeBottom() {
    	    	
    	    	//show answers
    	        doAdvance();
    	    }
    	};

        screenlayout.setOnTouchListener(this.mTouchListener);//set the touchlister to the screenlayout
        
    	//end change
        
    	findViewById(R.id.promptTextView).setOnLongClickListener(this);
    	findViewById(R.id.answerTextView).setOnLongClickListener(this);
    	findViewById(R.id.otherTextView).setOnLongClickListener(this);
    	
    	if (MainActivity.mode.equals("ec"))
    		lp = new EnglishChineseProject(ECDECKSIZE);	
    	else
    		lp = new ChineseEnglishProject(CEDECKSIZE);
    	
    	doAdvance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /**
	 * this function is called by swipedown gesture
	 * 
	 * 
	 */
    
	@SuppressLint("NewApi")
	private void doAdvance(){
		
		
		if (itemsShown.getTag().equals(0)){
			if (lp.next()){
				prompt.setText(lp.prompt());
				answer.setText(lp.answer());
				other.setText(lp.other());
				answer.setVisibility(View.INVISIBLE);
				other.setVisibility(View.INVISIBLE);
				
				status.setText(lp.deckStatus());
				itemsShown.setTag(1);
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
		} else if (itemsShown.getTag().equals(1)){
			answer.setVisibility(View.VISIBLE);
			itemsShown.setTag(2);
		} else if (itemsShown.getTag().equals( 2)){
			other.setVisibility(View.VISIBLE);
			//advance.setText("next");
			itemsShown.setTag(3);
		} 
		
	}
	
	/*	function addLayout
		
		create new layout that has 3 textviews , add it to the viewflipper.
	*/
	
	/**
	 * add Layout when swipe left 
	 * 
	 * 
	 */
	
	@SuppressLint({ "ParserError", "ParserError", "ParserError" })
	private void addLayout() {
		
		// create new layout
		
		LinearLayout datalayout1 = new LinearLayout(this);
		
		//set the layout orientation to vertical
		datalayout1.setOrientation(LinearLayout.VERTICAL);
		
		// create new question textview
		prompt = new TextView(this);
		prompt.setText(lp.prompt());
		prompt.setId(R.id.promptTextView);
		
		datalayout1.addView(prompt,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//create new answer textview
		answer = new TextView(this);
		answer.setText(lp.answer());
		answer.setVisibility(View.INVISIBLE);
		answer.setId(R.id.answerTextView);
		
		datalayout1.addView(answer,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//create new other textview
		other = new TextView(this);
		other.setText(lp.other());
		other.setVisibility(View.INVISIBLE);
		other.setId(R.id.otherTextView);
		
		
		datalayout1.addView(other,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		// create new status view
		status = new TextView(this);
		status.setText(lp.deckStatus());
		status.setId(R.id.statusTextView);
		
		datalayout1.addView(status,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//this is invisible view that include the value of swipedown guesture count.
		//
		itemsShown = new TextView(this);
		itemsShown.setTag(1);
		itemsShown.setId(R.id.itemshow);
		
		
		datalayout1.addView(itemsShown,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		//set text size and paddings
		prompt.setTextSize(30);
		answer.setTextSize(30);
		answer.setPadding(0, 20, 0, 0);
		other.setTextSize(30);
		other.setPadding(0, 20, 0, 0);
		status.setTextSize(20);
		status.setPadding(0, 20, 0, 0);
		
		prompt.setGravity(Gravity.CENTER);
		answer.setGravity(Gravity.CENTER);
		other.setGravity(Gravity.CENTER);
		status.setGravity(Gravity.CENTER);
		
		
		
		
		// add layout to the viewflipper.
		// set the left animation  to flipper 
		// call show next , so that new layout is diplayed
		vf.addView(datalayout1);
		
		vf.setInAnimation(AnimationUtils.loadAnimation(this,
                 R.anim.push_left_in));
		vf.setOutAnimation(AnimationUtils.loadAnimation(this,
                 R.anim.push_left_out));
		vf.showNext();
	}

	
	/**
	 * this function is called when swipe left and right
	 * @param w_r  flag of wrong or right
	 * 
	 */
    
	@SuppressLint("NewApi")
	private void doOkay(int w_r){
		
		if (vf.getDisplayedChild() < vf.getChildCount()-1)//viewflipper has nextview?
		{
			// set the left animation and show next view 
			vf.setInAnimation(AnimationUtils.loadAnimation(this,
	                 R.anim.push_left_in));
			vf.setOutAnimation(AnimationUtils.loadAnimation(this,
	                 R.anim.push_left_out));
			vf.showNext();
			// get the question & answer & other & itemsshown text view by id
			LinearLayout tmplayout = (LinearLayout) vf.getCurrentView();
			prompt  = (TextView) tmplayout.findViewById(R.id.promptTextView);
	        status  = (TextView) tmplayout.findViewById(R.id.statusTextView);
	        other   = (TextView) tmplayout.findViewById(R.id.otherTextView);
	        answer  = (TextView) tmplayout.findViewById(R.id.answerTextView);
	        itemsShown  = (TextView) tmplayout.findViewById(R.id.itemshow);
	        
	        
	        
	        if (w_r == 1 )
			{
	        	//if user swipe up, remove current view in the deck
	        	
	        	lp.right();
	        	lp.next();
	        	lp.seen--;
				vf.removeViewAt(vf.getDisplayedChild()-1);
				
			}
	        status.setText(lp.deckStatus());
			return;
		}
		if (nDone == 1)//all question displayed?
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
		if (itemsShown.getTag().equals(1) || itemsShown.getTag().equals(0))
		{
			Toast.makeText(LearnActivity.this, "Do nothing unless answer has been seen", Toast.LENGTH_SHORT).show();
			return;
		}
		// Got it right
		//----- add change -----
		
		//----- end change -----
		
		if (w_r == 1)//swipe up?
			lp.right();
		else//swipe left?
			lp.wrong();
		
		if (lp.next()){//have next question?
			
			addLayout();
			nDone = 0;
			if (w_r == 1 )//swipe up?
			{
				// remove current card in the deck
				vf.removeViewAt(vf.getDisplayedChild()-1);
				
			}
			status.setText(lp.deckStatus());
			
		} else {
			//((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			nDone = 1;
			//okay.setText("done");
			
		}
	}
    
	/**
	 * Called when a view has been clicked and held.
	 * @param v	The view that was clicked and held.
	 * @return true if the callback consumed the long click, false otherwise.
	 */


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
    
    /**
	 * Called when a view has been clicked and held.
	 * @param 	keyCode	The code for the physical key that was pressed
	 * @param	event	The KeyEvent object containing full information about the event.
	 * @return True if the listener has consumed the event, false otherwise.
	 */
    
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

