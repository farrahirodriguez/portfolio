package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import model.Dictionary;

public class DictionaryTester {

	@Test
	void TestDict0() throws Exception {
		Dictionary dict = new Dictionary();
		System.out.println(dict.selectedWord);
	}
	
	@Test
	void TestDict1() throws Exception {
		Dictionary dict = new Dictionary();
		System.out.println(dict.selectedWord);
		System.out.println(dict.checkWord("sling"));
		assertFalse(dict.checkWord("abcde"));
	}
	
	@Test
	void TestDictClear() throws Exception {
		Dictionary dict = new Dictionary();
		dict.generateWord();
		dict.generateWord();
		dict.generateWord();
		dict.generateWord();
		dict.generateWord();
		dict.generateWord();
		dict.generateWord();
		dict.setWord("Crane");
		dict.clearHistory();
		assertTrue(dict.previouslySelected.size()==0);
	}
	
	@Test
	void TestDict2() throws Exception {
		Dictionary dict = new Dictionary();
		System.out.println(dict.selectedWord);
		System.out.println(dict.checkWord("sling"));
		dict.clearHistory();
	}
}
