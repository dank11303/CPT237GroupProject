import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
                           Consumer<ScoreSaver.SaveData> onLoadSlotData,
                           Runnable onCreateLevel,
                           Runnable onCustomLevels)
    {
        //title label at the top
        Label title = new Label("Queens");
        title.setFont(Font.font(28));

        //instructions label below the title
        Label info = new Label("Choose a level to start the game.");
        info.setFont(Font.font(14));

        //create a grid pane that will hold levels
        GridPane levels = new GridPane();
        levels.setAlignment(Pos.CENTER);
        levels.setHgap(10);
        levels.setVgap(10);

        //loop through each level and create a small vbox for each to store level button and best time
        for (int i = 0; i < levelCount; i++)
        {
            //create a button and a label
            Button btnLevel = new Button("Level " + (i+1));
            Label lblTime = new Label();
            //set best time to none
            lblTime.setText("Best: N/A");
            if (bestTimes.containsKey(i)) //if there is actually a time, set it to that.
            {
                lblTime.setText("Best: " + timer.getElapsedTimeString(bestTimes.get(i)));
            }

            //set index variable because lambda doesn't like the i in the loop
            int index = i;
            //event handler for the button
            btnLevel.setOnAction(_ -> onStartLevel.accept(index));

            //button size
            btnLevel.setPrefSize(80, 80);


            VBox lvl = new VBox(btnLevel, lblTime); //create a VBox for each level and add the button and level to it
            //set VBox alignment
            lvl.setAlignment(Pos.CENTER);
            levels.add(lvl, (i % 5), (i / 5)); //add the level to the grid.
        }

        //load slot buttons (right column)
        Button load1 = makeLoadButton(1, onLoadSlotData);
        Button load2 = makeLoadButton(2, onLoadSlotData);
        Button load3 = makeLoadButton(3, onLoadSlotData);

        //bar for the slots
        HBox slots = new HBox(10, load1, load2, load3);
        slots.setAlignment(Pos.CENTER);

        //buttons for custom level creator and custom level selector
        Button levelCreator = new Button("Level Designer");
        Button customLevelSelector = new Button("Custom Levels");
        //put the buttons in an HBox and set styling
        HBox customs = new HBox(10, levelCreator, customLevelSelector);
        customs.setAlignment(Pos.CENTER);
        customs.setMargin(levelCreator, new Insets(30, 5, 10, 10));
        customs.setMargin(customLevelSelector, new Insets(30, 10, 10, 5));

        //button event handlers
        levelCreator.setOnAction(_ -> onCreateLevel.run());
        customLevelSelector.setOnAction(_ -> onCustomLevels.run());

        //root layout settings
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        //add everything to the root VBox in order
        root.getChildren().addAll(title, info, levels, slots, customs, status);
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