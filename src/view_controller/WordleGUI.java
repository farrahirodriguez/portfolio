package view_controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Wordle;

/**
 * Wordle GUI to show the Wordle game on the screen board.
 *
 */
public class WordleGUI extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private LoginPane loginPane;
	private BorderPane everything;
	private Button displayStatsBtn;
	private Button leaderBoardBtn;
	private Button tutorialBtn;
	private Button lightingBtn;
	private Button restartBtn;
	private Button changeDifficulty;
	private String lighting;
	private GridPane grid;
	private Button giveUpBtn;
	private Wordle game = new Wordle();

	private final int boardWidth = 6;
	private Label[][] boardLabels;
	private String[][] guesses = new String[6][5];
	private static int level = 0; // Can be used to get Guess Breakdown and streak
	private static int step = 0;
	private int[][] clues;
	private Scene scene;
	private VBox root;
	String chars = "qwertyuiopasdfghjkl↲zxcvbnm⇦";
	ArrayList<Label> keys = new ArrayList<Label>();
	int[] keyColors = new int[28];
	private Image statsIcon;
	private Image leaderBrdIcon;
	private Image lightingIcon;
	private Image restartIcon;
	private Image cancelIcon;
	private Image tutorialIcon;
	private ImageView statsView;
	private ImageView leaderBrdView;
	private ImageView lightingView;
	private ImageView restartView;
	private ImageView cancelView;
	private ImageView tutorialView;
	private Image statsDarkIcon;
	private Image leaderBrdDarkIcon;
	private Image lightingDarkIcon;
	private Image restartDarkIcon;
	private Image cancelDarkIcon;
	private Image tutorialDarkIcon;
	private ImageView statsDarkView;
	private ImageView leaderBrdDarkView;
	private ImageView lightingDarkView;
	private ImageView restartDarkView;
	private ImageView cancelDarkView;
	private ImageView tutorialDarkView;

	/**
	 * Overriden method to show and start Wordle GUI.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		System.out.println("Target: " + game.getCorrectWord());
		LayoutGUI();
		gameBoardGUI();
		setGameAction();
		resetKeys();
		scene = new Scene(everything, 400, 650);
		scene.getStylesheets().add("view_controller/regularStyle.css");
		// When user enter the guess with pressing Enter key, it will submit the guess.
		everything.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					if (step == 5) {
						if (checkUserGuess(guesses[level])) {
							displayClues(); // Show status.
							updateKeys();
							level++;
							step = 0;
						}
						// When a user lost the game, increment loses and timesPlayed here for the
						// account.
						if (level == 6) {
							WordleAccount acc = loginPane.getActiveUser();
							if (acc != null) {
								acc.addLose();
								loginPane.saveAccounts();
							}
							showAnsWhenLose("You lose for this game.");
							playMusicAfterGame("lose");
						}
					} else {
						shakeScene();
					}
					arg0.consume();
				}
			}
		});
		everything.setOnKeyPressed((event) -> {
			if (!game.isRunning()) { // When the game is over, it will stop updating the board.
				return;
			}
			String text = event.getText();
			if (text.matches("[a-zA-Z]") && step <= 4) { // It will accept only alphabets for guess
				if (text.length() != 1) {
					return;
				}
				if (boardLabels[level][step].getText().isEmpty()) { // It will fill the box with the one letter of user
																	// guess
					boardLabels[level][step].setText(text.toUpperCase());
					guesses[level][step] = text.toLowerCase();
					step++;
					return;
				}
			} else if (event.getCode() == KeyCode.BACK_SPACE) { // When a user pressed Backspace, it will erase a box.
				if (step > 0) {
					step--;
					boardLabels[level][step].setText("");
					guesses[level][step] = "";
				}
			}
		});
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * It shakes when the user submit the unavailable guess.
	 */
	private void shakeScene() {
		for (int i = 0; i < 5; i++) {
			Label box = boardLabels[level][i];
			TranslateTransition tt = new TranslateTransition(Duration.seconds(0.05), box);
			tt.setFromX(0);
			tt.setToX(10);
			tt.setCycleCount(4);
			tt.setAutoReverse(true);
			tt.play();
		}
	}

	/**
	 * Show alert with the answer for the user when the user lost the game.
	 * 
	 * @param message A string that is including how the user finished the game when
	 *                lost.
	 */
	private void showAnsWhenLose(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(message);
		alert.setHeaderText(null);
		alert.setContentText("Correct answer is " + game.getCorrectWord());
		alert.showAndWait();
		game.stopPlaying();
	}

	/**
	 * EventHandlers of the buttons on the GUI.
	 */
	private void setGameAction() {
		giveUpBtn.setOnAction(event -> {
			showAnsWhenLose("User gave up the game.");
			playMusicAfterGame("lose");
		});

		restartBtn.setOnAction(event -> {
			restartGame();
		});

		displayStatsBtn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				displayStats();
			}
		});

		leaderBoardBtn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				displayLeaderBrd();
			}
		});

		tutorialBtn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				displayTutorial();
			}
		});

		lightingBtn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				changeLighting();
			}
		});
		
		changeDifficulty.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				game.changeDifficulty();
			}
		});
	}

	/**
	 * To restart the game, it will erase the scene and clear the status of the
	 * board.
	 */
	private void restartGame() {
		try {
			game.restartGame();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		guesses = new String[6][5];
		level = 0;
		step = 0;
		clues = game.getClues();
		gameBoardGUI();
		resetKeys();
		System.out.println(game.getCorrectWord());
	}
	
	/**
	 * Animation to flip the clues for Wow Factor.
	 * 
	 * @param label  The box that will be flipped.
	 * @param column Get the place of the column to flip
	 */
	private void flipClues(Label label, int column) {
		int delay = 100;
		int speed = 300;

		RotateTransition rotateOut = new RotateTransition(Duration.millis(speed), label);
		rotateOut.setAxis(Rotate.X_AXIS);
		rotateOut.setByAngle(90);
		rotateOut.setOnFinished(event -> {
			RotateTransition rotateIn = new RotateTransition(Duration.millis(speed), label);
			rotateIn.setAxis(Rotate.X_AXIS);
			rotateIn.setByAngle(-90);
			rotateIn.play();
		});

		PauseTransition delayTransition = new PauseTransition(Duration.millis(column * delay));
		delayTransition.setOnFinished(event -> rotateOut.play());

		delayTransition.play();
	}

	/**
	 * Show the colors depending on the correctness. Correct->Green, Contain->Yello,
	 * and None->Grey
	 * Show the colors depending on the correctness. Correct->Green, Contain->Yello, and None->Grey
	 */
	private void displayClues() {
		for (int i = 0; i < 5; i++) {
			Label box = boardLabels[level][i];
			if (clues[level][i] == 1) { // When the guess is on the right spot, box will be filled to green.
				boardLabels[level][i].setStyle(
						"-fx-background-color: #00ff00; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
			} else if (clues[level][i] == 0) { // When the guess is contained, box will be filled to yellow
				boardLabels[level][i].setStyle(
						"-fx-background-color: #ffff00; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
			} else { // Otherwise, it will be gray color.
				boardLabels[level][i].setStyle(
						"-fx-background-color: #808080; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
			}
			BorderStroke borderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
					BorderStroke.THIN);
			Border border = new Border(borderStroke);

			flipClues(box, i);

			boardLabels[level][i].setBorder(border);
		}
	}


	/**
	 * It plays the sound effects when the game is done.
	 * @param status Detect the user won or lost the game.
	 */
	private void playMusicAfterGame(String status) {
		String filePath = "";
		if (status.equals("win")) {
			filePath = "songs/CorrectSound.mp3";
		} else {
			filePath = "songs/FailingSound.mp3";
		}
        Media media = new Media(new File(filePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
	}
	
	/**
	 * Checks the user guess with target word
	 * 
	 * @param chars char array of the user guess.
	 * @return true if the user guess is matched with the target word.
	 */
	private boolean checkUserGuess(String[] chars) { // It will get the user guess to check.
		WordleAccount acc = loginPane.getActiveUser();
		String guess = "";
		boolean done = false;
		for (String letter : chars) {
			guess += letter;
		}
		boolean correct = false;
		try {
			correct = game.checkGuess(guess);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!correct) { // When guess is not acceptable
			shakeScene();
		} else { // When the guess is acceptable.
			done = game.addGuess(guess);
			clues = game.getClues();
		}

		if (done) { // Means the user succeed to guess the word
			playMusicAfterGame("win");
			if (acc != null) {
				acc.addWin(level);
				loginPane.saveAccounts();
			}
		}

		return correct;
	}

	/**
	 * Implement a 6*5 grid to show the board of the Wordle game
	 */
	private void gameBoardGUI() { // Used GridPane and VBox with labels to show the board for Wordle game.
		boardLabels = new Label[boardWidth][];
		for (int i = 0; i < boardWidth; i++) {
			boardLabels[i] = new Label[26];
			for (int j = 0; j < 26; j++) {
				boardLabels[i][j] = new Label();
				boardLabels[i][j].setMinSize(60, 60);
				boardLabels[i][j].setMaxSize(80, 60);
				boardLabels[i][j]
						.setStyle("-fx-border-color: #888888; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
				boardLabels[i][j].setAlignment(Pos.CENTER);
			}
		}

		GridPane boardPane = new GridPane();
		boardPane.setVgap(5);
		boardPane.setHgap(5);
		boardPane.setAlignment(Pos.CENTER);
		for (int i = 0; i < boardWidth; i++) {
			for (int j = 0; j < 5; j++) {
				boardPane.add(boardLabels[i][j], j, i);
			}
		}

		root = new VBox(20, boardPane);
		root.setAlignment(Pos.CENTER);
		root.setPrefSize(400, 400);

		everything.setCenter(root);
	}

	private void keyboard() {
		ColumnConstraints constraints = new ColumnConstraints();
		constraints.setHalignment(HPos.RIGHT);
		ArrayList<HBox> rows = new ArrayList<HBox>();
		rows.add(new HBox(4));
		rows.add(new HBox(4));
		rows.add(new HBox(4));
//		for(HBox h : rows) {
//			h.setSpacing(4);
//		}
		int row1 = 1;
		VBox boardPane = new VBox(3);
		for (int i = 0; i < 3; i++) { //hardcoded but it's a goddamn keyboard
			for (int j = 0; j < 9+row1 ; j++) {
				int index = i*9+j+1-row1; //9*number of rows + position in row + 1 only after row 1 is done
				Label temp = keys.get(index);
				if (temp.getText().equals(Character.toString('\u21b2'))) {
					temp.setOnMouseClicked((event) -> {
						Robot r = new Robot();
						r.keyType(KeyCode.ENTER);
					});
				} else if (temp.getText().equals(Character.toString('\u21e6'))) {
					temp.setOnMouseClicked((event) -> {
						Robot r = new Robot();
						r.keyType(KeyCode.BACK_SPACE);
					});
				} else {
					temp.setOnMouseClicked((event) -> {
						Robot r = new Robot();
						r.keyType(KeyCode.getKeyCode(temp.getText().toUpperCase()));
					});
				}
				rows.get(i).getChildren().add(temp);
			}
			row1 = 0;
		}
		boardPane.getChildren().addAll(rows);
		VBox.setMargin(boardPane.getChildren().get(1), new Insets(0, 0, 0, 10));
		VBox.setMargin(boardPane.getChildren().get(2), new Insets(0, 0, 0, 20));
		VBox root = new VBox(20, boardPane);
		root.setAlignment(Pos.CENTER);
		root.setPrefSize(400, 300);

		everything.setBottom(root);
	}

	private void resetKeys() {
		keys = new ArrayList<Label>();
		for(int i = 0; i < chars.length(); i++) {
			Label temp = new Label(Character.toString(chars.charAt(i)).toUpperCase());
			temp.setMinSize(35, 35);
			temp.setStyle("-fx-border-color: black; -fx-font-size: 15; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24; ");
			temp.setAlignment(Pos.CENTER);
			keys.add(temp);
		}
		Arrays.fill(keyColors, -2);
		keyboard();
	}
	
	private void updateKeys() {
		int[] clues = game.getClues()[level];
		String[] word = guesses[level]; 
		for(int i = 0; i < word.length; i++) {
			int charIndex = chars.indexOf(word[i].charAt(0));
			if(clues[i] > keyColors[charIndex]) {
				keyColors[charIndex] = clues[i];
				if (clues[i] == 1) { // When the guess is on the right spot, box will be filled to green.
					keys.get(chars.indexOf(word[i].charAt(0))).setStyle("-fx-background-color: #00ff00; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
				} else if (clues[i] == 0) { // When the guess is contained, box will be filled to yellow
					keys.get(chars.indexOf(word[i].charAt(0))).setStyle("-fx-background-color: #ffff00; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
				} else { // Otherwise, it will be gray color.
					keys.get(chars.indexOf(word[i].charAt(0))).setStyle("-fx-background-color: #808080; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 24;");
				}
			}
		}
	}
	
	/**
	 * Setting buttons for Wordle game on GUI
	 */
	private void LayoutGUI() {
		everything = new BorderPane();
		loginPane = new LoginPane();
		everything.setTop(loginPane);

		Image statsIcon = new Image("/resources/icons8-combo-chart-24.png");
		Image leaderBrdIcon = new Image("/resources/icons8-leaderboard-24.png");
		Image lightingIcon = new Image("/resources/icons8-moon-and-stars-30.png");
		Image restartIcon = new Image("/resources/icons8-redo-48.png");
		Image cancelIcon = new Image("/resources/icons8-x-50.png");
		Image tutorialIcon = new Image("/resources/icons8-information-48.png");
		Image hardIcon = new Image("/resources/icons8-skull-32.png");

		ImageView statsView = new ImageView(statsIcon);
		ImageView leaderBrdView = new ImageView(leaderBrdIcon);
		ImageView lightingView = new ImageView(lightingIcon);
		ImageView restartView = new ImageView(restartIcon);
		ImageView cancelView = new ImageView(cancelIcon);
		ImageView tutorialView = new ImageView(tutorialIcon);
		ImageView hardView = new ImageView(hardIcon);

		statsView.setFitWidth(32);
		statsView.setFitHeight(32);
		leaderBrdView.setFitWidth(32);
		leaderBrdView.setFitHeight(32);
		lightingView.setFitWidth(32);
		lightingView.setFitHeight(32);
		restartView.setFitWidth(32);
		restartView.setFitHeight(32);
		cancelView.setFitWidth(32);
		cancelView.setFitHeight(32);
		tutorialView.setFitWidth(32);
		tutorialView.setFitHeight(32);
		hardView.setFitWidth(32);
		hardView.setFitHeight(32);

		giveUpBtn = new Button();
		restartBtn = new Button();
		displayStatsBtn = new Button();
		leaderBoardBtn = new Button();
		tutorialBtn = new Button();
		lightingBtn = new Button();
		changeDifficulty = new Button();

		giveUpBtn.setGraphic(cancelView);
		restartBtn.setGraphic(restartView);
		displayStatsBtn.setGraphic(statsView);
		leaderBoardBtn.setGraphic(leaderBrdView);
		tutorialBtn.setGraphic(tutorialView);
		lightingBtn.setGraphic(lightingView);
		changeDifficulty.setGraphic(hardView);

		lighting = "Light";

		grid = new GridPane();

		grid.add(giveUpBtn, 0, 0);
		grid.add(restartBtn, 0, 1);
		grid.add(displayStatsBtn, 0, 2);
		grid.add(leaderBoardBtn, 0, 3);
		grid.add(tutorialBtn, 0, 4);
		grid.add(lightingBtn, 0, 5);
		grid.add(changeDifficulty, 0, 6);
		grid.setVgap(10);

		everything.setRight(grid);
	}

	/**
	 * 
	 * Sets the display of the stats board. If the user is not logged in, a message
	 * will display asking them to login. If they are already logged in. They will
	 * be able to see their times played, their win percentage, current streak, and
	 * max streak. They will also be shown a bar graph that represents the amount of
	 * times they have won with 1-6 guesses. This is referred to as their guess
	 * distribution.
	 * 
	 */
	private void displayStats() {
		WordleAccount acc = loginPane.getActiveUser();
		Stage stage = new Stage();
		GridPane statsGrid = new GridPane();

		if (acc == null) {
			Label title = new Label("You must be logged in to view your Stats");
			BorderPane statsPane = new BorderPane();
			statsPane.setCenter(title);
			Scene statsScene = new Scene(statsPane, 250, 250);
			stage.setScene(statsScene);
			stage.show();
		} else {
			Label title = new Label("Statistics");
			Label played = new Label("Played");
			Label winPrct = new Label("Win %");
			Label curStreak = new Label("Current Streak");
			Label maxStreak = new Label("Max Streak");
			Label playedAmt = new Label("" + acc.getTimesPlayed());
			Label winPrctAmt = new Label("" + acc.getWinPrct());
			Label curStreakAmt = new Label("" + acc.getCurStreak());
			Label maxStreakAmt = new Label("" + acc.getMaxStreak());
			statsGrid.add(title, 1, 0);
			statsGrid.add(playedAmt, 3, 1);
			statsGrid.add(winPrctAmt, 2, 1);
			statsGrid.add(curStreakAmt, 1, 1);
			statsGrid.add(maxStreakAmt, 0, 1);

			statsGrid.add(played, 3, 2);
			statsGrid.add(winPrct, 2, 2);
			statsGrid.add(curStreak, 1, 2);
			statsGrid.add(maxStreak, 0, 2);
			statsGrid.setPadding(new Insets(10, 10, 10, 10));
			statsGrid.setVgap(5);
			statsGrid.setHgap(5);

			BorderPane statsPane = new BorderPane();
			statsPane.setTop(statsGrid);

			statsPane.setCenter(createGraph());

			Scene statsScene = new Scene(statsPane, 250, 250);
			stage.setScene(statsScene);
			stage.show();

			displayLighting(statsScene);
		}
	}

	/**
	 * 
	 * Creates a bar chart using a user's guess distribution data which is stored in
	 * int[] levels.
	 * 
	 * @return bar chart of user's guess distribution stats
	 */
	private BarChart<String, Number> createGraph() {
		WordleAccount acc = loginPane.getActiveUser();
		NumberAxis xAxis = new NumberAxis(0, acc.getWin(), 1);
		CategoryAxis yAxis = new CategoryAxis();

		BarChart<String, Number> barChart = new BarChart<>(yAxis, xAxis);
		barChart.setTitle("Guess Distribution");

		XYChart.Series<String, Number> series = new XYChart.Series<>();

		int[] levels = acc.getBreakPoints();
		series.getData().add(new XYChart.Data<>("1", levels[0]));
		series.getData().add(new XYChart.Data<>("2", levels[1]));
		series.getData().add(new XYChart.Data<>("3", levels[2]));
		series.getData().add(new XYChart.Data<>("4", levels[3]));
		series.getData().add(new XYChart.Data<>("5", levels[4]));
		series.getData().add(new XYChart.Data<>("6", levels[5]));

		barChart.getData().add(series);
		return barChart;
	}

	
	/**
	 * 
	 * Sets the display of the leaderboard. The leaderboard displays the top 5 users
	 * with the most wins. A user must have an account to be displayed on the
	 * leaderboard.
	 * 
	 */
	public void displayLeaderBrd() {
		Stage stage = new Stage();

		BorderPane border = new BorderPane();
		GridPane titleGrid = new GridPane();
		Label title = new Label("Leaderboard");
		title.setStyle("-fx-font-size: 15px");
		Label name = new Label("User");
		name.setStyle("-fx-font-size: 13px");
		Label pos = new Label("Pos");
		pos.setStyle("-fx-font-size: 13px");
		Label winsNum = new Label("Wins");
		winsNum.setStyle("-fx-font-size: 13px");
		
		titleGrid.add(winsNum, 2, 1);
		titleGrid.add(pos, 0, 1);
		titleGrid.add(name, 1, 1);
		titleGrid.setHgap(35);
		titleGrid.setAlignment(Pos.CENTER);

		ArrayList<WordleAccount> accounts = loginPane.getSortedAccounts();

		VBox box = new VBox();
		box.getChildren().add(titleGrid);
		box.setPadding(new Insets(10));

		int numOfAccounts = accounts.size();
		int i = 1;
		for (WordleAccount acc : accounts) {
			GridPane leader = new GridPane();
			leader.setHgap(30);
			leader.setAlignment(Pos.BASELINE_CENTER);
			Label pos1 = new Label(String.valueOf(i + "  "));
			Label name1 = new Label(acc.getUsername());
			String wins = String.valueOf(acc.getWin());
			Label winsNum1 = new Label(wins + "  ");
			leader.add(pos1, 0, 0);
			leader.add(name1, 1, 0);
			leader.add(winsNum1, 2, 0);
			box.getChildren().add(leader);
			i++;
		}
		
		border.setAlignment(title, Pos.CENTER);
		border.setTop(title);
		border.setCenter(box);
		
		Scene leaderScene = new Scene(border, 250, 250);
		stage.setScene(leaderScene);
		stage.show();

		displayLighting(leaderScene);
	}


	/**
	 * 
	 * Sets the display for the tutorial page. Explains to the user how to play
	 * Wordle.
	 * 
	 */
	public void displayTutorial() {
		Stage stage = new Stage();

		Label title = new Label("How To Play");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		Label subhead = new Label("Guess the Wordle in 6 tries.");
		subhead.setStyle("-fx-font-size: 18px");
		Label point1 = new Label("Each guess must be a valid 5-letter word.");
		Label point2 = new Label(
				"The color of the tiles will change to show");
		Label point3 = new Label("how close your guess was to the word.");
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(10));
		box.setStyle("-fx-font-size: 15px");

		box.getChildren().add(title);
		box.getChildren().add(subhead);
		box.getChildren().add(point1);
		box.getChildren().add(point2);
		box.getChildren().add(point3);

		Scene tutorialScene = new Scene(box, 325, 250);
		stage.setScene(tutorialScene);
		stage.show();

		displayLighting(tutorialScene);
	}
	/**
	 * 
	 * Changes the stylesheet attached to the scene based on the lighting button
	 * 
	 */
	public void changeLighting() {
		if (lighting == "Light") {
			scene.getStylesheets().remove("view_controller/regularStyle.css");
			scene.getStylesheets().add("view_controller/darkStyle.css");
			lighting = "Dark";

			Image statsDarkIcon = new Image("/resources/icons8-combo-chart-light-24.png");
			Image leaderBrdDarkIcon = new Image("/resources/icons8-leaderboard-light-24.png");
			Image lightingDarkIcon = new Image("/resources/icons8-sun-24.png");
			Image restartDarkIcon = new Image("/resources/icons8-redo-light-48.png");
			Image cancelDarkIcon = new Image("/resources/icons8-x-light-50.png");
			Image tutorialDarkIcon = new Image("/resources/icons8-information-light-48.png");
			Image hardDarkIcon = new Image("/resources/icons8-light-skull-32.png");

			
			// Create ImageViews for the icons
			ImageView statsDarkView = new ImageView(statsDarkIcon);
			ImageView leaderBrdDarkView = new ImageView(leaderBrdDarkIcon);
			ImageView lightingDarkView = new ImageView(lightingDarkIcon);
			ImageView restartDarkView = new ImageView(restartDarkIcon);
			ImageView cancelDarkView = new ImageView(cancelDarkIcon);
			ImageView tutorialDarkView = new ImageView(tutorialDarkIcon);
			ImageView hardDarkView = new ImageView(hardDarkIcon);

			
			
			// Set the size of the icons
			statsDarkView.setFitWidth(32);
			statsDarkView.setFitHeight(32);
			leaderBrdDarkView.setFitWidth(32);
			leaderBrdDarkView.setFitHeight(32);
			lightingDarkView.setFitWidth(32);
			lightingDarkView.setFitHeight(32);
			restartDarkView.setFitWidth(32);
			restartDarkView.setFitHeight(32);
			cancelDarkView.setFitWidth(32);
			cancelDarkView.setFitHeight(32);
			tutorialDarkView.setFitWidth(32);
			tutorialDarkView.setFitHeight(32);
			hardDarkView.setFitWidth(32);
			hardDarkView.setFitHeight(32);
			
			giveUpBtn.setGraphic(cancelDarkView);
			restartBtn.setGraphic(restartDarkView);
			displayStatsBtn.setGraphic(statsDarkView);
			leaderBoardBtn.setGraphic(leaderBrdDarkView);
			tutorialBtn.setGraphic(tutorialDarkView);
			lightingBtn.setGraphic(lightingDarkView);
			changeDifficulty.setGraphic(hardDarkView);
			
		} else {
			scene.getStylesheets().remove("view_controller/darkStyle.css");
			scene.getStylesheets().add("view_controller/regularStyle.css");
			lighting = "Light";

			Image statsIcon = new Image("/resources/icons8-combo-chart-24.png");
			Image leaderBrdIcon = new Image("/resources/icons8-leaderboard-24.png");
			Image lightingIcon = new Image("/resources/icons8-moon-and-stars-30.png");
			Image restartIcon = new Image("/resources/icons8-redo-48.png");
			Image cancelIcon = new Image("/resources/icons8-x-50.png");
			Image tutorialIcon = new Image("/resources/icons8-information-48.png");
			Image hardIcon = new Image("/resources/icons8-skull-32.png");


			ImageView statsView = new ImageView(statsIcon);
			ImageView leaderBrdView = new ImageView(leaderBrdIcon);
			ImageView lightingView = new ImageView(lightingIcon);
			ImageView restartView = new ImageView(restartIcon);
			ImageView cancelView = new ImageView(cancelIcon);
			ImageView tutorialView = new ImageView(tutorialIcon);
			ImageView hardView = new ImageView(hardIcon);

			statsView.setFitWidth(32);
			statsView.setFitHeight(32);
			leaderBrdView.setFitWidth(32);
			leaderBrdView.setFitHeight(32);
			lightingView.setFitWidth(32);
			lightingView.setFitHeight(32);
			restartView.setFitWidth(32);
			restartView.setFitHeight(32);
			cancelView.setFitWidth(32);
			cancelView.setFitHeight(32);
			tutorialView.setFitWidth(32);
			tutorialView.setFitHeight(32);
			hardView.setFitWidth(32);
			hardView.setFitHeight(32);

			giveUpBtn.setGraphic(cancelView);
			restartBtn.setGraphic(restartView);
			displayStatsBtn.setGraphic(statsView);
			leaderBoardBtn.setGraphic(leaderBrdView);
			tutorialBtn.setGraphic(tutorialView);
			lightingBtn.setGraphic(lightingView);
			changeDifficulty.setGraphic(hardView);

		}
	}

	/**
	 * 
	 * Changes the stylesheet attached to the scene based on the lighting button
	 * 
	 * @param scene a scene that is opened by a button
	 */
	public void displayLighting(Scene scene) {
		if (lighting == "Dark") {
			scene.getStylesheets().add("view_controller/darkStyle.css");
			scene.getStylesheets().remove("view_controller/regularStyle.css");

		} else {
			scene.getStylesheets().add("view_controller/regularStyle.css");
			scene.getStylesheets().remove("view_controller/darkStyle.css");
		}
	}

}
