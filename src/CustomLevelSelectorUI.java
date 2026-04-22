import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.function.IntConsumer;
import java.util.HashMap;

//this class builds the custom level selector GUI
public class CustomLevelSelectorUI
{
    //create a VBox to hold all of the elements
    private final VBox root = new VBox(15);

    //constructor. Accepts a custom level array, has a runnable onBack for a back button, IntConsumer
    public CustomLevelSelectorUI(ArrayList<CustomLevel> customLevels,
                                 Runnable onBack,
                                 IntConsumer onSelectLevel,
                                 HashMap<Integer, Long> customBestTimes,
                                 Timer timer)
    {
        //label for the page
        Label title = new Label("Custom Levels");
        title.setFont(Font.font(28));

        //prompt user for action
        Label info = new Label("Choose a level to start the game.");
        info.setFont(Font.font(14));

        //back button that will return to the main menu
        Button back = new Button("Return to Menu");
        back.setOnAction(_ -> onBack.run());

        //create a grid pane that will hold the levels. Kinda like in LevelSelectorUI
        GridPane levels = new GridPane();
        levels.setAlignment(Pos.CENTER);
        levels.setHgap(10);
        levels.setVgap(10);

        //loop through all levels and make a tile with the creator name and a best time
        for (int i = 0; i < customLevels.size(); i++)
        {
            //button, creator name, best time
            Button btnLevel = new Button("Level " + (i + 1));
            Label name = new Label(customLevels.get(i).creatorName);
            Label lblTime = new Label("Best: N/A");

            //if there is a best time, set the label to that
            if (customBestTimes.containsKey(i))
            {
                lblTime.setText("Best: " + timer.getElapsedTimeString(customBestTimes.get(i)));
            }

            //preferred size setting
            btnLevel.setPrefSize(80, 80);

            //save the index and create an action for each tile to select the proper level
            int index = i;
            btnLevel.setOnAction(_ -> onSelectLevel.accept(index));

            //make a vbox to add all of the small elements like the button, creator, and time
            VBox lvl = new VBox(5, btnLevel, name, lblTime);
            lvl.setAlignment(Pos.CENTER);
            levels.add(lvl, i % 5, i / 5);
        }

        //set some stuff for the root scene
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        //check if there are levels, if not, then say no levels are here. Otherwise, display levels
        if (customLevels.isEmpty())
        {
            root.getChildren().addAll(title, new Label("No custom levels yet!"), back);
        }
        else
        {
            root.getChildren().addAll(title, info, levels, back);
        }
    }

    //getter method to access the root used in QueensUI
    public Parent getRoot()
    {
        return root;
    }
}