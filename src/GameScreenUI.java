import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.function.IntConsumer;

public class GameScreenUI
{
    //main root node for the game screen (everything is placed inside this)
    private final VBox root = new VBox(10);

    //dropdown for level switching (Levels 1–10)
    private final ComboBox<String> levelPicker = new ComboBox<>();

    //label that shows if puzzle is solved or not
    private final Label puzzleCompleted = new Label("Not Checked");

    //had to move this so I can disable the buttons. This is the horizontal box for all of the save slot buttons.
    private final HBox slotsBar = new HBox(10);

    //constructor builds the entire game screen UI
    public GameScreenUI(HashMap<Integer, Long> bestTimes,
                        Timer timer,
                        int startLevelIndex,
                        Parent boardNode,
                        Label sharedTimerLabel,
                        Runnable onBack,
                        IntConsumer onLevelChange,
                        Runnable onClear,
                        Runnable onCheck,
                        IntConsumer onSaveSlot,
                        IntConsumer onLoadSlot)
    {
        //use the timer label that was created in QueensUI (shared label)

        //set font for the timer text so it looks readable
        sharedTimerLabel.setFont(Font.font(16));

        //title at the top of the game screen
        Label title = new Label("Queens");
        title.setFont(Font.font(24));

        //back button sends the player back to the level selector screen
        Button back = new Button("Back to Levels");
        back.setOnAction(_ -> onBack.run());

        //fill the level dropdown (show best time if it exists)
        for (int i = 0; i < Levels.LEVELS.length; i++)
        {
            //if the best time exists for this level, format it like 1:05
            String bt = bestTimes.containsKey(i) ? timer.getElapsedTimeString(bestTimes.get(i)) : "";

            //add level name to the dropdown
            levelPicker.getItems().add("Level " + (i + 1) + " " + bt);
        }

        //set current level selection (the level you started on)
        levelPicker.getSelectionModel().select(startLevelIndex);

        //when level changes, call QueensUI with the new selected index
        levelPicker.setOnAction(_ -> onLevelChange.accept(levelPicker.getSelectionModel().getSelectedIndex()));

        //clear button (QueensUI decides what "clear" does)
        Button clearButton = new Button("Clear Marks");
        clearButton.setOnAction(_ -> onClear.run());

        //check button (QueensUI decides how to check)
        Button checkButton = new Button("Check Puzzle");
        checkButton.setOnAction(_ -> onCheck.run());

        //save/load slot buttons bar (built in a loop to avoid repeated code)
        slotsBar.setAlignment(Pos.CENTER_LEFT);
        slotsBar.setPadding(new Insets(5, 10, 5, 10));

        //build Save/Load buttons for Slot 1, Slot 2, Slot 3
        for (int slot = 1; slot <= 3; slot++)
        {
            int s = slot; //local copy for lambda

            //save button for this slot
            Button save = new Button("Save " + s);
            save.setOnAction(_ -> onSaveSlot.accept(s));

            //load button for this slot
            Button load = new Button("Load " + s);
            load.setOnAction(_ -> onLoadSlot.accept(s));

            //add both buttons to the bar
            slotsBar.getChildren().addAll(save, load);
        }

        //top bar layout (holds title, dropdown, buttons, and timer label)
        HBox topBar = new HBox(12, back, title, levelPicker, clearButton, new Label("Time:"), sharedTimerLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        //main layout for the screen
        //topBar = header area
        //boardNode = the actual board (GridPane from BoardUI)
        //slotsBar = save/load row
        //checkButton = button to check the puzzle
        //puzzleCompleted = message label
        root.setPadding(new Insets(10));
        root.getChildren().addAll(topBar, boardNode, slotsBar, checkButton, puzzleCompleted);
    }

    //gives QueensUI the root node for the scene
    public Parent getRoot()
    {
        return root;
    }

    //gives QueensUI the level dropdown so it can update selection after loading a slot
    public ComboBox<String> getLevelPicker()
    {
        return levelPicker;
    }

    //lets QueensUI write messages like "Loaded Slot 1" or "Solved!"
    public Label getPuzzleCompletedLabel()
    {
        return puzzleCompleted;
    }

    //disables all save/load slots when playing a custom level
    public void disableSlots()
    {
        //loop through all nodes and disable them
        for (javafx.scene.Node node : slotsBar.getChildren())
        {
            node.setDisable(true);
        }
    }

    //hide the level picker when playing a custom level
    public void hideLevelPicker()
    {
        levelPicker.setVisible(false);
        levelPicker.setManaged(false);
    }
}