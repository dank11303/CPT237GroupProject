import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class LevelSelectorUI
{
    //main root node for this screen (everything goes inside this VBox)
    private final VBox root = new VBox(15);

    //message label for things like "Slot 2 is empty"
    private final Label status = new Label("");

    //constructor builds the whole selector UI
    public LevelSelectorUI(HashMap<Integer, Long> bestTimes,
                           Timer timer,
                           int levelCount,
                           IntConsumer onStartLevel,
                           Consumer<ScoreSaver.SaveData> onLoadSlotData)
    {
        //title label at the top
        Label title = new Label("Queens");
        title.setFont(Font.font(28));

        //instructions label below the title
        Label info = new Label("Choose a level and press Start Game.");
        info.setFont(Font.font(14));

        //dropdown list for choosing a level
        ComboBox<String> levelBox = new ComboBox<>();

        //fill dropdown with levels + best time if it exists
        for (int i = 0; i < levelCount; i++)
        {
            //bt is the extra text we add if the best time exists
            String bt = "";

            //if this level has the best time saved, show it
            if (bestTimes.containsKey(i))
            {
                bt = " (Best: " + timer.getElapsedTimeString(bestTimes.get(i)) + ")";
            }

            //add the level name to the dropdown
            levelBox.getItems().add("Level " + (i + 1) + bt);
        }

        //default selection (first level)
        levelBox.getSelectionModel().select(0);

        //start game button (middle column)
        Button startGame = new Button("Start Game");

        //when Start Game is clicked, send the selected level index back to QueensUI
        startGame.setOnAction(_ -> {
            //clear any old status message
            status.setText("");

            //tell QueensUI what level number was selected (0-based index)
            onStartLevel.accept(levelBox.getSelectionModel().getSelectedIndex());
        });

        //load slot buttons (right column)
        Button load1 = makeLoadButton(1, onLoadSlotData);
        Button load2 = makeLoadButton(2, onLoadSlotData);
        Button load3 = makeLoadButton(3, onLoadSlotData);

        //left column (level selector)
        VBox leftCol = new VBox(8, new Label("Level:"), levelBox);
        leftCol.setAlignment(Pos.CENTER_LEFT);

        //middle column (start button)
        VBox midCol = new VBox(startGame);
        midCol.setAlignment(Pos.CENTER);

        //right column (slot buttons stacked)
        VBox rightCol = new VBox(10, load1, load2, load3);
        rightCol.setAlignment(Pos.CENTER_RIGHT);

        //the main row layout (left / middle / right)
        HBox row = new HBox(40, leftCol, midCol, rightCol);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10));

        //root layout settings
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        //add everything to the root VBox in order
        root.getChildren().addAll(title, info, row, status);
    }

    //creates a load slot button that tries to load slot#.csv
    private Button makeLoadButton(int slotNumber, Consumer<ScoreSaver.SaveData> onLoadSlotData)
    {
        //create the button text based on slot number
        Button b = new Button("Load Slot " + slotNumber);

        //when clicked, try to load this slot
        b.setOnAction(_ -> {

            //clear any old status message
            status.setText("");

            //try to load the slot file using ScoreSaver
            ScoreSaver.SaveData data = ScoreSaver.loadSlot(slotNumber);

            //if slot doesn't exist, show message and stop
            if (data == null)
            {
                status.setText("Slot " + slotNumber + " is empty");
                return;
            }

            //send the loaded SaveData back to QueensUI; QueensUI will switch to the game screen and apply it
            onLoadSlotData.accept(data);
        });

        return b;
    }

    //gives QueensUI the root node so it can put it in a Scene
    public Parent getRoot()
    {
        return root;
    }
}