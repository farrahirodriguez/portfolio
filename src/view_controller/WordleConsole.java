package view_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.Wordle;

//Small changes may need to be made, but basically functional
/**
 *	Stores information and methods required to run the game Wordle in a console.
 */
public class WordleConsole {
	private static final String CORRECT = "\u001B[32m";
	private static final String CONTAINS = "\u001B[33m";
	private static final String CLEAR = "\u001B[0m";	
	
	private static Wordle game = new Wordle();
	private static Scanner s = new Scanner(System.in);

	//private static boolean hard = false;
	
	/**
	 * The game loop for Wordle.
	 * Handles inputs from the user and passes them to the appropriate methods
	 * for logic. Includes accepting user input, printing the state of the game,
	 * and handling end of game behavior.
	 * @param args	Unused.
	 */
	public static void main(String[] args) {
		setDifficulty();
		String guess = null;
		do {
			guess = getGuess();
			try {
				boolean check = game.checkGuess(guess);
				if(check) {
					game.addGuess(guess);
				} else {
					System.out.println("Invalid guess (not in dictionary, or previously guessed)");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			printBoard();
			if(!game.isRunning()) {
				if(guess.equals(game.getCorrectWord())) {
					System.out.println("Congratulations! You took " + game.getGuesses().size() + " guesses!");
				} else {
					System.out.println("Correct word was: " + game.getCorrectWord());
				}
				playAgain();
			}
		} while(game.isRunning());
	}
	
	private static void setDifficulty() {
		String ans;
		while (true) {
			System.out.println("please enter hard or normal for difficulty");
			ans = s.next();
			if (ans.equalsIgnoreCase("hard")) {
				game.changeDifficulty();
				return;
			}
			if (ans.equalsIgnoreCase("normal")) {
				return;
			}
		}
	}

	private static void playAgain() {
		String resp;
		while(true) {
			System.out.println("Would you like to play again? (Y/N)");
			resp = s.next();
			if(resp.toLowerCase().equals("y")) {
				try {
					game.restartGame();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			} else if(resp.toLowerCase().equals("n")) {
				break;
			}
		}		
	}

	private static String getGuess() {
		String guess;
		System.out.println("Enter a 5-letter guess!");
		while(true) {
			guess = s.next();
			if(guess.length() == 5) {
				break;
			} else if(guess.toLowerCase().equals("statistics")) {
				System.out.println("We are still working on a statistics feature!");
			}
			System.out.println("Guess must be 5 letters!");
			printBoard();
		}
		return guess.toLowerCase();
	}

	private static void printBoard() {
		ArrayList<String> guesses = game.getGuesses(); //Kinda wish this was just a normal array :/
		int[][] clues = game.getClues();
		System.out.println();
		for(int i = 0; i < guesses.size(); i++) {
			printWord(guesses.get(i), clues[i]);
		}
		System.out.println();
	}
	
	private static void printWord(String word, int[] clues) {
		String upWord = word.toUpperCase();
		for(int i = 0; i < clues.length; i++) {
			if(clues[i] == 1) {
				System.out.print(CORRECT);
			} else if(clues[i] == 0 ) {
				System.out.print(CONTAINS);
			}
			System.out.print(upWord.charAt(i) + " ");
			System.out.print(CLEAR);			
		}
		System.out.println();
	}

	
}
