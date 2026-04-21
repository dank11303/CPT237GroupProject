import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
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

    //store the main stage so we can switch screens
    private Stage primaryStage;

    //store the selector scene so we can return to it
    private Scene selectorScene;

    //label used to show messages on the selector screen
    private Label selectorStatus;

    //used to switch ComboBox selection after loading a slot
    private ComboBox<String> levelPicker;

    //check for best times file existence
    HashMap<Integer, Long> bestTimes = ScoreSaver.loadBestTimes(); //load any best times that may have been saved

    //holds a slot load request when user loads from selector screen
    private ScoreSaver.SaveData pendingLoadedSlot = null;

    private int currentLevel = 0;

    public int[][] getCurrentLevelMap()
    {
        return LEVELS[currentLevel];
    }

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
            },
            { // Level 3 (level 8 in Medium)
                    {0,0,0,1,1,1,2,2},
                    {0,0,0,0,1,1,2,2},
                    {0,0,7,0,1,2,2,2},
                    {0,0,7,7,7,7,2,2},
                    {0,6,6,7,7,7,7,2},
                    {0,0,0,7,7,7,4,4},
                    {5,0,0,7,7,7,4,3},
                    {5,5,5,5,7,4,4,3}
            },
            { // Level 4 (level 9 in Medium)
                    {0,0,0,0,0,0,1,1},
                    {0,0,0,0,0,2,2,1},
                    {0,0,4,4,4,2,2,2},
                    {7,0,7,4,7,2,2,2},
                    {7,7,7,7,7,7,7,2},
                    {7,6,7,7,7,7,3,3},
                    {5,6,5,5,7,7,7,3},
                    {5,5,5,7,7,7,7,7}
            },
            { // Level 5 (level 10 in Medium)
                    {1,1,1,1,0,0,0,0},
                    {1,1,2,1,0,0,0,0},
                    {1,2,2,2,0,0,7,0},
                    {1,2,2,0,0,8,7,7},
                    {3,3,0,0,8,8,5,5},
                    {3,3,3,3,3,4,5,5},
                    {3,3,3,3,4,4,5,5},
                    {3,3,3,3,3,4,4,4}
            },
            { // Level 6 (level 6 in Difficult)
                    {3,3,3,3,2,1,1,1},
                    {3,3,3,3,2,2,2,2},
                    {4,4,4,3,0,0,2,0},
                    {5,4,0,0,0,0,0,0},
                    {5,5,5,0,6,0,0,0},
                    {5,5,5,6,6,6,0,0},
                    {5,5,6,6,6,6,0,0},
                    {5,5,5,6,6,7,7,7}
            },
            { // Level 7 (level 7 in Difficult)
                    {1,2,2,2,2,3,3,4},
                    {1,1,2,2,0,4,4,4},
                    {1,1,2,0,0,0,0,4},
                    {1,1,2,0,0,0,0,4},
                    {0,0,0,0,0,0,5,5},
                    {0,0,7,0,6,6,0,0},
                    {0,0,7,0,0,6,0,0},
                    {7,7,7,0,0,0,0,0}
            },
            { // Level 8 (level 8 in Difficult)
                    {1,2,2,2,2,3,3,3},
                    {1,1,2,2,2,2,2,3},
                    {1,1,2,1,3,3,3,3},
                    {1,1,1,1,7,3,3,3},
                    {0,0,0,1,7,7,3,4},
                    {6,0,0,0,0,0,4,4},
                    {6,6,0,5,5,4,4,4},
                    {6,0,0,0,0,0,0,0}
            },
            { // Level 9 (level 9 in Difficult)
                    {0,1,1,0,0,0,0,0},
                    {0,1,0,0,4,4,0,3},
                    {0,0,0,0,4,4,0,3},
                    {0,2,0,4,4,4,3,3},
                    {4,2,0,4,7,7,7,7},
                    {4,4,4,4,6,6,6,6},
                    {5,5,5,4,4,6,5,6},
                    {5,5,5,5,5,5,5,6}
            },
            { // Level 10 (level 10 in Difficult)
                    {0,0,0,0,0,0,0,0},
                    {0,0,3,3,3,3,2,1},
                    {0,4,4,4,3,2,2,1},
                    {0,4,4,5,2,2,1,1},
                    {0,4,5,5,5,2,1,1},
                    {0,0,6,6,5,2,7,1},
                    {0,0,0,0,7,7,7,1},
                    {0,0,0,0,0,0,7,1}
            }
    };//end level array

    // solutions[level][row][col] = 1 means queen belongs here (optional for later)
    static final int[][][] SOLUTIONS = {
            { // Level 1 (level 7 in Medium)
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,1,0,0,0}
            },
            { // Level 2
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,1,0,0,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0}
            },
            { // Level 3
                    {0,0,0,0,1,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1}
            },
            { // Level 4
                    {0,0,0,0,0,0,1,0},
                    {0,0,0,0,1,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0}
            },
            { // Level 5
                    {1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,1,0,0,0},
                    {0,1,0,0,0,0,0,0}
            },
            { // Level 6
                    {0,0,0,0,0,1,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,0,1,0,0,0},
                    {0,0,0,0,0,0,0,1}
            },
            { // Level 7
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,0,0,1,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0}
            },
            { // Level 8
                    {0,0,0,0,0,0,0,1},
                    {0,0,0,0,0,1,0,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,1,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,0,1,0,0,0,0},
                    {1,0,0,0,0,0,0,0}
            },
            { // Level 9
                    {0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,1,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,1,0,0}
            },
            { // Level 10
                    {1,0,0,0,0,0,0,0},
                    {0,0,0,1,0,0,0,0},
                    {0,0,0,0,0,0,1,0},
                    {0,1,0,0,0,0,0,0},
                    {0,0,0,0,1,0,0,0},
                    {0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1}
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

    @Override
    public void start(Stage stage) {

        //store the stage so we can swap between screens
        primaryStage = stage;

        //build the level selector screen first
        selectorScene = new Scene(buildLevelSelectorUI(), 520, 340);

        //show the selector screen
        primaryStage.setTitle("Queens - Level Select");
        primaryStage.setScene(selectorScene);
        primaryStage.show();
    }

    //build the level selector screen (ui)
    private Parent buildLevelSelectorUI()
    {
        //title label for selector screen
        Label title = new Label("Queens");
        title.setFont(Font.font(28));

        //small instructions label
        Label info = new Label("Choose a level and press Start Game.");
        info.setFont(Font.font(14));

        //dropdown list to pick a level
        ComboBox<String> selectorPicker = new ComboBox<>();

        //fill the dropdown with levels + best time (if any)
        for (int i = 0; i < LEVELS.length; i++)
        {
            String bt = "";
            if (bestTimes.containsKey(i))
            {
                bt = " (Best: " + levelTimer.getElapsedTimeString(bestTimes.get(i)) + ")";
            }
            selectorPicker.getItems().add("Level " + (i + 1) + bt);
        }

        //default selection
        selectorPicker.getSelectionModel().select(0);

        //button that starts the game
        Button startGame = new Button("Start Game");

        //slot buttons on the selector screen (stacked)
        Button loadSlot1 = new Button("Load Slot 1");
        Button loadSlot2 = new Button("Load Slot 2");
        Button loadSlot3 = new Button("Load Slot 3");

        //label to show selector messages (slot empty, etc.)
        selectorStatus = new Label("");
        selectorStatus.setFont(Font.font(13));

        //when clicked, set the level and switch to the game screen
        startGame.setOnAction(_ -> {
            pendingLoadedSlot = null; //starting normally, no slot load
            currentLevel = selectorPicker.getSelectionModel().getSelectedIndex();
            showGameScreen();
        });

        //load a slot and start the game
        loadSlot1.setOnAction(_ -> loadSlotFromSelector(1));
        loadSlot2.setOnAction(_ -> loadSlotFromSelector(2));
        loadSlot3.setOnAction(_ -> loadSlotFromSelector(3));

        //LEFT column: level selector
        VBox leftCol = new VBox(8, selectorPicker);
        leftCol.setAlignment(Pos.CENTER_LEFT);

        //MIDDLE column: start game button
        VBox middleCol = new VBox(startGame);
        middleCol.setAlignment(Pos.CENTER);

        //RIGHT column: load slot buttons stacked
        VBox rightCol = new VBox(10, loadSlot1, loadSlot2, loadSlot3);
        rightCol.setAlignment(Pos.CENTER_RIGHT);

        //row that holds left/middle/right
        HBox row = new HBox(40, leftCol, middleCol, rightCol);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10));

        //whole selector screen layout
        VBox selectorRoot = new VBox(15, title, info, row, selectorStatus);
        selectorRoot.setPadding(new Insets(20));
        selectorRoot.setAlignment(Pos.CENTER);

        return selectorRoot;
    }

    //load a save slot directly from the selector screen
    private void loadSlotFromSelector(int slotNumber)
    {
        //try to load the slot file
        ScoreSaver.SaveData data = ScoreSaver.loadSlot(slotNumber);

        //if the slot file does not exist, show a message
        if (data == null)
        {
            selectorStatus.setText("Slot " + slotNumber + " is empty");
            return;
        }

        //save the loaded data so we can apply it after the game UI is created
        pendingLoadedSlot = data;
        currentLevel = data.levelIndex;

        //show the game screen
        showGameScreen();
    }

    //switch to the game screen
    private void showGameScreen()
    {
        //stop any old clock (only matters if you go back and forth)
        if (uiClock != null) uiClock.stop();

        //build the game UI and swap scenes
        Parent gameRoot = buildGameUI();
        Scene gameScene = new Scene(gameRoot, 720, 820);

        primaryStage.setTitle("Queens UI (Levels via 3D Array)");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        //if a slot was loaded from the selector screen, apply it now
        if (pendingLoadedSlot != null)
        {
            applyLoadedSlot(pendingLoadedSlot);
            pendingLoadedSlot = null;
        }
    }

    //apply loaded slot data after the game UI exists
    private void applyLoadedSlot(ScoreSaver.SaveData data)
    {
        //switch to saved level and update dropdown
        setCurrentLevelIndex(data.levelIndex);
        levelPicker.getSelectionModel().select(data.levelIndex);

        //load the saved board
        setUserState(data.userState);

        //load the saved time and resume the timer display
        levelTimer.setElapsedMilliseconds(data.elapsedMs);
        timerStarted = true;
        timerLabel.setText(levelTimer.getElapsedTimeString());
        uiClock.play();
    }

    //switch back to the level selector screen
    private void showSelectorScreen()
    {
        //stop the UI timer updates
        if (uiClock != null) uiClock.stop();

        //reload best times so selector shows newest values
        bestTimes = ScoreSaver.loadBestTimes();

        //rebuild selector screen so best times update
        selectorScene = new Scene(buildLevelSelectorUI(), 520, 340);

        //reset the timer display
        levelTimer.reset();
        timerStarted = false;
        timerLabel.setText("0:00");

        //go back to selector
        primaryStage.setTitle("Queens - Level Select");
        primaryStage.setScene(selectorScene);
        primaryStage.show();
    }

    //build the game screen (this is your old start() code moved into a method)
    private Parent buildGameUI()
    {
        Label title = new Label("Queens");
        title.setFont(Font.font(24));

        //label that will show up under the button to return game state
        Label puzzleCompleted = new Label("Not Checked");

        //check puzzle button that will check if the puzzle is complete or not
        Button checkState = getButton(puzzleCompleted);

        //button to go back to the selector screen
        Button backToLevels = new Button("Back to Levels");
        backToLevels.setOnAction(_ -> showSelectorScreen());

        //combo box that allows you to switch between levels.
        levelPicker = new ComboBox<>();
        for (int i = 0; i < LEVELS.length; i++)
        {
            String bt = "";
            if (bestTimes.containsKey(i))
            {
                bt = levelTimer.getElapsedTimeString(bestTimes.get(i));
            }
            levelPicker.getItems().add("Level " + (i + 1) + " " + bt);
        }

        //start on the chosen level
        levelPicker.getSelectionModel().select(currentLevel);

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

        //save/load slot buttons
        Button save1 = new Button("Save 1");
        Button load1 = new Button("Load 1");
        Button save2 = new Button("Save 2");
        Button load2 = new Button("Load 2");
        Button save3 = new Button("Save 3");
        Button load3 = new Button("Load 3");

        //save the current level, board, and time into slot 1
        save1.setOnAction(_ -> {
            ScoreSaver.saveSlot(1, currentLevel, userState, levelTimer.getElapsedMilliseconds());
            puzzleCompleted.setText("Saved to Slot 1");
        });

        //load slot 1 and restore the level, board, and time
        load1.setOnAction(_ -> {
            ScoreSaver.SaveData data = ScoreSaver.loadSlot(1);
            //if the slot file does not exist, show a message
            if (data == null)
            {
                puzzleCompleted.setText("Slot 1 is empty");
                return;
            }

            //show the load message
            puzzleCompleted.setText("Loaded Slot 1");

            //switch to the saved level and update the dropdown
            setCurrentLevelIndex(data.levelIndex);
            levelPicker.getSelectionModel().select(data.levelIndex);

            //load the saved board
            setUserState(data.userState);

            //load the saved time and resume the timer display
            levelTimer.setElapsedMilliseconds(data.elapsedMs);
            timerStarted = true;
            timerLabel.setText(levelTimer.getElapsedTimeString());
            uiClock.play();
        });

        //save the current level, board, and time into slot 2
        save2.setOnAction(_ -> {
            ScoreSaver.saveSlot(2, currentLevel, userState, levelTimer.getElapsedMilliseconds());
            puzzleCompleted.setText("Saved to Slot 2");
        });

        //load slot 2 and restore the level, board, and time
        load2.setOnAction(_ -> {
            ScoreSaver.SaveData data = ScoreSaver.loadSlot(2);
            //if the slot file does not exist, show a message
            if (data == null)
            {
                puzzleCompleted.setText("Slot 2 is empty");
                return;
            }

            //show the load message
            puzzleCompleted.setText("Loaded Slot 2");

            //switch to the saved level and update the dropdown
            setCurrentLevelIndex(data.levelIndex);
            levelPicker.getSelectionModel().select(data.levelIndex);

            //load the saved board
            setUserState(data.userState);

            //load the saved time and resume the timer display
            levelTimer.setElapsedMilliseconds(data.elapsedMs);
            timerStarted = true;
            timerLabel.setText(levelTimer.getElapsedTimeString());
            uiClock.play();
        });

        //save the current level, board, and time into slot 3
        save3.setOnAction(_ -> {
            ScoreSaver.saveSlot(3, currentLevel, userState, levelTimer.getElapsedMilliseconds());
            puzzleCompleted.setText("Saved to Slot 3");
        });

        //load slot 3 and restore the level, board, and time
        load3.setOnAction(_ -> {
            ScoreSaver.SaveData data = ScoreSaver.loadSlot(3);
            //if the slot file does not exist, show a message
            if (data == null)
            {
                puzzleCompleted.setText("Slot 3 is empty");
                return;
            }

            //show the load message
            puzzleCompleted.setText("Loaded Slot 3");

            //switch to the saved level and update the dropdown
            setCurrentLevelIndex(data.levelIndex);
            levelPicker.getSelectionModel().select(data.levelIndex);

            //load the saved board
            setUserState(data.userState);

            //load the saved time and resume the timer display
            levelTimer.setElapsedMilliseconds(data.elapsedMs);
            timerStarted = true;
            timerLabel.setText(levelTimer.getElapsedTimeString());
            uiClock.play();
        });

        HBox slotsBar = new HBox(10, save1, load1, save2, load2, save3, load3);
        slotsBar.setAlignment(Pos.CENTER_LEFT);
        slotsBar.setPadding(new Insets(5, 10, 5, 10));

        //horizontal box that will act as a header for the program.
        HBox topBar = new HBox(12, backToLevels, title, levelPicker, clear, new Label("Time:"), timerLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        //the gameplay area
        GridPane board = buildBoard();
        renderLevel(currentLevel);

        //setting the header and gameplay area into a pane
        VBox root = new VBox(10, topBar, board, slotsBar, checkState, puzzleCompleted);
        root.setPadding(new Insets(10));

        //stop the timer updater when the window closes
        primaryStage.setOnCloseRequest(_ -> {
            if (uiClock != null) uiClock.stop();
        });

        return root;
    }

    //create and return the "Check Puzzle" button that updates the label with the result
    private Button getButton(Label puzzleCompleted)
    {
        //create the check puzzle button
        Button checkState = new Button("Check Puzzle");

        //when the button is clicked, check if the puzzle is solved and show the result
        checkState.setOnAction(_ ->
        {
            //create a logic object to check the game state
            Logic logic = new Logic(this);

            //if the puzzle is solved
            if (logic.checkGameState() == true)
            {
                //show the win message and time
                puzzleCompleted.setText("The puzzle is correct!\nYour completion time is : " + levelTimer.getElapsedTimeString());

                //stop the timer and UI updates
                levelTimer.pause();
                uiClock.stop();

                //if a best time exists, only save if the new time is faster
                if (bestTimes.containsKey(currentLevel))
                {
                    long savedTime = bestTimes.get(currentLevel);
                    if (levelTimer.getElapsedMilliseconds() < savedTime)
                    {
                        bestTimes.put(currentLevel, levelTimer.getElapsedMilliseconds());
                    }
                }
                //if no best time exists, save the current time
                else
                {
                    bestTimes.put(currentLevel, levelTimer.getElapsedMilliseconds());
                }

                //save best times to the file
                ScoreSaver.saveBestTimes(bestTimes);
            }
            //if the puzzle is not solved
            else
            {
                //show the fail message
                puzzleCompleted.setText("The puzzle is incorrect or incomplete!");
            }
        });

        //return the button
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

    //getter for the current level number
    public int getCurrentLevelIndex()
    {
        //return the current selected level
        return currentLevel;
    }

    //setter for the current level number
    public void setCurrentLevelIndex(int levelIndex)
    {
        //set the level and reset the board
        currentLevel = levelIndex;
        clearUserState();
        renderLevel(currentLevel);

        //reset the timer for the new level
        levelTimer.reset();
        timerStarted = false;
        timerLabel.setText("0:00");
    }

    //setter to load a saved board into the grid
    public void setUserState(int[][] loaded)
    {
        //copy the loaded board into the current board
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                userState[r][c] = loaded[r][c];

        //redraw the symbols on the screen
        renderSymbols();
    }

    //for IDEs that need this stuff
    public static void main(String[] args) {
        launch(args);
    }
}