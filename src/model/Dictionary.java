package model;
// Jacob Truman

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;

/** 
 *  Dictionary class that will handle word generation and selection
 * 
 */
public class Dictionary {
	public String selectedWord;
	public ArrayList<String> previouslySelected;
	private SecureRandom rand;

	// Constructor for class, creates the random object
	// also sets up the previously used array so repeated words
	// are unlikely to occur
	public Dictionary() throws IOException {
		this.previouslySelected = new ArrayList<>();
		this.rand = new SecureRandom();		
		
		generateWord();
	}
	

	/**
	 * Looks through the Official wordle dictionary
	 * That holds all the words that can be used for a wordle puzzle
	 * uses a random number for a selection
	 * @throws IOException
	 */
	public void generateWord() throws IOException {
		File ODict = new File("src/documents/OfficialWordleDictionary.txt");
		BufferedReader br = new BufferedReader(new FileReader(ODict));
		
		String st;
		int pos = 1;
		int store = this.rand.nextInt(2, 2317);
		while ((st = br.readLine()) != null) { // Loop through all words
			if (pos >= store && !this.previouslySelected.contains(st)) {
				// if its the word or a later word if the word chosen was chosen
				// previously, then the next available is selected as the word to find then breaks
				this.selectedWord = st;
				break;
			}
			pos+=1;
		}
		// close reader and add the word to the previously selected list so it wont be selected again
		br.close();
		previouslySelected.add(this.selectedWord);
	}
	
	/**
	 * Takes in a string from caller and will check to see if the word
	 * is valid for wordle, is in the dictionary that contains all 5 letter words
	 * @param toCheck String to check for existence
	 * @return True if the word exists in the full Wordle dirctionary, false if not
	 * @throws IOException
	 */
	public boolean checkWord(String toCheck) throws IOException {
		File FDict = new File("src/documents/FullWordleDictionary.txt");
		BufferedReader br = new BufferedReader(new FileReader(FDict));
		
		String st;
		while ((st = br.readLine()) != null) {
			// loops through all words and tries to find if the word matches
			// and not not matter the case for the words
			if (st.equalsIgnoreCase(toCheck)) {
				br.close();
				return true;
			}
		}
		
		br.close();
		return false;
	}
	
	/**
	 * clears the previously used array so that any word can be used
	 */
	public void clearHistory() {
		previouslySelected.clear();
	}
	
	/**
	 * Forces the word to find to a specific word for testing
	 * @param word 
	 */
	public void setWord(String word) {
		selectedWord = word;
	}
}
