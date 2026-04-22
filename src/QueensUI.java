/*
Team Members: David Lawrence, Daniel Kocsis
Game: Queens
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class QueensUI extends Application
{
    //timer object that tracks how long the player takes on a level
    private final Timer levelTimer = new Timer();

    //JavaFX timer that updates the label every few milliseconds
    private Timeline uiClock;

    //label on screen that displays the timer text like 0:00
    private final Label timerLabel = new Label("0:00");

    //main JavaFX window (we reuse this and swap scenes)
    private Stage primaryStage;

    //the selector screen scene (so we can switch back to it)
    private Scene selectorScene;

    //best times loaded from scores.csv (level index -> best time in ms)
    private HashMap<Integer, Long> bestTimes = ScoreSaver.loadBestTimes();

    //custom best times loaded from customtimes.csv (level index -> best time in ms)
    private HashMap<Integer, Long> customBestTimes = ScoreSaver.loadCustomBestTimes();

    //current level index (0 = Level 1)
    private int currentLevel = 0;

    //the board UI (creates the grid and stores the player's clicks)
    private BoardUI boardUI;

    //the game screen UI (top bar, buttons, slots, and where the board is shown)
    private GameScreenUI gameUI;

    //holds slot data if the player loaded a slot from the selector screen
    private ScoreSaver.SaveData pendingLoadedSlot = null;

    //getter used by Logic.java to read the player's current board state
    public int[][] getUserState()
    {
        //if boardUI has not been created yet, return an empty board
        return boardUI == null ? new int[0][0] : boardUI.getUserState();
    }

    //store a custom map when testing a new level that is null if no level is passed
    private int[][] customRegionMap = null;

    //boolean to see if the user is testing a new level or not
    private boolean isTestingNewLevel = false;

    //variable for the current custom level index
    private int currentCustomLevelIndex = -1;

    //getter used by Logic.java to read the current level's region map (for region checking)
    public int[][] getCurrentLevelMap()
    {
        if (customRegionMap != null) return customRegionMap; //if there's a custom level, return that instead of a regular level.
        //  This happens when a custom level has been created and will be tested before adding to custom levels file.
        return Levels.LEVELS[currentLevel];
    }

    @Override
    public void start(Stage stage)
    {
        //save the stage so we can swap between selector screen and game screen
        primaryStage = stage;

        //build and show the selector screen first
        selectorScene = new Scene(buildSelectorRoot(), 520, 800);
        primaryStage.setTitle("Queens - Level Select");
        primaryStage.setScene(selectorScene);
        primaryStage.show();
    }

    //build the selector UI using the separate LevelSelectorUI class
    private Parent buildSelectorRoot()
    {
        //LevelSelectorUI is its own class that builds the selector screen layout
        LevelSelectorUI selector = new LevelSelectorUI(
                //best time data so the selector can display it
                bestTimes,
                //timer used to format best times like 1:05
                levelTimer,
                //how many levels exist
                Levels.LEVELS.length,

                //when the player presses Start Game
                levelIndex -> {
                    //starting a fresh level, so no slot load is pending
                    pendingLoadedSlot = null;

                    //save selected level
                    currentLevel = levelIndex;

                    //switch to the game screen
                    showGameScreen();
                },

                //when the player loads a slot from the selector screen
                data -> {
                    //store the slot data so we can apply it after the game screen is built
                    pendingLoadedSlot = data;

                    //set the level to whatever level was saved in the slot
                    currentLevel = data.levelIndex;

                    //switch to the game screen
                    showGameScreen();
                },
                this::showSizePicker,
                this::showCustomLevelSelector
        );

        //return the root node for the selector screen
        return selector.getRoot();
    }

    //switch to the game screen
    private void showGameScreen()
    {
        //stop old UI timer updates if they exist (helps when going back and forth)
        if (uiClock != null) uiClock.stop();

        //create a new board every time we enter the game screen
        boardUI = new BoardUI();

        //this runs ONE time on the first cell click (used to start timing)
        boardUI.setOnFirstAction(() -> {

            //if a slot is pending, keep the loaded time and just start counting
            if (pendingLoadedSlot != null)
            {
                levelTimer.start();
            }
            //otherwise reset the timer and start from 0
            else
            {
                levelTimer.reset();
                levelTimer.start();
            }
        });

        //color the board for the current level (uses the region map)
        boardUI.renderLevel(getCurrentLevelMap());

        //create the game screen UI and connect its buttons to our methods
        gameUI = new GameScreenUI(
                bestTimes,
                levelTimer,
                currentLevel,
                boardUI.getNode(),
                timerLabel,

                //back button action
                this::showSelectorScreen,

                //level dropdown changed
                this::switchLevel,

                //clear button action
                this::clearBoard,

                //check puzzle button action
                () -> checkPuzzle(gameUI.getPuzzleCompletedLabel()),

                //save slot action (slot number passed in)
                slot -> saveSlot(slot, gameUI.getPuzzleCompletedLabel()),

                //load slot action (slot number passed in)
                slot -> loadSlot(slot, gameUI.getPuzzleCompletedLabel())
        );

        //check if the level being played is custom and disable stuff if it is
        if (customRegionMap != null)
        {
            gameUI.disableSlots();
            gameUI.hideLevelPicker();
        }

        //start the JavaFX Timeline that updates the timer label
        setupClock();

        //show the game screen scene
        primaryStage.setTitle("Queens");
        primaryStage.setScene(new Scene(gameUI.getRoot(), 720, 820));
        primaryStage.show();

        //if we loaded a slot from selector screen, apply it now (after UI exists)
        if (pendingLoadedSlot != null)
        {
            applyLoadedSlot(pendingLoadedSlot);
            pendingLoadedSlot = null;
        }

        //stop the timer updater when the window closes
        primaryStage.setOnCloseRequest(_ -> { if (uiClock != null) uiClock.stop(); });
    }

    //go back to selector screen
    private void showSelectorScreen()
    {
        //clear any custom levels
        customRegionMap = null;

        //stop the UI timer updates
        if (uiClock != null) uiClock.stop();

        //reload best times so selector shows newest values
        bestTimes = ScoreSaver.loadBestTimes();

        //reload best times so the selector also knows the newest values
        customBestTimes = ScoreSaver.loadCustomBestTimes();

        //rebuild selector screen so it updates best times on screen
        selectorScene = new Scene(buildSelectorRoot(), 520, 500);

        //reset timer display back to 0:00
        resetTimerUI();

        //show selector screen
        primaryStage.setTitle("Queens - Level Select");
        primaryStage.setScene(selectorScene);
        primaryStage.show();
    }

    //builds the level creator and swaps the scene to it
    private void showLevelCreator(int size)
    {
        LevelCreatorUI creator = new LevelCreatorUI(
                size,
                this::showSizePicker, //back goes to the size picker
                regionMap ->{
                    customRegionMap = regionMap;
                    isTestingNewLevel = true;
                    showGameScreen();
                }
        );

        primaryStage.setTitle("Queens - Level Creator");
        primaryStage.setScene(new Scene(creator.getRoot(), 800, 900));
        primaryStage.show();
    }//end showLevelCreator

    //builds the selector for any custom levels that were made
    private void showCustomLevelSelector()
    {
        //get the list of custom levels from the csv that holds them
        ArrayList<CustomLevel> levels = ScoreSaver.loadCustomLevels();
        CustomLevelSelectorUI selector = new CustomLevelSelectorUI(
                levels,
                this::showSelectorScreen,
                index -> {
                    customRegionMap = levels.get(index).regionMap;
                    currentCustomLevelIndex = index;
                    isTestingNewLevel = false;
                    showGameScreen();
                },
                customBestTimes,
                levelTimer
        );
        primaryStage.setTitle("Queens - Custom Levels");
        primaryStage.setScene(new Scene(selector.getRoot(), 520, 800));
        primaryStage.show();
    }//end showCustomLevelSelector

    //set up the JavaFX timer that refreshes the timer label
    private void setupClock()
    {
        //set label to the current timer value immediately
        timerLabel.setText(levelTimer.getElapsedTimeString());

        //update the timer label every 200ms (0.2 seconds)
        uiClock = new Timeline(new KeyFrame(Duration.millis(200), _ -> timerLabel.setText(levelTimer.getElapsedTimeString())));

        //repeat forever
        uiClock.setCycleCount(Timeline.INDEFINITE);

        //start it
        uiClock.play();
    }

    //reset timer display back to 0:00
    private void resetTimerUI()
    {
        //reset the Timer object and label
        levelTimer.reset();
        timerLabel.setText("0:00");
    }

    //switch to a different level during gameplay
    private void switchLevel(int newLevelIndex)
    {
        //update level number
        currentLevel = newLevelIndex;

        //re-color the board using the new level map
        boardUI.renderLevel(Levels.LEVELS[currentLevel]);

        //reset timer display for the new level
        resetTimerUI();

        //allow timer to start again on the first click
        boardUI.resetFirstAction();
    }

    //clear the board and reset timer
    private void clearBoard()
    {
        //clear board state
        boardUI.clear();

        //reset timer display
        resetTimerUI();

        //allow timer to start again on the first click
        boardUI.resetFirstAction();
    }

    //check if puzzle is solved
    private void checkPuzzle(Label puzzleCompleted)
    {
        //create logic object that checks Queens rules
        Logic logic = new Logic(this);


        //if the puzzle is solved
        if (logic.checkGameState())
        {
            //show success message and completion time
            puzzleCompleted.setText("The puzzle is correct!\nYour completion time is : " + levelTimer.getElapsedTimeString());

            //pause timer
            levelTimer.pause();

            //stop label updater so time freezes on screen
            if (uiClock != null) uiClock.stop();

            //save a best time depending on the level type
            if (customRegionMap == null)
            {
                // built-in level — save best time normally
                long t = levelTimer.getElapsedMilliseconds();
                if (!bestTimes.containsKey(currentLevel) || t < bestTimes.get(currentLevel))
                    bestTimes.put(currentLevel, t);
                ScoreSaver.saveBestTimes(bestTimes);
            }
            else if (isTestingNewLevel)
            {
                // brand new level — show save dialog
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Save Level");
                dialog.setHeaderText("Your puzzle is valid and you solved it!\nDo you want to save it?");
                dialog.setContentText("Enter your name:");

                dialog.showAndWait().ifPresent(name -> {
                    CustomLevel level = new CustomLevel(name, customRegionMap.length, customRegionMap);
                    ScoreSaver.saveCustomLevel(level);
                });
            }
            else
            {
                // playing a saved custom level — save custom best time
                long t = levelTimer.getElapsedMilliseconds();
                if (!customBestTimes.containsKey(currentCustomLevelIndex) || t < customBestTimes.get(currentCustomLevelIndex))
                    customBestTimes.put(currentCustomLevelIndex, t);
                ScoreSaver.saveCustomBestTimes(customBestTimes);
            }
        }
        //if the puzzle is not solved
        else
        {
            puzzleCompleted.setText("The puzzle is incorrect or incomplete!");
        }
    }

    //save slot button logic
    private void saveSlot(int slotNumber, Label puzzleCompleted)
    {
        //save the current level, board, and time into slot#.csv
        ScoreSaver.saveSlot(slotNumber, currentLevel, boardUI.getUserState(), levelTimer.getElapsedMilliseconds());

        //show a message
        puzzleCompleted.setText("Saved to Slot " + slotNumber);
    }

    //load slot button logic
    private void loadSlot(int slotNumber, Label puzzleCompleted)
    {
        //try to load the slot file
        ScoreSaver.SaveData data = ScoreSaver.loadSlot(slotNumber);

        //if the slot file does not exist, show a message
        if (data == null)
        {
            puzzleCompleted.setText("Slot " + slotNumber + " is empty");
            return;
        }

        //show a message
        puzzleCompleted.setText("Loaded Slot " + slotNumber);

        //apply the loaded slot data to the game
        applyLoadedSlot(data);
    }

    //apply loaded slot data while in game screen
    private void applyLoadedSlot(ScoreSaver.SaveData data)
    {
        //switch to saved level
        currentLevel = data.levelIndex;

        //update dropdown selection
        gameUI.getLevelPicker().getSelectionModel().select(currentLevel);

        //redraw colors and load board state
        boardUI.renderLevel(Levels.LEVELS[currentLevel]);
        boardUI.setUserState(data.userState);

        //load saved time and update label
        levelTimer.setElapsedMilliseconds(data.elapsedMs);
        timerLabel.setText(levelTimer.getElapsedTimeString());

        //allow first click to start counting again without resetting the loaded time
        boardUI.resetFirstAction();
        boardUI.setOnFirstAction(levelTimer::start);

        //resume timer label updates
        if (uiClock != null) uiClock.play();
    }

    //this shows a size picker for the level creator before loading the LevelCreatorUI.
    private void showSizePicker()
    {
        SizePickerUI picker = new SizePickerUI(
                this::showSelectorScreen,
                this::showLevelCreator
        );
        primaryStage.setTitle("Queens - Create Level");
        primaryStage.setScene(new Scene(picker.getRoot(), 300, 200));
        primaryStage.show();
    }//end showSizePicker()

    public static void main(String[] args)
    {
        launch(args);
    }
}