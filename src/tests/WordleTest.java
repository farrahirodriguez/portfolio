package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import model.Wordle;

class WordleTest {

	@Test
	void getTargetWordTest() throws IOException {
		Wordle game = new Wordle();
		System.out.println("Game 1: " + game.getCorrectWord());
		game.restartGame();
		System.out.println("Game 2: " + game.getCorrectWord());
		game.restartGame();
		System.out.println("Game 3: " + game.getCorrectWord());
		
		assertFalse(game.checkGuess("abcde"));
	}
	
	@Test
	void getUserGuessTest() throws IOException {
		Wordle game = new Wordle();
		System.out.println("Game 1: " + game.getCorrectWord());
		
		if (game.checkGuess("chain"))
			game.addGuess("chain");
		
		if (game.checkGuess("cheer"))
			game.addGuess("cheer");
		
		if (game.checkGuess("abcde"))
			game.addGuess("abcde");
		
		if (game.checkGuess("saint"))
			game.addGuess("saint");
		
		if (game.checkGuess("black"))
			game.addGuess("black");
		
		if (game.checkGuess("waste"))
			game.addGuess("waste");
		
		System.out.println("User Guesses: " + game.getGuesses().toString());		
	}
	
	@Test
	void addGuessesTest() throws IOException {
		Wordle game = new Wordle();
		game.addGuess("aarti");
		game.addGuess("bawks");		
		game.addGuess("fehmi");		
		game.addGuess("huzzy");		
		game.addGuess("jupon");
		game.addGuess("maqui");
		assertFalse(game.isRunning());
	}
	
	@Test
	void checkMoreThanOne() throws IOException {
		Wordle game = new Wordle();
		game.setWord("scene");
		game.addGuess("melee");
		assertTrue(game.getClues()[0][1] == 0);
		assertTrue(game.getClues()[0][4] == 1);
		assertTrue(game.getClues()[0][3] == -1);
	}

	@Test
	void testHard0() throws IOException{
		Wordle game = new Wordle();
		game.changeDifficulty();
		game.setWord("spine");
		game.addGuess("crank");
		assertTrue(game.checkGuess("crane"));
	}

	@Test
	void testHard1() throws IOException {
		Wordle game = new Wordle();
		game.changeDifficulty();
		game.setWord("crush");
		game.addGuess("crane");
		assertFalse(game.checkGuess("waste"));
	}

	@Test
	void testHard2() throws IOException {
		Wordle game = new Wordle();
		game.changeDifficulty();
		game.setWord("spire");
		game.addGuess("crash");
		assertFalse(game.checkGuess("flame"));
		assertTrue(game.checkGuess("crane"));
	}
}
