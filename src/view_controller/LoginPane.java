package view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Sets the functionality of the account changing buttons in WordleGUI
 * @author Farrah Rodriguez
 * 
 *
 */
public class LoginPane extends GridPane {
	private WordleAccount activeUser;
	private Button loginButton;
	private Button signUpButton;
	private Button logoutButton;
	private WordleAccount wordleAccount;
	private ArrayList<WordleAccount> accountsArray;
	private ObservableList<WordleAccount> accountsObservable;

	/**
	 * 
	 * Sets all functions for the login and sign up systems
	 * including saving new accounts and searching existing accounts.
	 * 
	 */
	public LoginPane() {
		accountsArray = new ArrayList<WordleAccount>();
		accountsObservable = FXCollections.observableArrayList(accountsArray);
		loginButton = new Button("Login");
		signUpButton = new Button("Sign Up");
		logoutButton = new Button("Logout");

		loginButton.setVisible(true);
		signUpButton.setVisible(true);
		logoutButton.setVisible(false);

		this.add(loginButton, 0, 0);
		this.add(signUpButton, 1, 0);
		this.add(logoutButton, 0, 0);

		loginButton.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showLoginScreen();
			}
		});

		signUpButton.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSignUpScreen();
			}
		});

		logoutButton.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText("Click Cancel stay Logged In");
				alert.setContentText("To Logout, click OK");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					loginButton.setVisible(true);
					signUpButton.setVisible(true);
					logoutButton.setVisible(false);
					activeUser = null;
				}
			}
		});
	}

	/**
	 * 
	 * Attempts to login user with given inputs.
	 * Checks username and password based on saved accounts
	 * in accounts.ser. Displays alerts based on successful or
	 * unsuccessful login attempts.
	 * 
	 * @param username text from user in username input box
	 * @param password text from user in password input box
	 */
	private void attemptLogin(String username, String password) {
		readAccounts();
		for (WordleAccount acc : accountsObservable) {
			if (acc.checkUsername(username)) {
				if (acc.checkPassword(password)) {
					loginSuccessful(acc);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Successful login");
					alert.setHeaderText("Successful login");
					String s = username + " logged in!";
					alert.setContentText(s);
					alert.show();
					return;
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Password incorrect");
					alert.setHeaderText("Incorrect Password");
					alert.show();
					return;
				}
			}
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Username not found");
		alert.setHeaderText("Username not found");
		alert.show();
	}
	
	/**
	 * 
	 * Attempts to signu up user with given inputs.
	 * Checks for uniqueness of username based on 
	 * current saved accounts in accounts.ser. If the
	 * username already exists, the user is alerted. 
	 * When a unique username is given with a password, 
	 * an account will be created and saved to accounts.ser. 
	 * User will be alerted. 
	 * 
	 * @param username text from user in username input box
	 * @param password text from user in password input box
	 */
	private void attemptSignUp(String username, String password) {
		readAccounts();
		boolean accountFound = false;
		for (WordleAccount acc : accountsObservable) {
			if (acc.checkUsername(username)) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Username already exists");
				alert.setHeaderText("Username already exists");
				alert.show();
				accountFound = true;
			}
		}
		if (accountFound == false) {
			WordleAccount newAccount = new WordleAccount(username, password);
			accountsObservable.add(newAccount);
			saveAccounts();

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Account Created");
			alert.setHeaderText("Account Created");
			String s = username + " account created!";
			alert.setContentText(s);
			alert.show();
		}
	}

	/**
	 * Displays the login page where the user can
	 * input their username and password to login. 
	 * Will display an alert if the user attempts 
	 * to login with blank credentials. 
	 */
	public void showLoginScreen() {
		Stage stage = new Stage();

		VBox box = new VBox();
		box.setPadding(new Insets(10));
		box.setAlignment(Pos.CENTER);

		Label label = new Label("Enter username and password");
		TextField textUser = new TextField();
		textUser.setPromptText("enter username");
		PasswordField textPass = new PasswordField();
		textPass.setPromptText("enter password");

		Button btnLogin = new Button();
		btnLogin.setText("Login");

		btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String username = textUser.getText().trim();
				String password = textPass.getText();
				if (username.isEmpty() || password.isEmpty()) {
					Alert invalidAlert = new Alert(AlertType.ERROR);
					invalidAlert.setTitle("Invalid Input");
					invalidAlert.setHeaderText("Invalid Input");
					invalidAlert.setContentText("Input cannot be blank.");
					invalidAlert.showAndWait();
				} else {
					attemptLogin(textUser.getText(), textPass.getText());
					stage.close();
				}
			}
		});
		box.getChildren().add(label);
		box.getChildren().add(textUser);
		box.getChildren().add(textPass);
		box.getChildren().add(btnLogin);
		Scene scene = new Scene(box, 250, 150);
		scene.getStylesheets().add("view_controller/loginStyle.css");
		stage.setScene(scene);
		stage.show();

	}

	
	/**
	 * Displays the sign up page where the user can
	 * create a username and password for a new account. 
	 * Will display an alert if the user attempts 
	 * to sign up with blank credentials. 
	 */
	public void showSignUpScreen() {
		Stage stage = new Stage();

		VBox box = new VBox();
		box.setPadding(new Insets(10));

		box.setAlignment(Pos.CENTER);

		Label label = new Label("Create a username and password");

		TextField textUser = new TextField();
		textUser.setPromptText("enter username");
		TextField textPass = new TextField();
		textPass.setPromptText("enter password");

		Button btnLogin = new Button();
		btnLogin.setText("Sign Up");

		btnLogin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String username = textUser.getText().trim();
				String password = textPass.getText();
				if (username.isEmpty() || password.isEmpty()) {
					Alert invalidAlert = new Alert(AlertType.ERROR);
					invalidAlert.setTitle("Invalid Input");
					invalidAlert.setHeaderText("Invalid Input");
					invalidAlert.setContentText("Input cannot be blank.");
					invalidAlert.showAndWait();
				} else {
					attemptSignUp(textUser.getText(), textPass.getText());
					stage.close();
				}
			}
		});
		box.getChildren().add(label);
		box.getChildren().add(textUser);
		box.getChildren().add(textPass);
		box.getChildren().add(btnLogin);
		Scene scene = new Scene(box, 250, 150);
		scene.getStylesheets().add("view_controller/loginStyle.css");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * 
	 * Removes the login and sign up button from
	 * view and adds the logout button upon a 
	 * successful login. Also sets the active user.
	 * 
	 * @param acc the active/logged in user
	 */
	private void loginSuccessful(WordleAccount acc) {
		logoutButton.setVisible(true);
		loginButton.setVisible(false);
		signUpButton.setVisible(false);
		activeUser = acc;
	}

	/**
	 * 
	 * Gets the active/logged in user
	 * 
	 * @return the WordleAccount of the currently logged in user
	 */
	public WordleAccount getActiveUser() {
		return activeUser;
	}

	
	/**
	 * 
	 * Reads from accounts.ser to get saved accounts.
	 * 
	 */
	public void readAccounts() {
		String file = "accounts.ser";

		try {
			FileInputStream rawBytes = new FileInputStream(file);

			if (rawBytes.available() > 0) {
				ObjectInputStream inFile = new ObjectInputStream(rawBytes);
				ArrayList<WordleAccount> list = (ArrayList<WordleAccount>) inFile.readObject();

				accountsArray = list;
				accountsObservable = FXCollections.observableArrayList(accountsArray);
				System.out.println("Reading: " + accountsArray.toString());
				inFile.close();
			} else {
				System.out.println("Reading: File is Empty");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Reading: File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Reading: IOException");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Reading: ClassNotFound");
			e.printStackTrace();
		}
	}

	Comparator<WordleAccount> comparator = new Comparator<WordleAccount>() {
		public int compare(WordleAccount o1, WordleAccount o2) {
			return Integer.compare(o2.getWin(), (o1.getWin()));
		}

	};

	/**
	 * 
	 * Sorts the accounts based on number of wins. 
	 *  
	 * @return sorted ArrayList of WordleAccounts
	 */
	public ArrayList<WordleAccount> getSortedAccounts() {
		ArrayList<WordleAccount> sortedAccounts = accountsArray;
		Collections.sort(sortedAccounts, new Comparator<WordleAccount>() {
		    public int compare(WordleAccount account1, WordleAccount account2) {
		        return Integer.compare(account2.getWin(), account1.getWin()); // Compare the wins in descending order
		    }
		});
		return accountsArray;
	}

	/**
	 * Writes WordleAccounts to a ser file.
	 */
	public void saveAccounts() {
		String file = "accounts.ser";
		ArrayList<WordleAccount> account = new ArrayList<WordleAccount>();
		for (WordleAccount acc : accountsObservable) {
			account.add(acc);
			System.out.println("Writing: " + acc.toString());
		}

		try {
			FileOutputStream bytesToDisk = new FileOutputStream(file);
			ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
			outFile.writeObject(account);
			outFile.close();
		} catch (IOException ioe) {
			System.out.println("Writing objects failed");
		}
	}
}
