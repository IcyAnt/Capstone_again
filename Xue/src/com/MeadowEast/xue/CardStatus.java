package com.MeadowEast.xue;

public class CardStatus {
	private int index;
	private int level;
	
	/***
	 * 2 new variables
	 */
	//******* Correct :added boolean to control whether it was answered from first time right or wrong
	boolean correct; 
	//******* indexed : if it was true it means the card was answered correct and indexed in indexSet, otherwise
	//******* means it was decked back in the deck 
	boolean decked; 
	/*** ended here **/
	public CardStatus(int index, int level){
		this.level = level;
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void wrong(){
		if (level > 0){
			level -= 1;
		}
		correct=false; //****** added boolean to control whether it was answered from first time correctly or not(First time wrong)
		
		//****** decked true means that it was answered wrong therefore it was 
		//****** added to the deck by the call deck.put(cardStatus) in method wrong() in LearningProject
		decked = true; 
	}
	
	public void right(){
		if (level < 4){
			level += 1;
		}
		correct=true;  //****** added boolean to control whether it was answered from first time correctly or not(First Time right)
		
		//****** decked false means that it was answered right therefore it was 
	    //****** removed from the deck by the call in Deck class get() which calls poll() 
		//****** which by its role removes the card from the deck
		decked = false;
	}
	
	/**
	 *  2 new methods added 
	 */
	//***** new method for Undo a card that was answered wrong to right. it will add 2 rather than 1 if it was only answered wrong
	public void undoRight(){
		
		//*** if statement to check if it was answered wrong before Undo then continue the next statement otherwise do nothing
		if(!correct){ 
			if(level<3){ //***** if level <3 because the maximum number of level we can 2 on is 2 as we only have 4 levels
				level +=2;
			}else if (level==3){ //**** if level equals to 3 then only add on so it becomes 4
				level+=1;
			}
			correct=true; //**** answer is right now then it is correct so set it to true
		}
	}
	
	//***** new method for Undo a card that was answered right to wrong. it will remove one if it was only answered correct at firstTime
	public void UndoWrong(){
		//*** if statement to check if it was answered right before Undo then continue the next statement otherwise do nothing
		if(correct){
			if (level > 0){ //*** if was answered first time right and now its wrong then remove 1 other wise do nothing
				level -= 1;
			}
			correct=false; //**** answer is wrong the it isn't correct anymore so set it to false
		}
	}
	/**
	 * ended here
	 */
	
	public String toString(){
		return "CardStatus: index="+index+" level="+level;
	}
}
