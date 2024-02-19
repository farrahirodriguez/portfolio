package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import model.Wordle;
import view_controller.WordleAccount;

class WordleAccountTest {

	@Test
	void accountTest() throws IOException {
		WordleAccount acc = new WordleAccount("new", "user");
		
		assertTrue(acc.checkUsername("new"));
		assertFalse(acc.checkUsername("newest"));
		assertTrue(acc.checkPassword("user"));
		assertFalse(acc.checkPassword("username"));
	}
	
	@Test
	void winsAndLossesTest() throws IOException {
		WordleAccount acc = new WordleAccount("john", "smith");
		
		acc.addWin(2);
		acc.addLose();
		acc.addLose();
		acc.addWin(1);
		acc.addWin(4);
		
		assertEquals(3, acc.getWin());
		assertEquals(2, acc.getLose());
		assertEquals(5, acc.getTimesPlayed());
		
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(2);
		test.add(1);
		test.add(4);
		
		assertEquals(test, acc.getBreakPoints());
	}
	
	@Test
	void streaksTest() throws IOException {
		WordleAccount acc = new WordleAccount("john", "smith");
		
		acc.addWin(2);
		acc.addLose();
		assertEquals(1, acc.getMaxStreak());
		assertEquals(0, acc.getCurStreak());
		acc.addLose();
		acc.addWin(1);
		acc.addWin(4);
		acc.addWin(3);
		acc.addWin(5);
		
		assertEquals(71, acc.getWinPrct());
		assertEquals(4, acc.getMaxStreak());
		assertEquals(4, acc.getCurStreak());
		
		acc.addWin(1);
		acc.addWin(4);
		acc.addWin(3);
		acc.addWin(5);
		acc.addWin(6);
		acc.addLose();
		
		assertEquals(76, acc.getWinPrct());
		assertEquals(9, acc.getMaxStreak());
		assertEquals(0, acc.getCurStreak());
	}
	
	@Test
	void toStringTest() throws IOException {
		WordleAccount acc = new WordleAccount("new", "user");
		
		
		String test = "new" + "\n" + "user" + "\n";
		
		assertEquals(test, acc.toString());
	}
	
}
