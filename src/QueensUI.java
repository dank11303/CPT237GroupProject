import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.HashMap;

public class QueensUI extends Application {

    //keeps track of the level time
    private final Timer levelTimer = new Timer();
    //updates the timer label on a schedule
    private Timeline uiClock;
    //shows the timer on the screen
    private final Label timerLabel = new Label("0:00");
    //tracks if the timer has started yet
    private boolean timerStarted = false;

    //the number of columns and rows the grid has at the moment
    static final int N = 8;

    // levels[level][row][col] = region/color id
    static final int[][][] LEVELS = {
            { // Level 1 (level 7 in Medium folder)
                    {0,0,0,0,0,1,1,1},
                    {0,2,0,0,0,0,1,1},
                    {0,2,2,0,1,1,1,1},
                    {3,3,2,2,1,1,4,1},
                    {3,3,2,4,4,4,4,4},
                    {3,3,3,4,6,6,4,4},
                    {3,3,4,4,4,6,5,5},
                    {3,3,3,4,7,7,7,5}
            },
            { // Level 2 (level 6 in Medium)
                    {0,0,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,2},
                    {0,0,3,1,1,0,0,2},
                    {3,3,3,3,1,2,2,2},
                    {4,4,5,5,7,2,2,2},
                    {4,4,6,7,7,2,7,2},
                    {6,6,6,7,7,7,7,2},
                    {6,6,7,7,7,7,7,7}
            }
    };//end level array

    // solutions[level][row][col] = 1 means queen belongs here (optional for later)
    static final int[][][] SOLUTIONS = {
            { // Level 2 (level 6 in Medium)
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,1,0,0,0}
            }, // Level 2 solution placeholder
            { // Level 1 (level 7 in Medium folder)
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,1,0,0,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0}
            }
    };//end solution array

    //array to pre-define colors
    static final Color[] PALETTE = {
            Color.web("#b9a6e8"), // 0 purple
            Color.web("#f6c28f"), // 1 orange
            Color.web("#8fb6ff"), // 2 blue
            Color.web("#a7df9f"), // 3 green
            Color.web("#d9b58e"), // 4 tan
            Color.web("#e0e0e0"), // 5 gray
            Color.web("#a8dfb0"), // 6 light green
            Color.web("#f26c5f"), // 7 red
            Color.web("#f2e58c"), // 8 yellow
            Color.web("#cfcfcf")  // 9 extra gray
    };

    //arrays that handle each grid and the marks it has
    private final int[][] userState = new int[N][N]; // 0 empty, 1 x, 2 queen
    //stores the text shown in each grid cell
    private final Label[][] symbols = new Label[N][N];
    //stores the clickable boxes for each grid cell
    private final StackPane[][] cells = new StackPane[N][N];

    //declare the score saver object
    ScoreSaver scoreSaver =  new ScoreSaver();
    //check for best times file existence
    HashMap<Integer, Long> bestTimes = scoreSaver.loadBestTimes(); //load any best times that may have been saved. Returns a HashMap<Integer, Long>


    private int currentLevel = 0;

    @Override
    public void start(Stage stage) {
        //create logic object and pass "this" to it
        Logic gameLogic = new Logic(this);
        Label title = new Label("Queens");
        title.setFont(Font.font(24));

        //label that will show up under the button to return game state
        Label puzzleCompleted = new Label("Not Checked");

        //check puzzle button that will check if the puzzle is complete or not
        Button checkState = getButton(puzzleCompleted);

        //combo box that allows you to switch between levels.
        ComboBox<String> levelPicker = new ComboBox<>();
        for (int i = 0; i < LEVELS.length; i++)
        {
            String bt = "";
            if (bestTimes.containsKey(i))
            {
                bt = levelTimer.getElapsedTimeString(bestTimes.get(i));
            }
            levelPicker.getItems().add("Level " + (i + 1) + " " + bt);
            //if level has a best time, display that time (not done)
        }
        levelPicker.getSelectionModel().select(0);
        //switch levels, reset the board, and restart the timer display
        levelPicker.setOnAction(_ -> {
            puzzleCompleted.setText("Not Checked");
            currentLevel = levelPicker.getSelectionModel().getSelectedIndex();
            clearUserState();
            renderLevel(currentLevel);

            levelTimer.reset();
            timerStarted = false;
            timerLabel.setText("0:00");
            uiClock.play();
        });

        //clear button that calls functions that will reset the game state
        Button clear = new Button("Clear Marks");

        //clear the board, reset the status label, and restart the timer
        clear.setOnAction(_ -> {
            puzzleCompleted.setText("Not Checked");
            clearUserState();
            renderSymbols();

            levelTimer.reset();
            timerStarted = false;
            timerLabel.setText("0:00");
            uiClock.play();
        });

        timerLabel.setFont(Font.font(16));

        //update the timer label over and over while the program runs
        uiClock = new Timeline(new KeyFrame(Duration.millis(200), _ -> timerLabel.setText(levelTimer.getElapsedTimeString())));
        //make the timer update repeat forever
        uiClock.setCycleCount(Timeline.INDEFINITE);
        uiClock.play();

        //horizontal box that will act as a header for the program.
        HBox topBar = new HBox(12, title, levelPicker, clear, new Label("Time:"), timerLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        //the gameplay area
        GridPane board = buildBoard();
        renderLevel(currentLevel);

        //setting the header and gameplay area into a pane
        VBox root = new VBox(10, topBar, board, checkState, puzzleCompleted);
        root.setPadding(new Insets(10));

        //setting the scene for display
        stage.setScene(new Scene(root, 720, 820));
        stage.setTitle("Queens UI (Levels via 3D Array)");
        //stop the timer updater when the window closes
        stage.setOnCloseRequest(_ -> {
            if (uiClock != null) uiClock.stop();
        });
        stage.show();
    }

    //create and return the "Check Puzzle" button that updates the label with the result
    private Button getButton(Label puzzleCompleted) {
        Button checkState = new Button("Check Puzzle");
        //when the button is clicked, check if the puzzle is solved and show the result
        checkState.setOnAction(_ ->
        {
            Logic logic = new Logic(this);
            if (logic.checkGameState() == true)
            {
                puzzleCompleted.setText("The puzzle is correct!\nYour completion time is : " + levelTimer.getElapsedTimeString());
                levelTimer.pause();
                uiClock.stop();
                //check if the current level has a best time. If not, compare times and save soonest, if it is null, add time
                if (bestTimes.containsKey(currentLevel))
                {
                    long savedTime = bestTimes.get(currentLevel);
                    if (levelTimer.getElapsedMilliseconds() < savedTime)
                    {
                        bestTimes.put(currentLevel, levelTimer.getElapsedMilliseconds()); //save new time
                    }
                }
                else
                {
                    bestTimes.put(currentLevel, levelTimer.getElapsedMilliseconds());
                }
                scoreSaver.saveBestTimes(bestTimes);
            }
            else
            {
                puzzleCompleted.setText("The puzzle is incorrect or incomplete!");
            }
        });
        return checkState;
    }

    //generates a GirdPane for the levels to be displayed in
    private GridPane buildBoard() {
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setAlignment(Pos.CENTER);

        //for loop that loops as many times as the rows/columns number was set
        //loops for each row
        for (int r = 0; r < N; r++) {
            //nested loop that repeats for each column
            for (int c = 0; c < N; c++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(1))));

                Label txt = new Label("");
                txt.setFont(Font.font(26));

                symbols[r][c] = txt;
                cells[r][c] = cell;

                //start the timer on the first click, then cycle the cell state and redraw the board
                int rr = r, cc = c;
                cell.setOnMouseClicked(_ -> {
                    //start the level timer the first time the player clicks the board
                    if (!timerStarted)
                    {
                        levelTimer.reset();
                        levelTimer.start();
                        timerStarted = true;
                    }
                    //cycle this cell’s state (empty/x/queen) and update the display
                    userState[rr][cc] = (userState[rr][cc] + 1) % 3;
                    renderSymbols();
                });

                cell.getChildren().add(txt);
                grid.add(cell, c, r);
            }
        }
        return grid; //after the grid is created.
    }//end buildBoard

    //sets the colors of each cell then renders the symbols for the level
    private void renderLevel(int levelIndex) {
        int[][] map = LEVELS[levelIndex];

        //loops for each row
        for (int r = 0; r < N; r++) {
            //loops for each column
            for (int c = 0; c < N; c++) {
                //gets the color ID of each cell using the map array and fills that color according to the color code.
                int id = map[r][c];
                Color fill = PALETTE[id % PALETTE.length];
                cells[r][c].setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
        //place any symbols that are in the game.
        renderSymbols();
    }//end renderLevel()

    //function to place any symbols on the map
    private void renderSymbols() {
        //loop for each row
        for (int r = 0; r < N; r++) {
            //loop for each column
            for (int c = 0; c < N; c++) {
                //checks the symbols array for each cell and places the appropriate symbol depending on the state of the cell
                symbols[r][c].setText(
                        userState[r][c] == 0 ? "" :
                        userState[r][c] == 1 ? "x" : "♛"
                );
            }
        }
    }//end renderSymbols()

    //clear all cells in the grid
    private void clearUserState() {
        //loop for each row
        for (int r = 0; r < N; r++)
            //loop for each column
            for (int c = 0; c < N; c++)
                //reset the symbol in the grid
                userState[r][c] = 0;
    }//end clearUserState()

    //getter to be able to access the user state in different files
    public int[][] getUserState()
    {
        return userState;
    }

    //getter for the current solution
    public int[][] getSolution()
    {
        //return the proper array depending on the selected level
        return SOLUTIONS[currentLevel];
    }

    //for IDEs that need this stuff
    static void main(String[] args) {
        launch(args);
    }
}