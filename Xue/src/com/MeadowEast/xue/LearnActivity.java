package com.MeadowEast.xue;

import java.io.IOException;
import java.util.HashMap;

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
        itemsShown.setText("0");   //****** setTag Changed to setText only way you can save values of every question
        
        
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
    	    @SuppressWarnings("unchecked")
			public void onSwipeRight() {
    	    	//set the animation 
    	    	vf.setInAnimation(AnimationUtils.loadAnimation(LearnActivity.this,
    	                 R.anim.push_right_in));
    			vf.setOutAnimation(AnimationUtils.loadAnimation(LearnActivity.this,
    	                 R.anim.push_right_out));
    			
    			
    			if (vf.getDisplayedChild() != 0)//not first view
    			{

    				vf.showPrevious();//show previous view
    				
    				//************ since we went back to a previous card,
    				//************ we then get the card and card status values of question we currently viewing now in the Viewflipper
    				//************ which was set as tag to the viewflipper child
    				//************ and set it as the current card being viewed in the deck
    				// For example : we were on card 4, we then undo to go to question 2. first card 4 status will be set as tag to child 4 
    				// in viewflipper and when we go to child 2 where card 2 is. we get getTag() that was saved before when forst answered
    				// card 2. and set it as the current card and cardstatus bein delt with in learningProject.
    				lp.setCurrent((HashMap<Card, CardStatus>) vf.getChildAt(vf.getDisplayedChild()).getTag());
    				
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
		
		
		if (itemsShown.getText().toString().equals("0")){
			if (lp.next()){
				prompt.setText(lp.prompt());
				answer.setText(lp.answer());
				other.setText(lp.other());
				answer.setVisibility(View.INVISIBLE);
				other.setVisibility(View.INVISIBLE);
				
				status.setText(lp.deckStatus());
				itemsShown.setText("1");  //****** setTag Changed to setText only way you can save values of every question
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
		} else if (itemsShown.getText().toString().equals("1")){ //****** getText to get saved values inside the textView and as it returns charSequence we had to use toString() to compare it to another string
			answer.setVisibility(View.VISIBLE);
			itemsShown.setText("2"); //****** setTag Changed to setText only way you can save values of every question
		} else if (itemsShown.getText().toString().equals("2")){ //****** getText to get saved values inside the textView and as it returns charSequence we had to use toString() to compare it to another string
			other.setVisibility(View.VISIBLE);
			//advance.setText("next");
			itemsShown.setText("3"); //****** setTag Changed to setText only way you can save values of every question
		} 
		
	}
	
	/**	function addLayout
	 *	
	 * create new layout that has 3 textviews , add it to the viewflipper.
	 * add Layout when swipe left 
	 * 
	 *
	 */
	@SuppressLint({ "ParserError", "ParserError", "ParserError" })
	
	//********* Added ans as parameter so we can pass the value of swipe up (right) or left(wrong) to this method. to control how the
	//********* Exiting view will animate Upwards animation or left Animation
	private void addLayout(int ans) {
		
		// create new layout
		LinearLayout datalayout1 = new LinearLayout(this);
		
		//set the layout orientation to vertical
		datalayout1.setOrientation(LinearLayout.VERTICAL);
		
		// create new question textview
		prompt = new TextView(this);
		prompt.setText(lp.prompt());
		prompt.setId(R.id.promptTextView);
		
		//********** Changed from VIEWGROUP TO LINEARLAYOUT. its best to use the parent type when you add a new view
		//Like here datalayout1 is LinearLayout so when u add a view to it, Use layoutParams of LinearLayout
		//also added the 0.3 weight.
		datalayout1.addView(prompt,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.3f));
		
		//create new answer textview
		answer = new TextView(this);
		answer.setText(lp.answer());
		answer.setVisibility(View.INVISIBLE);
		answer.setId(R.id.answerTextView);
		
		//********** Changed from VIEWGROUP TO LINEARLAYOUT. its best to use the parent type when you add a new view
		//Like here datalayout1 is LinearLayout so when u add a view to it, Use layoutParams of LinearLayout
		//also added the 0.3 weight.
		datalayout1.addView(answer,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.3f));
		
		//create new other textview
		other = new TextView(this);
		other.setText(lp.other());
		other.setVisibility(View.INVISIBLE);
		other.setId(R.id.otherTextView);
		
		//********** Changed from VIEWGROUP TO LINEARLAYOUT. its best to use the parent type when you add a new view
		//Like here datalayout1 is LinearLayout so when u add a view to it, Use layoutParams of LinearLayout
		//also added the 0.3 weight.
		datalayout1.addView(other,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.3f));
		
		// create new status view
		status = new TextView(this);
		status.setText(lp.deckStatus());
		status.setId(R.id.statusTextView);
		
		//********** Changed from VIEWGROUP TO LINEARLAYOUT. its best to use the parent type when you add a new view
		//Like here datalayout1 is LinearLayout so when u add a view to it, Use layoutParams of LinearLayout
		//also added the 0.3 weight.
		datalayout1.addView(status,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.3f));
		
		//********** The View was supposed to be invisible but the code for invisible was not set
		//this is invisible view that include the value of swipedown guesture count.
		itemsShown = new TextView(this);
		itemsShown.setVisibility(View.GONE);//added
		itemsShown.setText("1");
		itemsShown.setId(R.id.itemshow);
		
		//********** Changed from VIEWGROUP TO LINEARLAYOUT. its best to use the parent type when you add a new view
		//Like here datalayout1 is LinearLayout so when u add a view to it, Use layoutParams of LinearLayout
		//also added the 0.3 weight.
		datalayout1.addView(itemsShown,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.3f));
		
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
		//********** Changed from VIEWGROUP TO VIEWFLIPPER. its best to use the parent type when you add a new view
		//Like here "vf" is ViewFlipper so when u add a view to it, Use layoutParams of ViewFlipper.***********
		vf.addView(datalayout1,new ViewFlipper.LayoutParams(
				ViewFlipper.LayoutParams.MATCH_PARENT, ViewFlipper.LayoutParams.MATCH_PARENT));
		
		
		vf.setInAnimation(AnimationUtils.loadAnimation(this,
                 R.anim.push_left_in));
		
		
		if (ans == 1 )//swipe up?
		{
			//************* if right, set Animation to move up(Our new Created Animation)
			vf.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_up_out));
		}else{
			//************* else, set normal action Animation to move left since answer was not right
			vf.setOutAnimation(AnimationUtils.loadAnimation(this,
		            R.anim.push_left_out));
		}
		vf.showNext();
		
		//********* we get current child number and set its tag to current card and cardstatus so we can do an undo at future and will be always able to retrive values by calling getTag() onChild
		vf.getChildAt(vf.getDisplayedChild()).setTag(lp.getCurrent()); 

	}

	
	/**
	 * this function is called when swipe left and right
	 * @param ans  flag of wrong or right
	 * 
	 */
	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	private void doOkay(boolean ans){
		
		if (vf.getDisplayedChild() < vf.getChildCount()-1)//viewflipper has nextview?
		{
			// set the left animation and show next view 
			vf.setInAnimation(AnimationUtils.loadAnimation(this,
	                 R.anim.push_left_in));
			
			
			/****
			 * Change stated here
			 * 
			 */
			if (ans == 1 )//swipe up?
			{
				//*********  if right, set Animation to move up( Our new Created Animation : res>anim>push_up_out.xml )
				vf.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_up_out));

			}else{
				
				//******* if answer was wrong move to next card, set normal action Animation to move left since answer was wrong
				vf.setOutAnimation(AnimationUtils.loadAnimation(this,
			            R.anim.push_left_out));
				
			}
			//******* added so if answer was right move to the top card
			vf.setDisplayedChild(vf.getChildCount()-1);

			/*
			 * ended here 
			 */
			
			// get the question & answer & other & itemsshown text view by id
			LinearLayout tmplayout = (LinearLayout) vf.getCurrentView();
			prompt  = (TextView) tmplayout.findViewById(R.id.promptTextView);
	        status  = (TextView) tmplayout.findViewById(R.id.statusTextView);
	        other   = (TextView) tmplayout.findViewById(R.id.otherTextView);
	        answer  = (TextView) tmplayout.findViewById(R.id.answerTextView);
	        itemsShown  = (TextView) tmplayout.findViewById(R.id.itemshow);
	        	        	        
	        if (ans == 1 )
			{
	        	//******* new methods that adds 2 to level to fix the wrong answer since we are in undo
	        	// only if the first answer was wrong (Method can be found in LearningProject)
	        	lp.UndoRight(); 
	        	
	        	//***** removed it as in this part because we are in UNDO so we don't need to check if there is next question
	        	//lp.next();
	        	
	        	//***** removed it as it will add another question although there was not answered yet
	        	//lp.seen--;
	        	
	        	//******* remove this code to allow undo for right answers otherwise it deletes it
				//vf.removeViewAt(dis);	
			}else//swipe left?
			{	
				lp.UndoWrong();
			}
	        

	        status.setText(lp.deckStatus());
	        
			//************ since we went back to a previous card,
			//************ we then get the card and card status values of question we currently viewing now in the Viewflipper
			//************ which was set as tag to the viewflipper child
			//************ and set it as the current card being viewed in the deck
			// For example : we were on card 4, we then undo to go to question 2. first card 4 status will be set as tag to child 4 
			// in viewflipper and when we go to child 2 where card 2 is. we get getTag() that was saved before when forst answered
			// card 2. and set it as the current card and cardstatus bein delt with in learningProject.
			lp.setCurrent((HashMap<Card, CardStatus>) vf.getChildAt(vf.getDisplayedChild()).getTag());
	
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
		if (itemsShown.getText().toString().equals("1") || itemsShown.getText().toString().equals("0"))
		{
			//******* Changed Toast_SHORT to TOAT_LONG so it can stay for a longer time on screen before it dissappers
			Toast.makeText(LearnActivity.this, "Please swipe down first to view answer", Toast.LENGTH_LONG).show();
			return;
		}
		
		// Got it right
		//----- add change -----		
		if (ans == 1)//swipe up?
		{	lp.right();
		}
		else//swipe left?
		{	lp.wrong();}

		//********* we get current child number and set its tag to current card and cardstatus so we can do an undo at future 
		vf.getChildAt(vf.getDisplayedChild()).setTag(lp.getCurrent()); 
		
		
		if (lp.next()){//have next question?
			
			addLayout(ans);// added ans to pass value of answer to 
			               // addLayout method to set the animation path upwards if 1 else if otherwise.
			nDone = 0;
			//******* remove this code to allow undo for both wrong and right answers 
			/******** as this code remove the view that is answered right
			 *
			if (ans == 1 )//swipe up?
			{
				// remove current card in the deck
				vf.removeViewAt(vf.getDisplayedChild()-1); 
				
			}
			*/
			status.setText(lp.deckStatus());
			
		} else {
			//((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			nDone = 1;
			
			//****** added this toast to show message telling user to swipe upwards to end
			Toast.makeText(LearnActivity.this, "Please swipe upwards to end the round", Toast.LENGTH_LONG).show();
			
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
