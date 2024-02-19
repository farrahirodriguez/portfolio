package view_controller;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores account information for users of the Wordle app.
 * @author Farrah Rodriguez
 * @author John Ko
 * 
 */
public class WordleAccount implements Serializable {
	private String username;
	private String password;
	private int timesPlayed;
	private int wins;
	private int loses;
	private int maxStreak;
	private int curStreak;
	private int[] breakPts;
	private ArrayList<Integer> numsGuess;
	
	/**
	 * 
	 * Constructor for WordleAccount. 
	 * Sets username and password based on user's input. 
	 * Sets other fields to 0 or empty data structures. 
	 * 
	 * @param username	user's unique identifier
	 * @param password	user's private key to login with their username
	 */
	public WordleAccount(String username, String password) {
		this.username = username;
    	this.password = password;
    	this.timesPlayed = 0;
    	this.wins = 0;
    	this.loses = 0;
    	this.maxStreak = 0;
    	this.curStreak = 0;
    	this.breakPts = new int[6];
    	this.numsGuess = new ArrayList<Integer>();
    	}
	
	/**
	 * 
	 * Determines if the username given is equal to 
	 * the username of the current account selected.
	 *  
	 * @param username	user's unique identifier 
	 * @return true if the usernames are the same, false otherwise
	 */
	public boolean checkUsername(String username) {
    	return this.username.equals(username);
	}
	
	/**
	 * 
	 * Gets username of account
	 * 
	 * @return string username
	 */
	public String getUsername() {
    	return this.username;
	}
	
	/**
	 * 
	 * Determines if the password given is equal to 
	 * the password of the current account selected.
	 *  
	 * @param password	user's private key to login with their username
	 * @return true if the passwords are the same, false otherwise
	 */
	public boolean checkPassword(String password) {
    	return this.password.equals(password);
	}
	

	/**
	 * 
	 * Provides a string representation of an account
	 * for testing purposes
	 *  
	 * @return string representation of the account
	 */
	@Override
	public String toString() {
		String account = "";
    	account += username + '\n' + password + '\n' +
    			+ timesPlayed + '\n' + maxStreak + '\n' +
    			+ curStreak + '\n' + getWinPrct();
		return account;
	}
	
	/**
	 * 
	 * adds a win to the accounts also adding to timesPlayed, 
	 * breakPts[level], and curStreak. Checks if curStreak is 
	 * greater than maxStreak. Sets maxStreak to curStreak if 
	 * curStreak is greater than maxStreak. 
	 * 
	 * @param level	the number of guesses it took the user to win 
	 */
	public void addWin(int level) {
		breakPts[level]++;
		timesPlayed++;
		wins++;
		
		curStreak++;
		if (curStreak > maxStreak) {
			maxStreak = curStreak;
		}
	}
	
	/**
	 * 
	 * Gets the number of wins a user has
	 * 
	 * @return int user wins
	 */
	public int getWin(){
		return wins;
	}
	
	/**
	 * 
	 * Adds a loss to the accounts also adding to timesPlayed. 
	 * Sets curStreak to 0.
	 * 
	 */
	public void addLose() {
		timesPlayed++;
		loses++;
		this.curStreak = 0;
	}
	
	/**
	 * 
	 * Gets the number of losses the user has
	 * 
	 * @return int user losses
	 */
	public int getLose() {
		return loses;
	}
	
	/**
	 * 
	 * Calculates and returns the user's percentage of wins
	 * based on their total times played. 
	 * 
	 * @return the percentage of wins a user has (int whole number)
	 */
	public int getWinPrct() {
		if (timesPlayed == 0) {
			return 0;
		}
		int percent = (this.wins * 100) / this.timesPlayed;
		return percent;
	}
	
	/**
	 * 
	 * Gets the user's current win streak
	 * 
	 * @return current win streak
	 */
	public int getCurStreak() {
		return this.curStreak;
	}
	
	/**
	 * 
	 * Gets the user's all time maximum win streak
	 * 
	 * @return user's maximum win streak
	 */
	public int getMaxStreak() {
		return this.maxStreak;
	}
	
	/**
	 * 
	 * Gets the user's guess distribution
	 * 
	 * @return int array of user's guess distribution
	 */
	public int[] getBreakPoints(){
		return breakPts;
	}
	
	/**
	 * 
	 * Gets the total number of times a user has played Wordle
	 * 
	 * @return total amount of times user has played
	 */
	public int getTimesPlayed() {
		return timesPlayed;
	}
}
