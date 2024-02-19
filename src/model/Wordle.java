package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Wordle class to implement and manage the Wordle game in console and GUI.
 *
 */
public class Wordle {  // Assigned dictionary and pastWords collections to not reset when start a new game.
	private Dictionary dictionary;
	private String wordToGuess;
	private ArrayList<String> pastGuesses;
	private int[][] pastClues;
	private boolean running;
	private boolean difficulty; // false is normal, true is hard
	
	public Wordle() {  // Constructor for user guess and target word.
		try {
			dictionary = new Dictionary();
			dictionary.generateWord();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pastGuesses = new ArrayList<>();
		pastClues = new int[6][5];
		wordToGuess = dictionary.selectedWord;
		running = true;
		difficulty = false; // set to normal functionality by default
	}
	
	/**
	 * To bring the user guess, it checks the user guess is acceptable.
	 * @param guess The user guess to check it is available.
	 * @return true if the guess is available word.
	 * @throws IOException
	 */
	public boolean checkGuess(String guess) throws IOException {  // It will check the user guess is matched or not.
		if (!dictionary.checkWord(guess) || !isRunning() || pastGuesses.contains(guess)) {  // It will check the user guess exists or not.
			return false;
		} 
		if (difficulty) { // only used when hard mode is active
			return furtherCheck(guess);
		}
		return true;
	}
	

	/**
	 * An advanced check for Wordle when difficulty is set to hard mode. Will make it so
	 * that the previously used hints must be used in the next guess. Loops through the past guesses
	 * and makes sure that it contains the letters that exist in the word, and will force the letter
	 * that are in the correct position to be in that specific position.
	 * @param guess The current word to check
	 * @return boolean that determines if the word is valid
	 */
	private boolean furtherCheck(String guess) { // advancd checks for hard mode
		if (pastGuesses.isEmpty()) { // first guess is free
			return true;
		}
		String oldWord = pastGuesses.get(pastGuesses.size()-1); // compares against most recent word
		for (int i = 0; i < pastClues.length; i++) {
			if (pastGuesses.size() == i) { // used to avoid index out of bounds errors
				break;
			}
			for (int j = 0; j < pastClues[i].length; j++) {
				if (pastClues[i][j] == 1 && oldWord.charAt(j) != guess.charAt(j)) { // enforces correct char placement
					return false;
				} else if (pastClues[i][j] == 0 && !guess.contains(Character.toString(oldWord.charAt(j)))) { // enforces contains char
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checking the user guess is perfectly matched to the target word.
	 * @param guess The user guess to check the correctness.
	 * @return true if the user guess is matched to the target word.
	 */
	public boolean addGuess(String guess) {
		pastGuesses.add(guess);
		TreeMap<Character, ArrayList<Integer>> unmatched = new TreeMap<Character, ArrayList<Integer>>();
		
		for(int i = 0; i < 5; i++) { // Determines exact matches or adds to unmatched letters
			if(guess.charAt(i) == wordToGuess.charAt(i)) {
				pastClues[pastGuesses.size()-1][i] = 1;
			} else {
				pastClues[pastGuesses.size()-1][i] = -1;
				if(unmatched.get(guess.charAt(i)) != null) {
					unmatched.get(guess.charAt(i)).add(i);
				} else {
					unmatched.put(guess.charAt(i), new ArrayList<Integer>(Arrays.asList(i)));
				}
			}
		}
		for(int i = 0; i < 5; i++) { // Second pass, determining wrong spot and no spot clues
			if(guess.charAt(i) != wordToGuess.charAt(i) && unmatched.get(wordToGuess.charAt(i)) != null && unmatched.get(wordToGuess.charAt(i)).size() > 0) {
				pastClues[pastGuesses.size()-1][unmatched.get(wordToGuess.charAt(i)).remove(0)] = 0;
			}
		}
		if(guess.equals(wordToGuess)) {
			running = false;
			return true;
		}
		return false;
	}
	
	/**
	 * Getter method to get the target word of the current game.
	 * @return A string that is target word
	 */
	public String getCorrectWord() {  // Get a target word
		return wordToGuess;
	}
	
	/**
	 * Method to restart the game with clear the board.
	 * @throws IOException
	 */
	public void restartGame() throws IOException {  // Restart game
		pastGuesses = new ArrayList<>();  // Clear user guesses
		pastClues = new int[6][5];
		dictionary.generateWord();  // Bring a new target word.
		wordToGuess = dictionary.selectedWord;
		running = true;
	}
	
	/**
	 * Getter method to get the guesses from the user.
	 * @return A string ArrayList to see the past user guesses
	 */
	public ArrayList<String> getGuesses(){ return pastGuesses; }
	
	/**
	 * Getter method to get the corrections of the user guesses.
	 * @return A integer 2d array to see the corrections of the guesses.
	 */
	public int[][] getClues(){ return pastClues; }
	
	/**
	 * Getter method to check the status of the game.
	 * @return true if the game is still running.
	 */
	public boolean isRunning() { return running; }
	
	/**
	 * Setter method to stop running the game.
	 */
	public void stopPlaying() {
		running = false;
	}
	
	/**
	 * Sets the current word to guess in the Wordle game.
	 * For testing purposes only.
	 * @param word	The word to be set to, gets validated.
	 * @throws IOException
	 */
	public void setWord(String word) throws IOException { // For testing **ONLY**
		if (!dictionary.checkWord(word) || !isRunning()) {
			System.out.println("Not a valid word or game not active");;
		}
		wordToGuess = word;
		dictionary.setWord(word);
	}

	/**
	 * Changes the stored difficulty between normal mode and hard mode.
	 */
    public void changeDifficulty() {
		if (difficulty == false) {
			difficulty = true;
		} else {
			difficulty = false;
		}
    }
}
