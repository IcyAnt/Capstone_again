package com.MeadowEast.xue;

import java.util.LinkedList;

public class Deck {
	private LinkedList<CardStatus> cardStatusQueue = new LinkedList<CardStatus>();
	// Get a random deck of the specified size
	public Deck() {}
	public CardStatus get(){
		return cardStatusQueue.poll();
	}
	public void put(CardStatus cs){
		cardStatusQueue.add(cs);
	}
	
	//***** added this method to allow me to remove cards from deck in undo when i answer it correctly in Undo
	//***** return true if cardStatus available in deck and was removed otherwise false
	public boolean remove(CardStatus cs){
		return cardStatusQueue.remove(cs);
	}	
	
	
	public boolean isEmpty(){
		return cardStatusQueue.isEmpty();
	}
	public int size(){
		return cardStatusQueue.size();
	}
}
